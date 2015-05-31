package services;

import actions.MasterAction;
import actions.handlers.DemandHandler;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import services.GlobalEnvService;
import services.PowerPoolEnvService;
import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.*;

import java.util.UUID;

public class MasterActionHandlerService extends GlobalEnvService implements ActionHandler{
    final private Logger logger = Logger.getLogger(DemandHandler.class);

    final protected EnvironmentServiceProvider serviceProvider;
    final protected EnvironmentSharedStateAccess sharedState;
    protected PowerPoolEnvService EnvService;

    @Inject
    public MasterActionHandlerService(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState)
    {
        super(sharedState);
        this.serviceProvider = serviceProvider;
        this.sharedState = sharedState;
    }

    @Override
    public boolean canHandle(Action action) {
        return (action instanceof MasterAction);
    }

    @Override
    public Object handle(Action action, UUID actor) throws ActionHandlingException {
        getService();
        if (action instanceof MasterAction)
        {
            final MasterAction a = (MasterAction)action;
            logger.info("Incrementing State. State was");		//Debug
            //Do allocate if State=allocate. Use Allocate action.
            this.EnvService.incrementState();
        }
        return null;
    }

    protected PowerPoolEnvService getService()
    {
        if (EnvService == null) {
            try {
                this.EnvService = serviceProvider
                        .getEnvironmentService(PowerPoolEnvService.class);
            } catch (UnavailableServiceException e) {
                logger.warn("Could not get EnvService", e);
            }
        }
        return EnvService;
    }
}
