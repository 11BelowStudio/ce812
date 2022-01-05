package crappy;

import java.util.UUID;

/**
 * For objects that have a UUID identifying them.
 */
public interface IHaveIdentifier {

    /**
     * Obtains unique identifier for object
     * @return unique identifier.
     */
    UUID getID();

    /**
     * Sees if these objects are identical by checking their identifiers
     * @param other the other object
     * @return true if they have the same ID, false otherwise
     */
    default boolean equalsID(final IHaveIdentifier other){
        return getID().compareTo(other.getID()) == 0;
    }
}
