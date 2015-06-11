package sim;

import actions.handlers.ChildDemandHandler;
import actions.handlers.DemandHandler;
import agents.MasterAgent;
import agents.ParentAgent;
import agents.ProsumerAgent;
import org.apache.log4j.Logger;
import services.ParentEnvService;
import services.PowerPoolEnvService;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

//import services.ChildEnvService;

public class dCES_Sim extends RunnableSimulation {

    //private final Logger logger = Logger.getLogger(this.getClass());
    private java.util.Random fRandom = new java.util.Random();
    private final Logger logger = Logger.getLogger(this.getClass());

    @Parameter(name = "output", optional = true)
    public String output = "request.csv";

    public String output2 = "allocation.csv";

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

    @Parameter (name = "hours")
    public int hours;

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

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output, true)));
            out.println("hour, name , Request D , Request G, Productivity, Social Utility");
            out.close(); //Fixing Resource specification not allowed here for source level below 1.7
        }catch (IOException e) {

        }

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output2, true)));
            out.println("hour, name , Allocated D , Allocated G");
            out.close(); //Fixing Resource specification not allowed here for source level below 1.7
        }catch (IOException e) {

        }

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("AgentRanking.csv", true)));
            out.println("hour, name, id, CanonEqualityRank, CanonNeedsRank, CanonProductivityRank, CanonSocialUtilityRank, CanonSupplyAndDemandRank"
            );
            out.close();
        }catch (IOException e) {
            logger.info("Failed to write to file" + "AgentRanking.csv");
        }

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("AgentCanonVotes.csv", true)));
            out.println("hour,id,Equalityweight,ProductivityWeight,UtilityWeight,NeedsWeight,SupplyAndDemandWeight"
            );
            out.close();
        }catch (IOException e) {
            logger.info("Failed to write to file" + "AgentCanonVotes.csv");
        }

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("BordaRank.csv", true)));
            out.println("hour,id,canon,assigned rank");
            out.close(); //Fixing Resource specification not allowed here for source level below 1.7
        }catch (IOException e) {

        }

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("CanonAllocationFromGlobal.csv", true)));
            out.println("hour, canon, id, Borda pts for ID, Borda Proportion, Borda Sum, Canon weight(aggregated votes for this canon)");
            out.close(); //Fixing Resource specification not allowed here for source level below 1.7
        }catch (IOException e) {

        }


        ArrayList<Double> WindProfile = new ArrayList<Double>();
        try{
            Scanner inFile1 = new Scanner(new File("./data/Wind Generation Profile.csv")).useDelimiter(",\\s*");
            while (inFile1.hasNext()) {
                double token1 = inFile1.nextDouble();
                WindProfile.add(token1);
//                Float[] tempsArray = WindProfile.toArray(new Float[0]);
//                for (Float s : tempsArray) {
//                    logger.info("reading: " + (s));
//                }
            }
            inFile1.close();
        }catch (IOException e) {

        }

        ArrayList<Double> SolarProfile = new ArrayList<Double>();
        try{
            Scanner inFile1 = new Scanner(new File("./data/Solar Generation Profile.csv")).useDelimiter(",\\s*");
            while (inFile1.hasNext()) {
                double token1 = inFile1.nextDouble();
                SolarProfile.add(token1);
//                Float[] tempsArray = WindProfile.toArray(new Float[0]);
//                for (Float s : tempsArray) {
//                    logger.info("reading: " + (s));
//                }
            }
            inFile1.close();
        }catch (IOException e) {

        }

        ArrayList<Double> DemandProfile = new ArrayList<Double>();
        try{
            Scanner inFile1 = new Scanner(new File("./data/Agent Demand Profile.csv")).useDelimiter(",\\s*");
            while (inFile1.hasNext()) {
                double token1 = inFile1.nextDouble();
                token1 = token1*1.2;
                DemandProfile.add(token1);
//                Float[] tempsArray = WindProfile.toArray(new Float[0]);
//                for (Float s : tempsArray) {
//                    logger.info("reading: " + (s));
//                }
            }
            inFile1.close();
        }catch (IOException e) {

        }


        for (int i = 0; i < agents; i++)
        {
            UUID parent_id = Random.randomUUID();
            ParentAgent Parent = new ParentAgent(parent_id, "parent" + i, 0, 0, agent_children);
            scenario.addAgent(Parent);

            supervisor.addChild(Parent.getID());

            for (int j = 0; j < agent_children/2; j++)
            {
                UUID child_id = Random.randomUUID();
                ProsumerAgent prosumer = new ProsumerAgent(child_id,
                        "parent" + i + "agent" + j, "parent" + i, parent_id);

                for (int k=0; k<hours; k++)
                {
                    prosumer.addProfileHourly(DemandProfile.get(k)*getGaussian(1, 0.2), WindProfile.get(k)*getGaussian(1, 0.2));
                }

                for (int k=0; k<hours; k++)
                {
                    prosumer.addProductivity(Random.randomInt(agent_children));
                    prosumer.addSocialUtility(Random.randomInt(agent_children));
                }

                scenario.addAgent(prosumer);
                Parent.addChild(child_id);

            }
            for (int j = agent_children/2; j < agent_children; j++)
            {
                UUID child_id = Random.randomUUID();
                ProsumerAgent prosumer = new ProsumerAgent(child_id,
                        "parent" + i + "agent" + j, "parent" + i, parent_id);

                for (int k=0; k<hours; k++)
                {
                    prosumer.addProfileHourly(DemandProfile.get(k)*getGaussian(1, 0.2), WindProfile.get(k)*getGaussian(1, 0.2));
                }

                for (int k=0; k<hours; k++)
                {
                    prosumer.addProductivity(Random.randomInt(agent_children));
                    prosumer.addSocialUtility(Random.randomInt(agent_children));
                }

                scenario.addAgent(prosumer);
                Parent.addChild(child_id);

            }
        }
    }

    private double getGaussian(double aMean, double aVariance){
        return aMean + fRandom.nextGaussian() * (aVariance/100);
    }
}