package myagents;

//import myagents.LocationVisualisationPlugin;
import org.apache.log4j.Logger;


import actions.DemandHandler;
//import uk.ac.imperial.evpool.EvEnvService;
//import uk.ac.imperial.evpool.Inject;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.location.LocationStoragePlugin;
import uk.ac.imperial.presage2.util.location.MoveHandler;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.location.area.Area;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;

public class SimpleSim extends RunnableSimulation {

	//private PoolAgentService game;
	//private Scenario scenario;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public PoolAgentService game;

	@Parameter(name = "size")
	public int size;
	
	@Parameter(name = "agents", optional = true)
	public int agents = 2;

	@Override
	public void initialiseScenario(Scenario scenario) {
		addModule(new AbstractEnvironmentModule()
				.addParticipantGlobalEnvironmentService(SimpleEnvService.class)
				.addActionHandler(DemandHandler.class)
				//Add the participant service and any other additional environment services here too
				);
		
		 
		 
		for (int i = 0; i < agents; i++) {
			scenario.addAgent(new SimpleAgent(
					Random.randomUUID(),
					"agent" + i,
					Random.randomInt(size), 
					Random.randomInt(size)
					));
		}
		
	}
}