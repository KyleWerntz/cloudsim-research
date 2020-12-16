import java.util.ArrayList;
import java.util.List;

public class Queen extends Chromosome {
	public List<Chromosome> spermatheca;
	
	public Queen(double[][] etc)	{
		super(etc);
	}

	public Queen(double[][] etc, List<Integer> genes) {
		super(etc, genes);
		spermatheca = new ArrayList<Chromosome>();
	}
	
	public Queen(Queen q)	{
		super(q);
		copySpermatheca(q);
	}
	
	public List<Chromosome> getCopyOfSpermatheca()	{
		List<Chromosome> spermathecaCopy = new ArrayList<Chromosome>();
		for (Chromosome c : spermatheca)
			spermathecaCopy.add(c);
		return spermathecaCopy;
	}
	
	public void addToSpermatheca(Chromosome chrom)	{
		spermatheca.add(chrom);
	}
	
	public void setSpermatheca(List<Chromosome> spermatheca)	{
		this.spermatheca = spermatheca;
	}
	
	public void copyQueen(Queen q)	{
		copyChromosome(q);
		this.spermatheca = q.getCopyOfSpermatheca();
	}
	
	public void copySpermatheca(Queen q)	{
		this.spermatheca = q.getCopyOfSpermatheca();
	}

}
