//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.Demand;
import actions.parentDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

import state.Canon;
import state.SimState;
import uk.ac.imperial.presage2.core.environment.*;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalEnvService extends EnvironmentService{
    double curtailmentFactor = 1;
    private SimState state;
    private int round = 0;
    private PowerPoolEnvService ChildEnvService;
    final protected EnvironmentServiceProvider serviceProvider;

    HashMap<UUID, ArrayList<Double>> AllocationHistory = new HashMap<UUID, ArrayList<Double>>();
    HashMap<UUID, ArrayList<Double>> DemandHistory = new HashMap<UUID, ArrayList<Double>>();
    HashMap<UUID, ArrayList<Double>> GenerationHistory = new HashMap<UUID, ArrayList<Double>>();
    HashMap<UUID, ArrayList<Integer>> ProductivityHistory = new HashMap<UUID, ArrayList<Integer>>();
    HashMap<UUID, ArrayList<Integer>> SocialUtilityHistory = new HashMap<UUID, ArrayList<Integer>>();

    ConcurrentHashMap<UUID, Integer> CanonEqualityRank = new ConcurrentHashMap<UUID, Integer>();
    ConcurrentHashMap<UUID, Integer> CanonNeedsRank = new ConcurrentHashMap<UUID, Integer>();
    ConcurrentHashMap<UUID, Integer> CanonProductivityRank = new ConcurrentHashMap<UUID, Integer>();
    ConcurrentHashMap<UUID, Integer> CanonSocialUtilityRank = new ConcurrentHashMap<UUID, Integer>();
    ConcurrentHashMap<UUID, Integer> CanonSupplyAndDemandRank = new ConcurrentHashMap<UUID, Integer>();


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
        HashMap<UUID, Double> AgentBordaPoints_local = new  HashMap<UUID, Double>();
        Double BordaSum = 0.0;

        AgentBordaPoints_local = calculateAllCanons(ChildrenList, AgentBordaPoints_local, Total);

        synchronized (BordaSum) {
            BordaSum = calcBordaSum(AgentBordaPoints_local); //Get BordaPoint sum
        }

        for (int i=0; i<ChildrenList.size(); i++)
        {
            UUID agent = ChildrenList.get(i);

            double proportion_borda = (double)AgentBordaPoints_local.get(agent)/(double)BordaSum;

            //logger.info("BordaSum: " +BordaSum + " For Agent: " + agent + " AgentBordaPoints:" + AgentBordaPoints.get(agent) + " Proportion: " + proportion_borda);

            parentDemand request = (parentDemand)ChildEnvService.getAgentDemand(agent);
            parentDemand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent); //todo fix this
            allocation.setProductivity(request.getProductivity());
            allocation.setSocial_utility(request.getSocial_utility());
            allocation.setCanonEqualityWeight(request.getCanonEqualityWeight());
            allocation.setCanonNeedsWeight(request.getCanonNeedsWeight());
            allocation.setCanonProductivityWeight(request.getCanonProductivityWeight());
            allocation.setCanonSocialUtilityWeight(request.getCanonSocialUtilityWeight());
            allocation.setCanonSupplyAndDemandWeight(request.getCanonSupplyAndDemandWeight());
            allocation.setHour(request.getHour());


            logger.info("Agent: " + agent + " Productivity: " + request.getProductivity() + " Social Utility: " + request.getSocial_utility());


            allocation.allocate(Total.getGenerationRequest()*proportion_borda, request.getGenerationRequest());
            setAllRanks(allocation);

            //logger.info("AgentBordaPoints = " + AgentBordaPoints_local.get(agent) + "BordaSum: " + BordaSum  + "Proportion_borda = " + proportion_borda); //todo
            //logger.info("Allocating to Agent: " + agent + " D: " + allocation.getAllocationD() + " G: " + allocation.getAllocationG());



            ChildEnvService.setGroupDemand(agent, allocation);
            environmentStore(agent, allocation);
        }
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

    protected void calculateCanonWeight()
    {
        //todo
    }

    protected void setAllRanks(Demand allocation)
    {
        allocation.setCanonRank("CanonEqualityRank", CanonEqualityRank.get(allocation.getAgentID()));
        allocation.setCanonEqualityRank(CanonEqualityRank.get(allocation.getAgentID()));

        allocation.setCanonRank("CanonNeedsRank", CanonNeedsRank.get(allocation.getAgentID()));
        allocation.setCanonNeedsRank(CanonNeedsRank.get(allocation.getAgentID()));

        allocation.setCanonRank("CanonProductivityRank", CanonProductivityRank.get(allocation.getAgentID()));
        allocation.setCanonProductivityRank(CanonProductivityRank.get(allocation.getAgentID()));
        //logger.info("Agent: " + allocation.getAgentID() + "Productivity Rank: " + CanonProductivityRank.get(allocation.getAgentID()));

        allocation.setCanonRank("CanonSocialUtilityRank", CanonSocialUtilityRank.get(allocation.getAgentID()));
        allocation.setCanonSocialUtilityRank(CanonSocialUtilityRank.get(allocation.getAgentID()));

        allocation.setCanonRank("CanonSupplyAndDemandRank",CanonSupplyAndDemandRank.get(allocation.getAgentID()));
        allocation.setCanonSupplyAndDemandRank(CanonSupplyAndDemandRank.get(allocation.getAgentID()));
    }

    protected HashMap<UUID, Double> calculateAllCanons(ArrayList<UUID> ChildrenList, HashMap<UUID, Double> AgentBordaPoints, Demand Total)
    {
        AgentBordaPoints = canon_of_equality(ChildrenList, AgentBordaPoints, Total);
        AgentBordaPoints = canon_of_needs(ChildrenList, AgentBordaPoints, Total);
        AgentBordaPoints = canon_of_productivity(ChildrenList, AgentBordaPoints, Total);
        AgentBordaPoints = canon_of_social_utility(ChildrenList, AgentBordaPoints, Total);
        AgentBordaPoints = canon_of_supply_and_demand(ChildrenList, AgentBordaPoints, Total);
        return AgentBordaPoints;
    }

    protected HashMap<UUID, Double> canon_of_equality( ArrayList<UUID> ChildrenList, HashMap<UUID, Double> AgentBordaPoints, Demand Total)
    {
        // TreeMap <UUID, Double> AvgAllocation = new TreeMap<UUID, Double>();
        HashMap <UUID, Double> AvgDemand = new HashMap<UUID, Double>();

        for (UUID ID : ChildrenList) {   //Sort the AvgAllocation by size
            AvgDemand.put(ID, calcAvgDemand(ID));
        }

        AvgDemand = sortByValue(AvgDemand);

        //logger.info("ChildrenList: " + ChildrenList +" Sorted AvgAllocation List: " + AvgDemand);
        return sortBordaPointsReverse(AvgDemand, AgentBordaPoints, CanonEqualityRank, Total, Canon.equality);

    }

    protected HashMap<UUID, Double> canon_of_needs(ArrayList<UUID> ChildrenList, HashMap<UUID, Double> AgentBordaPoints, Demand Total)
    {
        //sort by average demands
        HashMap <UUID, Double> AvgAllocation = new HashMap<UUID, Double>();
        for (UUID ID : ChildrenList) {   //Sort the AvgAllocation by size
            AvgAllocation.put(ID, calcAvgAllocation(ID));
        }

        AvgAllocation = sortByValue(AvgAllocation);

        return sortBordaPointsReverse(AvgAllocation, AgentBordaPoints, CanonNeedsRank, Total, Canon.needs);
        //return
    }

    protected HashMap<UUID, Double> canon_of_supply_and_demand(ArrayList<UUID> ChildrenList, HashMap<UUID, Double> AgentBordaPoints, Demand Total)
    {
        //rank agents by decreasing order of average provision
        HashMap <UUID, Double> AvgGeneration = new HashMap<UUID, Double>();
        for (UUID ID : ChildrenList) {   //Sort the AvgAllocation by size
            AvgGeneration.put(ID, calcAvgGeneration(ID));
        }

        AvgGeneration = sortByValue(AvgGeneration);

        return sortBordaPoints(AvgGeneration, AgentBordaPoints, CanonSupplyAndDemandRank, Total, Canon.supply_and_demand);
    }


    protected HashMap<UUID, Double> canon_of_social_utility(ArrayList<UUID> ChildrenList, HashMap<UUID, Double> AgentBordaPoints, Demand Total)
    {
        HashMap <UUID, Double> SocialUtilityHistory = new HashMap<UUID, Double>();
        for (UUID ID : ChildrenList) {   //Sort the AvgAllocation by size
            SocialUtilityHistory.put(ID, calcAvgSocialUtility(ID));//getEconOutput(agent)
        }

        SocialUtilityHistory = sortByValue(SocialUtilityHistory);

        return sortBordaPoints(SocialUtilityHistory, AgentBordaPoints, CanonSocialUtilityRank, Total, Canon.social_utility);
    }


    protected HashMap<UUID, Double> canon_of_productivity(ArrayList<UUID> ChildrenList, HashMap<UUID, Double> AgentBordaPoints, Demand Total)
    {
        //consider replacing with canon of productivity
        HashMap <UUID, Double> EconOutput = new HashMap<UUID, Double>();
        for (UUID ID : ChildrenList) {   //Sort the AvgAllocation by size
            EconOutput.put(ID, calcAvgEconOutput(ID));//getEconOutput(agent)
        }

        EconOutput = sortByValue(EconOutput);

        return sortBordaPoints(EconOutput, AgentBordaPoints, CanonProductivityRank, Total, Canon.productivity);

    }

    /**
     * Based on some code from http://www.programcreek.com/2013/03/java-sort-map-by-value/
     * @param unsortMap
     * @return
     */
    public static HashMap <UUID, Double> sortByValue(HashMap <UUID, Double> unsortMap) {
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

    protected void environmentStore(UUID agent, Demand allocation)
    {
        storeAllocation(agent, allocation.getAllocationD());
        storeDemand(agent, allocation.getDemandRequest());
        storeGenerataion(agent, allocation.getGenerationRequest());
        storeEconOutput(agent, allocation.getProductivity());
        storeSocialUtility(agent, allocation.getSocial_utility());
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

    protected void storeDemand(UUID id, double Demand)
    {
        ArrayList<Double> list;
        if (this.DemandHistory.containsKey(id))
        {
            list = this.DemandHistory.get(id);
            list.add(Demand);
        }
        else
        {
            list = new ArrayList<Double>();
            list.add(Demand);
        }
        this.DemandHistory.put(id, list);
    }

    protected void storeGenerataion(UUID id, double Demand)
    {
        ArrayList<Double> list;
        if (this.GenerationHistory.containsKey(id))
        {
            list = this.GenerationHistory.get(id);
            list.add(Demand);
        }
        else
        {
            list = new ArrayList<Double>();
            list.add(Demand);
        }
        this.GenerationHistory.put(id, list);
    }

    protected void storeEconOutput(UUID id, int Productivity)
    {
        ArrayList<Integer> list;
        if (this.ProductivityHistory.containsKey(id))
        {
            list = this.ProductivityHistory.get(id);
            list.add(Productivity);
        }
        else
        {
            list = new ArrayList<Integer>();
            list.add(Productivity);
        }
        this.ProductivityHistory.put(id, list);
    }

    protected void storeSocialUtility(UUID id, int Utility)
    {
        ArrayList<Integer> list;
        if (this.SocialUtilityHistory.containsKey(id))
        {
            list = this.SocialUtilityHistory.get(id);
            list.add(Utility);
        }
        else
        {
            list = new ArrayList<Integer>();
            list.add(Utility);
        }
        this.SocialUtilityHistory.put(id, list);
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

    protected double calcAvgDemand(UUID id)
    {
        if (this.DemandHistory.containsKey(id))
        {
            ArrayList<Double> list = this.DemandHistory.get(id);
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

    protected double calcAvgGeneration(UUID id)
    {
        if (this.GenerationHistory.containsKey(id))
        {
            ArrayList<Double> list = this.GenerationHistory.get(id);
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

    protected double calcAvgEconOutput(UUID id)
    {
        if (this.ProductivityHistory.containsKey(id))
        {
            ArrayList<Integer> list = this.ProductivityHistory.get(id);
            double sum = 0;
            for (Integer i : list)
                sum += i;
            return sum;
        }
        else
        {
            return 0;
        }
    }

    protected double calcAvgSocialUtility(UUID id)
    {
        if (this.SocialUtilityHistory.containsKey(id))
        {
            ArrayList<Integer> list = this.SocialUtilityHistory.get(id);
            double sum = 0;
            for (Integer i : list)
                sum += i;
            return sum;
        }
        else
        {
            return 0;
        }
    }

    protected double calcBordaSum(HashMap <UUID, Double> AgentBordaPoints)
    {
        double sum = 0;
        for (double d : AgentBordaPoints.values())
            sum += d;
        return sum;
    }

    protected HashMap <UUID, Double> sortBordaPoints(HashMap<UUID, Double> tMap, HashMap <UUID, Double> AgentBordaPoints, ConcurrentHashMap<UUID, Integer> BordaRank, Demand Total, Canon CanonName)
    {
        int BordaPt = 1;
        double prev_val = -1;       //-1 to eliminate issue when history is 0;

        int iterator = 0;
        double CanonBordaSum = 0;
        if (CanonName == Canon.equality)
        {
            CanonBordaSum = Total.getCanonEqualityWeight();
        }
        else if (CanonName == Canon.needs)
        {
            CanonBordaSum = Total.getCanonNeedsWeight();
        }
        else if (CanonName == Canon.productivity)
        {
            CanonBordaSum = Total.getCanonProductivityWeight();
        }
        else if (CanonName == Canon.social_utility)
        {
            CanonBordaSum = Total.getCanonSocialUtilityWeight();
        }
        else if (CanonName == Canon.supply_and_demand)
        {
            CanonBordaSum = Total.getCanonSupplyAndDemandWeight();
        }

        double BordaProportion = CanonBordaSum/(double)Total.getTotalCanonWeight();



        ArrayList<Integer> iteratorStorage = new ArrayList<Integer>();
        ArrayList<Double> BordaPtStorage = new ArrayList<Double>();

        boolean RecalcBorda = false;
        //logger.info("Average allocation: " + tMap);
        for (Map.Entry<UUID, Double> entry : tMap.entrySet())
        {
            double value = entry.getValue();
            //logger.info("Prev Value: " + prev_val + " Current Val: " + value);

            if (value == prev_val)
            {
                //logger.info("if (value == prev_val), Prev Value: " + prev_val + " Current Val: " + value);
                //BordaPt++;
                //BordaPtStorage.add(BordaPt);    //todo: simplify. same as the else statement
                iteratorStorage.add(iterator-1);
                RecalcBorda = true;
                //if multiple elements within tMap has the same rank, set the last one to have the correct BordaPt
            }
            //logger.info("else, Prev Value: " + prev_val + " Current Val: " + value + " RecalcBorda =" + RecalcBorda);
            BordaPtStorage.add((double)BordaPt*BordaProportion);    //todo: simplify

            prev_val = value;

            BordaRank.put(entry.getKey(), tMap.size() - iterator);

            BordaPt++;
            iterator++;
        }

        if (RecalcBorda == true) {
            iterator = iteratorStorage.size() - 1;
            while (iterator >= 0) {
                if (iterator < BordaPtStorage.size()-1) {
                    BordaPtStorage.set(iteratorStorage.get(iterator), BordaPtStorage.get(iteratorStorage.get(iterator) + 1));
                    //go back and replace the wrongly calculated Borda rank with the correct one
                    //todo: do the same for rank here
                }
                iterator--;
            }
        }

        iterator = 0;
        for (Map.Entry<UUID, Double> entry : tMap.entrySet())
        {
            UUID ID = entry.getKey();

            double sum = 0;
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

    //Takes a sorted list (small -> big) and allocates Borda points from big to small
    protected HashMap <UUID, Double> sortBordaPointsReverse(HashMap<UUID, Double> tMap, HashMap <UUID, Double> AgentBordaPoints, ConcurrentHashMap<UUID, Integer> BordaRank, Demand Total, Canon CanonName)
    {
        int BordaPt = tMap.size();
        double prev_val = -1;       //-1 to eliminate issue when history is 0;

        int iterator = 0;

        double CanonBordaSum = 0;
        if (CanonName == Canon.equality)
        {
            CanonBordaSum = Total.getCanonEqualityWeight();
        }
        else if (CanonName == Canon.needs)
        {
            CanonBordaSum = Total.getCanonNeedsWeight();
        }
        else if (CanonName == Canon.productivity)
        {
            CanonBordaSum = Total.getCanonProductivityWeight();
        }
        else if (CanonName == Canon.social_utility)
        {
            CanonBordaSum = Total.getCanonSocialUtilityWeight();
        }
        else if (CanonName == Canon.supply_and_demand)
        {
            CanonBordaSum = Total.getCanonSupplyAndDemandWeight();
        }

        double BordaProportion = CanonBordaSum/(double)Total.getTotalCanonWeight();

        //logger.info("BordaProportion: " + BordaProportion);

        ArrayList<Integer> iteratorStorage = new ArrayList<Integer>();
        ArrayList<Double> BordaPtStorage = new ArrayList<Double>();
        boolean RecalcBorda = false;
        //logger.info("Average allocation: " + tMap);
        for (Map.Entry<UUID, Double> entry : tMap.entrySet())
        {
            double value = entry.getValue();
            //logger.info("Prev Value: " + prev_val + " Current Val: " + value);

            if (value == prev_val)
            {
                //logger.info("if (value == prev_val), Prev Value: " + prev_val + " Current Val: " + value);
                //BordaPt--;
                //BordaPtStorage.add(BordaPt);    //todo: simplify. same as the else statement
                iteratorStorage.add(iterator-1);
                RecalcBorda = true;
                //if multiple elements within tMap has the same rank, set the last one to have the correct BordaPt
            }
            //logger.info("else, Prev Value: " + prev_val + " Current Val: " + value + " RecalcBorda =" + RecalcBorda);
            BordaPtStorage.add(BordaProportion*(double)BordaPt);

            prev_val = value;

            BordaPt--;
            iterator++;
            BordaRank.put(entry.getKey(), iterator);

        }

        if (RecalcBorda == true) {
            iterator = iteratorStorage.size() - 1;
            while (iterator >= 0) {
                if (iterator < BordaPtStorage.size()-1) {
                    BordaPtStorage.set(iteratorStorage.get(iterator), BordaPtStorage.get(iteratorStorage.get(iterator) + 1));
                    //go back and replace the wrongly calculated Borda rank with the correct one
                }
                iterator--;
            }
        }

        iterator = 0;
        for (Map.Entry<UUID, Double> entry : tMap.entrySet())
        {
            UUID ID = entry.getKey();
            double sum = 0;
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
}