package mcmc

import random.variable
import statistics.Generator

trait kernel[T] {
  def apply(X: variable[T], generator: Generator): variable[T]
}

// Slice sampler
class slice(val lower: Double, val upper: Double) extends kernel[Double] {
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
}