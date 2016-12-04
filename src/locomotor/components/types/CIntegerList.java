package locomotor.components.types;

import java.util.ArrayList;

/**
 * @todo describe the class
 */
public class CIntegerList implements CVehicleType {

	/**
	 * The list of integer value (32-bit)
	 */
	private ArrayList<Integer> _value;

	/**
	 * Constructs the CIntegerList object
	 *
	 * @param      value  The value
	 */
	public CIntegerList(ArrayList<Integer> value) {
		_value = value;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return     String representation of the object.
	 */
	public String toString() {
		String s = "";
		for (Integer l : _value) {
			s += " " + l;
		}
		return s;
	}

}