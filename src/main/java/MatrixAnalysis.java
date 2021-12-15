import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MatrixAnalysis {

	public static void main(String[] args) {
		ETCMatrixMaker.randomizeDataRandomU(14, 30, 0.8, 0.8, 1024, 8, "txt-files/", "HIHI_u_random", 50);
		analyzeETC("txt-files/", "HIHI_u_random", 50, 1024, 8, false);
	}
	
	public static void analyzeETC(String parentFolder, String dbType, int etcs, int cloudlets, int vms, boolean individualETCAnalysis)	{
		String filebase = parentFolder + dbType + "/etc" + dbType;
		String filename = "";
		File file;
		double[][] etc;
		double mean, stdDev, lowestValue;
		double totalMean = 0;
		double totalStdDev = 0;
		double averageLowestValue = 0;
		
		for (int x = 1; x <= etcs; x++)	{
			filename = filebase + x + ".txt";
			file = new File(filename);
			etc = getETC(file, cloudlets, vms);
			
			mean = getMean(etc);
			stdDev = getStandardDeviation(mean, etc);
			lowestValue = getLowestValue(etc);
			if (individualETCAnalysis)	{
				System.out.println("ETC " + x + " lowest value = " + lowestValue);
				System.out.println("ETC " + x + " mean    = "  + mean);
				System.out.println("ETC " + x + " std dev = "  + stdDev);
				System.out.println();
			}
			
			totalMean += mean;
			totalStdDev += stdDev;
			averageLowestValue += lowestValue;
		}
		
		totalMean /= etcs;
		totalStdDev /= etcs;
		averageLowestValue /= etcs;
		System.out.println("total mean of " + dbType + "    = " + totalMean);
		System.out.println("total std dev of " + dbType + " = " + totalStdDev);
		System.out.println("average lowest value of " + dbType + " = " + averageLowestValue);
	}
	
	private static double getLowestValue(double[][] etc)	{
		double lv = etc[0][0];
		for (int i = 0; i < etc.length; i++)	{
			for (int j = 0; j < etc[i].length; j++)	{
				lv = Math.min(lv, etc[i][j]);
			}
		}
		
		return lv;
	}
	
	private static double getMean(double[][] etc)	{
		double mean = 0;
		for (int i = 0; i < etc.length; i++)	{
			for (int j = 0; j < etc[i].length; j++)	{
				mean += etc[i][j];
			}
		}
		return mean / (etc.length * etc[0].length);
	}
	
	private static double getStandardDeviation(double mean, double[][] etc)	{
		double stdDev = 0;
		for (int i = 0; i < etc.length; i++)	{
			for (int j = 0; j < etc[i].length; j++)	{
				stdDev += Math.pow(etc[i][j]-mean, 2);
			}
		}
		stdDev /= (etc.length * etc[0].length) - 1; 
		return Math.sqrt(stdDev);
	}

	private static double[][] getETC(File txt, int cloudlets, int vms)	{
		Scanner scnr;
		double[][] etc = new double[cloudlets][vms];
		
		try	{
			scnr = new Scanner(txt);
			for(int i = 0; i < etc.length; i++)
				for(int j = 0; j < etc[i].length; j++)
					etc[i][j] = scnr.nextDouble();
			scnr.close();
		} catch(FileNotFoundException e)	{
			e.printStackTrace();
		}
		
		return etc;
	}
}
