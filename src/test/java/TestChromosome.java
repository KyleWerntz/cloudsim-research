import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestChromosome {

	@Test
	public void testDeepVSShallowCopies()	{
		double[][] etc = {{1, 2, 3, 2, 1},
		  		  {3, 2, 1, 2, 3},
		  		  {5, 4, 3, 2, 1},
		  		  {1, 2, 3, 4, 5},
		  		  {3, 3, 3, 3, 3}};
		ArrayList<Integer> genes = new ArrayList<Integer>(Arrays.asList(0,0,0,0,4));
		Chromosome chrom = new Chromosome(etc, genes);
		
		List<Double> deepLoad = chrom.getLoadForComparisonOnly();
		List<Double> shallowLoad = chrom.getCopyOfLoad();
		List<Integer> deepGenes = chrom.getGenesForComparisonOnly();
		List<Integer> shallowGenes = chrom.getCopyOfGenes();
		
		assertNotSame(deepLoad, shallowLoad);
		assertNotSame(shallowLoad, chrom.getCopyOfLoad());
		assertSame(deepLoad, chrom.getLoadForComparisonOnly());
		assertNotSame(deepGenes, shallowGenes);
		assertNotSame(shallowGenes, chrom.getCopyOfGenes());
		assertSame(deepGenes, chrom.getGenesForComparisonOnly());
	}
	
	@Test
	public void testCalculateLoadAndFitness()	{
		double[][] etc = {{1, 2, 3, 2, 1},
				  		  {3, 2, 1, 2, 3},
				  		  {5, 4, 3, 2, 1},
				  		  {1, 2, 3, 4, 5},
				  		  {3, 3, 3, 3, 3}};
		ArrayList<Integer> genes = new ArrayList<Integer>(Arrays.asList(0,0,0,0,4));
		Chromosome chrom = new Chromosome(etc, genes);
		
		List<Double> expectedLoad = new ArrayList<Double>(Arrays.asList(10.0, 0.0, 0.0, 0.0, 3.0));
		List<Double> actualLoad = chrom.getLoadForComparisonOnly();
		double expectedFitness = 10.0;
		double actualFitness = chrom.getFitness();
		
		assertTrue(listEquals(expectedLoad, actualLoad));
		assertEquals(expectedFitness, actualFitness);
	}
	
	@Test
	public void testUpdateLoad()	{
		double[][] etc = {{1, 2, 3, 2, 1},
		  		  {3, 2, 1, 2, 3},
		  		  {5, 4, 3, 2, 1},
		  		  {1, 2, 3, 4, 5},
		  		  {3, 3, 3, 3, 3}};
		ArrayList<Integer> genes = new ArrayList<Integer>(Arrays.asList(0,0,0,0,4));
		Chromosome chrom = new Chromosome(etc, genes);
		chrom.updateLoad(2, 1);
		
		List<Integer> expectedGenes = new ArrayList<Integer>(Arrays.asList(0,0,1,0,4));
		List<Integer> actualGenes = chrom.getGenesForComparisonOnly();
		List<Double> expectedLoad = new ArrayList<Double>(Arrays.asList(5.0, 4.0, 0.0, 0.0, 3.0));
		List<Double> actualLoad = chrom.getLoadForComparisonOnly();
		double expectedFitness = 5.0;
		double actualFitness = chrom.getFitness();
		
		assertTrue(listEquals(expectedGenes, actualGenes));
		assertTrue(listEquals(expectedLoad, actualLoad));
		assertEquals(expectedFitness, actualFitness);
		
		expectedLoad = new ArrayList<Double>(Arrays.asList(10.0, 0.0, 0.0, 0.0, 3.0));
		expectedFitness = 10.0;
		chrom.updateLoad(2, 0);
		actualGenes = chrom.getGenesForComparisonOnly();
		actualLoad = chrom.getLoadForComparisonOnly();
		actualFitness = chrom.getFitness();
		
		assertTrue(listEquals(genes, actualGenes));
		assertTrue(listEquals(expectedLoad, actualLoad));
		assertEquals(expectedFitness, actualFitness);
	}
	
	
	private <T> boolean listEquals(List<T> l1, List<T> l2)	{
		if (l1.size() != l2.size())
			return false;
		for(int i = 0; i < l1.size(); i++)	{
			if (!l1.get(i).equals(l2.get(i)))
				return false;
		}
		return true;
	}
}
