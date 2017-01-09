package locomotor.core;

import java.util.TreeMap;

/**
* Gives some methods to work with Standard Gaussian.
*/
public class Gaussian {
	TreeMap<Double, Double> _pdfLookUp;
	TreeMap<Double, Double> _cdfLookUp;

	static Gaussian _instance = null;
	int _steps;
	double _stepsLength;

	private Gaussian() {
		_pdfLookUp = new TreeMap<Double, Double>();
		_cdfLookUp = new TreeMap<Double, Double>();

		_steps = 1000; // the interval in the lookup table is [-4, 4]. We want 1000 steps in the interval
		_stepsLength = 8.0 / _steps;

		double sqrt2PI = Math.sqrt(2 * Math.PI);

		double value;
		double sum;
		double term;
		double value;

		// generate the lookup tables for the pdf functions (1000 steps from -4 to 4)
		for(int i = 0; i <= _steps / 2; ++i) {
			value = _stepsLength * i;
			// lookup table for the pdf function
			value = Math.exp(-value * value / 2) / sqrt2PI;
			_pdfLookUp.put(value, value);
			_pdfLookUp.put(-value, value);
		}

		// lookup table for the cdf function using taylor approximation
		for(int i = 0; i <= _steps / 2; ++i) {
			value = _stepsLength * i;

			sum = 0.0;
			term = value;

			for(int j = 3; sum + term != sum; j += 2) {
				sum = sum + term;
				term = term * value * value / j;
			}

			value = 0.5 + sum * pdf(value);

			_cdfLookUp.put(value, value);
			_cdfLookUp.put(-value, 1 - value);
		}
	}

	private double getNearestStep(double value) {
		return Math.round(value / _stepsLength) * _stepsLength;
	}

	/**
	* Returns a singleton on the class.
	*/
	public static synchronized Gaussian getInstance() {
		if(_instance == null) {
			_instance = new Gaussian();
		}

		return _instance;
	}

	/**
	* Returns the true value of the pdf (probability density function) function on the standard gaussian. Slow.
	* For a faster result, see the pdf method.
	* @param value pdf(value)
	*/
	public double truePdf(double value) {
		return Math.exp(-value * value / 2) / Math.sqrt(2 * Math.PI);
	}

	/**
	* Returns the true value of the cdf (cumulative density function) function on the standard gaussian. Slow.
	* For a faster result, see the cdf method.
	* @param value cdf(value)
	*/
	public double trueCdf(double value) {
		if(value < -4) {
			return 0;
		}
		if(value > 4) {
			return 1;
		}

		double sum = 0;
		double term = value;

		for(int i = 3; sum + term != sum; i += 2) {
			sum = sum + term;
			term = term * value * value / i;
		}

		return 0.5 + sum * truePdf(value);
	}

	/**
	* Returns an approximated value of the pdf function. Fast.
	* @param value pdf(value)
	*/
	public double pdf(double value) {
		if(value < -4) {
			return 0;
		}
		if(value > 4) {
			return 0;
		}

		return _pdfLookUp.get(getNearestStep(value));
	}

	/**
	* Returns an approcimated value of the cdf function. Fast.
	* @param value cdf(value)
	*/
	public double cdf(double value) {
		if(value < -4) {
			return 0;
		}
		if(value > 4) {
			return 1;
		}

		return _cdfLookUp.get(getNearestStep(value));
	}
}