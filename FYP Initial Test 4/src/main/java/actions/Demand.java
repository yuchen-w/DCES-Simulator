package actions;

public class Demand extends TimestampedAction { 
	
	double demand = 0;
	double generation = 0;
	
	public Demand(double demand, double generation)
	{
		this.demand = demand;
		this.generation = generation;
	}
	
//	@Override
//	public String toString() {
//		return "Demand [quantity=" + quantity + ", player=" + player.getName()
//				+ ", t=" + t + "]";
//	}

	public double getDemand() {
		return demand;
	}
	
	public double getGeneration(){
		return generation;
	}

}
