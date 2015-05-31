package actions.handlers;

import actions.childDemand;
import agents.ParentAgent;
import agents.ProsumerAgent;
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
            final childDemand d = (childDemand)demand_action;
            getParentService();
            logger.info("ProsumerAgent: " + actor +" requesting: " + d.getDemand() + " and is generating " + d.getGeneration());		//Debug
            this.ParentService.addtoPool(d);

           //TODO: Increment Global ChildrenNum varaiable here

            //this.ParentService.takefromPool(d);
            //logger.info("ParentEnvService::totalcDemand= " + this.ParentService.getTotalDemand());										//Debug
            //logger.info("ParentEnvService::totalcGeneration= " + this.ParentService.getTotalGeneration());										//Debug
            //logger.info("ParentEnvService::available= " + this.ParentService.getAvailable());
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

    //private

}
