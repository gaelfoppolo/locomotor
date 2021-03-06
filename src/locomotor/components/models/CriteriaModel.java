package locomotor.components.models;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import locomotor.components.types.CEnumItemType;
import locomotor.components.types.CEnumUniverseType;
import locomotor.components.types.CEnumUserType;
import locomotor.components.types.CUniverseType;

/**
 * Holds useful and reusable data (model) of a criteria.
 */
public class CriteriaModel {

	/**
	 * The identifier.
	 */
	private String _identifier;
	/**
	 * The name of the criteria.
	 */
	private String _name;

	/**
	 * The question to display to the user.
	 */
	private String _question;
	
	/**
	 * Wether the criteria can be used for computing or not.
	 */
	private Boolean _isComparable;

	/**
	 * The type of the structure that represents the universe.
	 */
	private CEnumUniverseType _universeType;

	/**
	 * The type of the structure that represents the item's criteria value.
	 */
	private CEnumItemType _itemType;

	/**
	 * The type of the structure that represents what the user will have to submit.
	 */
	private CEnumUserType _userType;

	/**
	 * The unit unique identifier.
	 */
	private String _unitID;

	/**
	 * The universe of the values accepted for this criteria.
	 */
	private CUniverseType _universe;

	/**
	 * Constructs the criteria.
	 *
	 * @param      identifier    The identifier
	 * @param      name          The name
	 * @param      question      The question
	 * @param      isComparable  Indicates if comparable
	 * @param      universeType  The universe type
	 * @param      itemType      The item type
	 * @param      userType      The user type
	 * @param      unitID        The unit id
	 * @param      universe      The universe
	 */
	public CriteriaModel(String identifier, String name, String question, Boolean isComparable, 
			CEnumUniverseType universeType, CEnumItemType itemType, 
			CEnumUserType userType, String unitID, CUniverseType universe) {
		_identifier = identifier;
		_name = name;
		_question = question;
		_isComparable = isComparable;
		_universeType = universeType;
		_itemType = itemType;
		_userType = userType;
		_unitID = unitID;
		_universe = universe;
	}

	/**
	 * Outputs a human readable representation of the model.
	 *
	 * @return    The representation of the model.
	 */
	public String getID() {
		return _identifier;
	}

	/**
	 * Gets the name.
	 *
	 * @return     The name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Gets the item type.
	 *
	 * @return     The item type.
	 */
	public CEnumItemType getItemType() {
		return _itemType;
	}

	/**
	 * Gets the user type.
	 *
	 * @return     The user type.
	 */
	public CEnumUserType getUserType() {
		return _userType;
	}

	/**
	 * Gets the universe.
	 *
	 * @return     The universe.
	 */
	public CUniverseType getUniverse() {
		return _universe;
	}

	/**
	 * Outputs a human readable representation of the model.
	 *
	 * @return    The representation of the model.
	 */
	public String toString() {
		String line = _identifier + " - " + _name + " - " + "Universe(" + _universeType
			+ ") - " + "Item(" + _itemType + ") - " + "User(" + _userType + ")\n";
		line += _universe.toString() + "\n";
		return line;
	}

	/**
	 * JSON representation of the class.
	 *
	 * @return     A JsonObject.
	 */
	public JsonObject toJSON() {
		JsonObject obj = Json.object();
		obj.add("_id", _identifier);
		obj.add("name", _name);
		obj.add("question", _question);
		obj.add("isComparable", _isComparable);
		obj.add("itemType", _itemType.getID());
		obj.add("userType", _userType.getID());
		obj.add("universeType", _universeType.getID());
		obj.add("unitID", _unitID);
		JsonValue universe = _universe.toJSON();
		obj.add("universe", universe);
		return obj;
	}
}