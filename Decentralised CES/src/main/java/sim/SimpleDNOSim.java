package sim;

import agents.ParentAgent;
import services.ChildEnvService;
import services.ParentEnvService;
import services.PowerPoolEnvService;
import agents.ProsumerAgent;
import actions.handlers.ChildDemandHandler;
import actions.handlers.DemandHandler;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;

import java.util.UUID;

public class SimpleDNOSim extends RunnableSimulation {

    //private final Logger logger = Logger.getLogger(this.getClass());

    @Parameter(name = "size")
    public int size;

    @Parameter(name = "children")
    public int agent_children;

    @Parameter(name = "agents")
    public int agents;

    @Parameter(name = "allocation", optional = true)
    public String allocation;

    @Parameter (name = "surplus")
    public double grid_surplus;

    public double returnGridSurplus()
    {
        return grid_surplus;
    }

    public void setGridSurplus(double value)
    {
        this.grid_surplus = value;
    }

    @Override
    public void initialiseScenario(Scenario scenario) {
        addModule(new AbstractEnvironmentModule()
                        .addParticipantGlobalEnvironmentService(PowerPoolEnvService.class)
                        .addParticipantGlobalEnvironmentService(ParentEnvService.class)
                        .addParticipantEnvironmentService(ChildEnvService.class)

                        .addActionHandler(DemandHandler.class)
                        .addActionHandler(ChildDemandHandler.class)
                //Add the participant service and any other additional environment services here too
        );



        for (int i = 0; i < agents; i++) {
            UUID parent_id = Random.randomUUID();
            scenario.addAgent(new ParentAgent(parent_id, "parent" + i, 0 , 0, agent_children));
            for (int j = 0; j < agent_children; j++)
                scenario.addAgent(new ProsumerAgent(Random.randomUUID(),
                        "parent" + i + "agent" + j, Random.randomInt(size),Random.randomInt(size),
                        "parent" + i, parent_id));
        }

//Testing:
//		for (int i = 0; i < agents; i++) {
//			scenario.addAgent(new ParentAgent(
//					Random.randomUUID(),
//					"agent" + i,
//					10, 
//					8
//					));
//		}	
    }
}