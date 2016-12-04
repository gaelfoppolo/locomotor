package locomotor.components.types;

/**
 * @todo describe the class.
 */
public abstract class CInterval<T extends Comparable> implements CUniverseType, CVehicleType,
	CUserType {

	private T _min;
	private T _max;

	/**
	 * Initializes a closed interval [min, max].
	 *
	 * @param  min the smaller endpoint
	 * @param  max the larger endpoint
	 * @throws IllegalArgumentException if the min endpoint is greater than the max endpoint
	 * @todo handle error better
	 */
	public CInterval(T min, T max) {
		if(min.compareTo(max) <= 0) {
			_min = min;
			_max = max;
		}
		else {
			// error
			System.err.println("Error: Illegal interval");
			System.exit(0);
		}
	}

	/**
	 * Returns the min endpoint of this interval.
	 *
	 * @return the min endpoint of this interval
	 */
	public T min() { 
		return _min;
	}

	/**
	 * Returns the max endpoint of this interval.
	 *
	 * @return the max endpoint of this interval
	 */
	public T max() { 
		return _max;
	}


	/**
	 * Returns true if this interval intersects the specified interval.
	 *
	 * @param  that the other interval
	 * @return true if this interval intersects the argument interval;
	 *         false otherwise
	 */
	public final boolean intersects(CInterval that) {
		if(_max.compareTo(that._min) < 0) {
			return false;
		}
		if(that._max.compareTo(_min) < 0) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if this interval contains the specified value.
	 *
	 * @param value the value
	 * @return true if this interval contains the value x;
	 *         false otherwise
	 */
	public final boolean contains(T value) {
		return (_min.compareTo(value) <= 0) && (_max.compareTo(value) >= 0);
	}
	
	/**
	 * Compares this transaction to the specified object.
	 *
	 * @param  other the other interval
	 * @return true if this interval equals the other interval;
	 *         false otherwise
	 */
	public final boolean equals(Object other) {
		if(other == this) {
			return true;
		}
		if(other == null) { 
			return false;
		}
		if(other.getClass() != this.getClass()) {
			return false;
		}
		CInterval that = (CInterval) other;
		return _min == that._min && _max == that._max;
	}

	/**
	 * Returns a string representation of this interval.
	 *
	 * @return a string representation of this interval in the form [min, max]
	 */
	public final String toString() {
		return "[" + _min + ", " + _max + "]";
	}
}