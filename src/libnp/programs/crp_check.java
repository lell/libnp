/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.programs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.commons.math3.analysis.UnivariateFunction;

import static java.lang.Math.log;
import static libnp.maths.Optimization.maximize;
import static libnp.statistics.Frequentist.chiSquareTest;
import static libnp.statistics.SpecialFunctions.crp_sizes;
import static libnp.util.Operation.loadFreeform;

public class crp_check {

	public static double mlAlpha(final Map<Integer, Integer> counts,
			final int n, final double max_alpha) {

		// Find the ML value of alpha
		return maximize(new UnivariateFunction() {
			@Override
			public double value(double a) {
				double[] sizes = crp_sizes(a, n);
				double L = 0.0;
				for (int i = 0; i < n; i++) {
					if (!counts.containsKey(i + 1)) {
						counts.put(i + 1, 0);
					}
					L += counts.get(i + 1) * log(sizes[i]);
				}
				return L;
			}
		}, 0.0, max_alpha);
	}

	public static double chiSquareCounts(final Map<Integer, Integer> counts,
			final int n, final double alpha) {

		double[] sizes = crp_sizes(alpha, n);
		Map<Integer, Double> probs = new HashMap();
		for (int i = 0; i < n; i++) {
			if (sizes[i] > 0.0) {
				probs.put(i + 1, log(sizes[i]));
			} else {
				assert counts.get(i+1) == 0;
				counts.remove(i+1);
			}
		}
		return chiSquareTest(counts, probs);
	}

	public static void main(String[] cmd_args) {
		CommandLine cmdline = null;
		double max_alpha = 100.0;
		Double alpha = null;
		{
			CommandLineParser parser = new PosixParser();
			final Options options = new Options();
			options.addOption("alpha", true,
					"Value of alpha to test (default: use ML value of alpha)");
			options.addOption("max_alpha", true,
					"Maximum value of to use in ML search (default: "
							+ max_alpha + ")");

			try {
				cmdline = parser.parse(options, cmd_args);
			} catch (ParseException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}

		String args[] = cmdline.getArgs();
		if (cmdline.hasOption("alpha")) {
			alpha = Double.valueOf(cmdline.getOptionValue("alpha"));
		}
		if (cmdline.hasOption("max_alpha")) {
			if (alpha != null) {
				System.err
						.println("max_alpha specified, but no ML search will be performed");
				System.exit(-1);
			}
			max_alpha = Double.valueOf(cmdline.getOptionValue("max_alpha"));
		}

		List<double[]> lines = loadFreeform(new BufferedReader(
				new InputStreamReader(System.in)));
		if (lines == null || lines.size() == 0) {
			return;
		}

		int n_ = 0;
		for (Double m : lines.get(0)) {
			n_ += m;
		}
		final int n = n_;

		for (double[] row : lines) {
			int total = 0;
			for (Double entry : row) {
				total += entry;
			}
			if (total != n) {
				System.err.println("Sizes did not add to " + n);
				System.exit(-1);
			}
		}

		final Map<Integer, Integer> counts = new HashMap();
		for (int i = 0; i < n; i++) {
			counts.put(i + 1, 0);
		}

		for (double[] line : lines) {
			int K = line.length;
			counts.put(K, counts.get(K) + 1);
		}

		if (alpha == null) {
			alpha = mlAlpha(counts, n, max_alpha);
			System.out.println("ML_alpha= " + alpha);
		}
		double p = chiSquareCounts(counts, n, alpha);
		System.out.println("chi-square_test(K)= " + p);
	}
}
