package crappy.utils.lazyFinal;

import java.io.Serializable;

/**
 * Like I_LazyFinal, but this one is more about protected overwriting and such I guess
 * @param <T> type of data to store in this
 * @author Rachel Lowe
 */
public interface IProtectedOverwrite<T> extends ILazyFinal<T>, Serializable {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Current status of the lock mode for the protected overwriting
     */
    enum ProtectedOverwriteLockMode {
        /**
         * Nobody is allowed to write to it
         */
        READ_ONLY,
        /**
         * Can write to this via a private workaround (up to the implementer)
         */
        PRIVATE_WRITEABLE,
        /**
         * Can write to this via this interface
         */
        PUBLIC_WRITEABLE;
    };


    /**
     * Attempts to write to this VIA THE PUBLIC INTERFACE.
     * @param newData the data we're attempting to store
     * @throws IllegalStateException if overwriting is currently disabled
     * @throws IllegalArgumentException if you're attempting to write a mcfucking null
     */
    void set(final T newData) throws IllegalStateException, IllegalArgumentException;

    /**
     * Obtains the current status of the overwrite lock
     * @return current status of overwrite lock
     */
    ProtectedOverwriteLockMode getLockMode();

}
