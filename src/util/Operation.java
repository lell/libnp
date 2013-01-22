package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Operation {

	public long getMemoryUsage() {
		return 0;
	}

	public static PrintStream tee(final String filename, final PrintStream p2) {
		try {
			return new PrintStream(new FileOutputStream(filename)) {
				final PrintStream stream = p2;
				@Override
				public void write(byte[] b, int off, int len) {
					super.write(b, off, len);
					stream.write(b, off, len);
					this.flush();
				}
			};

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return p2;
		}
	}

	public static double[][] loadArray(String filename) {
		BufferedReader fp;
		try {
			fp = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
		return loadArray(fp);
	}
		
	public static double[][]loadArray(BufferedReader fp) {
		String line = null;
		try {
			line = fp.readLine();
		} catch (IOException e1) {
			try {
				fp.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			return null;
		}
		
		if (line == null || line.equals("\n"))
			return null;

		int num_rows = 0;
		Boolean header = null;
		Integer num_cols = null;
		List<double[]> array = new ArrayList();
		while (line != null && !line.equals("\n")) {
			num_rows++;
			String cols[] = line.trim().split("\\s+");
			if (header == null) {
				num_cols = cols.length;
				header = false;
				for (String col : cols) {
					try {
						Double.parseDouble(col);
					}
					catch(NumberFormatException e) {
						header = true;
						break;
					}
				}

				if (header == true) {
					num_rows--;
				}
			}
			
			if (header == false || num_rows >= 1) {
				assert num_cols == cols.length;
				double[] row = new double[num_cols];
				for (int j = 0; j < num_cols; j++) {
					row[j] = Double.valueOf(cols[j]);
				}
				array.add(row);
			}
			
			try {
				line = fp.readLine();
			} catch (IOException e1) {
				try {
					fp.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				return null;
			}
		}
		
		try {
			fp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double[][] result = new double[num_rows][num_cols];
		for (int i = 0; i < num_rows; i++)
			for (int j = 0; j < num_cols; j++)
				result[i][j] = array.get(i)[j];
		
		return result;
	}
	
	public static List<double[]> loadFreeform(String filename) {
		BufferedReader fp;
		try {
			fp = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
		return loadFreeform(fp);
	}
	
	public static List<double[]> loadFreeform(BufferedReader fp) {
		String line = null;
		
		try {
			line = fp.readLine();
		} catch (IOException e1) {
			try {
				fp.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			return null;
		}
		
		if (line == null || line.equals("\n"))
			return null;

		int num_rows = 0;
		Boolean header = null;
		Integer num_cols = null;
		List<double[]> array = new ArrayList();
		while (line != null && !line.equals("\n")) {
			String cols[] = line.trim().split("\\s+");
			double[] row = new double[cols.length];
			for (int j = 0; j < cols.length; j++) {
				row[j] = Double.valueOf(cols[j]);
			}
			array.add(row);
			try {
				line = fp.readLine();
			} catch (IOException e) {
				return null;
			}
		}
		return array;
	}
}
