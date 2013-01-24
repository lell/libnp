package random.xfamily

import random.variable
import java.lang.Math.PI
import java.lang.Math.log

import maths.Vector
import maths.Matrix

class normal(val mean: variable[Double],
    val precision: variable[Double],
    val x: Double) extends variable[Double] {
  
  def logDensity() =
    -0.5 * (precision * (mean - x) * (mean - x) + log(2.0*PI) + log(precision))
    
  def get() =
    x
    
  def mutate(x: Double) =
    new normal(mean, precision, x)
}