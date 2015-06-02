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
    protected PowerPoolEnvService ChildEnvService;
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
        logger.info("GlobalEnvService.appropriate() called");
        getChildEnvService();
        double shortfall = Total.getDemand() - Total.getGeneration();

        if (shortfall < 0)
        {
            //Go through the children, and allocating their requests
            for (int i=0; i<ChildrenList.size(); i++)
            {
                UUID agent = ChildrenList.get(i);
                ChildEnvService.setGroupDemand(agent, ChildEnvService.getAgentDemand(agent));
            }
        }
        else
        {
            double proportion = Total.getGeneration()/Total.getDemand();
            for (int i=0; i<ChildrenList.size(); i++)
            {
                UUID agent = ChildrenList.get(i);
                Demand request = ChildEnvService.getAgentDemand(agent);
                Demand allocation = new Demand(request.getDemand()*proportion, request.getGeneration(), agent);
                ChildEnvService.setGroupDemand(agent, allocation);
            }
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

    protected PowerPoolEnvService getChildEnvService()
    {
        if (ChildEnvService == null)
        {
            try
            {
                logger.info("Getting ChildService (PowerPoolEnvService) of GlobalEnvService");
                this.ChildEnvService = serviceProvider.getEnvironmentService(PowerPoolEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ChildService (PowerPoolEnvService)", e);
            }
        }
        return ChildEnvService;
    }


    //Currently not working:
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
