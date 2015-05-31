//Generate the shared state. See ParticipantLocationService for template

package services;


import actions.childDemand;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;

import actions.Demand;

import com.google.inject.Inject;

import state.SimState;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import java.util.HashMap;
import java.util.UUID;

public class GlobalEnvService extends EnvironmentService{
    private SimState state = new SimState();

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    public GlobalEnvService(EnvironmentSharedStateAccess sharedState)
    {
        super(sharedState);
    }

    protected void incrementState()
    {
        this.state.IncrementState();
        logger.info("State incremented. State is now: " + state.getState());
    }
}
