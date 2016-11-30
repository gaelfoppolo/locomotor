package locomotor.components.types;

/**
 * @todo
 */
public enum CEnumUniverseType {
    
    // boolean
    BOOLEAN(2),
    // integer interval 
    INTEGER_INTERVAL(3),
    // float interval
    FLOAT_INTERVAL(4),
    // string interval
    STRING_INTERVAL(5),
    // weighted string list
    WEIGHTED_STRING_LIST(6),
    // tree
    TREE(7);

    /**
     * The identifier of the enum item.
     */
    private int _id;

    /**
     * Constructs the object (private to prevent other to instantiate new CEnumUniverseType).
     *
     * @param      id    The identifier.
     */
    private CEnumUniverseType(int id) {
        _id = id;
    }

    /**
     * Gets the id.
     *
     * @return     The id.
     */
    public int getID() {
        return _id;
    }
    
}