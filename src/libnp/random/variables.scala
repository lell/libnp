/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.random

import libnp.statistics.Generator

trait variable[T] extends Serializable {
  type Self <: variable[T]
  def logDensity(): Double
  def get(): T
  def set(x:T): Unit
  def mutate(x: T): variable[T]
}

trait resetable[T] extends variable[T] {
  def reset(): variable[T]
}

trait sampleable[T] extends variable[T] with Serializable {
  def sample(gen: Generator): variable[T]
}

class dirac[T](var x: T) extends variable[T] with Serializable {
  type Self = dirac[T]
  def logDensity(): Double = Double PositiveInfinity
  def get(): T = x
  def set(X:T): Unit = { x = X }
  def mutate(x: T) = null
}

object variable {
  implicit def variable2T[T](X: variable[T]): T = X get
  implicit def T2dirac[Double](x: Double): variable[Double] = new dirac(x)
}
