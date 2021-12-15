import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneticFastSolution extends Solution	{

	public GeneticFastSolution(String name, double[][] etc) {
		super(name, etc);
	}
	
	public void runDataSet(int pop, boolean minmin)	{
		ExecutionTimeMeasurer.start("genetic");
		List<Chromosome> oldGeneration = new ArrayList<>();
		if (minmin)
			oldGeneration.add(new Chromosome(getETC(), Helpers.minmin(getETC())));
		for (int i = 0; i < pop-1; i++)	{
			Chromosome chrom = new Chromosome(getETC());
			oldGeneration.add(chrom);
		}
		
		Collections.sort(oldGeneration);
		int iter = 0;
		double startFit = oldGeneration.get(0).getFitness();
		double bestFitness = oldGeneration.get(0).getFitness();
		double ogFitness = bestFitness;
		boolean keepRunning = true;
		while (keepRunning)	{
			
			List<Chromosome> newGeneration = new ArrayList<>();
			newGeneration.add(oldGeneration.get(0));
			
			newGeneration = selection(pop, newGeneration, oldGeneration);
			newGeneration = crossover(newGeneration, 0.8);
			newGeneration = mutation(newGeneration, 0.03);
			newGeneration = evaluation(newGeneration, oldGeneration);
			
			oldGeneration = newGeneration;
			iter++;
			bestFitness = oldGeneration.get(0).getFitness();
			if (iter == getMinIterations())	{
				if (ogFitness / bestFitness < getImprovementThreshold())
					keepRunning = false;
				else	{
					iter = 0;
					ogFitness = bestFitness;
				}
			}
		}
		
		oldGeneration.get(0).calculateLoad();
		double endFit = oldGeneration.get(0).getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(oldGeneration.get(0).getFitness(), ExecutionTimeMeasurer.end("genetic"));
	}
	
	private static List<Chromosome> selection(int pop, List<Chromosome> newGeneration, List<Chromosome> oldGeneration)	{
		
		for (int i = 0; i < pop; i++) {
			newGeneration.add(oldGeneration.get(selectOneChromosome(oldGeneration)));
		}
		
		return newGeneration;
	}

	private static int selectOneChromosome(List<Chromosome> population)	{
		Collections.sort(population);
		
		double sumfRanks = 0;
		for (int i = 0; i < population.size(); i++)
			sumfRanks += Helpers.FR(1.3, i+1, population.size());
		
		for (int i = 0; i < population.size(); i++) {
			double fr = Helpers.FR(1.3, i+1, population.size());
			population.get(i).setArea(fr/sumfRanks);
		}
		
		double prob = Math.random();
		int j = 0;
		boolean found = false;
		double cdf = 0; //cumulative density function
		
		while (j < population.size()-1 && !found)	{
			cdf = cdf + population.get(j).getArea();
			if (prob > cdf)	
				j++;
			else
				found = true;
		}
		return j;
	}
	
	private static List<Chromosome> crossover(List<Chromosome> newGeneration, double probCross)	{
		double prob = Math.random();
		if (prob < probCross)	{
			int loc1 = selectOneChromosome(newGeneration);
			int loc2;
			do	{
				loc2 = selectOneChromosome(newGeneration);
			} while (loc2 == loc1);
						
			Chromosome chrom1 = newGeneration.get(loc1);
			Chromosome chrom2 = newGeneration.get(loc2);
			List<Integer> chrom1Genes = chrom1.getCopyOfGenes();
			List<Integer> chrom2Genes = chrom2.getCopyOfGenes();

			int randomPoint = (int)(Math.random() * chrom1Genes.size());
			
			int temp = chrom1Genes.get(randomPoint);
			chrom1Genes.set(randomPoint, chrom2Genes.get(randomPoint));
			chrom2Genes.set(randomPoint, temp);
			
			newGeneration.add(new Chromosome(chrom1.getETC(), chrom1Genes));
			newGeneration.add(new Chromosome(chrom2.getETC(), chrom2Genes));
		}
		
		return newGeneration;
	}
	
	private static List<Chromosome> mutation(List<Chromosome> newGeneration, double probCross)	{
		double prob = Math.random();
		if (prob < probCross)	{
			Chromosome chrom = newGeneration.get(selectOneChromosome(newGeneration));
			List<Integer> genes = chrom.getCopyOfGenes();
			int randCloudlet = (int)(Math.random() * genes.size());
			int vm = genes.get(randCloudlet);
			int randVM; 
			
			do	{
				randVM = (int)(Math.random() * chrom.getNumVMs());
			} while (randVM == vm);
			
			genes.set(randCloudlet, randVM);
			newGeneration.add(new Chromosome(chrom.getETC(), genes));
		}
		
		return newGeneration;
	}
	
	private static List<Chromosome> evaluation(List<Chromosome> newGeneration, List<Chromosome> oldGeneration)	{
		for (int i = 0; i < oldGeneration.size(); i++)	{
			oldGeneration.get(i).calculateFitness();
		}
		
		for (int i = 0; i < newGeneration.size(); i++) {
			newGeneration.get(i).calculateFitness();
		}
		
		Collections.sort(oldGeneration);
		Collections.sort(newGeneration);
		newGeneration.set(newGeneration.size()-1, oldGeneration.get(0));
		
		return newGeneration;
	}
}
