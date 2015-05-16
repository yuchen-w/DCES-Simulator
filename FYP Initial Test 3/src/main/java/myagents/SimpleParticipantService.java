package myagents;

import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;

@ServiceDependencies({ EnvironmentMembersService.class })
public class SimpleParticipantService extends SimpleEnvService{
	
	private final Logger logger = Logger.getLogger(SimpleParticipantService.class);
	
	protected final UUID myID;
	
	protected final EnvironmentMembersService membersService;
	
	public SimpleParticipantService (UUID id, EnvironmentSharedStateAccess sharedState, EnvironmentServiceProvider serviceProvider)
	{
		super(sharedState, serviceProvider);
		this.myID = id;
		this.membersService = getMembersService(serviceProvider);
	}
	
	private EnvironmentMembersService getMembersService(
			EnvironmentServiceProvider serviceProvider) {
		try {
			return serviceProvider
					.getEnvironmentService(EnvironmentMembersService.class);
		} catch (UnavailableServiceException e) {
			logger.warn("Could not retrieve EnvironmentMembersService; functionality limited.");
			return null;
		}
	}

}

