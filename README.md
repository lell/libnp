libnp
=====

code for dealing with nonparametric distributions


example
=======
Here's a scala script to demonstrate some of the functionality of libnp. This code samples from a one dimensional Gaussian using MH with a symmetric uniform proposal distribution.

mh_test.scala:
````scala
import libnp.random.variable
import libnp.random.uniform
import libnp.random.xfamily.normal
import libnp.mcmc.mh
import libnp.statistics.Generator

// Parameters for mcmc
val initial_X = 0.0
val mu = 0.0
val sigma = 1.0
val iters = 1000
val burnin = 100
val w = 1.0

// set up the rng and the kernel
val gen = new Generator()
val kernel = new mh[Double](
    x => new uniform(x-w, x+w))

// initialize the chain
var X = new normal(mu, sigma, initial_X)

for (var iter <- 1 to iters) {
  // iterate the chain with mh kernel
  X = kernel.apply(X, gen)
  if (iter > burnin) {
    println(X.get())
  }
}
````

After running this program, we can use the normal_check program on its output. This should be run in the libnp repo trunk directory. If you want to run this in another directort, then change the -cp argument to point to the build subdirectory of the libnp repo trunk directory.
````
$ scala mh_test.scala | java -cp build libnp.programs normal_check
````


requirements/installing
=======================

The libnp library is written in scala and java and it is easiest to build it using eclipse 3.7 with the scala-ide 3.0.0 release plugin for scala 2.9.

To build the libnp project in eclipse, start a new empty workspace and then install the scala-ide 2.0 release plugin for scala 2.9 by following these directions: http://scala-ide.org/download/sdk.html

Next, place the following libraries in the subdirectory `lib' of the parent directory of the libnp repo trunk directory:
````
commons-cli-1.2.jar
commons-math3-3.1.1.jar
````
These can be found mirrored in the repo https://github.com/lell/apache-commons or by downloading the relevant zip files from inside the zip files provided on the site http://commons.apache.org/

Finally, add libnp as a new project to the eclipse workspace by selecting File | New Java Project. Then, unselect Use default location and Browse to select the libnp repo trunk directory and select Finish.

After these steps, the unit tests in unit.TestStatistics can be run by right clicking on TestStatistics.java in the Package Explorer and selecting Run As | JUnit Test.


TODO
====

- linear algebra package/multivariate distributions
- more work on the grammar of random variables so they can be added/subtracted and truncated with binary operations
- define grammar for random measure classes
