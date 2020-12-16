
public class MinMinSolution extends Solution {
	public MinMinSolution(String name, double[][] etc) {
		super(name, etc);
	}
	
	public void runDataSet(int size, boolean minmin)	{
		ExecutionTimeMeasurer.start("minmin");
		double[][] etc = this.getETC();
		Chromosome s = new Chromosome(etc, Helpers.minmin(etc));
		this.addRun(s.getFitness(), ExecutionTimeMeasurer.end("minmin"));
	}
}
