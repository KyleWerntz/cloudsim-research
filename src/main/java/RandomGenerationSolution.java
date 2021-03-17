public class RandomGenerationSolution extends Solution {
	public RandomGenerationSolution(String name, double[][] etc) {
		super(name, etc);
	}
	
	public void runDataSet(int iterations, boolean minmin)	{
		ExecutionTimeMeasurer.start("rand");
		Chromosome sol = new Chromosome(getETC());
		Chromosome chrom = new Chromosome(getETC());
		double startFit = 0, endFit;
		
		for (int i = 0; i < iterations; i++)	{
			if (sol.getFitness() > chrom.getFitness())
				sol.setGenes(chrom.getCopyOfGenes());
			if (i == 0)
				startFit = sol.getFitness();
		}
		
		endFit = sol.getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(sol.getFitness(), ExecutionTimeMeasurer.end("rand"));
	}
}
