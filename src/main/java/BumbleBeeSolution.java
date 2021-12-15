import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BumbleBeeSolution extends Solution {
	
	private int crossoverSize;
	private int localSearchOption;
	private int droneImprovementOption;

	public BumbleBeeSolution(String name, int crossoverSize, int localSearchOption, int droneImprovementOption, double[][] etc) {
		super(name, etc);
		this.crossoverSize = crossoverSize;
		this.localSearchOption = localSearchOption;
		this.droneImprovementOption = droneImprovementOption;
	}

	public void runDataSet(int pop, boolean minmin) {
		ExecutionTimeMeasurer.start("bumble");
		
		int maxQueens = 10;
		int maxDrones = 100;
		int spermathecaSize = 10;
		double droneByQueen = 0.4 * maxDrones;
		double droneByWorker = 0.6 * maxDrones;
		
		// Initalization
		List<Chromosome> population = new ArrayList<>();
		List<Queen> queens = new ArrayList<>();
		List<Chromosome> workers = new ArrayList<>();
		List<Chromosome> drones = new ArrayList<>();
		for (int i = 0; i < pop; i++)	{
			population.add(new Chromosome(getETC()));
		}
		Collections.sort(population);
		if (minmin)
			queens.add(new Queen(getETC(), Helpers.minmin(getETC())));
		else	{
			queens.add(new Queen(getETC(), population.get(0).getCopyOfGenes()));
			population.remove(0);
		}
		
		for (int i = 0; i < spermathecaSize; i++)	{
			queens.get(0).addToSpermatheca(population.get(i));
		}
		
		population.clear();
		double startFit = queens.get(0).getFitness();
		int iter = 0;
		Queen bestOldQueen = queens.get(0);
		double ogFitness = bestOldQueen.getFitness();
		boolean keepRunning = true;
		while (keepRunning)	{
			ExecutionTimeMeasurer.start("one_run");
				
			// crossover
			int queen = 0;
			for (int j = 0; j < crossoverSize; j++)	{
				int drone = (int)(Math.random() * spermathecaSize);
				population.add(Helpers.crossover(queens.get(queen), queens.get(queen).getCopyOfSpermatheca().get(drone)));
				queen += 1;
				queen %= queens.size();
			}
			
			// max of 5 queens, max of 45 workers
			Collections.sort(population);
			for (int i = 0; i < maxQueens; i++)	{
				queens.add(new Queen(getETC(), population.get(i).getCopyOfGenes()));
			}
			for (int i = maxQueens; i < population.size(); i++)	{
				workers.add(population.get(i));
			}
			
			int queenSize = queens.size();
			for (int i = 0; i < queenSize - maxQueens; i++)	{
				queens.remove(0);
			}
			
			List<Double> oldFitnesses = new ArrayList<>();
			for (int i = 0; i < queens.size(); i++)	{
				if (localSearchOption == 1)	{ // path relink
					oldFitnesses.add(queens.get(i).getFitness());
					queens.set(i, pathRelink(queens.get(i), bestOldQueen));
					
					int farthestWorkerLoc = 0;
					int farthestWorkerDist = 0;
					int workerDist;
					
					for (int j = 0; j < workers.size(); j++) {
						workerDist = hammingDistance(queens.get(i).getGenesForComparisonOnly(), workers.get(j).getGenesForComparisonOnly());
						if (workerDist > farthestWorkerDist)	{
							farthestWorkerDist = workerDist;
							farthestWorkerLoc = i;
						}
					}
					queens.set(i, pathRelink(queens.get(i), workers.get(farthestWorkerLoc)));
				} else if (localSearchOption == 2)	{ // VND
					Chromosome temp = new Chromosome(getETC(), queens.get(i).getCopyOfGenes());
					temp = Helpers.VND(temp);
					queens.get(i).setGenes(temp.getCopyOfGenes());
				} 
			}
			
			for (int i = 0; i < droneByQueen; i++)	{
				drones.add(Helpers.mutation(bestOldQueen));
			}
			
			for (int i = 0; i < droneByWorker; i++)	{
				int rand = (int)(Math.random() * workers.size());
				drones.add(Helpers.mutation(workers.get(rand)));
			}
			
			switch (this.droneImprovementOption) {
			case 1: // myNSA
				for (int i = 0; i < drones.size(); i++)	{
					drones.set(i, Helpers.myNSA(drones.get(i), 1000));
				}
				break;
			case 3: // tabuSearch (not recommended; brute force)
				int j = (int)(Math.random() * drones.size());
				drones.set(j, tabuSearch(drones.get(j), 3));
				j = (int)(Math.random() * drones.size());
				drones.set(j, tabuSearch(drones.get(j), 3));
				break;
			case 2: // VND
				for (int i = 0; i < drones.size(); i++)	{
					drones.set(i, Helpers.VND(drones.get(i)));
				}
				break;
			}
			
			Collections.sort(drones);

			//select drone and add to each new queen's spermatheca via algorithm 3
			queens = alg3(queens, drones, spermathecaSize);
			
			
			Collections.sort(queens);
			if (queens.get(0).getFitness() < bestOldQueen.getFitness())
				bestOldQueen = queens.get(0);
			population.clear();
			drones.clear();
			iter++;
			
			if (iter == getMinIterations())	{
				if (ogFitness / bestOldQueen.getFitness() < getImprovementThreshold())
					keepRunning = false;
				else	{
					iter = 0;
					ogFitness = bestOldQueen.getFitness();
				}
			}
		}
	
		bestOldQueen.calculateLoad();
		double endFit = bestOldQueen.getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(bestOldQueen.getFitness(), ExecutionTimeMeasurer.end("bumble"));
	}
	
	private static int hammingDistance (List<Integer> queenGenes, List<Integer> workerGenes)	{
		int dist = 0;
		for (int i = 0; i < queenGenes.size(); i++)	{
			if (queenGenes.get(i) != workerGenes.get(i))
				dist++;
		}
		
		return dist;
	}
	
	private static List<Queen> alg3 (List<Queen> queens, List<Chromosome> drones, int spermathecaSize)	{
		
		boolean even = false;
		int i = 0;
		while (i < drones.size() && queens.get(0).getCopyOfSpermatheca().size() < spermathecaSize)	{
			if (!even)	{
				for (int j = 0; j < queens.size(); j++) {
					queens.get(j).addToSpermatheca(drones.get(i));
					i++;
				}
				even = true;
			} else	{
				for(int j = queens.size()-1; j >= 0; j--)	{
					queens.get(j).addToSpermatheca(drones.get(i));
					i++;
				}
				even = false;
			}
		}
		
		return queens;
	}
	
	private static Chromosome tabuSearch(Chromosome chrom, int maxSuccesses)	{
		Chromosome temp = new Chromosome(chrom);
		Chromosome ret = new Chromosome(chrom);
		List<Integer> originalGenes;
		int successes = 0;
		
		for (int ti = 0; ti < temp.getNumCloudlets(); ti++)	{
			for (int mi = 0; mi < temp.getNumVMs(); mi++) {
				for (int tj = ti; tj < temp.getNumCloudlets(); tj++)	{
					for (int mj = 0; mj < temp.getNumVMs(); mj++)	{
						originalGenes = temp.getCopyOfGenes();
						if (ti == tj)	{
							temp.updateLoad(tj, mj);
						} else	{
							temp.updateLoad(ti, mi);
							temp.updateLoad(tj, mj);
						}	
						if (ret.getFitness() > temp.getFitness())	{
							ret.copyChromosome(temp);
							successes++;
							ti = 0; tj = 0; mi = 0; mj = 0;
						} else	{
							temp.setGenes(originalGenes);
						}
						if (successes >= maxSuccesses)	{
							ti = tj = temp.getNumCloudlets();
							mi = mj = temp.getNumVMs();
						}
					}
				}
			}
		}
		
		
		return new Chromosome(ret);
	}
	
	private static Queen pathRelink(Queen si, Chromosome st)	{
		Queen sopt = new Queen(si);
		Queen scurr = new Queen(si);
		int i = 0;
		int pos, currMachine, tMachine;
		
		List<Integer> stGenes = st.getCopyOfGenes();
		List<Integer> scurrGenes;
		while (i < stGenes.size())	{
			scurrGenes = scurr.getCopyOfGenes();
			if (scurrGenes.get(i) != stGenes.get(i))	{
				pos = i;
				currMachine = scurrGenes.get(i);
				tMachine = stGenes.get(i);
				
				boolean swapped = false;
				int j = 0;
				
				while (!swapped && j < scurrGenes.size())	{
					if (j != pos && scurrGenes.get(j) != stGenes.get(j))	{
						if (scurrGenes.get(j) == tMachine)	{
							scurr.updateLoad(j, currMachine);
							scurr.updateLoad(pos, tMachine);
							swapped = true;
						}
					}
					j++;
				}
				
				if (sopt.getFitness() > scurr.getFitness())	{
					sopt.copyQueen(scurr);
				}
			}
			i++;
		}
		
		return sopt;
	}
}
