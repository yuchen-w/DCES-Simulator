package actions.handlers;

import actions.Demand;
import actions.parentDemand;
import actions.childDemand;
import com.google.inject.Inject;
import services.ParentEnvService;
import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;

import java.util.UUID;

import org.apache.log4j.Logger;
public class ChildDemandHandler extends DemandHandler{

    final private Logger logger = Logger.getLogger(ChildDemandHandler.class);

    private ParentEnvService ParentService;

    @Inject
    public ChildDemandHandler(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState)
            throws UnavailableServiceException
    {
        super(serviceProvider, sharedState);
    }

    @Override
    public boolean canHandle(Action cDemand) {
        return cDemand instanceof childDemand;
    }

    @Override
    public Object handle(Action demand_action, UUID actor) throws ActionHandlingException {
        if (demand_action instanceof childDemand)
        {
            childDemand d = (childDemand)demand_action;
            getParentService();
            int CurrentState = d.getT()%d.getStateNum();

            if (CurrentState == 0)
            {
                logger.info("T= "+ d.getT() +". Children Request round");
                logger.info("ProsumerAgent: " + actor +" requesting: " + d.getDemandRequest() + " and is providing " + d.getGenerationRequest());		//Debug
                this.ParentService.addToAgentPool(d);
            }

            if (CurrentState == 4)
            {
                getParentService();
                //logger.info("CurrentState = " + CurrentState + " T = "+ d.getT() +". Children Receive round");
                //logger.info("Agent: " + actor + " attempting to retrieve allocation");

                //todo
                Demand allocated = ParentService.getAllocation(actor);

                logger.info("Agent demand was: " + d.getDemandRequest() + " " + d.getGenerationRequest());

                //todo
                d.allocate(allocated.getAllocationD(), allocated.getAllocationG());

                //logger.info("Agent allocation is now " + d.getAllocationD());
                //logger.info("Agent: " + actor + " allocation: d =" + allocated.getDemandRequest() + " g = " + allocated.getGenerationRequest());
            }
        }
        return null;
    }

    protected ParentEnvService getParentService()
    {
        if (ParentService == null)
        {
            try
            {
//                logger.info("Getting ParentService");
                this.ParentService = serviceProvider.getEnvironmentService(ParentEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ParentService", e);
            }
        }
        return ParentService;
    }

//    protected ParentEnvService getService()
//    {
//        if (EnvService == null)
//        {
//            try
//            {
////                logger.info("Getting ParentService");
//                this.EnvService = serviceProvider.getEnvironmentService(ChildEnvService.class);
//            }
//            catch (UnavailableServiceException e)
//            {
//                logger.warn("Could not get EnvService", e);
//            }
//        }
//        return ParentService;
//    }

    //private

}
