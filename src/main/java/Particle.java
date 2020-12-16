public class Particle implements Comparable<Particle>{

	int[][] particle, pBest, gBest;
	double[][] velocity, etc;
	double fitness;

	public Particle(double[][] etc)	{
		this.etc = etc;
		particle = new int[etc.length][etc[0].length];
		pBest = new int[etc.length][etc[0].length];
		gBest = new int[etc.length][etc[0].length];
		velocity = new double[etc.length][etc[0].length];
		initializeParticle();
		calculateFitness();
		pBest = copyParticle(particle);
	}
	
	public Particle(double[][] etc, int[][] solution)	{
		this.etc = etc;
		particle = copyParticle(solution);
		pBest = copyParticle(solution);
		gBest = new int[etc.length][etc[0].length];
		velocity = new double[etc.length][etc[0].length];
		calculateFitness();
	}
	
	public double getFitness()	{
		return fitness;
	}
	
	public void initializeParticle()	{
		int rand;
		for (int i = 0; i < particle.length; i++)	{
			rand = (int)(Math.random() * particle[0].length);
			particle[i][rand] = 1;
		}
		pBest = copyParticle(particle);
	}
	
	public int[][] getParticle()	{
		return particle;
	}
	
	public double[][] getVelocity()	{
		return velocity;
	}
	
	public void setGBest(int[][] g)	{
		gBest = copyParticle(g);
	}
	
	private int[][] copyParticle(int[][] other)	{
		int[][] ret = new int[other.length][other[0].length];
		for (int i = 0; i < ret.length; i++)	{
			for (int j = 0; j < ret[i].length; j++)	{
				ret[i][j] = other[i][j];
			}
		}
		
		return ret;
	}
	
	public void calculateFitness()	{
		fitness = 0;
		double colFit;
		for (int j = 0; j < particle[0].length; j++)	{
			colFit = 0;
			for (int i = 0; i < particle.length; i++)	{
				colFit += etc[i][j] * particle[i][j];
			}
			fitness = Math.max(fitness, colFit);
		}
	}
	
	public void getVelocityMaxes()	{
		double max = 0;
		for (int i = 0; i < velocity.length; i++)	{
			for (int j = 0; j < velocity[i].length; j++)	{
				max = Math.max(max, velocity[i][j]);
			}
			System.out.print(max + " ");
		}
		System.out.println();
	}
	
	public void updateVelocity(double w, double c1, double c2)	{
		double a, b, c, r1, r2;
//		double vMax = 50;
//		double vMin = -50;
		
		for (int i = 0; i < velocity.length; i++)	{
			for (int j = 0; j < velocity[i].length; j++)	{
				r1 = Math.random();
				r2 = Math.random();
				
				a = w * velocity[i][j];
				b = (pBest[i][j] - particle[i][j]) * c1 * r1;
				c = (gBest[i][j] - particle[i][j]) * c2 * r2 ;
				
				velocity[i][j] = a + b + c;
//				if (velocity[i][j] > vMax)
//					velocity[i][j] = vMax;
//				if (velocity[i][j] < vMin)
//					velocity[i][j] = vMin;
			}
		}
	}
	
	public void updatePosition()	{ 
		double max;
		int jMax = 0;
		for (int i = 0; i < particle.length; i++) {
			max = velocity[i][0];
			for (int j = 0; j < particle[i].length; j++) {
				particle[i][j] = 0;
				if (velocity[i][j] > max)	{
					max = velocity[i][j];
					jMax = j;
				}
			}
			particle[i][jMax] = 1;
		}
		calculateFitness();
	}
	
	public void printStats()	{
		System.out.println("genes");
		for (int i = 0; i < particle.length; i++)	{
			for (int j = 0; j < particle[i].length; j++)	{
				System.out.print(particle[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("fitness: " + this.fitness);
	}
	
	@Override
	public int compareTo(Particle other)	{
		if (fitness > other.getFitness())
			return 1;
		if (fitness < other.getFitness())
			return -1;
		return 0;
	}
}
