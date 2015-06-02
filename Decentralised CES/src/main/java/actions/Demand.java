//Object for passing demand and generation between agent and environment
package actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Demand extends MasterAction implements Serializable {

    double demand = 0;
    double generation = 0;

    double allocated_demand = 0;
    double allocated_generation = 0;

    double allocation = 0;
    UUID AgentID;

    public Demand(double demand, double generation, UUID AgentID)
    {
        super(AgentID, null);
        this.demand = demand;
        this.generation = generation;
        this.AgentID = AgentID;
    }

    public Demand(double demand, double generation, UUID AgentID, ArrayList<UUID> ChildrenList)
    {
        super(AgentID, ChildrenList);
        this.demand = demand;
        this.generation = generation;
        this.AgentID = AgentID;
    }

    public void Allocate (double allocation)
    {
        this.allocation = allocation;
    }
//	@Override
//	public String toString() {
//		return "parentDemand [quantity=" + quantity + ", player=" + player.getName()
//				+ ", t=" + t + "]";
//	}

    public double getDemand()
    {
        return demand;
    }

    public void allocateDemand(parentDemand d)
    {
        this.allocated_demand = d.demand;
        this.allocated_generation = d.generation;
    }

    public double getGeneration()
    {
        return generation;
    }

    public double getAllocation()
    {
        return allocation;
    }

    /**
     * Adds another Demand object to this one.
     * @param d
     */
    public Demand addDemand(parentDemand d)
    {
        this.demand 	+= d.getDemand();
        this.generation += d.getGeneration();
        this.allocation += d.getAllocation();
        return this;
    }

}
