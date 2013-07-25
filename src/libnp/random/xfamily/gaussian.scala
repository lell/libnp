/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.random.xfamily

import libnp.random.variable
import libnp.random.sampleable
import libnp.statistics.Generator
import java.lang.Math.PI
import java.lang.Math.log
import java.lang.Math.sqrt

import libnp.maths.Vector
import libnp.maths.Matrix

class normal(val mean: variable[Double],
  val precision: variable[Double],
  var x: Double = Double.NaN) extends variable[Double] with sampleable[Double] {
  
  type Self = normal

  def logNormalizer() = 
    0.5 * (log(2.0*PI)  - log(precision))
  
  def logDensity() = {
    val diff = mean - x
    -0.5 * diff * diff * precision - logNormalizer()
  }

  def get() =
    x
    
  def set(X:Double): Unit = { x = X }
    
  def mutate(x: Double) =
    new normal(mean, precision, x)
  
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

  def mutate(x: Double) =
    new lognormal(logmean, logprecision, x)
}