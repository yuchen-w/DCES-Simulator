package agents;

import actions.childDemand;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import sun.management.Agent;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class ProsumerAgent extends ParentAgent {

    protected String parent;
    protected UUID parent_id;
    childDemand AgentDemand;

    public String output = "output.csv";


    public ProsumerAgent(UUID id, String name, double consumption, double allocation, String parent, UUID parent_id)
    {
        super(id, name, consumption, allocation, 0);
        this.parent = parent;
        this.parent_id = parent_id;
        this.AgentDemand = new childDemand(consumption, allocation, id, parent_id);
        logger.info("Initiated " + name + " with d: " +consumption+ " and g=" +allocation );

        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output, true)))) {
            out.println(name + ", " +consumption+ ", "+ allocation);
        }catch (IOException e) {
            logger.info("Failed to write to file");
        }
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        AgentDemand.setT(t);
        try
        {
            environment.act(AgentDemand, getID(), authkey);
        } catch (ActionHandlingException e) {
            logger.warn("Failed to add demand to the pool", e);
        }
        if(t% AgentDemand.getStateNum() == 4){
            logger.info("Agent: " + this.getID() + " d: "+ AgentDemand.getDemandRequest() + " allocation: d =" + AgentDemand.getAllocationD() + " g = " + AgentDemand.getGenerationRequest() + " allocation g = " +AgentDemand.getAllocationG());
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output, true)))) {
                out.println(this.getName() + ", " +AgentDemand.getAllocationD()+ ", "+ AgentDemand.getGenerationRequest());
            }catch (IOException e) {
                logger.info("Failed to write to file");
            }
        }
    }
}
