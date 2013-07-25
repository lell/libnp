/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.random

import java.lang.Math.log
import libnp.statistics.Generator

class uniform(val left: variable[Double],
  val right: variable[Double],
  var x: Double = Double.NaN) extends variable[Double] with sampleable[Double] {
  
  type Self = uniform

  def logDensity() = {
	  if (x > right || x < left) {
	    Double.NegativeInfinity
	  } else {
		-log(right - left)
	  }
  	}

  def get() =
    x

  def set(X:Double):Unit =
    x = X
    
  def mutate(x: Double) =
    new uniform(left, right, x)
  
  def sample(gen: Generator) = {
    /*
     * TODO: implicits to make (left + gen.uniform() * (right - left)) work
     */
    var l:Double = left
    var r:Double = right
    mutate(l + gen.uniform() * (r - l))
  }
}

class loguniform(val left: variable[Double],
  val right: variable[Double],
  var x: Double = Double.NaN) extends variable[Double] {
  
  type Self = loguniform

  def logDensity() =
    -log(x) - log(log(right / left))

  def get() =
    x

  def set(X:Double):Unit = { x = X}
    
  def mutate(x: Double) =
    new loguniform(left, right, x)
  
}