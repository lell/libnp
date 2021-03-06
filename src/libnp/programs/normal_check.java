/* libnp
 * Copyright (c) 2013, Lloyd T. Elliott and Yee Whye Teh
 */

package libnp.programs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;
import static libnp.statistics.Frequentist.ksTest;
import static libnp.util.Operation.loadArray;

import org.apache.commons.math3.analysis.UnivariateFunction;

import static org.apache.commons.math3.special.Erf.erf;

public class normal_check {

	public static void main(String[] cmd_args) {
		double[][] raw = loadArray(new BufferedReader(new InputStreamReader(
				System.in)));

		double[] data = new double[raw.length];
		for (int i = 0; i < raw.length; i++) {
			data[i] = raw[i][0];
		}

		Map<Integer, Integer> sigmas = new HashMap();
		for (int i = 0; i < raw.length; i++) {
			int sigma = (int) (floor(data[i]));
			if (sigma > 1) {
				if (!sigmas.containsKey(sigma)) {
					sigmas.put(sigma, 0);
				}
				sigmas.put(sigma, sigmas.get(sigma) + 1);
			}
		}

		double D = ksTest(data, new UnivariateFunction() {
			@Override
			public double value(double x) {
				return 0.5 * (erf(x/sqrt(2.0)) + 1.0);
			}
		});

		System.out.println("D-statistic= " + D);
		for (Integer sigma : sigmas.keySet()) {
			System.out.println("#" + sigma + "-sigma_events= "
					+ sigmas.get(sigma));

		}
	}
}
