package xcategory;

import static util.Operation.loadArray;
import static util.Operation.saveArray;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main {
	public void main(String args[]) {
		double[][] data = (loadArray(new BufferedReader(new InputStreamReader(System.in))));
		
		XCat state = new XCat(data);
		Sampler sampler = new Sampler(state);
		sampler.run();
		
		saveArray(data, new BufferedWriter(new OutputStreamWriter(System.out)));
	}
}
