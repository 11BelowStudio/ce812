package crappy.utils.lazyFinal;

import java.io.Serializable;
import java.util.Optional;

/**
 * An interface for the inner container object within the I_LazyFinal implementations
 * @param <T> type of data stored in this
 * @author Rachel Lowe
 */
public interface ILazyData<T> extends Serializable {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Obtains the data within the I_LazyData as-is.
     * @return the data, as-is.
     */
    T getTheData();

    /**
     * Obtains the data within the I_LazyData wrapped in an Optional
     * @return the data, in an optional.
     */
    Optional<T> getOptData();
}
