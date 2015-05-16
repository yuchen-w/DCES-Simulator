package myagents;

import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class TempPoolAgent extends AbstractParticipant {

	public TempPoolAgent(UUID id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}
	
	private double Power_Pool = 0;
	//private double Generation_Pool = 0;
	private double Total_Demand = 0;
	
	
	public void addtoPool (double amount)
	{
		this.Power_Pool = this.Power_Pool + amount;
	}
	
	public double getPoolSize()
	{
		return this.Power_Pool;
	}
	
	public void addtoDemand (double amount)
	{
		this.Total_Demand = this.Total_Demand + amount;
	}
	
	public double getTotalDemand()
	{
		return this.Total_Demand;
	}
	
	@Step
	public void step(int t) throws ActionHandlingException
	{
		//logger.info("Total demand is: " + this.getTotalDemand());
		//logger.info("Total pool is: " + this.getPoolSize());
	}
}
