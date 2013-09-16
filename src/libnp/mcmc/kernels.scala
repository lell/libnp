/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.mcmc

import libnp.random.variable
import libnp.random.sampleable
import libnp.statistics.Generator

trait kernel[T] extends Serializable {
  def apply(X: variable[T], generator: Generator): variable[T]
}

// Slice sampler
class slice(val lower: Double, val upper: Double) extends kernel[Double] with Serializable {
  def apply(X: variable[Double], generator: Generator): variable[Double] = {
    val slice = X.logDensity() - generator.nextExponential()

    //@tailrec
    def iter(X: variable[Double], l: Double, u: Double): variable[Double] = {
      val Y = X.mutate(generator.nextUniform(l, u))

      if (Y.logDensity() > slice)
        return Y

      else if (Y > X)
        iter(X, l, Y)

      else
        iter(X, Y, u)
    }
    iter(X, lower, upper)
  }

  def getLower(): Double = lower
  def getUpper(): Double = upper
}

class mh[T](val q: T => sampleable[T]) extends kernel[T] with Serializable {
  def apply(X: variable[T], gen: Generator): variable[T] = {
    
    val Qf = q(X)
    val Y = X.mutate(Qf.sample(gen))
    Qf.set(Y)
    val Qb = q(Y)
    Qb.set(X)
    val a = Y.logDensity() - X.logDensity() + Qb.logDensity() - Qf.logDensity()
//    println("X " + X.get() + " PX " + X.logDensity() + " Y " + Y.get() + " PY " + Y.logDensity() + " QF " + Qf.logDensity() + " QB " + Qb.logDensity() + " A " + a)
    if (a > 0) {
      Y
    } else if (a > gen.loguniform()) {
      Y
    } else {
      X
    }
  }
}