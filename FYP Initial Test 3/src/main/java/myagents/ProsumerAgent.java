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
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class ProsumerAgent extends AbstractParticipant 
{

    public State<Location> myLocation;
    private double Power_Consumption = 10;
    private double Power_Allocation = 0;
    private double Power_Generation = 5;
    private double Power_Storage = 0;
    
    public boolean alive;
    
    protected PoolAgentService game;

    @Inject
    @Named("params.size")
    public int size;

    ProsumerAgent(UUID id, String name, double consumption, double allocation) 
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
			this.game = this.getEnvironmentService(PoolAgentService.class);
		}
		catch (UnavailableServiceException e)
		{
			logger.warn("Couldn't get environment service", e);
		}
    }

 
    public double getConsumption() {
        try {
            return Power_Consumption;
        } catch (Exception e) {
            return 99999999.99;
        }
    }
    
    public double getAllocation() {
        try {
            return Power_Allocation;
        } catch (Exception e) {
            return 88888888.99;
        }
    }
    
    public double getStorage() {
        try {
            return Power_Storage;
        } catch (Exception e) {
            return 77777777.99;
        }
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        logger.info("My consumption is: " + this.getConsumption());
        logger.info("My allocation is: " + this.getAllocation());
        logger.info("My storage is: " + this.getStorage());
        //this.Power_Allocation = this.Power_Allocation+1;
        //this.Power_Consumption = this.Power_Consumption+1;
        
        try {
			//environment.act(new Demand(1), getID(), authkey);		//game.addtoDemand(1);
			act(new Demand(1));
		} catch (ActionHandlingException e) {
			logger.warn("Failed to demand", e);
		}
        
        	
        logger.info("Total demand is now : " + game.getTotalDemand());
        
    }

     @Override
     protected Set<ParticipantSharedState> getSharedState() {
         Set<ParticipantSharedState> Power_Pool = super.getSharedState();
         //Power_Pool.add(new ParticipantSharedState("alive", alive, getID()));
         return Power_Pool;
     }
     
     //From Github:
//     @Override
//     protected Set<ParticipantSharedState> getSharedState() {
//         Set<ParticipantSharedState> ss = super.getSharedState();
//         ss.add(ParticipantLocationService.createSharedState(getID(), myLoc));
//         return ss;
//     }

}


