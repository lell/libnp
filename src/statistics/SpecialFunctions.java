/*
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 * All rights reserved.
 */

package statistics;

import static java.lang.Math.log;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static util.Float.compareFloats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.analysis.UnivariateFunction;

import util.Pair;

public final class SpecialFunctions {
	
	// Logarithm of gamma function http://www.cs.princeton.edu/introcs/91float/
	public static double logGamma(double x) {
		if ( x==0.0 ) return Double.POSITIVE_INFINITY;
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
				+ 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
				+  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
		return tmp + log(ser * sqrt(2 * PI));
	}
	
	public static double[] crp_sizes (
			double alpha,
			int n) {
		
		return crp_sizes(alpha, 0.0, n);
	}
	/*  Compute Pr(|pi|) = K, for pi ~ CRP(n, alpha, 0).
	 *  From X. H. Chen, A. P. Dempster, J. S. Liu 1994,
	 *  Weighted finite population sampling to maximize entropy.
	 *  Biometrika 81(3)
	 *  returns array x[] of length n such that x[K-1] = Pr(|pi|) = K
	 */
	public static double[] crp_sizes (
			double alpha,
			double d,
			int n) {
		
		if (n == 0) {
			return new double[]{};
		} else if (n == 1) {
			return new double[]{1.0};
		}
		double[] pdf = new double[n];
		double[] T = new double[n-1];
		for (int i = 1; i < n; i++) {
			T[i-1] = 0.0;
			for (int j = 1; j < n; j++)
				T[i-1] += pow(alpha / j, i);
		}
		
		pdf[0] = 1.0;
		for (int i = 1; i < n; i++) {
			pdf[0] *= 1.0 - alpha/(i+alpha);
		}
		for (int k = 1; k < n; k++) {
			double p = 0.0;
			int sign = 1;
			for (int i = 1; i <= k; i++) {
				p += sign * pdf[k - i] * T[i-1];
				sign *= -1;
			}
			pdf[k] = p/k;
		}

		for (int i = 1; i < n; i++) {
			if (Double.isNaN(pdf[i]) || pdf[i] < 0.0) {
				pdf[i] = 0.0;
			}
		}
		return pdf;
	}

	public static double logsumexp(double x, double y) {
		if (x<y) return y+log(exp(x-y)+1.0);
		else return x+log(exp(y-x)+1.0);
	}
	
	public static double logsumexp(Collection<Double> cs) {
		double[] xs = new double[cs.size()];
		int i = 0;
		for (Double x : cs) {
			xs[i++] = x;
		}
		return logsumexp(xs);
	}
	
	public static double logsumexp(double []xs) {
		assert xs.length > 0;
		double m = xs[0];
		int index = 0;
		for (int i = 1; i < xs.length; i++) {
			if (xs[i] > m) {
				m = xs[i];
				index = i;
			}
		}
		
		double y = 1.0;
		for (int i = 0; i < xs.length; i++) {
			if (i != index) {
				y += exp(xs[i] - m);
			}
		}
		double result = log(y) + m;
		return result;
	}
	public static void logNormalize(double []xs) {
		double z = logsumexp(xs);
		for (int i = 0; i < xs.length; i++) {
			xs[i] -= z;
		}
	}
	
	public static <T> void logNormalize(Map<T, Double> xs) {
		double z = logsumexp(xs.values());
		for (T x : xs.keySet()) {
			xs.put(x, xs.get(x) - z);
		}
	}

	public static double sum(double[] xs) {
		return sum(xs, xs.length);
	}
	
	public static double sum(double[] xs, int length) {
		assert length > 0;
		double result = xs[0];
		for (int i = 1; i < length; i++)
			result += xs[i];

		assert !Double.isNaN(result);
		return result;
	}
	
	public static<T> double sum(Map<T, Double> xs) {
		double result = 0.0;
		for (T tt : xs.keySet())
			result += xs.get(tt);
		
		return result;
	}
	
	public static void normalize(double []xs) {
		normalize(xs, xs.length);
	}
	
	public static void normalize(double []xs, int length) {
		double z = sum(xs, length);
		assert compareFloats(z, 0.0, 1e-10) != 0;
		assert !Double.isNaN(z);
		for (int i = 0; i < length; i++) {
			xs[i] /= z;
		}
	}
	
	public static<T> void scale(double s, Map<T, Double> xs) {
		for (T tt : xs.keySet()) {
			xs.put(tt, xs.get(tt) * s);
		}
	}
	
	public static<T> void normalize(Map<T, Double> xs) {
		double z = sum(xs);
		for (T tt : xs.keySet()) {
			xs.put(tt, xs.get(tt)/z);
		}

		assert compareFloats(sum(xs)-1.0, 0.0, 1e-10) == 0 : sum(xs)-1.0;
		assert !Double.isNaN(z);
	}
}
