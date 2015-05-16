//package myagents;
//
//import actions.Demand;
//
//import com.google.inject.Inject;
//import com.google.inject.name.Named;
//
//import java.util.Set;
//import java.util.UUID;
//
//import org.apache.log4j.Logger;
//
//import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
//import uk.ac.imperial.presage2.core.environment.EnvironmentService;
//import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
//import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
//import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
//import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
//import uk.ac.imperial.presage2.core.event.EventBus;
//import uk.ac.imperial.presage2.core.simulator.Initialisor;
//import uk.ac.imperial.presage2.core.simulator.Step;
//import uk.ac.imperial.presage2.util.location.Location;
//import uk.ac.imperial.presage2.util.location.Move;
////import uk.ac.imperial.presage2.util.participant.AbstractParticipant;
//
////******See:****************
////https://github.com/sammacbeth/LPG--Game/blob/master/src/main/java/uk/ac/imperial/lpgdash/LPGService.java
////**************************
//public class PoolAgentService extends EnvironmentService 
//{
//	//private final Logger logger = Logger.getLogger(this.getClass());
//	
//
//	EnvironmentServiceProvider serviceProvider;
//	private double Power_Pool = 0;
//	//private double Generation_Pool = 0;
//	private double Total_Demand = 0;
//	
//	@Inject
//	protected PoolAgentService(EnvironmentSharedStateAccess sharedState,
//			EnvironmentServiceProvider serviceProvider) {
//		super(sharedState);
//		this.serviceProvider = serviceProvider;
//	}
//	
//	@Initialisor
//	public void init()
//	{
//		
//	}
//	
//	//Adds generation individually:
//	public void addtoGeneration (double amount)
//	{
//		this.Power_Pool = this.Power_Pool + amount;
//	}
//	
//	//Adds demand individually:
//	public void addtoDemand (double amount)
//	{
//		this.Total_Demand = this.Total_Demand + amount;
//	}
//	
//	//Adds demand and generation together
//	public void addtoPool (Demand d)
//	{
//		this.Total_Demand = this.Total_Demand + d.getDemand();
//		this.Power_Pool = this.Power_Pool + d.getGeneration();
//	}
//	
//	//Gets the total available power
//	public double getTotalGeneration()
//	{
//		return this.Power_Pool;
//	}
//	
//	//Gets total area demand
//	public double getTotalDemand()
//	{
//		return this.Total_Demand;
//	}
//	
//	@Step
//	public void step(int t) throws ActionHandlingException
//	{
//		//logger.info("Total demand is: " + this.getTotalDemand());
//		//logger.info("Total pool is: " + this.getPoolSize());
//	}
//}
