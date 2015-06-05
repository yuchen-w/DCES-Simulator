package actions.handlers;

import java.util.UUID;

import actions.parentDemand;
import actions.childDemand;
import services.ParentEnvService;
import services.PowerPoolEnvService;

import org.apache.log4j.Logger;
//import org.drools.runtime.StatefulKnowledgeSession;


import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;

public class DemandHandler implements ActionHandler {

	final private Logger logger = Logger.getLogger(DemandHandler.class);

	ParentEnvService EnvService;
	PowerPoolEnvService ParentEnvService;
	final protected EnvironmentServiceProvider serviceProvider;
	final protected EnvironmentSharedStateAccess sharedState;
    private final EnvironmentMembersService membersService;
	
	@Inject
	public DemandHandler(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState) throws UnavailableServiceException
	{
		this.serviceProvider = serviceProvider;
		this.sharedState = sharedState;
        this.membersService = serviceProvider.getEnvironmentService(EnvironmentMembersService.class);
	}
	
	@Override
	public boolean canHandle(Action demand) {
		return (demand instanceof parentDemand) & !(demand instanceof childDemand);
	}

	@Override
	public Object handle(Action action, UUID actor) throws ActionHandlingException {
		getService();
		getParentService();
		if (action instanceof parentDemand)
		{

			final parentDemand d = (parentDemand)action;
			int CurrentState = d.getT()%d.getStateNum();
			if (CurrentState == 1)
            {
                logger.info("T=" + d.getT() + " Parent Request round");
                //logger.info("DemandHandler: parentDemand d.parentDemand = " + d.getDemandRequest() + " and parentDemand d.Generation = " + d.getGenerationRequest());        //Debug
                parentDemand GroupDemand = this.EnvService.getGroupDemand(d);
                this.ParentEnvService.addToAgentPool(GroupDemand);
                logger.info("GroupDemand D= " +GroupDemand.getDemandRequest()+" G= " + GroupDemand.getGenerationRequest());
            }

            if (CurrentState == 3)
            {
                //logger.info("T=" + d.getT() + ". Appropriate to agent round");
                parentDemand allocated = ParentEnvService.getAllocation(actor);
                d.allocateDemandObj(allocated);
                logger.info("Parent " + actor + " allocation: d =" + allocated.getDemandRequest() + " g = " + allocated.getGenerationRequest());
                ParentEnvService.allocate(allocated, d.getChildrenList());
            }

		}
		return null;
	}
	
	protected ParentEnvService getService()
	{
		if (EnvService == null)
		{
			try
			{
				logger.info("Getting ParentService (GlobalService) of ParentEnvService");
				this.EnvService = serviceProvider.getEnvironmentService(ParentEnvService.class);
			}
			catch (UnavailableServiceException e)
			{
				logger.warn("Could not get ParentService (GlobalService)", e);
			}
		}
		return EnvService;
	}

	private PowerPoolEnvService getParentService()
	{
		if (ParentEnvService == null) {
			try {
				this.ParentEnvService = serviceProvider
						.getEnvironmentService(PowerPoolEnvService.class);
			} catch (UnavailableServiceException e) {
				logger.warn("Could not get EnvService", e);
			}
		}
		return ParentEnvService;
	}


}
