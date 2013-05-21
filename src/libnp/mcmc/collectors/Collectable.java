/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.mcmc.collectors;

public interface Collectable {
	public Object get(String property_name);

	public Object get(String property_name, Object arg);
}
