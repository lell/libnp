import libnp.random.xfamily.normal
import libnp.random.variable
import libnp.mcmc.slice
import libnp.statistics.Generator


object main {
  def main(args: Array[String]) = {
    val slicer = new slice(-1.0, 1.0)
    var n0 = new normal(0.0, 1.0, 0.0)
    var chain: List[variable[Double]] = List(n0)
    val iters = 100000

    val generator = new Generator(1234)
    val out = new java.io.FileWriter("normal")
    for (i <- 1 to iters) {
      chain = slicer.apply(chain.head, generator) :: chain
      out.write(chain.head.get() + "\n")
    }
    out.close()
  }
}

