/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2012 OpsResearch LLC (a Delaware company)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License, version 3,
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * ***** END LICENSE BLOCK ***** */


package libnp.statistics;

import static libnp.statistics.SpecialFunctions.logGamma;


public class IncompleteBeta {
	

	protected double _epsilon = 1.0E-7;
	protected double _valueMax = Double.MAX_VALUE;
	protected double _valueMin = Double.MIN_VALUE;
	protected int _maxIterations = 25;
	private double _zero = 1.0e-30;
	private double _negZero = -_zero;

	
	private double _a, _b, _k, _t, _s;
	private double _o, _q, _l;
	private double _p, _r, _m;
	private boolean _isValid = false;
	
	private double notZero(double d) {
		if (d < 0.0) {
			return d < _negZero ? d : _negZero;
		} else {
			return d > _zero ? d : _zero;
		}
	}
	
	private double getEpsilon() {
		return _epsilon;
	}

	public void setParameters(double a, double b) {
		if (_isValid && _a == a && _b == b)
			return;
		_isValid = false;
		_a = a;
		_b = b;
		_t = (a + 1.0) / (a + b + 2.0);
		_k = logGamma(a + b) - logGamma(a) - logGamma(b);
		_s = a + b;
		_o = a - 1.0;
		_q = a + 1.0;
		_l = _s / _q;
		_p = b - 1.0;
		_r = b + 1.0;
		_m = _s / _r;
		_isValid = true;
	}

	public double evaluate(double x) {
		if (x < 0.0) {
			System.err.println("'x' can't be less than '0.0'.");
			System.exit(-1);
		}
		
		if (x > 1.0) {
			System.err.println("'x' can't be greated than '1.0'.");
			System.exit(-1);
		}
		double bt = (x == 0.0 || x == 1.0) ? 0.0 : Math.exp(_k + Math.log(x) * _a + Math.log(1.0 - x) * _b);
		if (x < _t)
			return bt * f1(x) / _a;
		else
			return 1.0 - bt * f2(1.0 - x) / _b;
	}
	private double f1(double x) {
		double c = 1.0;
		double d = notZero(1.0 - x * _l);
		d = 1.0 / d;
		double h = d;
		for (int i = 1; i <= 100; i++) {
			double m = i;
			double m2 = 2 * m;
			double aa = m * (_b - m) * x / ((_o + m2) * (_a + m2));
			d = notZero(1.0 + aa * d);
			c = notZero(1.0 + aa / c);
			d = 1.0 / d;
			h *= d * c;
			aa = -(_a + m) * (_s + m) * x / ((_a + m2) * (_q + m2));
			d = notZero(1.0 + aa * d);
			c = notZero(1.0 + aa / c);
			d = 1.0 / d;
			double del = d * c;
			h *= del;
			if (Math.abs(del - 1.0) < getEpsilon())
				return h;
		}
		System.err.println("Can't solve BetaCF to _epsilonilon.");
		System.exit(-1);
		return 0.0;
	}

	private double f2(double x) {
		double c = 1.0;
		double d = notZero(1.0 - x * _m);
		d = 1.0 / d;
		double h = d;
		for (int i = 1; i <= 100; i++) {
			double m = i;
			double m2 = 2 * m;
			double aa = m * (_a - m) * x / ((_p + m2) * (_b + m2));
			d = notZero(1.0 + aa * d);
			c = notZero(1.0 + aa / c);
			d = 1.0 / d;
			h *= d * c;
			aa = -(_b + m) * (_s + m) * x / ((_b + m2) * (_r + m2));
			d = notZero(1.0 + aa * d);
			c = notZero(1.0 + aa / c);
			d = 1.0 / d;
			double del = d * c;
			h *= del;
			if (Math.abs(del - 1.0) < getEpsilon())
				return h;
		}
		System.err.println("Can't solve BetaCF to _epsilonilon.");
		System.exit(-1);
		return 0.0;
	}
}
