package crappy.utils.containers;

import java.io.Serializable;

/**
 * A class representing a triplet of 3 items
 * @param <T1> first item type
 * @param <T2> second item type
 * @param <T3> third item type
 * @author Rachel Lowe
 */
public interface ITriplet <T1, T2, T3> extends IPair<T1, T2>, Serializable {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Obtains the third item from the triplet
     * @return third item from the triplet.
     */
    T3 getThird();

    /**
     * Creates a new triplet of these three arguments
     * @param i1 first item
     * @param i2 second item
     * @param i3 third item
     * @param <T1> type of first item
     * @param <T2> type of second item
     * @param <T3> type of third item
     * @return a triplet of items i1, i2, i3
     */
    static <T1, T2, T3> ITriplet<T1, T2, T3> of(T1 i1, T2 i2, T3 i3){ return new Triplet<>(i1, i2, i3); }
}
