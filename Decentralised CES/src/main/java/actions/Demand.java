//Object for passing demand and generation between agent and environment
package actions;

import actions.TimestampedAction;
import sun.management.Agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Demand extends TimestampedAction implements Serializable {
	
	double demand = 0;
	double generation = 0;

	double allocation = 0;
	UUID AgentID;

	ArrayList<UUID> ChildrenList;
	
	public Demand(double demand, double generation, UUID AgentID)
	{
		this.demand = demand;
		this.generation = generation;
		this.AgentID = AgentID;
	}

	public Demand(double demand, double generation,UUID AgentID, ArrayList<UUID> ChildrenList)
	{
		this.demand = demand;
		this.generation = generation;
		this.AgentID = AgentID;
		this.ChildrenList = ChildrenList;
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

	public ArrayList<UUID> getChildrenList()
	{
		return ChildrenList;
	}

	public UUID getAgentID()
	{
		return AgentID;
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
