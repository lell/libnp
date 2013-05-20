package libnp.random

trait measure[T] {
  def draw():T;
}
