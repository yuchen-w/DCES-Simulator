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

    double curtailmentFactor = 1;

    int productivity = 0;
    int social_utility = 0;

    int hour = 0;

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

    public void setProductivity(Integer i)
    {
        this.productivity = i;
    }

    public void setSocial_utility(int social_utility) {
        this.social_utility = social_utility;
    }

    public int getProductivity()
    {
        return this.productivity;
    }

    public int getSocial_utility()
    {
        return this.social_utility;
    }

    public void setHour(int t)
    {
        this.hour = t;
    }

    public int getHour()
    {
        return hour;
    }

    public void allocate(double allocated_demand, double allocated_generation)
    {
        this.allocated_demand = allocated_demand;
        this.allocated_generation = allocated_generation;
    }

    /**
     * Curtails allocated generation from requested generation
     * @param factor
     */
    public void curtail(double factor)
    {
        this.allocated_generation = this.generation*factor;
        this.curtailmentFactor = factor;
    }

    public double getCurtailmentFactor()
    {
        return this.curtailmentFactor;
    }

	@Override
	public String toString() {
		return "parentDemand [quantity=" + getDemandRequest() + ", player=" + this.getAgentID()
				+ ", t=" + t + "]";
	}

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
        this.social_utility += d.getSocial_utility();
        this.productivity += d.getProductivity();
        return this;
    }

}
