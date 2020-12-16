import java.util.ArrayList;
import java.util.List;

public class ShortestTimeToCompletionSolution extends Solution{
	public ShortestTimeToCompletionSolution(String name, double[][] etc) {
		super(name, etc);
	}

	@Override
	public void runDataSet(int pop, boolean minmin) {
		convertETC();
//		Helpers.printETC(this.getETC());
		
		ExecutionTimeMeasurer.start("sttc");
		Chromosome solution = new Chromosome(getETC());
		List<Integer> genes = new ArrayList<>();
		double[] vmTimes = new double[getETC()[0].length];
		int vm;
//		Helpers.printETC(getETC());
		for (int i = 0; i < solution.getNumCloudlets(); i++)	{
			vm = chooseVM(vmTimes);
			genes.add(i, vm);
			vmTimes[vm] += getETC()[i][vm];
		}
		
		solution.setGenes(genes);
		this.addRun(solution.getFitness(), ExecutionTimeMeasurer.end("sttc"));
	}
	
	private void convertETC()	{
		double[][] newETC = new double[this.getETC().length][this.getETC()[0].length];
		double[][] oldETC = this.getETC();
		double min, curr;
			
		for(int i = 0; i < newETC.length; i++)	{
			min = Integer.MAX_VALUE;
			for(int j = 0; j < newETC[i].length; j++)	{
				curr = oldETC[i][j];
				min = Math.min(min, curr);
			}
			for (int j = 0; j < newETC[i].length; j++)
				newETC[i][j] = min;
		}
		
		this.setETC(newETC);
	}
	
	private static int chooseVM(double[] vms)	{
		int ans = 0;
		for (int i = 0; i < vms.length; i++)	{
			if (vms[i] < vms[ans])
				ans = i;
		}
		
		return ans;
	}
}
