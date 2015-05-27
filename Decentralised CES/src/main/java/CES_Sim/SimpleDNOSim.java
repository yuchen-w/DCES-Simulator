package CES_Sim;

import CES_Agents.PowerChildPoolEnvService;
import CES_Agents.PowerPoolEnvService;
import CES_Agents.ProsumerAgent;
import CES_Agents.ProsumerChildAgent;
import actions.DemandHandler;
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
                        //.addParticipantEnvironmentService(PowerChildPoolEnvService.class)
                                //.addParticipantGlobalEnvironmentService(EnvironmentMembersService.class)
                        .addActionHandler(DemandHandler.class)
                //Add the participant service and any other additional environment services here too
        );



        for (int i = 0; i < agents; i++) {
            UUID parent_id = Random.randomUUID();
            scenario.addAgent(new ProsumerAgent(parent_id, "parent" + i, 0 , 0));
            for (int j = 0; j < agent_children; j++)
                scenario.addAgent(new ProsumerChildAgent(Random.randomUUID(),
                        "parent" + i + "agent" + j, Random.randomInt(size),Random.randomInt(size),
                        "parent" + i, parent_id));
        }

//		for (int i = 0; i < agents; i++) {
//			scenario.addAgent(new ProsumerAgent(
//					Random.randomUUID(),
//					"agent" + i,
//					10, 
//					8
//					));
//		}	
    }
}