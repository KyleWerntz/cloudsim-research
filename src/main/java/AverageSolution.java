public class AverageSolution extends Solution {
	public AverageSolution(String name, double[][] etc) {
		super(name, etc);
	}
	
	public void runDataSet(int iterations, boolean minmin)	{
		ExecutionTimeMeasurer.start("rand");
		Chromosome chrom = new Chromosome(getETC());
		double avgFit = 0;
		
		for (int i = 0; i < iterations; i++)	{
			chrom.generateGenes();
			avgFit += chrom.getFitness();
		}

		avgFit /= iterations;
		this.addRun(avgFit, ExecutionTimeMeasurer.end("rand"));
	}
}
