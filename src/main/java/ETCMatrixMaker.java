import org.apache.commons.math3.distribution.GammaDistribution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class ETCMatrixMaker {
	final static double LOW = 0.1;
	final static double HIGH = 0.9;

	public static void main(String[] args) {
		randomizeData(30, 0.2, 0.2, 1024, 8, "txt-files/", "HiHi_u_14", 50);
	}
	
	public static void randomizeDataRandomU(double uLow, double uHigh, double vtask, double vmach, int tSize, int vSize, String filefolder, String dbType, int numETCs)	{
		double atask, amach, btask, qi, bmach, value;
		double[][] etc = new double[tSize][vSize];
		atask = 1.0 / (Math.pow(vtask, 2));
		amach = 1.0 / (Math.pow(vmach, 2));
		DecimalFormat format = new DecimalFormat("##.##");
		String fileContents = "";
		String filename = "";
		int u;
		GammaDistribution gammaT, gammaM;
		FileWriter writer;
		File folder = new File(filefolder + dbType);
		folder.mkdir();
		
		for (int x = 0; x < numETCs; x++)	{
			for (int i = 0; i < tSize; i++)	{
				u = (int)(Math.random() * (uHigh - uLow) + uLow);
				btask = u / atask;
				gammaT = new GammaDistribution(atask, btask);
				do	{
					qi = gammaT.sample();
				} while (qi < 15);
				bmach = qi / amach;
				
				gammaM = new GammaDistribution(amach, bmach);
				for (int j = 0; j < vSize; j++)	{
					value = gammaM.sample();
					if (value < 1)
						value = 1.0;
					etc[i][j] = value;
				}
			}
			
			if (filefolder.equals(""))
				Helpers.printETC(etc);
			else	{
				try {
					filename = filefolder + dbType + "/etc" + dbType + (x+1) + ".txt"; 
					writer = new FileWriter(filename, false);
					fileContents = "";
					for (int i = 0; i < etc.length; i++)	{
						for (int j = 0; j < etc[i].length; j++)	{
							fileContents += format.format(etc[i][j]) + "\t";
						}
						fileContents += "\n";
					}
					writer.write(fileContents);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("done");
		
	}

	public static void randomizeData(double u, double vtask, double vmach, int tSize, int vSize, String filefolder, String dbType, int numETCs)	{
		double atask, amach, btask, qi, bmach, value;
		double[][] etc = new double[tSize][vSize];
		atask = 1.0 / (Math.pow(vtask, 2));
		amach = 1.0 / (Math.pow(vmach, 2));
		btask = u / atask;
		DecimalFormat format = new DecimalFormat("##.##");
		String fileContents = "";
		String filename = "";
		GammaDistribution gammaT = new GammaDistribution(atask, btask);
		GammaDistribution gammaM;
		FileWriter writer;
		File folder = new File(filefolder + dbType);
		folder.mkdir();
		
		for (int x = 0; x < numETCs; x++)	{
			for (int i = 0; i < tSize; i++)	{
				do	{
					qi = gammaT.sample();
				} while (qi < 15);
				bmach = qi / amach;
				
				gammaM = new GammaDistribution(amach, bmach);
				for (int j = 0; j < vSize; j++)	{
					value = gammaM.sample();
					if (value < 1)
						value = 1.0;
					etc[i][j] = value;
				}
			}
			
			if (filefolder.equals(""))
				Helpers.printETC(etc);
			else	{
				try {
					filename = filefolder + dbType + "/etc" + dbType + (x+1) + ".txt"; 
					writer = new FileWriter(filename, false);
					fileContents = "";
					for (int i = 0; i < etc.length; i++)	{
						for (int j = 0; j < etc[i].length; j++)	{
							fileContents += format.format(etc[i][j]) + "\t";
						}
						fileContents += "\n";
					}
					writer.write(fileContents);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("done");
		
	}
}
