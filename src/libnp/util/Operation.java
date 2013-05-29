/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Operation {

	/* Following code is from stackoverflow */
	public static String removeExtention(String filePath) {
		// These first few lines the same as Justin's
		File f = new File(filePath);

		// if it's a directory, don't remove the extention
		if (f.isDirectory()) return filePath;

		String name = f.getName();

		// Now we know it's a file - don't need to do any special hidden
		// checking or contains() checking because of:
		final int lastPeriodPos = name.lastIndexOf('.');
		if (lastPeriodPos <= 0)
		{
			// No period after first character - return name as it was passed in
			return filePath;
		}
		else
		{
			// Remove the last period and everything after it
			File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
			return renamed.getPath();
		}
	}
	
	public static List<Double> range(int n) {
		ArrayList<Double> result = new ArrayList();
		for (int i = 1; i <= n; i++) {
			double d = i;
			result.add(d);
		}
		return result;
	}

	public static double[] arange(int n) {
		double[] result = new double[n];
		for (int i = 0; i < n; i++) {
			result[i] = (double)i;
		}
		return result;
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
	public static List<String> loadHeaders(String filename) {
		BufferedReader fp;
		try {
			fp = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
		return loadHeaders(fp);
	}

	public static List<String> loadHeaders(BufferedReader fp) {
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

		if (line == null || line.equals("\n")) {
			return null;
		}

		List<String> headers = new ArrayList();
		
		String cols[] = line.trim().split("\\s+");
		for (String col : cols) {
			headers.add(col);
		}
		return headers;
	}

	/*
	 * Loads a file consisting of doubles into a matrix. Assumes that each row
	 * of the file is a row of the matrix and there is optionally one row of
	 * headers. Entries must be able to be parsed with parseDouble (i.e., NaN is
	 * allowed, or scientific notation).
	 */
	public static double[][] loadArray(String filename) {
		BufferedReader fp;
		try {
			fp = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
		return loadArray(fp);
	}

	public static double[][] loadArray(BufferedReader fp) {
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

		if (line == null || line.equals("\n")) {
			return null;
		}

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
					} catch (NumberFormatException e) {
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
		for (int i = 0; i < num_rows; i++) {
			for (int j = 0; j < num_cols; j++) {
				result[i][j] = array.get(i)[j];
			}
		}

		return result;
	}

	public static void saveArray(double[][] data, String filename) {
		BufferedWriter fp = null;
		try {
			fp = new BufferedWriter(new FileWriter(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		saveArray(data, fp);
	}

	public static void saveArray(double[][] data, BufferedWriter fp) {
		if (data.length == 0 || data[0].length == 0) {
			return;
		}

		try {
			for (int i = 0; i < data.length; i++) {
				String first = "";
				for (int j = 0; j < data[0].length; j++) {
					fp.write(first + data[i][j]);
					first = " ";
				}
				fp.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Loads a file consisting of doubles into a list of arrays. Each array can
	 * be a different size. If the first row of the file has headers for the
	 * columns, then that row is skipped.
	 */
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

		if (line == null || line.equals("\n")) {
			return null;
		}

		int num_rows = 0;
		boolean first = true;
		Integer num_cols = null;
		List<double[]> array = new ArrayList();
		while (line != null && !line.equals("\n")) {
			String cols[] = line.trim().split("\\s+");
			double[] row = new double[cols.length];
			for (int j = 0; j < cols.length; j++) {
				try {
					row[j] = Double.valueOf(cols[j]);
				} catch (NumberFormatException e) {
					if (!first) {
						throw e;
					}
				}

			}
			array.add(row);
			try {
				line = fp.readLine();
			} catch (IOException e) {
				return null;
			}
			first = false;
		}
		return array;
	}
}
