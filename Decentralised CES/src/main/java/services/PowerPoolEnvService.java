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

public class PowerPoolEnvService extends GlobalEnvService{

	@Inject
	@Named("params.children")
	protected int children;	//Variable here needs to be the same as it is called in the SimpleSim.java file

	protected int ChildrenNum = children;	//TODO: tidy this up


	protected HashMap<UUID, Integer>      RequestCounter = new HashMap<UUID, Integer>();
	protected HashMap<UUID, Demand> AgentDemandStorage = new HashMap<UUID, Demand>();
    protected HashMap<UUID, parentDemand> GroupDemandAllocationStorage = new HashMap<UUID, parentDemand>();
	protected HashMap<UUID, parentDemand> AgentAllocationStorage = new HashMap<UUID, parentDemand>();

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
        logger.info("Agent " + d.getAgentID() + "is adding to AgentDemandStorage");
        AgentDemandStorage.put(d.getAgentID(), d);
    }

    //Used by handlers to pass GroupDemand up to the next level
	public parentDemand getGroupDemand(parentDemand Parent)
	{
		parentDemand sum = new parentDemand(0, 0, Parent.getAgentID(), null);
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

    protected void setGroupDemand(UUID ParentID, parentDemand d)
    {
        logger.info("Allocating to ID: " + ParentID);
        GroupDemandAllocationStorage.put(ParentID, d);
    }

    public parentDemand getAllocation(UUID ParentID)
    {
        logger.info("getting allocation");
        if (GroupDemandAllocationStorage.containsKey(ParentID))
        {
            return GroupDemandAllocationStorage.get(ParentID);
        }
        else
        {
            logger.info("Error!");
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
    public void allocate(parentDemand allocated, ArrayList<UUID> ChildrenList) {
        logger.info("PowerPoolEnvService.allocate() called");
		logger.info("appropriating: D=" + allocated.getDemandRequest() + " G="+allocated.getGenerationRequest());
        getChildEnvService();
        Demand GroupDemand = getAgentDemand(allocated.getAgentID());   //also the same as ChildEnvService.getGroupDemand(allocated);
        double shortfall = GroupDemand.getDemandRequest() - allocated.getDemandRequest();

        if (shortfall <= 0) {
            //Go through the children, and allocating their requests
            for (int i = 0; i < ChildrenList.size(); i++) {
                UUID agent = ChildrenList.get(i);
                logger.info("shortfall= " + shortfall +" shortfall < 0; Appropriating: D=" + ChildEnvService.getAgentDemand(agent).getDemandRequest() + " G=" + ChildEnvService.getAgentDemand(agent).getGenerationRequest() + " to Agent: " + agent);
                ChildEnvService.setGroupDemand(agent, (parentDemand)ChildEnvService.getAgentDemand(agent));

            }
        } else {
            double proportion = allocated.getDemandRequest() / GroupDemand.getDemandRequest();
            for (int i = 0; i < ChildrenList.size(); i++) {
                UUID agent = ChildrenList.get(i);
                Demand request = ChildEnvService.getAgentDemand(agent);
                parentDemand allocation = new parentDemand(request.getDemandRequest() * proportion, request.getGenerationRequest(), agent);
                logger.info("shortfall > 0; proportion factor is:" + proportion + " Appropriating: D =" + allocation.getDemandRequest() + " G=" + allocation.getGenerationRequest() + " to Agent: " + agent);
                ChildEnvService.setGroupDemand(agent, allocation);

            }
        }
    }
}
