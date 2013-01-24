package random

trait variable[T] {
  def logDensity(): Double
  def get(): T
  def mutate(x: T): variable[T]
}

class constant[T](val x: T) extends variable[T]  {
  def logDensity(): Double = Double PositiveInfinity
  def get(): T = x
  def mutate(x: T): variable[T] = null
}

object variable {
  implicit def variable2T[T](X: variable[T]): T = X get
  implicit def T2constant[Double](x: Double): variable[Double] = new constant(x)
}
