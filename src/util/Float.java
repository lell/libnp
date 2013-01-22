package util;

public class Float {
	private static boolean MEPS_known = false;
	private static double MEPS = 1.0;
	
	private static void computeMEPS() {
		MEPS = 1.0;
		do {
			MEPS /= 2.0;
		} while ((1.0+(MEPS/2.0)) != 1.0);
		MEPS_known = true;
	}
	
	public static double getMachineEpsilon() {
		if (!MEPS_known) {
			computeMEPS();
		}
		return 10 * MEPS;
	}

	public static int compareFloats(double a, double b) {
		return compareFloats(a, b, getMachineEpsilon());
	}
	public static int compareFloats(double a, double b, double precision) {
		if ((a > (b - precision)) && (b > (a - precision))) {
			return 0;
		} else if (a > (b - precision)) {
			return 1;
		} else {
			return -1;
		}
	}
}
