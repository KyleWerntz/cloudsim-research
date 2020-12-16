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
		List<Particle> population = new ArrayList<Particle>();
		double c1 = 1;
		double c2 = 100;
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
		
		population.get(0).calculateFitness();
		double endFit = population.get(0).getFitness();
		this.addImprovement(startFit, endFit);
		this.addRun(population.get(0).getFitness(), ExecutionTimeMeasurer.end("particle"));
	}
	
	private int[][] minmin()	{
		int[][] gene = new int[getETC().length][getETC()[0].length]; 
		double min;
		int jMin = 0;
		for (int i = 0; i < gene.length; i++) {
			min = getETC()[i][0];
			for (int j = 0; j < gene[i].length; j++) {
				if (getETC()[i][j] < min)	{
					min = getETC()[i][j];
					jMin = j;
				}
			}
			gene[i][jMin] = 1;
		}
		
		return gene;
	}
}
