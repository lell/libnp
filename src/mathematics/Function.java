package mathematics;

// Map domain to range
public interface Function<D, R> {
	public R evaluate(D x);
}
