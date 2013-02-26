package xcategory;

import java.util.ArrayList;
import java.util.List;

public class XCat implements Sampleable {
	private double alpha;
	private double[][] data;
	
	List<Cluster> rows;
	List<Cluster> cols;
	
	List<Cluster[]> assign;
	
	public XCat(double[][] data) {
		this.alpha = 1.0;
		this.data = data;
		rows = new ArrayList();
		cols = new ArrayList();
		assign = new ArrayList();
		assign.add(new Cluster[data.length]);
		assign.add(new Cluster[data[0].length]);
	}
	
	public void removeRow(int dim, int i) {
		Cluster cluster = assign.get(dim)[i];
		assign.get(dim)[i] = null;
		
		cluster.decSize();
	}
	
	@Override
	public void resample(boolean collect) {
		
	}
}
