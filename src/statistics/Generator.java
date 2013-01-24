package statistics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Map;
import java.util.Set;

import static statistics.SpecialFunctions.logsumexp;
import static statistics.SpecialFunctions.sum;
import static util.Float.compareFloats;
import static java.lang.Math.*;

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
		return log( (exp(logupper) - exp(loglower)) * nextDouble() + exp(loglower));
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
	
	public int discrete(double[] probs) {
		return discrete(probs, probs.length);
	}
	
	public int discrete(double[] probs, int length) {
		assert length > 0;
		if (length == 1)
			return 0;

		double u = uniform();
		double cdf = probs[0];
		if (cdf > u)
			return 0;
		
		for (int i = 1; i < length; i++) {
			cdf += probs[i];
			if (cdf > u)
				return i;
		}

		assert false;
		return -1;
	}
	
	/* Fast discrete sampler, assumes probs are
	 *  normalized.
	 */
	public int logDiscrete(double[] probs) {
		assert probs.length > 0 : "Sampling from empty set.";
		double u = loguniform();
		double cdf = probs[0];
		if (cdf > u)
			return 0;

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
}
