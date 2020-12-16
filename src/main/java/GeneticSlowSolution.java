import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneticSlowSolution extends Solution {
	public GeneticSlowSolution(String name, double[][] etc) {
		super(name, etc);
	}
	
	public void runDataSet(int pop, boolean minmin)	{
		ExecutionTimeMeasurer.start("genetic");
		List<Chromosome> population = new ArrayList<Chromosome>();
		if (minmin)
			population.add(new Chromosome(getETC(), Helpers.minmin(getETC())));
		for (int i = 0; i < pop-1; i++)	{
			population.add(new Chromosome(getETC()));
		}
		
		int iter = 0;
		double bestFitness = population.get(0).getFitness();
		double startFit = population.get(0).getFitness();
		double ogFitness = bestFitness;
		boolean keepRunning = true;
		while (keepRunning)	{
			
			// crossover
			int popSize = population.size();
			for (int j = 0; j < popSize; j++)	{
				int parent1 = (int)(Math.random() * popSize);
				int parent2 = (int)(Math.random() * popSize);
				population.add(Helpers.crossover(population.get(parent1), population.get(parent2)));
			}
			
			//mutation
			double prob;
			for (int j = 0; j < population.size(); j++) {
//				System.out.println(j);
				prob = Math.random();
				if (prob <= 0.03)
					population.set(j, mutate(population.get(j)));
			}
			
			//selection
			population = selection(population);
			
			iter++;
			bestFitness = population.get(0).getFitness();
			if (iter == 50)	{
				if (ogFitness / bestFitness < 1.05)
					keepRunning = false;
				else	{
					iter = 0;
					ogFitness = bestFitness;
				}
			}
		}
		
		population.get(0).calculateLoad();
		double endFit = population.get(0).getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(population.get(0).getFitness(), ExecutionTimeMeasurer.end("genetic"));
	}
	
	private static Chromosome mutate(Chromosome chrom)	{
		int gene2;
		List<Integer> genes = chrom.getCopyOfGenes();
		for (int i = 0; i < genes.size(); i++) {
			if (Math.random() <= 0.1)	{
				// gene swap
				do	{
					gene2 = (int)(Math.random() * genes.size());
				} while (gene2 == i);
				int temp = genes.get(i);
				genes.set(i,genes.get(gene2));
				genes.set(gene2, temp);
			}
			else if (Math.random() <= 0.1)	{
				// random assignment
				gene2 = (int)(Math.random() * chrom.getNumVMs());
				genes.set(i, gene2);
			}
		}
		
		return new Chromosome(chrom.getETC(), genes);
	}
	
	private static List<Chromosome> selection(List<Chromosome> population)	{
		List<Chromosome> generation = new ArrayList<Chromosome>();
		List<Double> probs = new ArrayList<Double>();
		double sumfRanks = 0;
		Chromosome chrom;
		for (int i = 0; i < population.size(); i++) {
			chrom = population.get(i);
			chrom.calculateFitness();
		}
		
		// sort then get the sum of ranks
		Collections.sort(population);
		for (int i = 0; i < population.size(); i++)
			sumfRanks += Helpers.FR(1.3, i, population.size());
		
		// set individual f(rank) values for each chromosome
		for (int i = 0; i < population.size(); i++)	{
			if (i == 0)
				probs.add(Helpers.FR(1.3, i, population.size())/sumfRanks);
			else
				probs.add((Helpers.FR(1.3, i, population.size())/sumfRanks) + probs.get(i-1));
		}
		
		// elitism: add first
		generation.add(population.get(0));
		double wheel;
		for (int i = 0; i < 99; i++)	{
			wheel = Math.random();
			generation.add(population.get(binSearch(probs, 0, probs.size(), wheel)));
		}
		
		return generation;
	}
	
	private static int binSearch (List<Double> list, int low, int high, double val) {
	    if (high >= low) { 
	        int mid = low + (high - low) / 2; 
	        if (list.get(mid) == val) 
	            return mid; 
	        if (list.get(mid) > val) 
	            return binSearch(list, low, mid - 1, val); 
	        return binSearch(list, mid + 1, low, high); 
	    } 
	    
	    if (low < list.size() && low >= 0)
	    	return low; 
	    return -1;
	} 
}
