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
	
	public int[][] getPBest()	{
		return pBest;
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
	
	public double getFitness(int[][] p)	{
		double fit = 0;
		double colFit;
		for (int j = 0; j < particle[0].length; j++)	{
			colFit = 0;
			for (int i = 0; i < particle.length; i++)	{
				colFit += etc[i][j] * particle[i][j];
			}
			fit = Math.max(fit, colFit);
		}
		return fit;
	}
	
	public void calculateFitness()	{
		fitness = getFitness(particle);
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
	
	public void checkPBest()	{
		
	}
	
	public void updateVelocity(double w, double c1, double c2)	{
		double a, b, c, r1, r2;
		
		for (int i = 0; i < velocity.length; i++)	{
			for (int j = 0; j < velocity[i].length; j++)	{
				r1 = Math.random();
				r2 = Math.random();
				
				a = w * velocity[i][j];
				b = (pBest[i][j] - particle[i][j]) * c1 * r1;
				c = (gBest[i][j] - particle[i][j]) * c2 * r2 ;
				
				velocity[i][j] = a + b + c;
			}
		}
	}
	
	public void updatePosition()	{ 
		double max;
		int jMax = 0;
		for (int i = 0; i < particle.length; i++) {
			max = velocity[i][0];
			jMax = 0;
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
		updatePBest();
	}
	
	public void updatePBest()	{
		double pBestFit = getFitness(pBest);
		if (pBestFit > fitness)	{
			pBest = copyParticle(particle);
		}
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
	
	public void printParticle(int[][] particle)	{
		for (int i = 0; i < particle.length; i++)	{
			System.out.print(i + " ");
			for (int j = 0; j < particle[i].length; j++)	{
				System.out.print(particle[i][j] + " ");
			}
			System.out.println();
		}
	}
}
