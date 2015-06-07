//Object for passing demand and generation between agent and environment
package actions;

import java.util.ArrayList;
import java.util.UUID;

public class DemandProfile{

    ArrayList<Double> demand = new ArrayList<Double>();
    ArrayList<Double> generation = new ArrayList<Double>();
    ArrayList<Double> allocated_demand = new ArrayList<Double>();
    ArrayList<Double> allocated_generation = new ArrayList<Double>();

    public DemandProfile()
    {}

//    public void setProfile(int hour, double D, double G)
//    {
//        this.demand.set(hour, D);
//        this.generation.set(hour, G);
//    }

    public void addProfile(double D, double G)
    {
        this.demand.add(D);
        this.generation.add(G);
    }

    public void allocate(double D, double G)
    {
        this.allocated_demand.add(D);
        this.allocated_generation.add(G);
    }

    public double getDemandRequest(int hour)
    {
        return demand.get(hour);
    }

    public ArrayList<Double> getDemandRequest()
    {
        return demand;
    }


    public double getGenerationRequest(int hour)
    {
        return generation.get(hour);
    }

    public ArrayList<Double> getGenerationRequest()
    {
        return generation;
    }

    public int getSize()
    {
        return Math.max(Math.max(Math.max(demand.size(), generation.size()), allocated_demand.size()), allocated_generation.size());
    }



    /**
     * Adds another Demand object to this one.
     * @param d
     */
}
