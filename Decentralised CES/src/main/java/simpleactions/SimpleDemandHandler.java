package simpleactions;

import com.google.inject.Inject;
import CES_Agents.PowerPoolEnvService;
import org.apache.log4j.Logger;
import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.*;

import java.util.UUID;

//import org.drools.runtime.StatefulKnowledgeSession;

public class SimpleDemandHandler implements ActionHandler {

	final private Logger logger = Logger.getLogger(SimpleDemandHandler.class);
	
	PowerPoolEnvService EnvService;
	final protected EnvironmentServiceProvider serviceProvider;
	final protected EnvironmentSharedStateAccess sharedState;
	
	@Inject
	public SimpleDemandHandler(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState)
	{
		this.serviceProvider = serviceProvider;
		this.sharedState = sharedState;
	}
	
	@Override
	public boolean canHandle(Action demand) {
		return demand instanceof SimpleDemand;
	}

	@Override
	public Object handle(Action demand_action, UUID actor) throws ActionHandlingException {
		getService();
		if (demand_action instanceof SimpleDemand)
		{
			final SimpleDemand d = (SimpleDemand)demand_action;
			logger.info("DemandHandler: Demand d.Demand = " + d.getDemand() + " and Demand d.Generation = " + d.getGeneration());		//Debug
			//this.EnvService.addtoPool(d);
			//this.EnvService.takefromPool(d);
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
