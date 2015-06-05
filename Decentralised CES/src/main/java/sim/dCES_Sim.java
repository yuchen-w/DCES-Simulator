package sim;

import actions.handlers.ChildDemandHandler;
import actions.handlers.DemandHandler;
import agents.MasterAgent;
import agents.ParentAgent;
import agents.ProsumerAgent;
import services.ParentEnvService;
import services.PowerPoolEnvService;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;

import java.util.UUID;

//import services.ChildEnvService;

public class dCES_Sim extends RunnableSimulation {

    //private final Logger logger = Logger.getLogger(this.getClass());
    private java.util.Random fRandom = new java.util.Random();

    @Parameter(name = "mean")
    public int mean;

    @Parameter(name = "variance")
    public int variance;

    @Parameter(name = "children")
    public int agent_children;

    @Parameter(name = "agents")
    public int agents;

    @Parameter(name = "allocationType", optional = true)
    public int allocationType;

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

                        .addActionHandler(DemandHandler.class)
                        .addActionHandler(ChildDemandHandler.class)
                        .addActionHandler(services.handler.MasterActionHandlerService.class)
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
                        "parent" + i + "agent" + j, this.getGaussian(mean, variance), this.getGaussian(mean, variance) - 10,
                        "parent" + i, parent_id));
                Parent.addChild(child_id);

            }
        }
    }

    private double getGaussian(double aMean, double aVariance){
        return aMean + fRandom.nextGaussian() * (aVariance/100);
    }
}