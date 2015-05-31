//Object for passing demand and generation between agent and environment
package actions;

import actions.TimestampedAction;

import java.io.Serializable;

public class Demand extends TimestampedAction implements Serializable {
	
	double demand = 0;
	double generation = 0;

	double allocation = 0;
	int ChildrenNum;
	
	public Demand(double demand, double generation)
	{
		this.demand = demand;
		this.generation = generation;
	}

	public Demand(double demand, double generation, int ChildrenNum)
	{
		this.demand = demand;
		this.generation = generation;
		this.ChildrenNum = ChildrenNum;
	}
	
	public void Allocate (double allocation)
	{
		this.allocation = allocation;
	}
//	@Override
//	public String toString() {
//		return "Demand [quantity=" + quantity + ", player=" + player.getName()
//				+ ", t=" + t + "]";
//	}

	public double getDemand() 
	{
		return demand;
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
	public Demand addDemand(Demand d)
	{
		this.demand 	+= d.getDemand();
		this.generation += d.getGeneration();
		this.allocation += d.getAllocation();
		return this;
	}

}
