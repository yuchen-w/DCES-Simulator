//Object for passing demand and generation between agent and environment
package actions;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Demand extends MasterAction implements Serializable {

    final private Logger logger = Logger.getLogger(Demand.class);

    UUID AgentID;

    int hour = 0;

    double demand = 0;
    double generation = 0;

    double allocated_demand = 0;
    double allocated_generation = 0;

    double curtailmentFactor = 1;

    int productivity = 0;
    int social_utility = 0;

    HashMap<String, Integer> CanonRank = new HashMap<String, Integer>();
    int CanonEqualityRank;
    int CanonNeedsRank;
    int CanonProductivityRank;
    int CanonSocialUtilityRank;
    int CanonSupplyAndDemandRank;


    int CanonEqualityWeight = 0;
    int CanonNeedsWeight = 0;
    int CanonProductivityWeight = 0;
    int CanonSocialUtilityWeight = 0;
    int CanonSupplyAndDemandWeight = 0;


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

    public int getCanonEqualityWeight() {
        return CanonEqualityWeight;
    }

    public void setCanonEqualityWeight(int canonEqualityWeight) {
        CanonEqualityWeight = canonEqualityWeight;
    }

    public int getCanonNeedsWeight() {
        return CanonNeedsWeight;
    }

    public void setCanonNeedsWeight(int canonNeedsWeight) {
        CanonNeedsWeight = canonNeedsWeight;
    }

    public int getCanonProductivityWeight() {
        return CanonProductivityWeight;
    }

    public void setCanonProductivityWeight(int canonProductivityWeight) {
        CanonProductivityWeight = canonProductivityWeight;
    }

    public int getCanonSocialUtilityWeight() {
        return CanonSocialUtilityWeight;
    }

    public void setCanonSocialUtilityWeight(int canonSocialUtilityWeight) {
        CanonSocialUtilityWeight = canonSocialUtilityWeight;
    }

    public int getCanonSupplyAndDemandWeight() {
        return CanonSupplyAndDemandWeight;
    }

    public void setCanonSupplyAndDemandWeight(int canonSupplyAndDemandWeight) {
        CanonSupplyAndDemandWeight = canonSupplyAndDemandWeight;
    }

    public void setCanonRank(String Canon, int Rank)
    {
        this.CanonRank.put(Canon, Rank);
    }

    public HashMap<String, Integer> getCanonRank()
    {
        return CanonRank;
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

    @Deprecated
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

        this.CanonEqualityWeight += d.getCanonEqualityWeight();
        this.CanonNeedsWeight += d.getCanonNeedsWeight();
        this.CanonProductivityWeight += d.getCanonProductivityWeight();
        this.CanonSocialUtilityWeight += d.getCanonSocialUtilityWeight();
        this.CanonSupplyAndDemandWeight += d.getCanonSupplyAndDemandWeight();

        return this;
    }


    public void setCanonEqualityRank(int rank)
    {
        this.CanonEqualityRank = rank;
    }

    public void setCanonNeedsRank(int rank)
    {
        this.CanonNeedsRank = rank;
    }

    public void setCanonProductivityRank(int rank)
    {
        this.CanonProductivityRank = rank;
    }

    public void setCanonSocialUtilityRank(int rank)
    {
        this.CanonSocialUtilityRank = rank;
    }

    public void setCanonSupplyAndDemandRank(int rank)
    {
        this.CanonSupplyAndDemandRank = rank;
    }

    public int getCanonEqualityRank()
    {
        return this.CanonEqualityRank;
    }

    public int getCanonNeedsRank()
    {
        return this.CanonNeedsRank;
    }

    public int getCanonProductivityRank()
    {
        return this.CanonProductivityRank;
    }

    public int getCanonSocialUtilityRank()
    {
        return this.CanonSocialUtilityRank;
    }

    public int getCanonSupplyAndDemandRank()
    {
        return this.CanonSupplyAndDemandRank;
    }

    public int getTotalCanonWeight()
    {
        //logger.info("getTotalCanonWeight: " + CanonNeedsWeight+CanonEqualityWeight+CanonProductivityWeight+CanonSocialUtilityWeight+CanonSupplyAndDemandWeight);
        return CanonNeedsWeight+CanonEqualityWeight+CanonProductivityWeight+CanonSocialUtilityWeight+CanonSupplyAndDemandWeight;
    }




}
