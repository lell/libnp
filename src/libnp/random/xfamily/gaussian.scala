/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.random.xfamily

import libnp.random.variable
import libnp.random.sampleable
import libnp.statistics.Generator
import libnp.statistics.SpecialFunctions.logGamma
import java.lang.Math.PI
import java.lang.Math.log
import java.lang.Math.sqrt

import libnp.maths.Vector
import libnp.maths.Matrix

class beta(val a1: variable[Double],
    val a2: variable[Double],
    var x: Double = Double.NaN) extends variable[Double] with sampleable[Double] with Serializable {
  
  type Self = beta
  
  def logNormalizer() = {
    val alpha1: Double = a1
    val alpha2: Double = a2
    logGamma(alpha1) + logGamma(alpha2) - logGamma(alpha1 + alpha2)
  }
  
  def logDensity() = {
    val alpha1: Double = a1
    val alpha2: Double = a2
    (alpha1 - 1.0) * log(x) + (alpha2 - 1.0) * log(1.0 - x) - logNormalizer()
  }
  
  def get() = 
    x
    
  def set(X:Double): Unit = { x = X }
  
  def mutate(X: Double) = {
    new beta(a1, a2, X)
  }
  
  def sample(gen: Generator) = {
    mutate(gen.nextBeta(a1, a2))
  }
  
  def mean() = {
    val alpha1: Double = a1
    val alpha2: Double = a2
    alpha1/(alpha1+alpha2)
  }
  
  def variance() = {
    val alpha1: Double = a1
    val alpha2: Double = a2
    (alpha1*alpha2)/((alpha1+alpha2)*
    		(alpha1+alpha2)*
			(alpha1+alpha2+1)); 
  }
}


class exponential(val rate: variable[Double],
    var x: Double = Double.NaN) extends variable[Double] with sampleable[Double] with Serializable {
  
  type Self = exponential
  
  def logNormalizer() = {
    -log(rate)
  }
  
  def logDensity() = {
    -rate*x - logNormalizer()
  }
  
  def get() = 
    x
    
  def set(X:Double): Unit = { x = X }
  
  def mutate(X: Double) = {
    new exponential(rate, X)
  }
  
  def sample(gen: Generator) = {
    mutate(gen.nextExponential(rate))
  }
}

class normal(val mean: variable[Double],
  val precision: variable[Double],
  var x: Double = Double.NaN) extends variable[Double] with sampleable[Double] with Serializable {
  
  type Self = normal

  def logNormalizer() = 
    0.5 * (log(2.0*PI)  - log(precision))
  
  def logDensity() = {
    val mean_d = mean.get()
    val diff = mean_d - x
    -0.5 * diff * diff * precision - logNormalizer()
  }

  def get() =
    x
    
  def set(X:Double): Unit = { x = X }
    
  def mutate(X: Double) =
    new normal(mean, precision, X)
  
  def sample(gen: Generator) = {
    mutate(gen.nextGaussian()/sqrt(precision) + mean)
  }
}

class lognormal(val logmean: variable[Double],
  val logprecision: variable[Double],
  var x: Double = Double.NaN) extends variable[Double] {
  
  type Self = lognormal

  def logNormalizer() = 
    0.5 * (log(2.0*PI)  - log(logprecision))
  
  def logDensity() = {
    val logx = log(x)
    val diff = logmean - logx
    -0.5 * diff * diff * logprecision - logNormalizer() - logx
  }

  def get() =
    x
    
  def set(X:Double): Unit = { x = X }

  def mutate(X: Double) =
    new lognormal(logmean, logprecision, X)
}
