import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticleSwarmSolution extends Solution {
	private int maxIter;

	public ParticleSwarmSolution(String name, int maxIter, double[][] etc) {
		super(name, etc);
		this.maxIter = maxIter;
	}

	@Override
	public void runDataSet(int pop, boolean minmin) {
		ExecutionTimeMeasurer.start("particle");
		List<Particle> population = new ArrayList<>();
		double c1 = 2;
		double c2 = 2;
		double w = 0.65;
		
		for (int i = 0; i < pop; i++)	{
			population.add(new Particle(getETC()));
		}
		if (minmin)
			population.add(new Particle(getETC(), minmin()));
		Collections.sort(population);
		double startFit = population.get(0).getFitness();
		int[][] gBest = population.get(0).getParticle();
		for (Particle p : population)	{
			p.setGBest(gBest);
		}
		
		int iter = 0;
		while (iter < maxIter)	{
			for (Particle p : population)	{
				p.updateVelocity(w, c1, c2);
				p.updatePosition();
			}
			Collections.sort(population);
			
			gBest = population.get(0).getParticle();
			for (Particle p : population)	{
				p.setGBest(gBest);
			}

			iter++;
		}
		
		double endFit = population.get(0).getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(endFit, ExecutionTimeMeasurer.end("particle"));
	}
	
	private int[][] minmin()	{
		int[][] gene = new int[getETC().length][getETC()[0].length]; 
		double[][] etc = getETC();
		double min;
		int jMin = 0;
		for (int i = 0; i < gene.length; i++) {
			min = etc[i][0];
			jMin = 0;
			for (int j = 0; j < gene[i].length; j++) {
				if (etc[i][j] < min)	{
					min = etc[i][j];
					jMin = j;
				}
			}
			gene[i][jMin] = 1;
		}
		
		return gene;
	}
}
