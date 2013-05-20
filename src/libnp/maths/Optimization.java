package libnp.maths;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;

public class Optimization {
	public static double maximize(UnivariateFunction f, double a, double b) {
		UnivariateOptimizer optimizer = new BrentOptimizer(1e-12, 1e-14);
		return optimizer.optimize(new MaxEval(1000),
				new UnivariateObjectiveFunction(f),
				GoalType.MAXIMIZE,
				new SearchInterval(a, b)).getPoint();
	}
}
