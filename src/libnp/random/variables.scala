/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.random

trait variable[T] {
  def logDensity(): Double
  def get(): T
  def mutate(x: T): variable[T]
}

class dirac[T](val x: T) extends variable[T] {
  def logDensity(): Double = Double PositiveInfinity
  def get(): T = x
  def mutate(x: T): variable[T] = null
}

class weighted_dirac[T](val weight: Double, val x: T) extends variable[T] {
  def logDensity(): Double = Double PositiveInfinity
  def get(): T = x
  def mutate(x: T): variable[T] = null
}

object variable {
  implicit def variable2T[T](X: variable[T]): T = X get
  implicit def T2constant[Double](x: Double): variable[Double] = new dirac(x)
}