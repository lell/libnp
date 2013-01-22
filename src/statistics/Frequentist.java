package statistics;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.analysis.UnivariateFunction;
import static statistics.SpecialFunctions.logsumexp;
import static util.Float.compareFloats;
import java.util.Map;
import static java.lang.Math.exp;
import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.sort;

public class Frequentist {
	
	public static double ksTest(double[] xs, UnivariateFunction cdf) {
		double max = 0.0;
		sort(xs);
		int n = xs.length;
		assert n > 0;
		double empirical = 0.0;
		for (Double x : xs) {
			double value;
			double left;
			double right;
			value = cdf.value(x);
			assert compareFloats(value, 0.0) >= 0 : value + " was < 0.";
			assert compareFloats(value, 1.0) <= 0 : value + " was > 1.";
			assert !Double.isInfinite(x);
			assert !Double.isNaN(x);

			left = abs(value - empirical);
			empirical += 1.0/n;
			right = abs(value - empirical);

			if (left > max) 
				max = left;
			
			if (right > max)
				max = right;
			
		}
		
		return max;
	}
	
	/* Test the hypothesis that the counts c are
	 *  distributed as the log density p. If p is 
	 *  not normalized, assumes there is a tail
     *  and that the rest of the mass is in a tail
	 *  which was not observed among the counts.
	 *  
	 *  returns true if the alpha value of the chi square
	 *  statistic is less than the specified alpha.
	 */
	
	public static double chiSquareTest(
			int[] c,
			double[] p) {
		
		Map<Integer, Integer> counts = new HashMap();
		Map<Integer, Double> probs = new HashMap();
		
		assert c.length == p.length;
		for (int i = 0; i < p.length; i++) {
			counts.put(i, c[i]);
			probs.put(i, p[i]);
		}
		
		return chiSquareTest(counts, probs);
	}
		
	public static <T> double chiSquareTest(
			Map<T, Integer> counts,
			Map<T, Double> probs) {
		
		assert probs.keySet().containsAll(counts.keySet()) : 
			"Frequencies are not supported by supplied probability function.";
	
		Set<T> domain = new HashSet(probs.keySet());
		for (T dom : domain) {
			if (exp(probs.get(dom)) < 1e-12) {
				counts.remove(dom);
				probs.remove(dom);
			}
		}
		
		int total = 0;
		for (T key : counts.keySet()) {
			total += counts.get(key);
		}
		
		Double cdf = null;
		for (T key : counts.keySet()) {
			if (cdf == null) {
				cdf = probs.get(key);
			} else {
				cdf = logsumexp(cdf,
						probs.get(key));
			}
		}
		
		boolean tail;
		if (compareFloats(cdf, 0.0, 1e-13) != 0) {
			tail = true;
		} else {
			tail = false;
		}
	
		final int support = counts.size() + (tail ? 1 : 0);
		double[] expected = new double[support];
		long[] observed = new long[support];
		
		int index = 0;
		for (T key : counts.keySet()) {
			observed[index] = counts.get(key);
			expected[index] = exp(probs.get(key)) * total;
			index++;
		}
		
		if (tail) {
			observed[support-1] = 0;
			expected[support-1] = (1.0 - exp(cdf))*total;
		}
		
		for (int i = 0; i < expected.length; i++) {
			if (expected[i] == -0) {
				expected[i] = 0;
			}
		}
		double alpha = (new ChiSquareTest()).chiSquareTest(expected, observed);
		
		return alpha;
	}
}
