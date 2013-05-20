package programs;

import static util.Operation.loadFreeform;
import org.apache.commons.math3.stat.inference.ChiSquareTest; //.chiSquareTestDataSetsComparison;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class compare_eppfs {
	public static void main(String[] args) {
		String f1 = args[0];
		String f2 = args[1];
		
		List<double[]> s1 = loadFreeform(f1);
		List<double[]> s2 = loadFreeform(f2);
		
		Map<Integer, Integer> Kc1 = new HashMap();
		Map<Integer, Integer> Kc2 = new HashMap();
		
		Double m = Double.MIN_VALUE;
		
		for (double[] a1 : s1) {
			int K = a1.length;
			if (K > m) m = (double)K;
			if (!Kc1.containsKey(K)) {
				Kc1.put(K, 0);
			}
			Kc1.put(K, Kc1.get(K) + 1);
		}
		
		for (double[] a2 : s2) {
			int K = a2.length;
			if (K > m) m = (double)K;
			if (!Kc2.containsKey(K)) {
				Kc2.put(K, 0);
			}
			Kc2.put(K, Kc2.get(K) + 1);
		}
		
		long[] l1 = new long[(int)m.doubleValue()];
		long[] l2 = new long[(int)m.doubleValue()];
		System.out.print("K ");
		for (int K = 1; K <= m; K++) {
			if (Kc1.containsKey(K)) {
				System.out.print(Kc1.get(K));
				l1[K-1] = Kc1.get(K);
			} else {
				System.out.print("0");
				l1[K-1] = 0;
			}
			System.out.print(":");
			if (Kc2.containsKey(K)) {
				System.out.print(Kc2.get(K));
				l2[K-1] = Kc2.get(K);
			} else {
				System.out.print("0");
				l2[K-1] = 0;
			}
			System.out.print(" ");
		}
		System.out.println();
		double p = new ChiSquareTest().chiSquareTestDataSetsComparison(l1, l2);
		System.out.println("chi2 = " + p);
		
		
	}
}
