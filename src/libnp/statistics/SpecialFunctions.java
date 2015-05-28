/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.statistics;

import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.tan;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static libnp.util.Float.compareFloats;
import static libnp.util.Float.equal;

import java.util.Collection;
import java.util.Map;

public final class SpecialFunctions {

	// Logarithm of gamma function http://www.cs.princeton.edu/introcs/91float/
	public static double logGamma(double x) {
		if (x == 0.0) {
			return Double.POSITIVE_INFINITY;
		}
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
				+ 24.01409822 / (x + 2) - 1.231739516 / (x + 3) + 0.00120858003
				/ (x + 4) - 0.00000536382 / (x + 5);
		return tmp + log(ser * sqrt(2 * PI));
	}

	
	public static double incompleteBeta(double a, double b, double x) {
		IncompleteBeta beta = new IncompleteBeta();
		beta.setParameters(a, b);
		return beta.evaluate(x);
	}

	public static double[] crp_sizes(double alpha, int n) {

		return crp_sizes(alpha, 0.0, n);
	}

	/*
	 * Compute Pr(|pi|) = K, for pi ~ CRP(n, alpha, 0). From X. H. Chen, A. P.
	 * Dempster, J. S. Liu 1994, Weighted finite population sampling to maximize
	 * entropy. Biometrika 81(3) returns array x[] of length n such that x[K-1]
	 * = Pr(|pi|) = K
	 */
	public static double[] crp_sizes(double alpha, double d, int n) {

		if (n == 0) {
			return new double[] {};
		} else if (n == 1) {
			return new double[] { 1.0 };
		}
		double[] pdf = new double[n];
		double[] T = new double[n - 1];
		for (int i = 1; i < n; i++) {
			T[i - 1] = 0.0;
			for (int j = 1; j < n; j++) {
				T[i - 1] += pow(alpha / j, i);
			}
		}

		pdf[0] = 1.0;
		for (int i = 1; i < n; i++) {
			pdf[0] *= 1.0 - alpha / (i + alpha);
		}
		for (int k = 1; k < n; k++) {
			double p = 0.0;
			int sign = 1;
			for (int i = 1; i <= k; i++) {
				p += sign * pdf[k - i] * T[i - 1];
				sign *= -1;
			}
			pdf[k] = p / k;
		}

		for (int i = 1; i < n; i++) {
			if (Double.isNaN(pdf[i]) || pdf[i] < 0.0) {
				pdf[i] = 0.0;
			}
		}
		return pdf;
	}

	public static double logsumexp(double x, double y) {
		if (x < y) {
			return y + log(exp(x - y) + 1.0);
		} else {
			return x + log(exp(y - x) + 1.0);
		}
	}

	public static double logsumexp(Collection<Double> cs) {
		double[] xs = new double[cs.size()];
		int i = 0;
		for (Double x : cs) {
			xs[i++] = x;
		}
		return logsumexp(xs);
	}

	public static double logsumexp(double[] xs) {
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

	public static void logNormalize(double[] xs) {
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

	public static double sum(float[] xs) {
		return sum(xs, xs.length);
	}

	public static double sum(float[] xs, int length) {
		assert length > 0;
		double result = xs[0];
		for (int i = 1; i < length; i++) {
			result += xs[i];
		}

		assert !Double.isNaN(result);
		return result;
	}

	public static <T> double sum(Map<T, Double> xs) {
		double result = 0.0;
		for (T tt : xs.keySet()) {
			result += xs.get(tt);
		}

		return result;
	}

	public static void normalize(float[] xs) {
		normalize(xs, xs.length);
	}

	public static void normalize(float[] xs, int length) {
		double z = sum(xs, length);
		assert !Double.isNaN(z);
		for (int i = 0; i < length; i++) {
			xs[i] /= z;
		}
	}

	public static <T> void scale(double s, Map<T, Double> xs) {
		for (T tt : xs.keySet()) {
			xs.put(tt, xs.get(tt) * s);
		}
	}

	public static <T> void normalize(Map<T, Double> xs) {
		double z = sum(xs);
		for (T tt : xs.keySet()) {
			xs.put(tt, xs.get(tt) / z);
		}

		assert compareFloats(sum(xs) - 1.0, 0.0, 1e-10) == 0 : sum(xs) - 1.0;
		assert !Double.isNaN(z);
	}
	

	  // logarithm of Kramp's symbol [cc]^{aa}_{bb}
	  public static double logKramps(double cc, double aa, double bb) {
	    if (equal(aa+cc/bb,0.0) ||
	            equal(cc/bb,0.0) ||
	            (aa+cc/bb < 0.0) ||
	            (cc/bb < 0.0))
	    {
	      double result = 0.0;
	      for(int i=0;i<aa;i++)
	      {
	        result += Math.log(cc + i*bb);
	      }
	      return result;
	    }
	    else
	    {
	      return Math.log(bb)*aa + logGamma(aa + cc/bb) - logGamma(cc/bb);
	    }
	  }


	  public static double logChoose(double nn,double xx)
	  {
	    return logGamma(1.0+nn) - logGamma(1.0+nn-xx) - logGamma(1.0+xx) ;
	  }

	  public static double logBeta(double aa, double bb) {
	    if ( aa<=0.0 || bb<=0.0 ) return Double.POSITIVE_INFINITY;
	    return logGamma(aa) + logGamma(bb) - logGamma(aa+bb);
	  }

	  /**
	   * Compute logarithm of unsigned Stirling number of first kind log(s(n,m)). 
	   * Use Temme (Studies in Applied Mathematics 89:233-243 1993).
	   * Note that accuracy is limited!
	   * 
	   * @param nn
	   * @param mm
	   * @return
	   */
	  public static double logStirling1(double nn, double mm) {

	    // boundary conditions
	    if (equal(nn,mm)) {
	      return 0.0;
	    } else if (equal(mm, 1.0) && nn>1.0) {
	      return logGamma(nn);
	    } else if (mm < 1.0 || mm > nn) {
	      return Double.NEGATIVE_INFINITY;
	    }

	    double n = nn-1;
	    double m = mm-1;

	    double t0 = m / (n-m);
	    // Solve for x0 using Newton's method.
	    double x0 = .5*log(n*m/(n-m));
	    int converged = 0;
	    for ( int ii = 1; ii <= 20; ii++ ) {
	      double oldx = x0;
	      x0 -= (exp(x0)*(digamma(exp(x0)+n+1)-digamma(exp(x0)+1))-m)/
	           (exp(x0)*(digamma(exp(x0)+n+1)-digamma(exp(x0)+1)) +
	            exp(2*x0)*(trigamma(exp(x0)+n+1)-trigamma(exp(x0)+1)));
	      if (abs(oldx-x0)<1e-10) {
	        converged = 1;
	        break;
	      }
	    }
	    if (converged == 0)
	      System.out.print("logstirling1>solvex0: Newton steps did not converge.");
	    x0 = exp(x0);

	    return logGamma(x0+n+1) - logGamma(x0+1) - (m+1)*log(x0)
	            - n*log(t0+1) + m*log(t0)
	            + .5*log(m*(n-m)/(n*(trigamma(x0+n+1)-trigamma(x0+1)+m/x0/x0)))
	            + logGamma(n+1) - logGamma(m+1) - logGamma(n-m+1);
	  }


	  /** The digamma function is the derivative of gammaln.
	    * Translated from Tom Minka's lightspeed package.
	    *
	    * Reference:
	    *  J Bernardo,
	    *  Psi ( Digamma ) Function,
	    *  Algorithm AS 103,
	    *  Applied Statistics,
	    *  Volume 25, Number 3, pages 315-317, 1976.
	    *  From http://www.psc.edu/~burkardt/src/dirichlet/dirichlet.f
	    *  (with modifications for negative numbers and extra precision)
	    */
	    public static double digamma(double x) {
	      final double c = 12;
	      final double d1 = -0.57721566490153286;
	      final double d2 = 1.6449340668482264365; /* pi^2/6 */
	      final double s = 1e-6;
	      final double s3 = 1.0/12.0;
	      final double s4 = 1.0/120.0;
	      final double s5 = 1.0/252.0;
	      final double s6 = 1.0/240.0;
	      final double s7 = 1.0/132.0;
	      final double s8 = 691.0/32760.0;
	      final double s9 = 1.0/12.0;
	      final double s10 = 3617.0/8160.0;
	      double result;

	      /* Illegal arguments */
	      if((x == Double.NEGATIVE_INFINITY) || x == Double.NaN )
	        return Double.NaN;

	     /* Singularities */
	     if((x <= 0.0) && (floor(x) == x))
	       return Double.NEGATIVE_INFINITY;

	     /* Negative values */
	     /* Use the reflection formula (Jeffrey 11.1.6):
	      * digamma(-x) = digamma(x+1) + pi*cot(pi*x)
	      *
	      * This is related to the identity
	      * digamma(-x) = digamma(x+1) - digamma(z) + digamma(1-z)
	      * where z is the fractional part of x
	      * For example:
	      * digamma(-3.1) = 1/3.1 + 1/2.1 + 1/1.1 + 1/0.1 + digamma(1-0.1)
	      *               = digamma(4.1) - digamma(0.1) + digamma(1-0.1)
	      * Then we use
	      * digamma(1-z) - digamma(z) = pi*cot(pi*z)
	      */
	     if(x < 0.0) 
	       return digamma(1-x) + PI/tan(-PI*x);
	  
	     /* Use Taylor series if argument <= S */
	     if(x <= s) return d1 - 1/x + d2*x;
	     /* Reduce to digamma(X + N) where (X + N) >= C */
	     result = 0.0;
	     while(x < c) {
	       result -= 1.0/x;
	       x++;
	     }
	     /* Use de Moivre's expansion if argument >= C */
	     /* This expansion can be computed in Maple via asympt(Psi(x),x) */
	     if(x >= c) {
	       double r = 1/x;
	       result += log(x) - 0.5*r;
	       r *= r;
	       result -= r * (s3 - r * (s4 - r * (s5 - r * (s6 - r * s7))));
	     }
	     return result;
	   }

	   /** The trigamma function is the derivative of the digamma function.
	     * Translated from Tom Minka's lightspeed package.
	     *
	     * Reference:

	     * B Schneider,
	     * Trigamma Function,
	     * Algorithm AS 121,
	     * Applied Statistics,
	     * Volume 27, Number 1, page 97-99, 1978.
	     *
	     * From http://www.psc.edu/~burkardt/src/dirichlet/dirichlet.f
	     * (with modification for negative arguments and extra precision)
	     */
	   public static double trigamma(double x) {
	    final double small = 1e-4,
	      large = 8.0,
	      c = 1.6449340668482264365, /* pi^2/6 = Zeta(2) */
	      c1 = -2.404113806319188570799476,  /* -2 Zeta(3) */
	      b2 =  1.0/6.0,
	      b4 = -1.0/30.0,
	      b6 =  1.0/42.0,
	      b8 = -1.0/30.0,
	      b10 = 5.0/66.0;
	    double result;
	    /* Illegal arguments */
	    if((x == Double.NEGATIVE_INFINITY) || Double.isNaN(x))
	      return Double.NaN;

	    /* Singularities */
	    if((x <= 0.0) && (floor(x) == x))
	      return Double.NEGATIVE_INFINITY;
	  
	    /* Negative values */
	    /* Use the derivative of the digamma reflection formula:
	     * -trigamma(-x) = trigamma(x+1) - (pi*csc(pi*x))^2
	     */
	    if(x < 0.0) {
	      result = PI/sin(-PI*x);
	      return -trigamma(1.0-x) + result*result;
	    }
	    /* Use Taylor series if argument <= small */
	    if(x <= small) 
	      return 1.0/(x*x) + c + c1*x;
	    
	    result = 0;
	    /* Reduce to trigamma(x+n) where ( X + N ) >= B */
	    while(x < large) {
	      result += 1.0/(x*x);
	      x++;
	    }
	    /* Apply asymptotic formula when X >= B */
	    /* This expansion can be computed in Maple via asympt(Psi(1,x),x) */
	    if(x >= large) {
	      double r = 1/(x*x);
	      result += 0.5*r + (1 + r*(b2 + r*(b4 + r*(b6 + r*(b8 + r*b10)))))/x;
	    }
	    return result;
	  }
	
}

