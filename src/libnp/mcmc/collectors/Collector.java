/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.mcmc.collectors;

public interface Collector {
	public void collect();

	public void close();
}
