//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.Demand;
import actions.parentDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

import state.SimState;
import sun.management.Agent;
import uk.ac.imperial.presage2.core.environment.*;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.util.*;

public class GlobalEnvService extends EnvironmentService{
    double curtailmentFactor = 1;
    private SimState state;
    private int round = 0;
    private PowerPoolEnvService ChildEnvService;
    final protected EnvironmentServiceProvider serviceProvider;

    HashMap<UUID, ArrayList<Double>> AllocationHistory = new HashMap<UUID, ArrayList<Double>>();
    HashMap <UUID, Integer> AgentBordaPoints = new HashMap<UUID, Integer>();

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    @Named("params.allocationType")
    public int allocationType;

//    @Inject
    @Parameter (name = "parent_level")
    private int parent_level;

    @Inject
    public GlobalEnvService(EnvironmentSharedStateAccess sharedState, EnvironmentServiceProvider serviceProvider)
    {
        super(sharedState);
        this.serviceProvider = serviceProvider;
    }

    public void allocate(Demand Total, ArrayList<UUID> ChildrenList)
    {
        //logger.info("GlobalEnvService.allocate() called");
        getChildEnvService();
        double shortfall = Total.getDemandRequest() - Total.getGenerationRequest();
        //logger.info("Shortfall = " + shortfall);
        if (shortfall < 0)
        {
            //Go through the children, and allocating their requests
            curtailmentFactor = Total.getDemandRequest()/Total.getGenerationRequest();
            for (int i=0; i<ChildrenList.size(); i++)
            {
                UUID agent = ChildrenList.get(i);
                Demand request = ChildEnvService.getAgentDemand(agent);
                Demand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent);
                allocation.allocate(request.getDemandRequest(), request.getGenerationRequest());
                allocation.curtail(curtailmentFactor);
                //logger.info("Allocating: " + allocation.getAllocationD() + " " + allocation.getAllocationG());
                ChildEnvService.setGroupDemand(agent, allocation);
            }
        }
        else
        {
            if (allocationType == 1 ) {
                allocate_fairly(Total, ChildrenList);
            }
            else {
                allocate_proportionally(Total, ChildrenList);
            }
        }
    }

    private void allocate_proportionally(Demand Total, ArrayList<UUID> ChildrenList)
    {
        logger.info("GlobalEnvService allocating proportionally");
        double proportion = Total.getGenerationRequest()/Total.getDemandRequest();
        for (int i=0; i<ChildrenList.size(); i++)
        {
            UUID agent = ChildrenList.get(i);
            parentDemand request = (parentDemand)ChildEnvService.getAgentDemand(agent);
            parentDemand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent); //todo fix this
            allocation.allocate(request.getDemandRequest()*proportion, request.getGenerationRequest());
            ChildEnvService.setGroupDemand(agent, allocation);
            //storeAllocation(agent, allocation.getAllocationD());
        }
    }

    //This needs to be overridden because ChildEnvSerivce is different
    private void allocate_fairly(Demand Total, ArrayList<UUID> ChildrenList)
    {
        //logger.info("GlobalEnvService allocating fairly");

        double proportion_available = Total.getGenerationRequest()/Total.getDemandRequest();
        canon_of_equality(ChildrenList);

        int BordaSum = calcBordaSum(); //Get BordaPoint sum

        for (int i=0; i<ChildrenList.size(); i++)
        {
            UUID agent = ChildrenList.get(i);


            double proportion_borda = (double)AgentBordaPoints.get(agent)/(double)BordaSum;
            double proportion = proportion_available * proportion_borda;

            //logger.info("BordaSum: " +BordaSum + " For Agent: " + agent + " AgentBordaPoints:" + AgentBordaPoints.get(agent) + " Proportion: " + proportion);

            parentDemand request = (parentDemand)ChildEnvService.getAgentDemand(agent);
            parentDemand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent); //todo fix this

            allocation.allocate(Total.getGenerationRequest()*proportion_borda, request.getGenerationRequest());

            //logger.info("Allocating to Agent: " + agent + " D: " + allocation.getAllocationD() + " G: " + allocation.getAllocationG());

            ChildEnvService.setGroupDemand(agent, allocation);
            storeAllocation(agent, allocation.getAllocationD());
        }
        resetBordaPoints();
    }

    protected PowerPoolEnvService getChildEnvService()
    {
        if (ChildEnvService == null)
        {
            try
            {
                logger.info("Getting ChildService (PowerPoolEnvService) of GlobalEnvService");
                this.ChildEnvService = serviceProvider.getEnvironmentService(PowerPoolEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ChildService (PowerPoolEnvService)", e);
            }
        }
        return ChildEnvService;
    }

    //Currently not working:
    @Step
    public void step(int t) throws ActionHandlingException
    {
        logger.info("Incrementing State");
        round++;
        if (this.state.getState() == null)
        {
            logger.info("Starting simulation. Initialising state");
            this.state = new SimState();
        }
        else
        {
            if (round > parent_level)
                this.state.incrementState();
        }
        logger.info("State incremented. State is now: " + state.getState());
    }

    public state.State getState()
    {
        return state.getState();
    }

    protected void canon_of_equality( ArrayList<UUID> ChildrenList)
    {
        // TreeMap <UUID, Double> AvgAllocation = new TreeMap<UUID, Double>();
        HashMap <UUID, Double> AvgAllocation = new HashMap<UUID, Double>();

        for (UUID ID : ChildrenList) {   //Sort the AvgAllocation by size
            AvgAllocation.put(ID, calcAvgAllocation(ID));
        }

        AvgAllocation = sortByValue(AvgAllocation);

        //logger.info("ChildrenList: " + ChildrenList +" Sorted AvgAllocation List: " + AvgAllocation);
        sortBordaPoints(AvgAllocation, ChildrenList);

    }

    /**
     * Taken from http://www.programcreek.com/2013/03/java-sort-map-by-value/
     * @param unsortMap
     * @return
     */
    public static HashMap <UUID, Double>  sortByValue(HashMap <UUID, Double> unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        LinkedHashMap <UUID, Double> sortedMap = new LinkedHashMap <UUID, Double>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            HashMap.Entry entry = (Map.Entry) it.next();
            sortedMap.put((UUID)entry.getKey(), (Double)entry.getValue());
        }
        return sortedMap;
    }

    protected void storeAllocation(UUID id, double allocationD)
    {
        ArrayList<Double> list;
        if (this.AllocationHistory.containsKey(id))
        {
            list = this.AllocationHistory.get(id);
            list.add(allocationD);
        }
        else
        {
            list = new ArrayList<Double>();
            list.add(allocationD);
        }
        this.AllocationHistory.put(id, list);

    }

    protected double calcAvgAllocation(UUID id)
    {
        if (this.AllocationHistory.containsKey(id))
        {
            ArrayList<Double> list = this.AllocationHistory.get(id);
            double sum = 0;
            for (Double d : list)
                sum += d;
            return sum;
        }
        else
        {
            return 0;
        }
    }

    protected int calcBordaSum()
    {
        int sum = 0;
        for (int d : AgentBordaPoints.values())
            sum += d;
        return sum;
    }

    protected HashMap <UUID, Integer> sortBordaPoints(HashMap<UUID, Double> tMap, ArrayList<UUID> ChildrenList)
    {
        int BordaPt = 1;
        double prev_val = -1;       //-1 to eliminate issue when history is 0;

        int iterator = 0;
        ArrayList<Integer> iteratorStorage = new ArrayList<Integer>();
        ArrayList<Integer> BordaPtStorage = new ArrayList<Integer>();
        int BordaPtIncrement = 0;
        boolean RecalcBorda = false;
        //logger.info("Average allocation: " + tMap);
        for (Map.Entry<UUID, Double> entry : tMap.entrySet())
        {
            double value = entry.getValue();
            //logger.info("Prev Value: " + prev_val + " Current Val: " + value);

            if (value == prev_val)
            {
                //logger.info("if (value == prev_val), Prev Value: " + prev_val + " Current Val: " + value);
                BordaPtStorage.add(BordaPt);
                iteratorStorage.add(iterator-1);
                RecalcBorda = true;
                //if multiple elements within tMap has the same rank, set the last one to have the correct BordaPt
            }
            else{
                //logger.info("else, Prev Value: " + prev_val + " Current Val: " + value + " RecalcBorda =" + RecalcBorda);
                BordaPtStorage.add(BordaPt);
            }
            prev_val = value;
            BordaPt++;
            iterator++;
        }

        if (RecalcBorda == true) {
            iterator = BordaPtStorage.size() - 1;
            while (iterator >= 0) {
                if (iterator < BordaPtStorage.size()-1) {
                    BordaPtStorage.set(iterator, BordaPtStorage.get(iterator + 1));
                    //go back and replace the wrongly calculated Borda rank with the correct one
                }
                iterator--;
            }
        }

        iterator = 0;
        for (Map.Entry<UUID, Double> entry : tMap.entrySet()) //Rank Agents in increasing order of average allocation
        {
            UUID ID = entry.getKey();


            int sum = 0;
            if (AgentBordaPoints.containsKey(ID))
            {
                sum = AgentBordaPoints.get(ID);
            }
            sum += BordaPtStorage.get(iterator);

            //logger.info("Storing AgentBordaPoints: " + sum + " to Agent: " + ID);

            AgentBordaPoints.put(ID, sum);
            iterator++;
        }

        //logger.info("Input Map: " + tMap);
        //logger.info("Agent Point Allocation" + AgentBordaPoints);

        return AgentBordaPoints;

    }

    protected void resetBordaPoints()
    {
        AgentBordaPoints = new HashMap<UUID, Integer>();
    }
}