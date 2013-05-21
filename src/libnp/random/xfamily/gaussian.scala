/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.random.xfamily

import libnp.random.variable
import java.lang.Math.PI
import java.lang.Math.log

import libnp.maths.Vector
import libnp.maths.Matrix

class normal(val mean: variable[Double],
  val precision: variable[Double],
  val x: Double = Double.NaN) extends variable[Double] {

  def logDensity() =
    -0.5 * (precision * (mean - x) * (mean - x) + log(2.0 * PI) + log(precision))

  def get() =
    x

  def mutate(x: Double) =
    new normal(mean, precision, x)
}

class lognormal(val logmean: variable[Double],
  val logprecision: variable[Double],
  val x: Double = Double.NaN) extends variable[Double] {

  def logDensity() =
    -0.5 * (logprecision * (logmean - log(x)) * (logmean - log(x)) + log(2.0 * PI) + log(logprecision))

  def get() =
    x

  def mutate(x: Double) =
    new lognormal(logmean, logprecision, x)
}