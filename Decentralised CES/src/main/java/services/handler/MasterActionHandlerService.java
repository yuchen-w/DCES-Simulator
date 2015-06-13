package services.handler;

import actions.parentDemand;
import actions.MasterAction;
import actions.childDemand;
import actions.handlers.ParentDemandHandler;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import services.GlobalEnvService;
import services.PowerPoolEnvService;
import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.*;

import java.util.UUID;

public class MasterActionHandlerService extends GlobalEnvService implements ActionHandler{
    final private Logger logger = Logger.getLogger(ParentDemandHandler.class);

    final protected EnvironmentSharedStateAccess sharedState;
    protected PowerPoolEnvService EnvService;

    private int hour;

    @Inject
    public MasterActionHandlerService(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState)
    {
        super(sharedState, serviceProvider);
        this.sharedState = sharedState;
    }

    @Override
    public boolean canHandle(Action action) {
        return (action instanceof MasterAction & !((action instanceof childDemand)|(action instanceof parentDemand)));
    }

    @Override
    public Object handle(Action action, UUID actor) throws ActionHandlingException {
        getService();
        parentDemand TotalDemand = new parentDemand(0,0,actor);

        if (action instanceof MasterAction)
        {
            final MasterAction a = (MasterAction)action;
            int CurrentState = a.getT()%a.getStateNum();
            if (CurrentState == 2)
            {
                //logger.info("T=" + a.getT() + " Appropriating requests");
                //logger.info("Parents are:" + a.getChildrenList());
                TotalDemand = TotalDemand.addDemand(EnvService.getGroupDemand(a));
                TotalDemand.setHour(hour);
                logger.info("Global Total D = " + TotalDemand.getDemandRequest() + "G = " + TotalDemand.getGenerationRequest() + " Global Hour = " + TotalDemand.getHour());
                //logger.info("Global Canon Weight: " + TotalDemand.getTotalCanonWeight());
                allocate(TotalDemand, a.getChildrenList());
                hour++;
            }

            //Do allocate if State=allocate. Use Allocate action.
            //this.EnvService.incrementState();
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
