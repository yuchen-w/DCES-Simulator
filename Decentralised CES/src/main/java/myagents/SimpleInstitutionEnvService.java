//Generate the shared state. See ParticipantLocationService for template

package myagents;


import actions.Demand;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;

//import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
//import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
//import uk.ac.imperial.presage2.core.participant.Participant;
//import uk.ac.imperial.presage2.util.location.LocationService;

public class SimpleInstitutionEnvService extends EnvironmentService{

	protected EnvironmentSharedStateAccess sharedState;

	double totalDemand = 0;
	double totalGeneration = 0;
	double available = 0;

	private final Logger logger = Logger.getLogger(this.getClass());

	@Inject
	public SimpleInstitutionEnvService(EnvironmentSharedStateAccess sharedState)
	{
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
		//this.available = this.available + d.getGeneration();
	}
	
	public void takefromPool (Demand d)
	{
		double shortfall = d.getDemand() - d.getGeneration();
		double allocation;
		if (shortfall < 0)
		{
			allocation = d.getDemand();
			this.available -= shortfall;
		}
		else if (shortfall >= 0 && shortfall <= this.available)
		{
			allocation = d.getDemand();
			this.available -= shortfall;
		}
		else
		{
			allocation = d.getGeneration() + this.available;
			this.available = 0;
		}
		logger.info("Allocating: " + allocation);
		
		d.Allocate(allocation);
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
	
	public double getAvailable()
	{
		try {
			return this.available;
		} catch (Exception e)
		{
			logger.warn("Failed to getTotalGeneration", e);
			return 909090.9090;	//Fix this
		}
	}

}
