package locomotor.components.types;

import locomotor.components.Compare;

/**
 * Encapsulate a Double value.
 * @see CEnumItemType.
 */
public class CFloat implements CItemType, CComparable<CIntervalDouble, CIntervalDouble> {

	/**
	 * The float value (double-precision 64-bit IEEE 754 floating point).
	 */
	private Double _value;

	/**
	 * Constructs the CFloat object.
	 *
	 * @param      value  The value
	 */
	public CFloat(Double value) {
		_value = value;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return     String representation of the object.
	 */
	public String toString() {
		return "(" + _value + ")";
	}

	/**
	 * Compare boolean value
	 *
	 * @param      user                The user criteria
	 * @param      universe            The universe
	 * @param      disableFlexibility  Disable the flexibility
	 *
	 * @return     1.0 (match), 0.0 otherwise or -1.0 if does not match perfectly (flexibility disable)
	 */
	public double compare(CIntervalDouble user, CIntervalDouble universe, boolean disableFlexibility) {
		return Compare.uniqueValue(user.min().doubleValue(), user.max().doubleValue(), _value.doubleValue(), disableFlexibility);
	}

}