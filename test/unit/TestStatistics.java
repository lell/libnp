package unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libnp.statistics.Generator;
import libnp.programs.crp_check;
import static libnp.util.Operation.range;
import static libnp.util.Float.compareFloats;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestStatistics {

	Generator generator = new Generator(12345);

	public void test_crp_check(double alpha, int n, int iters) {
		Map<Integer, Integer> counts = new HashMap();
		List<Double> r = range(n);
		assert r.size() == n;

		for (int i = 1; i <= n; i++) {
			counts.put(i, 0);
		}

		for (int iter = 0; iter < iters; iter++) {
			int K = generator.drawCRP(r, alpha).size();
			counts.put(K, counts.get(K) + 1);
		}
		double p = crp_check.chiSquareCounts(counts, n, alpha);
		assertTrue("chiSquare was " + p, p > 0.0001);

		double mlAlpha = crp_check.mlAlpha(counts, n, 100.0);

		assertTrue("mlAlpha was " + mlAlpha,
				compareFloats(alpha, mlAlpha, 1.0) == 0);
	}

	@Test
	public void all_crp_check() {
		test_crp_check(0.5, 15, 1000);
		test_crp_check(1.0, 10, 1000);
		test_crp_check(2.0, 10, 1000);
		test_crp_check(3.0, 10, 1000);
		test_crp_check(4.0, 10, 1000);
		test_crp_check(7.0, 10, 1000);
	}

}
