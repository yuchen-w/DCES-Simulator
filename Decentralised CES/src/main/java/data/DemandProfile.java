//Object for passing demand and generation between agent and environment
package data;

import java.util.UUID;

public class DemandProfile{

    double[] demand;
    double[] generation;

    double[] allocated_demand;
    double[] allocated_generation;

    int size;

    public DemandProfile(double[] demand, double[] generation)
    {
        this.demand = demand;
        this.generation = generation;
        this.size = demand.length;
    }

    public double getDemandRequest(int hour)
    {
        return demand[hour];
    }

    public double[] getDemandRequest()
    {
        return demand;
    }

    public void allocate(int hour, double allocated_demand, double allocated_generation)
    {
        this.allocated_demand[hour] = allocated_demand;
        this.allocated_generation[hour] = allocated_generation;
    }

    public double getGenerationRequest(int hour)
    {
        return generation[hour];
    }

    public double[] getGenerationRequest()
    {
        return generation;
    }

    public int getSize()
    {
        return this.size;
    }



    /**
     * Adds another Demand object to this one.
     * @param d
     */
}
