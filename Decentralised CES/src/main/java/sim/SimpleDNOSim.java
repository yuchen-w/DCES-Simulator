package sim;

import agents.MasterAgent;
import agents.ParentAgent;
//import services.ChildEnvService;
import services.ParentEnvService;
import services.PowerPoolEnvService;
import agents.ProsumerAgent;
import actions.handlers.ChildDemandHandler;
import actions.handlers.ParentDemandHandler;
import services.handler.MasterActionHandler;
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

    @Parameter (name = "parent_level", optional = true)
    public int parent_level = 2;

    @Override
    public void initialiseScenario(Scenario scenario) {
        addModule(new AbstractEnvironmentModule()
                        .addParticipantGlobalEnvironmentService(PowerPoolEnvService.class)
                        .addParticipantGlobalEnvironmentService(ParentEnvService.class)
                        //.addParticipantEnvironmentService(ChildEnvService.class)

                        .addActionHandler(ParentDemandHandler.class)
                        .addActionHandler(ChildDemandHandler.class)
                        .addActionHandler(MasterActionHandler.class)
                //Add the participant service and any other additional environment services here too
        );


        MasterAgent supervisor = new MasterAgent(Random.randomUUID(), "Supervisor/NGC");
        scenario.addAgent(supervisor);

        for (int i = 0; i < agents; i++)
        {
            UUID parent_id = Random.randomUUID();
            ParentAgent Parent = new ParentAgent(parent_id, "parent" + i, 0, 0, agent_children);
            scenario.addAgent(Parent);

            supervisor.addChild(Parent.getID());

            for (int j = 0; j < agent_children; j++)
            {
                UUID child_id = Random.randomUUID();
                scenario.addAgent(new ProsumerAgent(child_id,
                        "parent" + i + "agent" + j, Random.randomInt(size), Random.randomInt(size-10),
                        "parent" + i, parent_id));
                Parent.addChild(child_id);

            }
        }
    }
}