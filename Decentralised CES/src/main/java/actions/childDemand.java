package actions;

import java.util.UUID;

public class childDemand extends Demand {

    UUID ParentID;

    public childDemand(double demand, double generation, UUID actor, UUID parent) {
        super(demand, generation, actor);
        this.ParentID = parent;
        this.AgentID = actor;

    }

    public UUID getParentID()
    {
        return ParentID;
    }
}

