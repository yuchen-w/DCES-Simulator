package actions;

import java.util.ArrayList;
import java.util.UUID;

public class parentDemand extends Demand{

    public parentDemand(double demand, double generation, UUID AgentID, ArrayList<UUID> ChildrenList) {
        super(demand, generation, AgentID, ChildrenList);
    }

    public parentDemand(double demand, double generation, UUID AgentID)
    {
        super(demand, generation, AgentID);
    }

    /**
     * Adds another Demand object to this one.
     * @param d
     */
    public parentDemand addDemand(parentDemand d)
    {
        this.demand 	+= d.getDemand();
        this.generation += d.getGeneration();
        this.allocation += d.getAllocation();
        return this;
    }
}