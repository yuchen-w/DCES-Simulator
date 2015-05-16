package myagents;

import actions.Demand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class SimpleAgent extends AbstractParticipant 
{
    private double Power_Consumption = 10;
    private double Power_Allocation = 0;
    private double Power_Generation = 5;
    private double Power_Storage = 0;
    
    Demand demand;
    
    public boolean alive;
    
    protected SimpleEnvService EnvService;

    @Inject
    @Named("params.size")
    public int size;

    SimpleAgent(UUID id, String name, double consumption, double allocation) 
    {
        super(id, name);
        this.Power_Allocation = allocation;
        this.Power_Consumption = consumption;
        this.Power_Storage = consumption - allocation;
    }

    @Initialisor
    public void init() 
    {
    	try
		{
			this.EnvService = this.getEnvironmentService(SimpleEnvService.class);
		}
		catch (UnavailableServiceException e)
		{
			logger.warn("Couldn't get environment service", e);
		}
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        logger.info("My consumption is: " + this.Power_Consumption);
        logger.info("My allocation is: " + this.Power_Allocation);
        logger.info("My storage is: " + this.Power_Storage);
        
        try {
			this.demand = new Demand(1);
			environment.act(demand, getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn("Failed to demand", e);
		}
        
        	
        logger.info("Total demand is now : " + this.EnvService.getTotalDemand());
        logger.info("This demand is now : " + this.demand.getQuantity());
        
    }

     @Override
     protected Set<ParticipantSharedState> getSharedState() {
         Set<ParticipantSharedState> Power_Pool = super.getSharedState();
         Power_Pool.add(new ParticipantSharedState("Demand", demand.getQuantity(), getID()));
         return Power_Pool;
     }

}


