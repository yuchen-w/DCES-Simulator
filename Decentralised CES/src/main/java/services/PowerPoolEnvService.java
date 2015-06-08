//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.Demand;
import actions.MasterAction;
import actions.parentDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

import state.SimState;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PowerPoolEnvService extends GlobalEnvService{



    @Inject
	@Named("params.children")
	protected int children;	//Variable here needs to be the same as it is called in the SimpleSim.java file

	protected int ChildrenNum = children;	//TODO: tidy this up


	protected HashMap<UUID, Integer>      RequestCounter = new HashMap<UUID, Integer>();
	protected ConcurrentHashMap<UUID, Demand> AgentDemandStorage = new ConcurrentHashMap<UUID, Demand>();
    protected HashMap<UUID, Demand> GroupDemandAllocationStorage = new HashMap<UUID, Demand>();
	protected HashMap<UUID, parentDemand> AgentAllocationStorage = new HashMap<UUID, parentDemand>();
    protected HashMap<UUID,ArrayList<Double>> AgentSatisfaction = new HashMap <UUID,ArrayList<Double>>();

	protected SimState state = new SimState();
	double totalDemand = 0;
	double totalGeneration = 0;
	double available = 0;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    private ParentEnvService ChildEnvService;

	@Inject
	public PowerPoolEnvService(EnvironmentSharedStateAccess sharedState, EnvironmentServiceProvider serviceProvider)
	{
		super(sharedState, serviceProvider);
		//sharedState.create("group.demand", )
	}

	public void addtoDemand(double d)
	{
		this.totalDemand = this.totalDemand + d;
	}
	
	public void addtoGeneration(double d)
	{
		this.totalDemand = this.totalDemand + d;
	}
	
	public void addtoPool (parentDemand d)
	{
		this.totalDemand = this.totalDemand + d.getDemandRequest();
		this.totalGeneration = this.totalGeneration + d.getGenerationRequest();
		//this.available = this.available + d.getGenerationRequest();
	}

	public double getTotalDemand()
	{
		return this.totalDemand;
	}

	public double getTotalGeneration()
	{
		return this.totalGeneration;
	}

	public double getAvailable()
	{
		return this.available;
	}

	public void incrementRequestCounter(UUID ParentID)
	{
		if (RequestCounter.containsKey(ParentID) == false)
		{
			RequestCounter.put(ParentID, 1);
		}
		else
		{
			RequestCounter.put(ParentID,RequestCounter.get(ParentID)+1);
			if (RequestCounter.get(ParentID) >= ChildrenNum)
			{
				RequestCounter.put(ParentID, 0);    //Reset to zero if exceeds bigger than No. of children
			}
		}
	}



    public void addToAgentPool (Demand d)
    {
        //logger.info("Agent " + d.getAgentID() + " is adding to AgentDemandStorage");
        synchronized (AgentDemandStorage) {
            AgentDemandStorage.put(d.getAgentID(), d);
        }
        //logger.info("AgentDemandStorage, Agent: " + d.getAgentID() + " Canon total weight: " + AgentDemandStorage.get(d.getAgentID()).getTotalCanonWeight());
    }

    //Used by handlers to pass GroupDemand up to the next level
	public parentDemand getGroupDemand(parentDemand Parent)
	{
		parentDemand sum = new parentDemand(0, 0, Parent.getAgentID(), null);
        logger.info("AgentDemandStorage: " + AgentDemandStorage);
        for (int i=0; i<Parent.getChildrenList().size(); i++)
		{
            sum.addDemand(AgentDemandStorage.get(Parent.getChildrenList().get(i)));
		}
		return sum;
	}

    //Used by handlers to pass GroupDemand up to the next level
	public parentDemand getGroupDemand(MasterAction action)
	{
		parentDemand sum = new parentDemand(0, 0, action.getAgentID(), null);

		for (int i=0; i<action.getChildrenList().size(); i++)
		{
			sum.addDemand(AgentDemandStorage.get(action.getChildrenList().get(i)));
		}
		return sum;
	}

    protected Demand getAgentDemand (UUID ParentID)
    {
        if (AgentDemandStorage.containsKey(ParentID))
        {
            return AgentDemandStorage.get(ParentID);
        }
        else
        {
            parentDemand nullDemand = new parentDemand(0, 0, ParentID);
            return nullDemand;
        }
    }

    protected void setGroupDemand(UUID ParentID, Demand d)
    {
        logger.info("Allocating to ID: " + ParentID);
        GroupDemandAllocationStorage.put(ParentID, d);
    }

    public Demand getAllocation(UUID ParentID)
    {
        logger.info("getting allocation");
        if (GroupDemandAllocationStorage.containsKey(ParentID))
        {
            return GroupDemandAllocationStorage.get(ParentID);
        }
        else
        {
            logger.info("PowerPoolEnvService.getAllocation error! No allocation can be found for Agent");
            parentDemand nullDemand = new parentDemand(0, 0, ParentID);
            return nullDemand;
        }
    }

    @Override
    protected ParentEnvService getChildEnvService()
    {
        if (ChildEnvService == null)
        {
            try
            {
                logger.info("Getting ChildEnvService (ParentEnvService) of PowerPoolEnvService");
                this.ChildEnvService = serviceProvider.getEnvironmentService(ParentEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ChildEnvService (ParentEnvService)", e);
            }
        }
        return ChildEnvService;
    }

    @Override
    public void allocate(Demand allocated, ArrayList<UUID> ChildrenList) {
        logger.info("PowerPoolEnvService.allocate() called");
		logger.info("Allocating: D=" + allocated.getAllocationD() + " G=" + allocated.getAllocationG());
        getChildEnvService();
        Demand GroupDemand = getAgentDemand(allocated.getAgentID());   //also the same as ChildEnvService.getGroupDemand(allocated);
        double shortfall = GroupDemand.getDemandRequest() - allocated.getAllocationD();

        //No special algorithm if Gen > Demand:
        if (shortfall <= 0) {
            //Go through the children, and allocating their requests
            for (int i = 0; i < ChildrenList.size(); i++) {
                UUID agent = ChildrenList.get(i);
                logger.info("shortfall= " + shortfall +" shortfall < 0; Appropriating: D=" + allocated.getAllocationD() + " G=" + allocated.getAllocationG() + " to Agent: " + agent);

                Demand request = ChildEnvService.getAgentDemand(agent);
                parentDemand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent);
                allocation.allocate(request.getDemandRequest(), request.getGenerationRequest());

                curtailmentFactor = allocated.getCurtailmentFactor();
                allocation.curtail(curtailmentFactor);

                logger.info("Curtailment factor = " + curtailmentFactor);
                logger.info("Allocation post curtailment, D,G = " + allocation.getAllocationD() +"  "+allocation.getAllocationG());
                ChildEnvService.setGroupDemand(agent, allocation);
            }
        } else {
            if (allocationType == 1 ) {
                logger.info("PowerPoolEnvService Allocating Fairly");
                allocate_fairly(allocated, GroupDemand,  ChildrenList);
            }
            else {
                allocate_proportionally(allocated, GroupDemand, ChildrenList);
            }

        }
    }

    private void allocate_proportionally(Demand allocated, Demand GroupDemand, ArrayList<UUID> ChildrenList)
    {
        double proportion = allocated.getAllocationD() / GroupDemand.getDemandRequest();
        for (int i = 0; i < ChildrenList.size(); i++) {
            UUID agent = ChildrenList.get(i);
            Demand request = ChildEnvService.getAgentDemand(agent);
            parentDemand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent);
            allocation.allocate(request.getDemandRequest()*proportion, request.getGenerationRequest());
            logger.info("shortfall > 0; proportion factor is:" + proportion + " Appropriating: D =" + allocation.getAllocationD() + " G=" + allocation.getAllocationG() + " to Agent: " + agent);
            ChildEnvService.setGroupDemand(agent, allocation);
        }
    }

    //This needs to be overridden because ChildEnvSerivce refers to different services
    private void allocate_fairly(Demand allocated, Demand GroupDemand, ArrayList<UUID> ChildrenList)
    {
        logger.info("GlobalEnvService allocating fairly");

        HashMap<UUID, Double> AgentBordaPoints = new HashMap<UUID, Double>();
        logger.info(" AgentBordaPoints = calculateAllCanons(ChildrenList, AgentBordaPoints, allocated); ChildrenList: " + ChildrenList);
        AgentBordaPoints = calculateAllCanons(ChildrenList, AgentBordaPoints, allocated);

        for (int i=0; i<ChildrenList.size(); i++)
        {
            UUID agent = ChildrenList.get(i);

            //logger.info("For Agent: " + agent + "AgentBordaPoints" + AgentBordaPoints);

            ConcurrentHashMap<UUID, Double> AgentBordaPoints_l_CC = new ConcurrentHashMap<UUID, Double>();

            for (UUID ID : AgentBordaPoints.keySet()) {
                AgentBordaPoints_l_CC.put(ID, AgentBordaPoints.get(ID));
            }

            double BordaSum = calcBordaSum(ChildrenList, AgentBordaPoints_l_CC); //Get BordaPoint sum
            double BordaPts = (double)AgentBordaPoints.get(agent);
            double proportion_Borda = BordaPts/BordaSum;

            logger.info("BordaSum: " +BordaSum + " For Agent: " + agent + " AgentBordaPoints:" + AgentBordaPoints.get(agent) + " Proportion: " + proportion_Borda); //debug todo tag

            Demand request = ChildEnvService.getAgentDemand(agent);
            parentDemand allocation = new parentDemand(request.getDemandRequest(), request.getGenerationRequest(), agent); //todo fix this
            allocation.setProductivity(request.getProductivity());
            allocation.setSocial_utility(request.getSocial_utility());
            allocation.setHour(request.getHour());
            setAllRanks(allocation);

            allocation.allocate(allocated.getAllocationD()*proportion_Borda, request.getGenerationRequest());


            logger.info("Allocating to Agent: " + agent + " D: " + allocation.getAllocationD() + " G: " + allocation.getAllocationG());

            ChildEnvService.setGroupDemand(agent, allocation);
            environmentStore(agent, allocation);
        }
    }

    protected double calcBordaSum(ArrayList<UUID> ChildrenList, ConcurrentHashMap<UUID, Double> AgentBordaPoints)
    {
        double sum = 0;
        for (UUID ID : AgentBordaPoints.keySet())
        {

            if (ChildrenList.contains(ID))
            {
                sum += AgentBordaPoints.get(ID);
            }
        }
        return sum;
    }

    public void Feedback(UUID id, double satisfaction)
    {
        ArrayList<Double> list;
        if (this.AgentSatisfaction.containsKey(id))
        {
            list = this.AgentSatisfaction.get(id);
            list.add(satisfaction);
        }
        else
        {
            list = new ArrayList<Double>();
            list.add(satisfaction);
        }
        this.AgentSatisfaction.put(id, list);
    }

    public ArrayList<Double> getSatisfaction(UUID id)
    {
        return this.AgentSatisfaction.get(id);
    }


}
