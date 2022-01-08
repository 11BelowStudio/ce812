package crappy.utils.lazyFinal;

import java.util.Objects;
import java.util.Optional;

/**
 * The class that actually holds the data within LazyFinal
 * @param <T> type of the data
 *
 * @author Rachel Lowe
 */
final class LazyData<T> implements ILazyData<T> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * The main event
     */
    private final T theData;

    /**
     * A wrapper for the main event
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<T> optData;

    /**
     * Creates this to hold the data
     * @param data the data to hold
     */
    LazyData(final T data){
        theData = data;
        optData = Optional.of(theData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return theData.equals(((LazyData<?>) o).theData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theData);
    }

    public final T getTheData(){
        return theData;
    }

    public final Optional<T> getOptData(){
        return optData;
    }
}