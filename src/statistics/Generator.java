package statistics;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Map;
import org.apache.commons.math3.distribution.GammaDistribution;

import static statistics.SpecialFunctions.logsumexp;
import static statistics.SpecialFunctions.sum;
import static util.Float.compareFloats;
import static java.lang.Math.*;


public class Generator extends Random {
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

	public int bernoulli(double rate) {
		if (rate > nextDouble())
			return 1;

		else
			return 0;
	}

	public double nextExponential() {
		return -log(nextDouble()); 
	}

	public double uniform(double low, double high) {
		assert compareFloats(low, high) != 1;
		return (high - low) * (nextDouble()) + low;
	}
	public int nextUniform(int ss) { /* between 0 and ss-1 */
		return (int)floor(nextDouble()*ss);
	}

	public double nextUniform(double bb) {
		return nextDouble()*bb;
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
		
		assert compareFloats(sum(probs, length), 1.0, 1.0e-10) == 0 : sum(probs, length);
		
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

	public int nextBernoulli(double pp) {
		if ( nextDouble() < pp ) return 1;
		return 0;
	}

	public double nextExponential(double rate) {
		return nextExponential()/rate;
	}

	public double nextGamma2(double shape) {
		double xx = new GammaDistribution(shape, 1.0).sample();
		assert xx > 0.0;
		return xx;
	}

	public double nextGamma2(double shape, double invscale) {
		assert invscale > 0.0;
		return nextGamma2(shape)/invscale;
	}

	public double nextGamma(double shape) {
		double aa, bb, cc, dd;
		double uu, vv, ww, xx, yy, zz;

		if ( shape <= 0.0 ) {
			/* Not well defined, set to NaN. */
			assert false;
			return Double.NaN;
		} else if ( shape == 1.0 ) {
			/* Exponential */
			return nextExponential();
		
		} else if ( shape < 1.0 ) {
			/* Use Johnks generator */
			cc = 1.0 / shape;
			dd = 1.0 / (1.0-shape);
			while (true) {
				xx = pow(nextDouble(), cc);
				yy = xx + pow(nextDouble(), dd);
				if ( yy <= 1.0 ) {
					return - log(nextDouble()) * xx / yy;
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
					if ( ( zz <= (1.0 - 2.0 * yy * yy / xx) ) ||
							( log(zz) <= 2.0 * (bb * log(xx / bb) - yy) ) ) {

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
		return nextGamma(shape)/invscale;
	}

	public double nextBeta(double aa, double bb) {
		if (aa==0.0 && bb==0.0) {
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
		assert aa  >= 0;
		assert bb >= 0;
		return aa/(aa+bb);
	}

	public double[] nextDirichlet(double[] aa) {
		double[] gg = new double[aa.length];
		double sum = 0.0;
		for ( int ii = 0 ; ii < aa.length ; ii++ )
			sum += gg[ii] = nextGamma(aa[ii]);
		for ( int ii = 0 ; ii < aa.length ; ii++ )
			gg[ii] /= sum;
		return gg;
	}

	public <T extends Collection> T nextCRPTable(double mass, Collection<T> tables) {
		return nextCRPTable(mass,0.0,tables);
	}
	public <T extends Collection> T nextCRPTable(double mass, double discount,
			Collection<T> tables) {
		if (discount < 0.0 || discount>= 1.0 || mass < -discount)
			throw new Error("CRP parameters out of range.");
		double rr = nextUniform(mass + tables.size());
		for ( T table : tables ) {
			rr -= ((double)table.size()) - discount;
			if ( rr < 0.0 ) {
				return table;
			}
		}
		return null;
	}
	public <T> T nextUniform(Collection<T> set) {
		int rr = nextUniform(set.size());
		for ( T element : set ) {
			rr--;
			if (rr==-1) return element;
		}
		return null;
	}

	public int nextCRPNumTable(double mass, int numcustomer) {
		return nextCRPNumTable(mass,0.0,numcustomer);
	}
	public int nextCRPNumTable(double mass, double discount, int numcustomer) {
		if (numcustomer == 0) return 0;
		if (numcustomer < 0 || discount < 0.0 ||
				discount>= 1.0 || mass < -discount)
			throw new Error("CRP parameters out of range.");
		int numtable = 1;
		for ( int ii = 1 ; ii < numcustomer ; ii ++ ) {
			double prob = (mass+numtable*discount)/(mass+ii);
			numtable += nextBernoulli(prob);
		}
		return numtable;
	}


}
