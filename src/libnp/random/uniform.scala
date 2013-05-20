package libnp.random
import java.lang.Math.log

class uniform(val left: variable[Double],
    val right: variable[Double],
    val x: Double = Double.NaN) extends variable[Double] {
  
  def logDensity() =
    -log(right-left)
    
  def get() =
    x
    
  def mutate(x: Double) =
    new uniform(left, right, x)
}

class loguniform(val left: variable[Double],
    val right: variable[Double],
    val x: Double = Double.NaN) extends variable[Double] {
  
  def logDensity() =
    -log(x) - log(log(right/left))
    
  def get() =
    x
    
  def mutate(x: Double) =
    new loguniform(left, right, x)
}