import java.util.ArrayList;
import java.util.List;

public abstract class Solution {

	private String name;
	private double bestMakespan;
	private double sumTime;
	private double sumMakespan;
	private double iter;
	private List<Double> indMakespans;
	private double avgMS;
	private double stdDev;
	private double[][] etc;
	private double sumImprovement;
	
	public Solution(String name, double[][] etc)	{
		this.name = name;
		bestMakespan = 0;
		sumTime = 0;
		sumMakespan = 0;
		iter = 0;
		stdDev = 0;
		avgMS = 0;
		sumImprovement = 0;
		indMakespans = new ArrayList<Double>();
		this.etc = etc;
	}
	
	public double[][] getETC()	{
		return this.etc;
	}
	
	public void setETC(double[][] etc)	{
		this.etc = etc;
	}
	
	public void addImprovement(double start, double end)	{
		sumImprovement += ((start - end) / start) * 100;
	}
	
	public double getPercentChange()	{
		return sumImprovement / iter;
	}
	
	public void addRun(double makespan, double time) {
		sumMakespan += makespan;
		sumTime += time;
		iter++;
		
		indMakespans.add(makespan);
		if (makespan < bestMakespan)
			bestMakespan = makespan;
	}
	
	public abstract void runDataSet(int pop, boolean minmin);
	
	public String getName()	{
		return name;
	}
	
	public double getAverageTime()	{
		return sumTime / iter;
	}
	
	public double getAverageMakespan()	{
		return sumMakespan / iter;
	}
	
	public void setAverageMS()	{
		avgMS = sumMakespan / iter;
	}
	
	public void setStdDev()	{
		double sumIndAvg = 0;
		setAverageMS();
		for (int i = 0; i < indMakespans.size(); i++)	{
			sumIndAvg += Math.pow(indMakespans.get(i)-avgMS, 2);
		}
		double a = 1.0 / (iter-1);
		stdDev = Math.sqrt(a*sumIndAvg);
	}
	
	public double getStdDev()	{
		return stdDev;
	}
	
	public double getLowerLimit()	{
		double a = 1.96 * (stdDev/Math.sqrt(iter));
		setAverageMS();
		return avgMS - a;
	}
	
	public double getUpperLimit()	{
		double a = 1.96 * (stdDev/Math.sqrt(iter));
		setAverageMS();
		return avgMS + a;
	}
	
	public String getResults()	{
		String result = "";
		setStdDev();
		
		result += name + " over " + iter + " iterations:\n";
		result += getAverageMakespan();
		result += "\n" + getAverageTime();
		result += "\n" + getLowerLimit();
		result += "\n" + getUpperLimit();
		result += "\n" + getPercentChange() + "\n";
		
		clearData();
		return result;
	}
	
	private void clearData()	{
		bestMakespan = 0;
		sumTime = 0;
		sumMakespan = 0;
		iter = 0;
		stdDev = 0;
		avgMS = 0;
		sumImprovement = 0;
		indMakespans.clear();
	}
}
