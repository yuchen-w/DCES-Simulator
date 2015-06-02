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

    public void allocate (double allocated_demand, double allocated_generation)
    {
        this.allocated_demand = allocated_demand;
        this.allocated_generation = allocated_generation;
    }
//	@Override
//	public String toString() {
//		return "parentDemand [quantity=" + quantity + ", player=" + player.getName()
//				+ ", t=" + t + "]";
//	}

    public double getDemandRequest()
    {
        return demand;
    }

    public void allocateDemandObj(Demand d)
    {
        this.allocated_demand = d.demand;
        this.allocated_generation = d.generation;
    }

    public double getGenerationRequest()
    {
        return generation;
    }

    public double getAllocationD()
    {
        return allocated_demand;
    }
    public double getAllocationG()
    {
        return allocated_generation;
    }

    /**
     * Adds another Demand object to this one.
     * @param d
     */
    public Demand addDemand(Demand d)
    {
        this.demand 	+= d.getDemandRequest();
        this.generation += d.getGenerationRequest();
        this.allocated_demand += d.getAllocationD();
        this.allocated_generation += d.getAllocationG();
        return this;
    }

}