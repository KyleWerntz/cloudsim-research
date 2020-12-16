import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.math3.distribution.GammaDistribution;

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
	
	public static Chromosome VND(Chromosome chrom)	{
		int u = 1;
		Chromosome ret = new Chromosome(chrom);
		Chromosome temp = new Chromosome(chrom);
		

		while (u <= 2)	{
			int heaviestLoadLoc = temp.getLargestLoadLoc();
			List<Integer> genes = temp.getCopyOfGenes();
			int currVM = heaviestLoadLoc;
			int newVM;
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
						newVM = (int)(Math.random() * chrom.getNumVMs());
					}
				}
			} else if (u == 2)	{
				//swap heaviest task of heaviest load with a task of a different VM
				do	{
					newVM = (int)(Math.random() * chrom.getNumVMs());
				} while (newVM == currVM);
				boolean swapCurrVM = false;
				boolean swapNewVM = false;
				for (int k = genes.size()-1; k >= 0; k--) {
					if (swapNewVM && swapCurrVM)	{
						temp.setGenes(genes);
						k = 0;
					}
					else if (!swapNewVM && genes.get(k) == currVM)	{
						genes.set(k, newVM);
						swapNewVM = true;
					}
					else if (!swapCurrVM && genes.get(k) == newVM)	{
						genes.set(k, currVM);
						swapCurrVM = true;
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
	
	public static double[][] randomizeData(int cloudlets, int vms, double u, double vtask, double vmach, boolean LH)	{
		
		double[][] etc = new double[cloudlets][vms];
		double atask = 1.0 / (Math.pow(vtask, 2));
		double amach = 1.0 / (Math.pow(vmach, 2));
		
		if (!LH)	{
			double btask = u / atask;
			
			GammaDistribution gammaT = new GammaDistribution(atask, btask);
			GammaDistribution gammaM;
			double qi, bmach;
			for (int i = 0; i < cloudlets; i++)	{
				qi = gammaT.sample();
				bmach = qi / amach;
				
				gammaM = new GammaDistribution(amach, bmach);
				for (int j = 0; j < vms; j++) {
					etc[i][j] = gammaM.sample();
				}
			}
		} else	{
			double bmach = u / amach;
			
			GammaDistribution gammaT = new GammaDistribution(amach, bmach);
			GammaDistribution gammaM;
			double pj, btask;
			for (int i = 0; i < cloudlets; i++)	{
				pj = gammaT.sample();
				btask = pj / atask;
				
				gammaM = new GammaDistribution(atask, btask);
				for (int j = 0; j < vms; j++) {
					etc[j][i] = gammaM.sample();
				}
			}
		}
		
		return etc;
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
		double rand;
		List<Integer> c1Genes = c1.getCopyOfGenes();
		List<Integer> c2Genes = c2.getCopyOfGenes();
		for (int i = 0; i < c1Genes.size(); i++)	{
			rand = Math.random();
			if (rand < 0.7)
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
			// this is so good is it real? talk to almalag & alghannami ab it
			
			test = new Chromosome(ret);
			if (success) // fitness changed, therefore processtimes changed. find potential new underloaded vms
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
		for (int i = 0; i < etc.length; i++)	{
			System.out.print(i + "\t");
			for (int j = 0; j < etc[i].length; j++)	{
				System.out.print(etc[i][j] + "\t");
			}
			System.out.println();
		}
	}
}
