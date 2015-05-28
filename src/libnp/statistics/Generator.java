/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.statistics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.*;
import static libnp.statistics.SpecialFunctions.logsumexp;
import static libnp.statistics.SpecialFunctions.sum;
import static libnp.util.Float.compareFloats;

public class Generator extends Random {
	private static final long serialVersionUID = -6511067795205329327L;

	long seed;

	public Generator() {
		seed = nextLong();
		setSeed(seed);
	}

	public List<Integer> shuffleRange(int n) {
		List<Integer> range = new ArrayList();
		for (int i = 0; i < n; i++) {
			range.add(i);
		}
		shuffle(range);
		return range;
	}

	public <T> List<T> shuffled(Collection<T> list) {
		List result = new ArrayList();
		result.addAll(list);
		Collections.shuffle(result, this);
		return result;
	}

	public void shuffle(List list) {
		Collections.shuffle(list, this);
	}

	public Generator(long seed) {
		this.seed = seed;
		setSeed(seed);
	}

	public long getSeed() {
		return seed;
	}

	public double loguniform() {
		return log(nextDouble());
	}

	public double loguniform(double loglower, double logupper) {
		return log((exp(logupper) - exp(loglower)) * nextDouble()
				+ exp(loglower));
	}

	public double nextExponential() {
		return -log(nextDouble());
	}

	public double nextExponential(double rate) {
		return -log(nextDouble()) / rate;
	}

	public double nextUniform(double a, double b) {
		return a + (nextDouble() * (b - a));
	}

	public double uniform() {
		return nextDouble();
	}

	public <T> T discrete(Map<T, Double> probs) {
		assert probs.size() > 0;
		double u = uniform();
		double cdf = 0.0;
		assert compareFloats(sum(probs), 1.0, 1e-10) == 0;

		for (T x : probs.keySet()) {
			cdf += probs.get(x);
			if (cdf > u) {
				return x;
			}
		}
		assert false;
		return null;
	}

	public int discrete(float[] probs) {
		return discrete(probs, probs.length);
	}

	public int discrete(float[] probs, int length) {
		assert length > 0;
		if (length == 1) {
			return 0;
		}

		for (int i  = 0; i < probs.length; i++) {
			assert !Double.isNaN(probs[i]);
			assert !Double.isInfinite(probs[i]);
		}
		
		assert (compareFloats(sum(probs, length), 1.0, 1e-8) == 0) : sum(probs, length);
		double u = uniform();
		double cdf = probs[0];
		if (cdf > u) {
			return 0;
		}

		for (int i = 1; i < length; i++) {
			cdf += probs[i];
			if (cdf > u) {
				return i;
			}
		}

		assert false;
		return -1;
	}

	/*
	 * Fast discrete sampler, assumes probs are normalized.
	 */
	public int logDiscrete(double[] probs) {
		assert probs.length > 0 : "Sampling from empty set.";
		double u = loguniform();
		double cdf = probs[0];
		assert (compareFloats(logsumexp(probs), 0.0) == 0);
		if (cdf > u) {
			return 0;
		}

		for (int i = 1; i < probs.length; i++) {
			cdf = logsumexp(cdf, probs[i]);
			if (cdf > u) {
				return i;
			}
		}
		assert false : "Fell out of discrete sampler.";
		return -1;
	}

	public <T> T logDiscrete(Map<T, Double> probs) {
		assert probs.keySet().size() > 0 : "Sampling from empty set.";
		double u = loguniform();
		Double cdf = null;
		for (T object : probs.keySet()) {
			if (cdf == null) {
				cdf = probs.get(object);
			} else {
				cdf = logsumexp(cdf, probs.get(object));
			}

			if (cdf > u) {
				return object;
			}
		}
		assert false : "Fell out of discrete sampler.";
		return null;
	}

	public int nextBernoulli(double pp) {
		if (nextDouble() < pp) {
			return 1;
		}
		return 0;
	}

	public double nextGamma(double shape) {
		double aa, bb, cc, dd;
		double uu, vv, ww, xx, yy, zz;

		if (shape <= 0.0) {
			/* Not well defined, set to zero and skip. */
			assert false;
			return 0.0;
		} else if (shape == 1.0) {
			/* Exponential */
			return nextExponential();
		} else if (shape < 1.0) {
			/* Use Johnks generator */
			cc = 1.0 / shape;
			dd = 1.0 / (1.0 - shape);
			while (true) {
				xx = pow(nextDouble(), cc);
				yy = xx + pow(nextDouble(), dd);
				if (yy <= 1.0) {
					double result = -log(nextDouble()) * xx / yy;
					assert !Double.isNaN(result);
					assert !Double.isInfinite(result);
					// assert result > 0.0;
					return result;
				}
			}
		} else { /* shape > 1.0 */
			/* Use bests algorithm */
			bb = shape - 1.0;
			cc = 3.0 * shape - 0.75;
			while (true) {
				uu = nextDouble();
				vv = nextDouble();
				ww = uu * (1.0 - uu);
				yy = sqrt(cc / ww) * (uu - 0.5);
				xx = bb + yy;
				if (xx >= 0) {
					zz = 64.0 * ww * ww * ww * vv * vv;
					if ((zz <= (1.0 - 2.0 * yy * yy / xx))
							|| (log(zz) <= 2.0 * (bb * log(xx / bb) - yy))) {

						double result = xx;
						assert !Double.isNaN(result);
						assert !Double.isInfinite(result);
						assert result > 0.0;
						return result;
					}
				}
			}
		}
	}

	public double nextGamma(double shape, double invscale) {
		assert invscale > 0.0;
		return nextGamma(shape) / invscale;
	}

	public double nextBeta(double aa, double bb) {
		if (aa == 0.0 && bb == 0.0) {
			return nextBernoulli(0.5);
		}
		assert (aa > 0 && bb > 0);
		aa = nextGamma(aa);
		bb = nextGamma(bb);
		assert !Double.isNaN(aa);
		assert !Double.isInfinite(aa);
		assert !Double.isNaN(bb);
		assert !Double.isInfinite(bb);
		assert (aa + bb > 0.0);
		assert aa >= 0;
		assert bb >= 0;
		return aa / (aa + bb);
	}

	public double[] nextDirichlet(double[] aa) {
		double[] gg = new double[aa.length];
		double sum = 0.0;
		for (int ii = 0; ii < aa.length; ii++) {
			sum += gg[ii] = nextGamma(aa[ii]);
		}
		for (int ii = 0; ii < aa.length; ii++) {
			gg[ii] /= sum;
		}
		return gg;
	}

	public <T> Map<T, Double> nextDirichlet(Map<T, Double> aa) {
		List<T> ordered = new ArrayList();
		for (T key : aa.keySet()) {
			ordered.add(key);
		}

		double[] gg = new double[aa.size()];
		double sum = 0.0;
		for (int ii = 0; ii < aa.size(); ii++) {
			sum += gg[ii] = nextGamma(aa.get(ordered.get(ii)));
		}
		for (int ii = 0; ii < aa.size(); ii++) {
			gg[ii] /= sum;
		}

		Map<T, Double> result = new HashMap();
		for (int ii = 0; ii < aa.size(); ii++) {
			result.put(ordered.get(ii), gg[ii]);
		}

		return result;
	}

	public <T> Set<Set<T>> drawCRP(Collection<T> S, double alpha) {
		Set<Set<T>> tables = new HashSet();
		int n = S.size();
		if (n == 0) {
			return tables;
		}
		int i = 0;
		for (T item : S) {
			int K = tables.size();
			double U = uniform();
			double cdf = alpha / (i + alpha);

			boolean found = false;
			if (cdf > U) {
				Set<T> table = new HashSet();
				table.add(item);
				tables.add(table);
				found = true;
			} else {
				for (Set<T> table : tables) {
					cdf += table.size() / (i + alpha);
					if (cdf > U) {
						table.add(item);
						found = true;
						break;
					}
				}
			}
			assert found;
			i++;
		}
		return tables;
	}

	public <T> Set<T> nextSubset(Set<T> set, int num) {
		Set<T> result = new HashSet();
		List<T> shuffled = new ArrayList();
		shuffled.addAll(set);
		this.shuffle(shuffled);
		for (int i = 0; i < num; i++) {
			result.add(shuffled.get(i));
		}

		return result;
	}

	public <T> T uniform(Set<T> range) {
		return nextSubset(range, 1).iterator().next();
	}
}
