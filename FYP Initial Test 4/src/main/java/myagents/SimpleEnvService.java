//Generate the shared state. See ParticipantLocationService for template

package myagents;


import org.apache.log4j.Logger;

import actions.Demand;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
//import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
//import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
//import uk.ac.imperial.presage2.core.participant.Participant;
//import uk.ac.imperial.presage2.util.location.LocationService;

public class SimpleEnvService extends EnvironmentService{

	
	double totalDemand = 0;
	double totalGeneration = 0;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Inject
	public SimpleEnvService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
	}
	
	public void addtoDemand(double d)
	{
		this.totalDemand = this.totalDemand + d;
	}
	
	public void addtoGeneration(double d)
	{
		this.totalDemand = this.totalDemand + d;
	}
	
	public void addtoPool (Demand d)
	{
		this.totalDemand = this.totalDemand + d.getDemand();
		this.totalGeneration = this.totalGeneration + d.getGeneration();
	}
	
	public double getTotalDemand()
	{
		try {
			return this.totalDemand;
		} catch (Exception e)
		{
			logger.warn("Failed to getTotalDemand", e);
			return 909090.9090;	//Fix this
		}
	}
	
	public double getTotalGeneration()
	{
		try {
			return this.totalGeneration;
		} catch (Exception e)
		{
			logger.warn("Failed to getTotalGeneration", e);
			return 909090.9090;	//Fix this
		}
	}

}
