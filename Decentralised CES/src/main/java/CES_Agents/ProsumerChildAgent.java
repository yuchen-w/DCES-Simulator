package CES_Agents;

import java.util.UUID;
import java.util.Set;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;

public class ProsumerChildAgent extends ProsumerAgent {

    protected String parent;
    protected UUID parent_id;

    public ProsumerChildAgent(UUID id, String name, double consumption, double allocation, String parent, UUID parent_id)
    {
        super(id, name, consumption, allocation);
        this.parent = parent;
        this.parent_id = parent_id;
    }

    @Override
    protected Set<ParticipantSharedState> getSharedState() {
        Set<ParticipantSharedState> ss = super.getSharedState();
        ss.add(new ParticipantSharedState("agent.demand", super.AgentDemand, getID()));
        return ss;
    }

}
