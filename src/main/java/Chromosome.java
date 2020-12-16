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
		
		public Chromosome(double[][] etc)	{
			this.etc = etc;
			this.cloudlets = etc.length;
			this.vms = etc[0].length;
			genes = new ArrayList<Integer>();
			load = new ArrayList<Double>();
			generateGenes();
		}
		
		public Chromosome(double[][] etc, List<Integer> genes)	{
			this.etc = etc;
			this.cloudlets = etc.length;
			this.vms = etc[0].length;
			load = new ArrayList<Double>();
			setGenes(genes);
		}
		
		public Chromosome(Chromosome chrom)	{
			this.etc = chrom.etc;
			this.cloudlets = etc.length;
			this.vms = etc[0].length;
			copyChromosome(chrom);
			calculateProcessTime();
		}
		
		public double[][] getETC()	{
			return etc;
		}
		
		public List<Integer> getGenesForComparisonOnly()	{
			return genes;
		}
		
		public List<Double> getLoadForComparisonOnly()	{
			return load;
		}
		
		public int getNumCloudlets()	{
			return cloudlets;
		}
		
		public int getNumVMs()	{
			return vms;
		}
		
		public List<Integer> getCopyOfGenes()	{
			List<Integer> genesCopy = new ArrayList<Integer>();
			for (Integer i : genes)
				genesCopy.add(i);
			return genesCopy;
		}
		
		public List<Double> getCopyOfLoad()	{
			List<Double> loadCopy = new ArrayList<Double>();
			for (Double d : load)
				loadCopy.add(d);
			return loadCopy;
		}
		
		public double getAverageProcessTime()	{
			return avgProcessTime;
		}
		
		public double getFitness()	{
			return fitness;
		}
		
		public double getArea()	{
			return area;
		}
		
		public int getVMStatus(int vm)	{
			double vmLoad = load.get(vm);
			if (vmLoad > avgProcessTime)
				return -1;
			else if (vmLoad < avgProcessTime)
				return 1;
			return 0;
		}
		
		public Queue<Integer> getUnderLoads()	{
			Queue<Integer> underloads = new LinkedList<>();
			for (int i = 0; i < load.size(); i++) {
				if (getVMStatus(i) == 1)	{
					underloads.add(i);
				}
			}
			
			return underloads;
		}
		
		public double getGenesAt(int i)	{
			return genes.get(i);
		}
		
		public double getLoadAt(int i)	{
			return load.get(i);
		}
		
		public void setArea(double area)	{
			this.area = area;
		}
		
		public void setGenes(List<Integer> genes)	{
			this.genes = genes;
			calculateLoad();
		}
		
		public void copyChromosome(Chromosome copy)	{
			genes = copy.getCopyOfGenes();
			load = copy.getCopyOfLoad();
			fitness = copy.getFitness();
		}
		
		public void generateGenes()	{
			genes.clear();
			for (int i = 0; i < cloudlets; i++)	{
				genes.add((int)(Math.random() * vms));
			}
			calculateLoad();
		}
		
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
		public int compareTo(Chromosome other)	{
			if (fitness > other.getFitness())
				return 1;
			if (fitness < other.getFitness())
				return -1;
			return 0;
		}
		
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
		
		public List<Integer> getTasksOnVMByTime(int vm)	{
			List<SortHelp> sort = new ArrayList<SortHelp>();
			List<Integer> tasks = new ArrayList<Integer>();
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
		
		public void calcAndPrintLoad()	{
			calculateLoad();

			System.out.print("load: ");
			for (double d : load)
				System.out.print((int)d + " ");
		}
		
		/*
		 * old version; //			load.clear();
//			double totalExec;
//			
//			for (int k = 0; k < vms; k++)	{
//				totalExec = 0;
//				for (int i = 0; i < cloudlets; i++)	{
//					totalExec += etc[i][k] * decide(i, k);
//				}
//				load.add(totalExec);
//			}
		 */
		public void calculateLoad()	{
			if (load.size() > 0)
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
		
		public void calculateProcessTime()	{
			avgProcessTime = 0;
			for (double d : load)	{
				avgProcessTime += d;
			}
			avgProcessTime /= load.size();
		}
		
		public void calculateFitness()	{
			fitness = 0;
			for (double d : load)	{
				fitness = Math.max(fitness, d);
			}
		}
		
//		private Integer decide(int taskID, int VMID)	{
//			if (genes.get(taskID) == VMID)
//				return 1;
//			return 0;
//		}
		
	}