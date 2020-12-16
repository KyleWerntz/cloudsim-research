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
		double bestFitness = 0;
		int iter = 0;
		boolean keepRunning = true;
		List<Chromosome> nspFindings = new ArrayList<Chromosome>();
		while (keepRunning)	{
			
			// employed bee phase is done already because fitness is ranked
			// and can get the load of each vm by doing population.get(i).getLoad().get(j)
			// when i is cloudlet and j is vm
			
			
			// onlooker bee
			for (int i = 0; i < nsp; i++)	{
				Chromosome chrom = Helpers.mutation(population.get(i % population.size()));
				
				//added this to speed up. OG paper doesnt include the if statement, just adds to lists
				if (chrom.getFitness() < population.get(bees-1).getFitness())	{
					nspFindings.add(chrom);
					population.add(chrom);
				}
			}
			for (int i = 0; i < nep; i++)	{
				Chromosome chrom = Helpers.mutation(nspFindings.get(i % nspFindings.size()));
//				
//				//added this to speed up. OG paper doesnt include the if statement, just adds to lists
				if (chrom.getFitness() < population.get(bees-1).getFitness())	{
					population.add(chrom);
				}
				
//				population.add(chrom);

			}
			Collections.sort(population);
			population = population.subList(0, bees);
			
			// scout bee
			for (int i = 0; i < bees; i++)	{
				population.add(new Chromosome(this.getETC()));
			}
			Collections.sort(population);
			
			iter++;
			if (iter > 50)	{
				bestFitness = population.get(0).getFitness();
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
