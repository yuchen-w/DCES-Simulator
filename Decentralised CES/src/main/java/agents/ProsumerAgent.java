package agents;

import actions.childDemand;
import sun.management.Agent;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.util.UUID;

public class ProsumerAgent extends ParentAgent {

    protected String parent;
    protected UUID parent_id;
    childDemand AgentDemand;

    public ProsumerAgent(UUID id, String name, double consumption, double allocation, String parent, UUID parent_id)
    {
        super(id, name, consumption, allocation, 0);
        this.parent = parent;
        this.parent_id = parent_id;
        this.AgentDemand = new childDemand(consumption, allocation, id, parent_id);
        logger.info("Initiated " + name + " with d: " +consumption+ " and g=" +allocation );
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        //logger.info("My required parentDemand is: " 	+ this.AgentDemand.getDemandRequest());
        //logger.info("My Group Generation is: " 	+ this.AgentDemand.getGenerationRequest());
        //logger.info("My Group Allocation is: "+ this.AgentDemand.getAllocation());
        AgentDemand.setT(t);
        try
        {
            environment.act(AgentDemand, getID(), authkey);
        } catch (ActionHandlingException e) {
            logger.warn("Failed to add demand to the pool", e);
        }
        if(t% AgentDemand.getStateNum() == 4){
            logger.info("Agent: " + this.getID() + " allocation: d =" + AgentDemand.getAllocationD() + " g = " + AgentDemand.getGenerationRequest());
        }
    }
//    @Override
//    protected Set<ParticipantSharedState> getSharedState() {
//        Set<ParticipantSharedState> ss = super.getSharedState();
//        ss.add(new ParticipantSharedState("agent.demand", super.AgentDemand, getID()));
//        return ss;
//    }

}
