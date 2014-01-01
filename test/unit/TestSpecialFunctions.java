package unit;

import static org.junit.Assert.*;

import org.junit.Test;

import static libnp.statistics.SpecialFunctions.incompleteBeta;
import static libnp.util.Float.compareFloats;

public class TestSpecialFunctions {
	
	@Test
	public void test_incompleteBeta() {
		for (double x = 0.0; x <= 1.0; x += 0.01) {
			assertTrue(compareFloats(
					incompleteBeta(1.0, 1.0, x),
					x,
					1e-10
					) == 0);
		}
		

		assertEquals(
				966.05087898981337,
				incompleteBeta(1e-15, 1e-3, 1e-3),
				1e-12);
		
		assertTrue(compareFloats(
				incompleteBeta(0.5, 7.0, 1.0),
				1.0,
				1e-10
				) == 0);

		assertTrue(compareFloats(
				incompleteBeta(7.0, 0.5, 1.0),
				1.0,
				1e-10
				) == 0);

		assertTrue(compareFloats(
				incompleteBeta(7.0, 7.0, 1.0),
				1.0,
				1e-10
				) == 0);

		assertTrue(compareFloats(
				incompleteBeta(0.5, 0.5, 1.0),
				1.0,
				1e-10
				) == 0);
	}

}
