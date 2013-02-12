package mcmc.collectors;

import java.io.*;

public class MatrixCollector implements Collector {
	private Collectable cc;
	private String property;
	private int index = 0;
	boolean collapse = false; // only use one  
	String prefix;
	
	public MatrixCollector(Collectable cc, String property, String prefix) {
		this.cc = cc;
		this.property = property;
		this.prefix = prefix;
	}
	
	public MatrixCollector(Collectable cc, String property, String prefix, boolean collapse) {
		this(cc, property, prefix);
		this.collapse = collapse;

		if (collapse == true)
			if (new File(prefix).exists() && !new File(prefix).delete()) {
				System.out.println("Could not delete existing prefix: " + prefix);
				System.exit(-1);
			}
	}

	@Override
	public void collect() {
		String filename = prefix;
		if (!collapse) {
			filename += "." + String.format("%04d", index++);
		}
		
		PrintStream output = null;
		try {
			output = new PrintStream(
					new BufferedOutputStream(
							new FileOutputStream(
									new File(filename),
									collapse))); // append if it already exists, if we are collapsing to a single file.
			
			
		} catch (IOException ee) {
			System.err.println("Unable to open "+filename+": "+ee.getMessage());
		}
		
		Object[][] returned = (Object[][])cc.get(property);
		Integer rows = returned.length;
		Integer cols = returned[0].length;
		for (int i = 0; i < returned.length; i++) {
			for (int j = 0; j < returned[i].length; j++) {
				output.print(returned[i][j]);
				if (j < returned[0].length-1) {
					output.print(" ");
				}
			}
			output.println();			
		}
		output.close();
	}

	public void close() {
	}
}
