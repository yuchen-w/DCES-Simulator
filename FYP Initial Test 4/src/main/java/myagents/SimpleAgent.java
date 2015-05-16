package myagents;

import actions.Demand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

//import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
//import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
//import uk.ac.imperial.presage2.util.location.Location;
//import uk.ac.imperial.presage2.util.location.Move;
//import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class SimpleAgent extends AbstractParticipant 
{   
    Demand AgentDemand;
    
    public boolean alive;
    
    protected SimpleEnvService EnvService;

    @Inject
    @Named("params.size")
    public int size;

    SimpleAgent(UUID id, String name, double consumption, double allocation) 
    {
        super(id, name);
        this.AgentDemand = new Demand(consumption, allocation);
    }

    @Initialisor
    public void init() 
    {
        super.initialise();
    }
    
    @Inject
    public void setServiceProvider(EnvironmentServiceProvider serviceProvider) {
        try {
            this.EnvService = serviceProvider.getEnvironmentService(SimpleEnvService.class);
        } catch (UnavailableServiceException e) {
            logger.warn("unable to load SimpleEnvService class", e);
        }
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        logger.info("My consumption is: " 	+ this.AgentDemand.getDemand());
        logger.info("My generation is: " 	+ this.AgentDemand.getGeneration());
        
        try {
			environment.act(AgentDemand, getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn("Failed to add demand to the pool", e);
		}
        
        //logger.info("Total demand is now : " + this.EnvService.getTotalDemand());
        //logger.info("Total Generation Pool is now: " + this.EnvService.getTotalGeneration());
        
        
    }
    
     /*@Override
     protected Set<ParticipantSharedState> getSharedState() {
         Set<ParticipantSharedState> Power_Pool = super.getSharedState();
         Power_Pool.add(new ParticipantSharedState("Demand", demand.getQuantity(), getID()));
         return Power_Pool;
     }*/

}


