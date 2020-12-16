
public class SortHelp implements Comparable<SortHelp> {
	private int id;
	private double cost;
	
	public SortHelp(int id, double cost)	{
		this.id = id;
		this.cost = cost;
	}
	
	public int getID()	{
		return id;
	}
	
	public double getCost()	{
		return cost;
	}
	
	public int compareTo(SortHelp o)	{
		if (this.cost == o.getCost())
			return 0;
		if (this.cost > o.getCost())
			return 1;
		return -1;
	}
}
