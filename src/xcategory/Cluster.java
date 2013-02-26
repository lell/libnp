package xcategory;

public class Cluster {
	int dim;
	double weight;
	private int size;
	public void decSize() {
		size--;
		assert size >= 0;
	}
	
}
