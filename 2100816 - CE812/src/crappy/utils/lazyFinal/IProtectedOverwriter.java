package crappy.utils.lazyFinal;

import java.io.Serializable;

/**
 * The 'private' interface for I_ProtectedOverwrite, permitting one to
 * overwrite the data when it's normally locked for editing, and change the lock settings.
 * @param <T> datatype held
 * @author Rachel Lowe
 */
public interface IProtectedOverwriter<T> extends IProtectedOverwrite<T>, Serializable {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Private override for lock mode
     * @param newData the new data to store in the instance of this object
     */
    void setOverride(final T newData);

    /**
     * Allows the lock mode to be set.
     * @param mode the lock mode
     */
    void setLockMode(final IProtectedOverwrite.ProtectedOverwriteLockMode mode);

}
