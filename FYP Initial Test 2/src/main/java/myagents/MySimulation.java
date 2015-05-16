package myagents;

//import myagents.LocationVisualisationPlugin;
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

public class MySimulation extends RunnableSimulation {

	@Parameter(name = "size")
	public int size;

	@Parameter(name = "agents", optional = true)
	public int agents = 1;

	@Override
	public void initialiseScenario(Scenario scenario) {
		addModule(Area.Bind.area2D(size, size));
		addModule(new AbstractEnvironmentModule()
				.addActionHandler(MoveHandler.class)
				.addParticipantEnvironmentService(
						ParticipantLocationService.class)
				.addParticipantGlobalEnvironmentService(AreaService.class));
		addObjectClass(LocationStoragePlugin.class);
		//addObjectClass(LocationVisualisationPlugin.class);

		for (int i = 0; i < agents; i++) {
			scenario.addAgent(new SituatedAgent(
					Random.randomUUID(),
					"agent" + i,
					Random.randomInt(size), 
					Random.randomInt(size)
					));
		}
		
	}
}