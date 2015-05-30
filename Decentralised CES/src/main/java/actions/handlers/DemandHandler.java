package actions.handlers;

import java.util.UUID;

import actions.Demand;
import actions.childDemand;
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
	
	PowerPoolEnvService EnvService;
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
		return (demand instanceof Demand) & !(demand instanceof childDemand);
	}

	@Override
	public Object handle(Action GroupDemand, UUID actor) throws ActionHandlingException {
		getService();
		if (GroupDemand instanceof Demand)
		{
			final Demand d = (Demand)GroupDemand;
			logger.info("DemandHandler: Demand d.Demand = " + d.getDemand() + " and Demand d.Generation = " + d.getGeneration());		//Debug
			this.EnvService.addtoPool(d);
			this.EnvService.takefromPool(d);
			logger.info("PowerPoolEnvService::totalDemand= " + this.EnvService.getTotalDemand());										//Debug
			logger.info("PowerPoolEnvService::totalGeneration= " + this.EnvService.getTotalGeneration());										//Debug
			logger.info("PowerPoolEnvService::available= " + this.EnvService.getAvailable());
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
