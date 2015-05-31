//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.childDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import actions.Demand;

import com.google.inject.Inject;

import state.SimState;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import java.util.HashMap;
import java.util.UUID;

public class PowerPoolEnvService extends GlobalEnvService{

	@Inject
	@Named("params.children")
	protected int children;	//Variable here needs to be the same as it is called in the SimpleSim.java file

	protected int ChildrenNum = children;	//TODO: tidy this up


	protected HashMap<UUID, Integer> RequestCounter = new HashMap<UUID, Integer>();
	protected HashMap<UUID, Demand>	AgentDemandStorage = new HashMap<UUID, Demand>();
	protected SimState state = new SimState();
	double totalDemand = 0;
	double totalGeneration = 0;
	double available = 0;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Inject
	public PowerPoolEnvService(EnvironmentSharedStateAccess sharedState)
	{
		super(sharedState);
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
	
	public void takefromPool (Demand d)
	{
		double shortfall = d.getDemand() - d.getGeneration();
		double allocation;
		if (shortfall < 0)
		{
			allocation = d.getDemand();
			this.available -= shortfall;
		}
		else if (shortfall >= 0 && shortfall <= this.available)
		{
			allocation = d.getDemand();
			this.available -= shortfall;
		}
		else
		{
			allocation = d.getGeneration() + this.available;
			this.available = 0;
		}
		logger.info("Allocating: " + allocation);
		
		d.Allocate(allocation);
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

	public Demand getGroupDemand(Demand ParentID)
	{
		Demand sum = new Demand(0, 0, ParentID.getAgentID());
		for (int i=0; i<ParentID.getChildrenList().size(); i++)
		{
			sum.addDemand(AgentDemandStorage.get(ParentID.getChildrenList().get(i)));
		}
		return sum;
	}
}
