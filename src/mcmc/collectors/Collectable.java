package mcmc.collectors;

public interface Collectable {
	public Object get(String property_name);
	public Object get(String property_name, Object arg);
}
