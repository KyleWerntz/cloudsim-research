import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Driver {
	
	private static double[][] etc;
	
	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		
		int numVMs = 8;
		int numCloudlets = 1024;
		String[] heterogenityTypes = {"LOLO", "LOHI", "HILO","HIHI"}; 
		boolean[] minminSet = {true, false}; 
		String str;
		File txt;
		etc = new double[numCloudlets][numVMs];
		final int MY_NSA = 1;
		final int PATH_RELINK = 1;
		final int VND = 2;
		final int TABU = 3;
		int bumbleCrossoverSize = 50;
		int nep = 768;
		int nsp = 192;
		List<Solution> solutions = new ArrayList<Solution>();
		double percentDone, percentDoneTotal;
		int population = 200;
		
//		solutions.add(new BumbleBeeSolution("bumble w/ PATH_RELINK NSA", bumbleCrossoverSize, PATH_RELINK, MY_NSA, etc));
//		solutions.add(new BumbleBeeSolution("bumble w/ PATH_RELINK VND", bumbleCrossoverSize, PATH_RELINK, VND, etc));
//		solutions.add(new BumbleBeeSolution("bumble w/ VND VND", bumbleCrossoverSize, VND, VND, etc));
//		solutions.add(new ArtificialBeeSolution("abc", nsp, nep, etc));
//		solutions.add(new HoneybeeSolution("honey", etc));
//		solutions.add(new GeneticFastSolution("genetic w/ little crossover and roulette wheel", etc));
//		solutions.add(new GeneticSlowSolution("genetic w/ large crossover and binary search 200 pop", etc));
		solutions.add(new RandomGenerationSolution("average random solution over 10k iterations", etc));
//		solutions.add(new ParticleSwarmSolution("pso", 50, etc));
//		solutions.add(new ShortestTimeToCompletionSolution("stcs", etc));
		solutions.add(new MinMinSolution("minmin solution", etc));
		
		boolean storeSolutions = true;
		int iter = 5;
		try {
			FileWriter print = null;
			System.out.println("starting!");
			int totalIter = heterogenityTypes.length * minminSet.length * iter;
			int currentIter = 0;
			
			LocalDateTime time = LocalDateTime.now();
			DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String timeObtained = "data obtained at " + time.format(formattedTime) + "\n\n";
			
			for (int i = 0; i < heterogenityTypes.length; i++)	{
				String fileName = "txt-files/" + heterogenityTypes[i] + "/etc" + heterogenityTypes[i];
				for (int j = 0; j < minminSet.length; j++)	{
					if (storeSolutions)	{
						String resultsFileName = "txt-files/results/" + heterogenityTypes[i] + "_";
						if (minminSet[j])
							resultsFileName += "minmin";
						else
							resultsFileName += "no_minmin";
						print = new FileWriter(resultsFileName, false);
					}
					
					for (int k = 0; k < iter; k++)	{
						str = fileName + Integer.toString(k+1) + ".txt";
						txt = new File(str);
						etc = getETC(txt, numCloudlets, numVMs);
						
						for (Solution s : solutions)	{
							s.setETC(etc);
							s.runDataSet(population, minminSet[j]);
						}
						double divisor = iter;
						currentIter++;
						percentDone = ((100.0/divisor) * (k+1));
						percentDoneTotal = ((100.0/totalIter) * currentIter);
						System.out.println(percentDone + "% done with " + heterogenityTypes[i] + " minmin=" + minminSet[j] + "\t(" + percentDoneTotal + "% total)");
					}
					
					String sol;
					if (storeSolutions)
						print.write(timeObtained);
					for (Solution s : solutions)	{
						if (storeSolutions)	{
							sol = s.getResults();
							print.write(sol);
							print.write("\n");
						} else	{
							System.out.println(s.getResults());
						}
					}
					if (storeSolutions)
						print.close();
				}
			}
			
			System.out.println("complete!");
			if (storeSolutions)
				print.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static double[][] getETC(File txt, int cloudlets, int vms)	{
		Scanner scnr;
		double[][] etc = new double[cloudlets][vms];
		
		try	{
			scnr = new Scanner(txt);
			
			for(int i = 0; i < etc.length; i++)	{
				for(int j = 0; j < etc[i].length; j++)	{
					etc[i][j] = scnr.nextDouble();
				}
			}
			
			scnr.close();
		} catch(FileNotFoundException e)	{
			System.out.println("file not found");
			e.printStackTrace();
		}
		
		return etc;
	}
	
}