package CES_Agents;

import actions.Demand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

//import java.util.Set;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.*;
//import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
//import uk.ac.imperial.presage2.util.location.Location;
//import uk.ac.imperial.presage2.util.location.Move;
//import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;



//useless?:


public class ProsumerAgent extends AbstractParticipant
{
    Demand AgentDemand;

    public boolean alive;

    protected PowerPoolEnvService EnvService;

    //Modify this method to check which environment agent it's being registered to
    public <T extends EnvironmentService> T getEnvironmentService_yw4311(Class<T> type) throws UnavailableServiceException {
        Iterator j$ = this.services.iterator();

        EnvironmentService s;
        do {
            if(!j$.hasNext()) {
                throw new UnavailableServiceException(type);
            }

            s = (EnvironmentService)j$.next();
        } while(s.getClass() != type);

        return (T)s;
    }

    @Inject
    @Named("params.size")
    public int size;

    public ProsumerAgent(UUID id, String name, double consumption, double allocation)
    {
        super(id, name);
        this.AgentDemand = new Demand(consumption, allocation);
    }

    ProsumerAgent(UUID id, String name, double consumption, double allocation, String behaviour)
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
        try{
            logger.info("does this even run?");
            this.EnvService = serviceProvider.getEnvironmentService(PowerPoolEnvService.class);
            //this.EnvService = this.getEnvironmentService_yw4311(PowerPoolEnvService.class);   //Trying out my method
        } catch (UnavailableServiceException e) {
            logger.warn("unable to load PowerPoolEnvService class", e);
        }
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        logger.info("My required Demand is: " 	+ this.AgentDemand.getDemand());
        logger.info("My generation is: " 	+ this.AgentDemand.getGeneration());
        logger.info("My allocation is: "+ this.AgentDemand.getAllocation());

        try
        {
			environment.act(AgentDemand, getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn("Failed to add demand to the pool", e);
		}

        Set<ParticipantSharedState> ss = this.getSharedState();
        //TODO
        //get from shared state Demand

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


