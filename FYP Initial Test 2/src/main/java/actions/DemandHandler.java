package actions;

import java.util.UUID;

import myagents.SimpleEnvService;

import org.apache.log4j.Logger;
//import org.drools.runtime.StatefulKnowledgeSession;





import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;

public class DemandHandler implements ActionHandler {

	
	final private Logger logger = Logger.getLogger(DemandHandler.class);
	//final StatefulKnowledgeSession session;
	
	SimpleEnvService EnvService;
	final protected EnvironmentServiceProvider serviceProvider;
	final protected EnvironmentSharedStateAccess sharedState;
	
	@Inject
	public DemandHandler(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState)
	{
		this.serviceProvider = serviceProvider;
		this.sharedState = sharedState;
	}
	
	
	@Override
	public boolean canHandle(Action demand) {
		return demand instanceof Demand;
	}

	@Override
	public Object handle(Action demand_action, UUID actor) throws ActionHandlingException {
		// TODO Auto-generated method stub
		getService();
		if (demand_action instanceof Demand)
		{
			final Demand d = (Demand)demand_action;
			this.EnvService.addtoDemand(d.getQuantity());
		}
		return null;
	}
	
	protected SimpleEnvService getService()
	{
		if (EnvService == null) {
			try {
				this.EnvService = serviceProvider
						.getEnvironmentService(SimpleEnvService.class);
			} catch (UnavailableServiceException e) {
				//logger.warn("Could not load location service", e);
			}
		}
		return EnvService;
	}

}
