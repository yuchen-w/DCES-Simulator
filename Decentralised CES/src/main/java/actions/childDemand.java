package actions;

import java.util.UUID;

public class childDemand extends Demand {

    UUID ParentID;

    public childDemand(double demand, double generation, UUID actor, UUID parent) {
        super(demand, generation, actor);
        this.ParentID = parent;

    }

    public UUID getParentID()
    {
        return ParentID;
    }

    /**
     * Adds another Demand object to this one.
     * @param d
     */
    public childDemand addDemand(childDemand d)
    {
        this.demand 	+= d.getDemandRequest();
        this.generation += d.getGenerationRequest();
        this.allocated_demand += d.getAllocationD();
        this.allocated_generation += d.getAllocationG();
        return this;
    }
}

