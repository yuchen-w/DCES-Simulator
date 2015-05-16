//Generate the shared state. See ParticipantLocationService for template

package myagents;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.location.LocationService;

public class SimpleEnvService extends EnvironmentService{

	
	EnvironmentServiceProvider serviceProvider;
	double totalDemand;
	
	@Inject
	public SimpleEnvService(EnvironmentSharedStateAccess sharedState,
			EnvironmentServiceProvider serviceProvider) {
		super(sharedState);
		this.serviceProvider = serviceProvider;
	}
	
	
	
	protected SimpleEnvService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
		// TODO Auto-generated constructor stub
	}
	
	
	public void addtoDemand(double d)
	{
		this.totalDemand = this.totalDemand + d;
	}
	
	public double getTotalDemand()
	{
		try {
			return this.totalDemand;
		} catch (Exception e)
		{
			return 909090.9090;
		}
	}

}
