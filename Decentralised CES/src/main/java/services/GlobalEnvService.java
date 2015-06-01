//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.childDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import actions.Demand;

import com.google.inject.Inject;

import state.SimState;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GlobalEnvService extends EnvironmentService{
    private SimState state;
    private int round = 0;
    private PowerPoolEnvService ChildEnvService;
    final protected EnvironmentServiceProvider serviceProvider;

    private final Logger logger = Logger.getLogger(this.getClass());

//    @Inject
    @Parameter (name = "parent_level")
    private int parent_level;

    @Inject
    public GlobalEnvService(EnvironmentSharedStateAccess sharedState, EnvironmentServiceProvider serviceProvider)
    {
        super(sharedState);
        this.serviceProvider = serviceProvider;
    }

    public void appropriate (Demand Total,  ArrayList<UUID> ChildrenList)
    {
        getChildEnvService();
        double shortfall = Total.getDemand() - Total.getGeneration();

        if (shortfall < 0)
        {
            //HashMap<UUID, Demand> = HashMap(ParentID, RequestedDemand)
            //Or:
            //ChildEnvService.AllocateSame();
        }

//        double allocation;
//        if (shortfall < 0)
//        {
//            allocation = d.getDemand();
//            this.available -= shortfall;
//        }
//        else if (shortfall >= 0 && shortfall <= this.available)
//        {
//            allocation = d.getDemand();
//            this.available -= shortfall;
//        }
//        else
//        {
//            allocation = d.getGeneration() + this.available;
//            this.available = 0;
//        }
//        logger.info("Allocating: " + allocation);
//
//        d.Allocate(allocation);
    }

    private PowerPoolEnvService getChildEnvService()
    {

        if (ChildEnvService == null)
        {
            try
            {
                logger.info("Getting ParentService (GlobalService) of ParentEnvService");
                this.ChildEnvService = serviceProvider.getEnvironmentService(PowerPoolEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ParentService (GlobalService)", e);
            }
        }
        return ChildEnvService;
    }

    @EventListener
    protected void incrementState()
    {
        round++;
        if (this.state.getState() == null)
        {
            logger.info("Starting simulation. Initialising state");
            this.state = new SimState();
        }
        else
        {
            if (round > parent_level)
                this.state.incrementState();
        }
        logger.info("State incremented. State is now: " + state.getState());
    }

    public state.State getState()
    {
        return state.getState();
    }

    //public double returnGridSurplus()
//    {
//        return grid_surplus;
//    }
//
//    public void setGridSurplus(double value)
//    {
//        this.grid_surplus = value;
//    }
}
