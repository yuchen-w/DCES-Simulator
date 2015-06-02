//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.MasterAction;
import actions.childDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import actions.Demand;

import com.google.inject.Inject;

import state.SimState;
import sun.management.Agent;
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


	protected HashMap<UUID, Integer> RequestCounter = new HashMap<UUID, Integer>();
	protected HashMap<UUID, Demand>	AgentDemandStorage = new HashMap<UUID, Demand>();
    protected HashMap<UUID, Demand> GroupDemandAllocationStorage = new HashMap<UUID, Demand>();
	protected HashMap<UUID, Demand> AgentAllocationStorage = new HashMap<UUID, Demand>();

	protected SimState state = new SimState();
	double totalDemand = 0;
	double totalGeneration = 0;
	double available = 0;
	
	private final Logger logger = Logger.getLogger(this.getClass());

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
	
	public void addtoPool (Demand d)
	{
		this.totalDemand = this.totalDemand + d.getDemand();
		this.totalGeneration = this.totalGeneration + d.getGeneration();
		//this.available = this.available + d.getGeneration();
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

	public void addToAgentPool (Demand d)
	{
		logger.info("Agent " + d.getAgentID() + "is adding to AgentDemandStorage");
		AgentDemandStorage.put(d.getAgentID(), d);
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

    //Used by handlers to pass GroupDemand up to the next level
	public Demand getGroupDemand(Demand Parent)
	{
		Demand sum = new Demand(0, 0, Parent.getAgentID(), null);
		for (int i=0; i<Parent.getChildrenList().size(); i++)
		{
			sum.addDemand(AgentDemandStorage.get(Parent.getChildrenList().get(i)));
		}
		return sum;
	}

    //Used by handlers to pass GroupDemand up to the next level
	public Demand getGroupDemand(MasterAction action)
	{
		Demand sum = new Demand(0, 0, action.getAgentID(), null);
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
            Demand nullDemand = new Demand(0, 0, ParentID);
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
            logger.info("Error!");
            Demand nullDemand = new Demand(0, 0, ParentID);
            return nullDemand;
        }
    }

    @Override
    protected PowerPoolEnvService getChildEnvService()
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
    public void appropriate (Demand Total,  ArrayList<UUID> ChildrenList) {
        logger.info("GlobalEnvService.appropriate() called");
		logger.info("appropriating: D=" + Total.getDemand() + " G="+Total.getGeneration());
        getChildEnvService();
        double shortfall = Total.getDemand() - Total.getGeneration();

        if (shortfall <= 0) {
            //Go through the children, and allocating their requests
            for (int i = 0; i < ChildrenList.size(); i++) {
                UUID agent = ChildrenList.get(i);
                logger.info("shortfall < 0; Appropriating: D=" + ChildEnvService.getAgentDemand(agent).getDemand() + " G=" + ChildEnvService.getAgentDemand(agent).getGeneration() + " to Agent: " + agent);
                ChildEnvService.setGroupDemand(agent, ChildEnvService.getAgentDemand(agent));

            }
        } else {
            double proportion = Total.getGeneration() / Total.getDemand();
            for (int i = 0; i < ChildrenList.size(); i++) {
                UUID agent = ChildrenList.get(i);
                Demand request = ChildEnvService.getAgentDemand(agent);
                Demand allocation = new Demand(request.getDemand() * proportion, request.getGeneration(), agent);
                logger.info("shortfall > 0; proportion factor is:" + proportion + "Appropriating: D =" + allocation.getDemand() + " G=" + allocation.getGeneration() + " to Agent: " + agent);
                ChildEnvService.setGroupDemand(agent, allocation);

            }
        }
    }
}
