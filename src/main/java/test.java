import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.math3.distribution.GammaDistribution;

public class test {
	
	private static double[][] etc;
	public static void main(String[] args) {
		
//		LocalDateTime time = LocalDateTime.now();
//		DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//		System.out.println("data obtained at " + time.format(formattedTime));
		
		String dbType = "LoLo_ETC";
		String filebase = "txt-files/" + dbType + "/etc" + dbType;
		String filename = "";
		File f;
		
		double[][] etc;
		FileWriter print;
		String fileContents = "";
		for (int i = 1; i < 50; i++)	{
			int x = i+1;
			filename = filebase + x + ".txt";
			System.out.println(filename);
			f = new File(filename);
			etc = Helpers.getETC(f, 1024, 9);
			try {
				print = new FileWriter(filename, false);
				fileContents = "";
				for (int j = 0; j < etc.length; j++)	{
					for (int k = 1; k < etc[j].length; k++)	{
						print.append(etc[j][k] + "\t");
					}
					print.append("\n");
				}
				print.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("here...");
				e.printStackTrace();
			}
		}
		System.out.println("done!");
		
		
//		System.out.println("hi?");
//		for (int i = 0; i < 50; i++)	{
//			filename = "txt-files/LoLo_ETC/etc_LOLO." + i;
//			f = new File(filename);
//			String newName = "txt-files/LoLo_ETC/etcLoLo_ETC" + (i+1) + ".txt";
//			f.renameTo(new File(newName));
//		}
//		System.out.println("done!");
		
	}
	
	
	
	
	
	
	private static String getOG()	{
		return "txt-files/ETC_HIHI100/etc_HIHI100.";
	}
	
	private static double[][] randomizeRange(double u, double vtask, double vmach, int tSize, int vSize)	{
		etc = new double[tSize][vSize];
		
//		System.out.println(vtask * Math.sqrt(3));
		
		double atask = u * (1 - (vtask * Math.sqrt(3)));
		double btask = (2 * u) - atask;

//		System.out.println(atask);
//		System.out.println(btask);
		for (int i = 0; i < tSize; i++)	{
			double q = (Math.random() * btask) + atask;
			double amach = q * (1 - (vmach * Math.sqrt(3)));
			double bmach = (2 * q) - amach;
			
//			System.out.println(q);
//			System.out.println(amach);
//			System.out.println(bmach);
			for (int j = 0; j < vSize; j++)	{
				etc[i][j] = ((Math.random() * bmach) + amach);
			}
		}
		
		return etc;
	}
	
	private static double[][] randomizeData(double u, double vtask, double vmach, int tSize, int vSize, boolean LH)	{
		
		etc = new double[tSize][vSize];
		double atask = 1.0 / (Math.pow(vtask, 2));
		double amach = 1.0 / (Math.pow(vmach, 2));
		
//		System.out.println("atask: " + atask);
//		System.out.println("amach: " + amach);
		
		if (!LH)	{
			double btask = u / atask;
//			System.out.println("btask: " + btask);
			
			
			GammaDistribution gammaT = new GammaDistribution(atask, btask);
			GammaDistribution gammaM;
			double qi, bmach;
			for (int i = 0; i < tSize; i++)	{
				do	{
					qi = gammaT.sample();
//					System.out.println(qi);
				} while (qi < 15);
//				System.out.println(i + ": " + qi);
				bmach = qi / amach;
				
//				System.out.print("qi: " + qi + "\t");
//				System.out.println("bmach: " + bmach);
				gammaM = new GammaDistribution(amach, bmach);
				for (int j = 0; j < vSize; j++) {
					etc[i][j] = gammaM.sample();
				}
			}
		} else	{
			double bmach = u / amach;
			
			GammaDistribution gammaT = new GammaDistribution(amach, bmach);
			GammaDistribution gammaM;
			double pj, btask;
			for (int i = 0; i < vSize; i++)	{
				pj = gammaT.sample();
				btask = pj / atask;
				
				gammaM = new GammaDistribution(atask, btask);
				for (int j = 0; j < tSize; j++) {
					etc[j][i] = gammaM.sample();
				}
			}
		}
		return etc;
	}
}


