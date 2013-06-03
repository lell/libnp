/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.statistics;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.analysis.UnivariateFunction;
import java.util.Map;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.sort;
import static libnp.statistics.SpecialFunctions.logsumexp;
import static libnp.util.Float.compareFloats;

public class Frequentist {

	public static double ksTest(double[] xs, UnivariateFunction cdf) {
		double D = 0.0;
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
			empirical += 1.0 / n;
			right = abs(value - empirical);

			if (left > D) {
				D = left;
			}

			if (right > D) {
				D = right;
			}

		}

		double lambda = (sqrt(n) + 0.12 + 0.11/sqrt(n))*D;
		double lambda_squared = lambda * lambda; 
		Double previous = null;
		Double pval = 0.0;
		
		int j = 0;
		while (previous == null || compareFloats(previous,pval)==0.0) {
			previous = pval;
			int sign = (j%2==0)?-1:1;
			pval += 2.0 * sign * exp(-2.0 * lambda_squared * j * j);
			j++;
		}
		
		return D;
	}

	/*
	 * Test the hypothesis that the counts c are distributed as the log density
	 * p. If p is not normalized, assumes there is a tail and that the rest of
	 * the mass is in a tail which was not observed among the counts.
	 * 
	 * returns true if the alpha value of the chi square statistic is less than
	 * the specified alpha.
	 */
	public static double chiSquareTest(int[] c, double[] p) {

		Map<Integer, Integer> counts = new HashMap();
		Map<Integer, Double> probs = new HashMap();

		assert c.length == p.length;
		for (int i = 0; i < p.length; i++) {
			counts.put(i, c[i]);
			probs.put(i, p[i]);
		}

		return chiSquareTest(counts, probs);
	}

	public static <T> double chiSquareTest(Map<T, Integer> counts0,
			Map<T, Double> probs0) {

		if (counts0.size() == 1) {
			return 1.0;
		}

		Map<T, Integer> counts = new HashMap();
		for (T key : counts0.keySet()) {
			counts.put(key, counts0.get(key));
		}

		Map<T, Double> probs = new HashMap();
		for (T key : probs0.keySet()) {
			probs.put(key, probs0.get(key));
		}

		assert probs.keySet().containsAll(counts.keySet()) : "Frequencies are not supported by supplied probability function.";

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
				cdf = logsumexp(cdf, probs.get(key));
			}
		}

		boolean tail = false;
		/*
		 * boolean tail; if (compareFloats(cdf, 0.0, 1e-8) != 0) { tail = true;
		 * } else { tail = false; }
		 */

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
			observed[support - 1] = 0;
			expected[support - 1] = (1.0 - exp(cdf)) * total;
		}

		for (int i = 0; i < expected.length; i++) {
			assert expected[i] > 0;
			//assert observed[i] > 0;
			//System.out.println(expected[i] + " " + observed[i]);
		}
		double alpha = (new ChiSquareTest()).chiSquareTest(expected, observed);
		return alpha;
	}
}
