package locomotor.components.types;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represent a N-ary tree, that handle an Integer (32-bit) and a String.
 * @see CEnumUniverseType.
 * @see CEnumItemType.
 * @see CEnumUserType.
 */
public class CTree implements CItemType, CUserType, CComparable<CTree, CGraphTree> {
	
	/**
	 * The identifier.
	 */
	private Integer _identifier;
	/**
	 * The string.
	 */
	private String _data;
	/**
	 * The list of children.
	 */
	private ArrayList<CTree> _children;

	/**
	 * Constructs the CTree.
	 *
	 * @param      id    The string
	 * @param      data  The data
	 */
	public CTree(Integer id, String data) {
		_children = new ArrayList<CTree>();
		_identifier = id;
		_data = data;
	}

	/**
	 * Gets the ID.
	 *
	 * @return     The identifier.
	 */
	public Integer getID() {
		return _identifier;
	}

	/**
	 * Gets the data.
	 *
	 * @return     The data.
	 */
	public String getData() {
		return _data;
	}

	/**
	 * Sets the data.
	 *
	 * @param      data  The data
	 */
	public void setData(String data) {
		_data = data;
	}

	/**
	 * Get the children.
	 * 
	 * @return     The children.
	 */
	public ArrayList<CTree> getChildren() {
		return _children;
	}

	/**
	 * Adds a child.
	 *
	 * @param      identifier  The identifier
	 * @param      data        The data
	 */
	public void addChild(Integer identifier, String data) {
		CTree child = new CTree(identifier, data);
		_children.add(child);
	}

	/**
	 * Adds a child.
	 *
	 * @param      child  The child
	 */
	public void addChild(CTree child) {
		_children.add(child);
	}

	/**
	 * Determines if leaf.
	 *
	 * @return     True if leaf, False otherwise.
	 */
	public boolean isLeaf() {
		return (_children.size() == 0);
	}

	/**
	 * Count the number of leaf.
	 *
	 * @return     The number of leaf in the CTree
	 */
	public int leafCount() {
		int count = 0;
		for(CTree child : _children) {
			if(child.isLeaf()) {
				count++;
			}
			else {
				count += child.leafCount();
			}
		}
		return count;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return     String representation of the object.
	 */
	public String toString() {
		String str = "";
		str += "(" + _identifier + "," + _data;
		if(!isLeaf()) {
			str += "\n";
			for(CTree child : _children) {
				str += child.toString();
			}
			str += "\n";
		}
		str += ")";
		return str;
	}

	/**
	 * Return a set representation of the leaves.
	 *
	 * @return     Set representation of the leaves.
	 */
	public Set<Integer> toSet() {
		Set<Integer> set = new HashSet<Integer>();
		if(!isLeaf()) {
			for(CTree child : _children) {
				set.addAll(child.toSet());
			}
		}
		else {
			set.add(this._identifier);
		}
		return set;
	}

	/**
	 * Compare the tree of the vehicle with the tree of the user
	 *
	 * @param      user                The user criteria
	 * @param      universe            The universe tree (containg the graph)
	 * @param      disableFlexibility  Disable the flexibility
	 *
	 * @return     1.0 (match), tend toward 0.0 otherwise or -1.0 if does not match perfectly (flexibility disable)
	 */
	public double compare(CTree user, CGraphTree universe, boolean disableFlexibility) {
		
		Set<Integer> userKey = user.toSet();
		Set<Integer> itemKey = this.toSet();

		return universe.compare(userKey, itemKey, disableFlexibility);
	}

	/**
	 * Return the JSON value of the universe.
	 *
	 * @return     The nodes and the relations
	 */
	public JsonValue toJSON() {
		JsonObject tree = Json.object();
		tree.add("value", _data);
		tree.add("id", _identifier);
		if (!isLeaf()) {
			JsonArray children = Json.array();
			for (CTree child : _children) {
				children.add(child.toJSON());
			}
			tree.add("children", children);
		}
		return tree;
	}

	/**
	 * Factory from representation JSON.
	 *
	 * @param      json  The json
	 *
	 * @return     A new CTree object.
	 */
	public static CTree fromJSON(JsonValue json) {
		int id = json.asObject().get("id").asInt();
		CTree root = new CTree(id, "");

		// check children
		if(json.asObject().get("children") != null) {
			JsonArray children = json.asObject().get("children").asArray();
			
			for (JsonValue childJSON : children) {
				CTree child = CTree.fromJSON(childJSON);
				root.addChild(child);
			}

		}
		return root;
	}
}