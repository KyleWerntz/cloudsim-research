import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Chromosome	implements Comparable<Chromosome>{
		private List<Integer> genes;
		private List<Double> load;
		private double fitness;
		private double area;
		private int cloudlets;
		private int vms;
		private double avgProcessTime;
		private double[][] etc;
		
		/**
		 * Creates a chromosome solution with random initial genes
		 * @param etc the etc to be used
		 */
		public Chromosome(double[][] etc)	{
			this.etc = etc;
			this.cloudlets = etc.length;
			this.vms = etc[0].length;
			genes = new ArrayList<>();
			load = new ArrayList<>();
			generateGenes();
		}
		
		/**
		 * Creates a chromosome with given genes
		 * @param etc the etc to be used
		 * @param genes the initial genes of this chromosome
		 */
		public Chromosome(double[][] etc, List<Integer> genes)	{
			this.etc = etc;
			this.cloudlets = etc.length;
			this.vms = etc[0].length;
			load = new ArrayList<>();
			setGenes(genes);
		}
		
		/**
		 * Creates a new deep copy of an existing chromosome
		 * @param chrom the existing chromosome to be deep copied
		 */
		public Chromosome(Chromosome chrom)	{
			this.etc = chrom.etc;
			this.cloudlets = etc.length;
			this.vms = etc[0].length;
			copyChromosome(chrom);
			calculateProcessTime();
		}
		
		/**
		 * @return the ETC
		 */
		public double[][] getETC()	{
			return etc;
		}
		
		/**
		 * returns a shallow copy of the genes. DO NOT MODIFY THIS OBJECT OR PASS IT TO OTHERS
		 * @return a shallow copy of the genes
		 */
		public List<Integer> getGenesForComparisonOnly()	{
			return genes;
		}
		
		/**
		 * returns a shallow copy of the load. DO NOT MODIFY THIS OBJECT OR PASS IT TO OTHERS
		 * @return a shallow copy of the load
		 */
		public List<Double> getLoadForComparisonOnly()	{
			return load;
		}
		
		/**
		 * @return the number of cloudlets
		 */
		public int getNumCloudlets()	{
			return cloudlets;
		}
		
		/**
		 * @return the number of vms
		 */
		public int getNumVMs()	{
			return vms;
		}
		
		/**
		 * returns a deep copy of the genes to be modified
		 * @return a deep copy of the genes
		 */
		public List<Integer> getCopyOfGenes()	{
			List<Integer> genesCopy = new ArrayList<>();
			for (Integer i : genes)
				genesCopy.add(i);
			return genesCopy;
		}
		
		/**
		 * returns a deep copy of the load to be modified
		 * @return a deep copy of the load
		 */
		public List<Double> getCopyOfLoad()	{
			List<Double> loadCopy = new ArrayList<>();
			for (Double d : load)
				loadCopy.add(d);
			return loadCopy;
		}
		
		/**
		 * @return the average process time
		 */
		public double getAverageProcessTime()	{
			return avgProcessTime;
		}
		
		/**
		 * @return the fitness (makespan) of the solution
		 */
		public double getFitness()	{
			return fitness;
		}
		
		/**
		 * @return the area of the solution
		 */
		public double getArea()	{
			return area;
		}
		 
		/**
		 * Checks if a vm is overloaded or underloaded on this solution.
		 * A vm is overloaded if its load is greater than average process time.
		 * A vm is underloaded if its load is less than the avearge process time.
		 * @param vm the vm being checked
		 * @return -1 if overloaded, 1 if underloaded, 0 if balanced
		 */
		public int getVMStatus(int vm)	{
			double vmLoad = load.get(vm);
			if (vmLoad > avgProcessTime)
				return -1;
			else if (vmLoad < avgProcessTime)
				return 1;
			return 0;
		}
		
		/**
		 * gets a queue of all vms that are underloaded for balancing
		 * @return a queue of all underloaded vms
		 */
		public Queue<Integer> getUnderLoads()	{
			Queue<Integer> underloads = new LinkedList<>();
			for (int i = 0; i < load.size(); i++) {
				if (getVMStatus(i) == 1)	{
					underloads.add(i);
				}
			}
			
			return underloads;
		}
		
		/**
		 * gets a single gene, the vm that this cloudlet is tied to
		 * @param i the cloudlet being checked
		 * @return the vm the cloudlet is tied to
		 */
		public double getGenesAt(int i)	{
			return genes.get(i);
		}
		
		/**
		 * gets the load of a single vm
		 * @param i the vm being checked
		 * @return the load of the specific vm
		 */
		public double getLoadAt(int i)	{
			return load.get(i);
		}
		
		/**
		 * sets the area of the solution
		 * @param area the area of the solution
		 */
		public void setArea(double area)	{
			this.area = area;
		}
		
		/**
		 * sets a new solution for the solution
		 * @param genes the new solution
		 */
		public void setGenes(List<Integer> genes)	{
			this.genes = genes;
			calculateLoad();
		}
		
		/**
		 * Creates a deep copy of a chromosome's solution
		 * @param copy the chromosome being copied
		 */
		public void copyChromosome(Chromosome chrom)	{
			genes = chrom.getCopyOfGenes();
			load = chrom.getCopyOfLoad();
			fitness = chrom.getFitness();
		}
		
		/**
		 * replaces the current solution with a random one
		 */
		public void generateGenes()	{
			genes.clear();
			for (int i = 0; i < cloudlets; i++)	{
				genes.add((int)(Math.random() * vms));
			}
			calculateLoad();
		}
		
		/**
		 * finds the location of the heaviest vm
		 * @return the location of the heaviest vm
		 */
		public int getLargestLoadLoc()	{
			int largestLoadLoc = 0;
			double loadSize = load.get(0);
			for (int i = 0; i < load.size(); i++) {
				if (loadSize < load.get(i))	{
					loadSize = load.get(i);
					largestLoadLoc = i;
				}
			}	
			return largestLoadLoc;
		}
		
		/**
		 * finds the average task length on a specific vm
		 * @param loc the vm being checked
		 * @return the average task length of the vm
		 */
		public double getAverageTaskLengthAt(int loc)	{
			double d = 0;
			double count = 0;
			for (int i = 0; i < genes.size(); i++) {
				if (genes.get(i) == loc)	{
					d += etc[i][loc];
					count++;
				}
			}
			
			return d/count;
		}
		
		/**
		 * prints useful stats regarding this solution like genes, load, and fitness
		 */
		public void printStats()	{
			String genes = "";
			String load = "";
			for (int i = 0; i < this.genes.size(); i++) {
				genes += this.genes.get(i) + " ";
			}
			for (int i = 0; i < this.load.size(); i++) {
				load += this.load.get(i).intValue() + " ";
			}
			
			System.out.println("genes: " + genes);
			System.out.println("load: " + load);
			System.out.println("fitness: " + this.fitness + "\n");
		}
		 
		/**
		 * prints useful stats regarding the fitness of the solution
		 */
		public void printFitnessStats()	{
			int loc = getLargestLoadLoc();
			int k = 0;
			System.out.println("*********************************************");
			System.out.println("Largest VM = " + loc);
			System.out.println("Average task length = " + getAverageTaskLengthAt(loc));
			for (int i = 0; i < genes.size(); i++) {
				if (genes.get(i) == loc)	{
					System.out.print(i + ":\t" + etc[i][loc] + "\t\t");
					if (k == 5)
						System.out.println();
					k = (k+1) % 6;
				}
			}
				
			System.out.println("\n");
		}
		
		@Override
		/**
		 * chromosome objects are compared using their solution. lower fitness is desireable
		 */
		public int compareTo(Chromosome other)	{
			if (fitness > other.getFitness())
				return 1;
			if (fitness < other.getFitness())
				return -1;
			return 0;
		}
		
		/**
		 * updates the load when swapping genes, which is faster than recaculating the load every time
		 * @param taskID the task being swapped
		 * @param newVM the new vm it is being swapped to
		 */
		public void updateLoad(int taskID, int newVM)	{
			int oldVM = genes.get(taskID);
			genes.set(taskID, newVM);
			double oldLoad = load.get(oldVM) - etc[taskID][oldVM];
			load.set(oldVM, oldLoad);
			oldLoad = load.get(newVM) + etc[taskID][newVM];
			load.set(newVM, oldLoad);
			calculateFitness();
			calculateProcessTime();
		}
		
		/**
		 * returns a list of ids of cloudlets on a certain vm
		 * @param vm the vm being checked
		 * @return a list of cloudlet ids pertaining to the cloudlets on the vm
		 */
		public List<Integer> getTasksOnVMByTime(int vm)	{
			List<SortHelp> sort = new ArrayList<>();
			List<Integer> tasks = new ArrayList<>();
			for (int i = 0; i < genes.size(); i++)	{
				if (genes.get(i) == vm)
					sort.add(new SortHelp(i, etc[i][vm]));
			}
			Collections.sort(sort);
			Collections.reverse(sort);
			for (SortHelp s : sort)	{
				tasks.add(s.getID());
			}
			return tasks;
		}
		
		/**
		 * calculates load, then prints it
		 */
		public void calcAndPrintLoad()	{
			calculateLoad();

			System.out.print("load: ");
			for (double d : load)
				System.out.print((int)d + " ");
		}
		
		/**
		 * recalculates the load of each vm
		 */
		public void calculateLoad()	{
			if (!load.isEmpty())
				for (int i = 0; i < vms; i++)
					load.set(i, 0.0);
			else 
				for (int i = 0; i < vms; i++)
					load.add(0.0);
			
			int loc;
			for (int i = 0; i < genes.size(); i++)	{
				loc = genes.get(i);
				load.set(loc, load.get(loc) + etc[i][loc]);
			}
			
			calculateFitness();
			calculateProcessTime();
		}
		
		/**
		 * calculates the average process time, or the average of all the loads
		 */
		public void calculateProcessTime()	{
			avgProcessTime = 0;
			for (double d : load)	{
				avgProcessTime += d;
			}
			avgProcessTime /= load.size();
		}
		
		/**
		 * calculates the fitness of the solution, which is the largest makespan
		 */
		public void calculateFitness()	{
			fitness = 0;
			for (double d : load)	{
				fitness = Math.max(fitness, d);
			}
		}
		
	}