package crappy;

import java.util.UUID;

/**
 * For objects that have a UUID identifying them.
 * @author Rachel Lowe
 */
@FunctionalInterface
public interface IHaveIdentifier {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

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
