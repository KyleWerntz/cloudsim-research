import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

public class Helpers {
	public static List<Integer> minmin(double[][] etc)	{
		List<Integer> gene = new ArrayList<Integer>(); 
		double min;
		int jMin = 0;
		for (int i = 0; i < etc.length; i++) {
			min = etc[i][0];
			for (int j = 0; j < etc[i].length; j++) {
				if (etc[i][j] < min)	{
					min = etc[i][j];
					jMin = j;
				}
			}
			gene.add(jMin);
			jMin = 0;
		}
		
		return gene;
	}
	
	public static double[][] getETC(File txt, int cloudlets, int vms)	{
		Scanner scnr;
		double[][] etc = new double[cloudlets][vms];
		
		try	{
			scnr = new Scanner(txt);
			for(int i = 0; i < etc.length; i++)
				for(int j = 0; j < etc[i].length; j++)
					etc[i][j] = scnr.nextDouble();
			scnr.close();
		} catch(FileNotFoundException e)	{
			System.out.println("file not found");
			e.printStackTrace();
		}
		return etc;
	}
	
	public static Chromosome VND(Chromosome chrom)	{
		int u = 1;
		Chromosome ret = new Chromosome(chrom);
		Chromosome temp = new Chromosome(chrom);
		int heaviestLoadLoc, currVM, newVM;
		boolean swapToCurrVM, swapToNewVM;

		while (u <= 2)	{
			heaviestLoadLoc = temp.getLargestLoadLoc();
			List<Integer> genes = temp.getCopyOfGenes();
			currVM = heaviestLoadLoc;
			do	{
				newVM = (int)(Math.random() * chrom.getNumVMs());
			} while (newVM == currVM);
			
			if (u == 1)	{
				//move task of heaviest load to a different VM
				for (int k = 0; k < genes.size(); k++) {
					if (genes.get(k) == currVM)	{
						genes.set(k, newVM);
						temp.setGenes(genes);
						k = genes.size();
					}
				}
			} else if (u == 2)	{
				//swap heaviest task of heaviest load with a task of a different VM
				swapToCurrVM = false;
				swapToNewVM = false;
				for (int k = genes.size()-1; k >= 0; k--) {
					if (swapToNewVM && swapToCurrVM)	{
						temp.setGenes(genes);
						k = 0;
					}
					else if (!swapToNewVM && genes.get(k) == currVM)	{
						genes.set(k, newVM);
						swapToNewVM = true;
					}
					else if (!swapToCurrVM && genes.get(k) == newVM)	{
						genes.set(k, currVM);
						swapToCurrVM = true;
					}
				}
			}
			temp.setGenes(genes);
			
			if (temp.getFitness() < ret.getFitness())	{
				ret.copyChromosome(temp);
				u = 1;
			} else	{
				u += 1;
			}
		}
		
		return new Chromosome(ret);
	}
	
	public static Chromosome mutation(Chromosome chrom)	{
		List<Integer> genes = chrom.getCopyOfGenes();
		int randCloudlet = (int)(Math.random() * genes.size());
		int vm = genes.get(randCloudlet);
		int randVM; 
		do	{
			randVM = (int)(Math.random() * chrom.getNumVMs());
		} while (randVM == vm);
		genes.set(randCloudlet, randVM);
		return new Chromosome(chrom.getETC(), genes);
	}
	
	public static Chromosome crossover(Chromosome c1, Chromosome c2)	{
		List<Integer> genes = new ArrayList<Integer>();
		List<Integer> c1Genes = c1.getCopyOfGenes();
		List<Integer> c2Genes = c2.getCopyOfGenes();
		double rand;
		for (int i = 0; i < c1Genes.size(); i++)	{
			rand = Math.random();
			if (rand < 0.40)
				genes.add(c1Genes.get(i));
			else
				genes.add(c2Genes.get(i));
		}
		
		return new Chromosome(c1.getETC(), genes);
	}
	
	public static double FR(double SP, double rank, double size)	{
		double w = size-rank;
		double x = size-1;
		double y = w/x;
		double z = 2 * (SP - 1);
		return 2 - SP + (y * z);
	}

	public static Chromosome myNSA(Chromosome chrom, int maxIter)	{
		Chromosome test;
		Chromosome ret = new Chromosome(chrom);
		int task, newVM, heaviestVM;
		Queue<Integer> vmsByProcessTimeQ = ret.getUnderLoads();
		List<Integer> tasksInLargestVM;
		int iter = 0;
		boolean success = false;
		
		while (!vmsByProcessTimeQ.isEmpty() && iter < maxIter)	{
			
			test = new Chromosome(ret);
			if (success) 
				vmsByProcessTimeQ = test.getUnderLoads();
			success = false;
			heaviestVM = test.getLargestLoadLoc();
			tasksInLargestVM = test.getTasksOnVMByTime(heaviestVM);
			newVM = vmsByProcessTimeQ.poll();

			for (int i = 0; i < tasksInLargestVM.size(); i++) {
				task = tasksInLargestVM.get(i);
				test.updateLoad(task, newVM);
				if (test.getFitness() < ret.getFitness())	{
					ret.updateLoad(task, newVM);
					i = tasksInLargestVM.size();
					vmsByProcessTimeQ.add(newVM);
					success = true;
				}	else	{
					test.updateLoad(task, heaviestVM);
				}
			}
			
			iter++;
		}
		return new Chromosome(ret);
	}
	
	public static void printETC(double[][] etc)	{
		DecimalFormat format = new DecimalFormat("##.##");
		for (int i = 0; i < etc.length; i++)	{
			System.out.print(i + "\t");
			for (int j = 0; j < etc[i].length; j++)	{
				System.out.print(format.format(etc[i][j]) + "\t");
			}
			System.out.println();
		}
	}
}
