import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ArtificialBeeSolution extends Solution {

	private int nsp;
	private int nep;

	public ArtificialBeeSolution(String name, int nsp, int nep, double[][] etc) {
		super(name, etc);
		this.nsp = nsp;
		this.nep = nep;
	}
	
	public void runDataSet(int bees, boolean minmin)	{
		ExecutionTimeMeasurer.start("abc");
		List<Chromosome> population = new ArrayList<Chromosome>();
		if (minmin)
			population.add(new Chromosome(this.getETC(), Helpers.minmin(this.getETC())));
		else
			population.add(new Chromosome(this.getETC()));
		for (int i = 0; i < bees-1; i++)	{
			population.add(new Chromosome(this.getETC()));
		}
		
		Collections.sort(population);
		double startFit = population.get(0).getFitness();
		double ogFitness = population.get(0).getFitness();
		double bestFitness = population.get(0).getFitness();
		int iter = 0;
		boolean keepRunning = true;
		long end = System.currentTimeMillis() + (120*1000); // 2 min
//		while (System.currentTimeMillis() < end)	{
		while (keepRunning)	{
			
			// employed bee phase is done already because fitness is ranked
			// and can get the load of each vm by doing population.get(i).getLoad().get(j)
			// when i is cloudlet and j is vm
			
			
			// onlooker bee
			for (int i = 0; i < nsp; i++)	{
				Chromosome chrom = Helpers.mutation(population.get(0));
				population.add(chrom);
			}
			for (int i = 0; i < nep; i++)	{
				Chromosome chrom = Helpers.mutation(population.get(i % population.size()));
				population.add(chrom);
			}
			Collections.sort(population);
			bestFitness = Math.min(bestFitness, population.get(0).getFitness());
			Chromosome best = population.get(0);
			population.clear();
			population.add(best);
			
			// scout bee
			for (int i = 0; i < bees; i++)	{
				population.add(new Chromosome(this.getETC()));
			}
			Collections.sort(population);
			iter++;
			if (iter > 50)	{
				if (ogFitness / bestFitness < 1.05)	{
					keepRunning = false;
				}
				else	{
					iter = 0;
					ogFitness = bestFitness;
				}
			}
		}
		
		population.get(0).calculateLoad();
		double endFit = population.get(0).getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(population.get(0).getFitness(), ExecutionTimeMeasurer.end("abc"));
	}
}
