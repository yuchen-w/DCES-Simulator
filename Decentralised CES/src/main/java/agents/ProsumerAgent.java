package agents;

import actions.Demand;
import actions.childDemand;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.util.UUID;

public class ProsumerAgent extends ParentAgent {

    protected String parent;
    protected UUID parent_id;
    childDemand AgentDemand;

    public ProsumerAgent(UUID id, String name, double consumption, double allocation, String parent, UUID parent_id)
    {
        super(id, name, consumption, allocation);
        this.parent = parent;
        this.parent_id = parent_id;
        this.AgentDemand = new childDemand(consumption, allocation, parent_id, id);
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        logger.info("My required Demand is: " 	+ this.AgentDemand.getDemand());
        logger.info("My Group Generation is: " 	+ this.AgentDemand.getGeneration());
        logger.info("My Group Allocation is: "+ this.AgentDemand.getAllocation());

        try
        {
            environment.act(AgentDemand, getID(), authkey);
        } catch (ActionHandlingException e) {
            logger.warn("Failed to add demand to the pool", e);
        }

    }
//    @Override
//    protected Set<ParticipantSharedState> getSharedState() {
//        Set<ParticipantSharedState> ss = super.getSharedState();
//        ss.add(new ParticipantSharedState("agent.demand", super.AgentDemand, getID()));
//        return ss;
//    }

}
