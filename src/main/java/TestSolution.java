import java.util.List;

public class TestSolution extends Solution{
	private int maxIter;
	
	/**
	 * A solution to test some combination of algorithms
	 * @param name name of the solution
	 * @param maxIter max iterations to run
	 * @param etc etc to operate on
	 */
	public TestSolution(String name, int maxIter, double[][] etc) {
		super(name, etc);
		this.maxIter = maxIter;
	}

	@Override
	public void runDataSet(int pop, boolean minmin) {
		ExecutionTimeMeasurer.start("myNSA");
		Chromosome solution, chrom;
		if (minmin)
			solution = new Chromosome(getETC(), Helpers.minmin(getETC()));
		else
			solution = new Chromosome(getETC());
		solution = Helpers.myNSA(solution, maxIter);
		
		double ogFitness = solution.getFitness();
		boolean keepRunning = true;
		int iter = 0;
		while (keepRunning)	{
			chrom = new Chromosome(getETC());
			chrom = Helpers.myNSA(chrom, maxIter);
			if (solution.getFitness() > chrom.getFitness())	{
				solution.copyChromosome(chrom);
			}
			iter++;
			if (iter >= 50)	{
				if (ogFitness / solution.getFitness() < 1.05)
					keepRunning = false;
				else	{
					ogFitness = solution.getFitness();
				}
			}
		}
		
		this.addRun(solution.getFitness(), ExecutionTimeMeasurer.end("myNSA"));
	}

}
