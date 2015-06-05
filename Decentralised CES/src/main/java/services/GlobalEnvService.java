//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.Demand;
import actions.parentDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

import state.SimState;
import uk.ac.imperial.presage2.core.environment.*;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.util.ArrayList;
import java.util.UUID;

public class GlobalEnvService extends EnvironmentService{
    private SimState state;
    private int round = 0;
    private PowerPoolEnvService ChildEnvService;
    final protected EnvironmentServiceProvider serviceProvider;

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    @Named("params.allocationType")
    public int allocationType;

//    @Inject
    @Parameter (name = "parent_level")
    private int parent_level;

    @Inject
    public GlobalEnvService(EnvironmentSharedStateAccess sharedState, EnvironmentServiceProvider serviceProvider)
    {
        super(sharedState);
        this.serviceProvider = serviceProvider;
    }

    public void allocate(parentDemand Total, ArrayList<UUID> ChildrenList)
    {
        //logger.info("GlobalEnvService.allocate() called");
        getChildEnvService();
        double shortfall = Total.getDemandRequest() - Total.getGenerationRequest();

        if (shortfall < 0)
        {
            //Go through the children, and allocating their requests
            for (int i=0; i<ChildrenList.size(); i++)
            {
                UUID agent = ChildrenList.get(i);
                ChildEnvService.setGroupDemand(agent, (parentDemand)ChildEnvService.getAgentDemand(agent));
            }
        }
        else
        {
            if (allocationType == 1 ) {
                allocate_fairly();
            }
            else {
                allocate_proportionally(Total, ChildrenList);
            }
        }
    }

    private void allocate_proportionally(parentDemand Total, ArrayList<UUID> ChildrenList)
    {
        double proportion = Total.getGenerationRequest()/Total.getDemandRequest();
        for (int i=0; i<ChildrenList.size(); i++)
        {
            UUID agent = ChildrenList.get(i);
            parentDemand request = (parentDemand)ChildEnvService.getAgentDemand(agent);
            parentDemand allocation = new parentDemand(request.getDemandRequest()*proportion, request.getGenerationRequest(), agent);
            ChildEnvService.setGroupDemand(agent, allocation);
        }
    }

    private void allocate_fairly()
    {
        logger.info("GlobalEnvService Allocating fairly");
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
    @Step
    public void step(int t) throws ActionHandlingException
    {
        logger.info("Incrementing State");
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

}
