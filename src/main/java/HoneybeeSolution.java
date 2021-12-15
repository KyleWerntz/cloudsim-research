import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HoneybeeSolution extends Solution {
	public HoneybeeSolution(String name, double[][] etc) {
		super(name, etc);
	}
	
	public void runDataSet(int spermathecaS, boolean minmin)	{
		ExecutionTimeMeasurer.start("honeybee");
		int spermathecaSize = spermathecaS;
		List<Chromosome> spermatheca = new ArrayList<>();
		int currSperm = 0;
		double alpha = 0.9;
		double beta;

		Chromosome queen;
		if (minmin)
			queen = new Chromosome(getETC(), Helpers.minmin(getETC()));	
		else
			queen = new Chromosome(getETC());
		
		queen = Helpers.VND(queen);
		
		int iter = 0;
		double startFit = queen.getFitness();
		double ogFitness = queen.getFitness();
		double bestFitness = queen.getFitness();
		boolean keepRunning = true;
		while (keepRunning)	{
			
			beta = Math.random();
			Chromosome rand = new Chromosome(getETC());
			double sMax = (queen.getFitness() - rand.getFitness()) / Math.log(beta);
			double sMin = (queen.getFitness() - rand.getFitness()) / Math.log(0.05);
			double queenSpeed = sMax;

			while (queenSpeed > sMin && currSperm < spermathecaSize)	{
				Chromosome drone = new Chromosome(getETC());
				
				double prob = Math.random();
				double probQueen = probQueenMate(queen.getFitness(), drone.getFitness(), queenSpeed);
				if(prob < probQueen)	{
					spermatheca.add(drone);
					currSperm++;
				}
				queenSpeed = updateSpeed(queenSpeed, alpha);
			} 
			
			List<Chromosome> broods = new ArrayList<>();
			for (int i = 0; i < spermatheca.size(); i++)	{
				Chromosome brood = new Chromosome(getETC(), generateBrood(queen.getGenesForComparisonOnly(), spermatheca.get(i).getCopyOfGenes()));
				brood = Helpers.VND(brood);
				broods.add(brood);
			}

			Collections.sort(broods);
			if (!broods.isEmpty() && broods.get(0).getFitness() < queen.getFitness())	{
				queen.setGenes(broods.get(0).getCopyOfGenes());
			}
			
			broods.clear();
			currSperm = 0;
			spermatheca.clear();
			iter++;
			bestFitness = queen.getFitness();
			if (iter == 50)	{
				if (ogFitness / bestFitness < 1.05)
					keepRunning = false;
				else	{
					iter = 0;
					ogFitness = bestFitness;
				}
			}
		}
		
		queen.calculateLoad();
		double endFit = queen.getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(queen.getFitness(), ExecutionTimeMeasurer.end("honeybee"));
	}
	
	private static Double probQueenMate(double queenFitness, double droneFitness, double queenSpeed)	{
		double fitDifference = Math.abs(queenFitness - droneFitness);
		return Math.pow(Math.E, -fitDifference/queenSpeed);
	}
	
	private static Double updateSpeed(double currSpeed, double alpha)	{
		return currSpeed * alpha;
	}
	
	private static List<Integer> generateBrood(List<Integer> queen, List<Integer> drone)	{
		
		List<Integer> brood = new ArrayList<>();
		boolean[] marker = new boolean[queen.size()];
		int loc;
		for (int i = 0; i < marker.length/2; i++)	{
			do	{
				loc = (int) (Math.random() * marker.length);
			} while (marker[loc]);
			marker[loc] = true;
		}
		
		for (int i = 0; i < queen.size(); i++) {
			if (marker[i]) {
				brood.add(drone.get(i));
			} else	{
				brood.add(queen.get(i));
			}
		}
		
		return brood;
	}
}
