package crappy.utils.containers;

import java.io.Serializable;

/**
 * An interface for quadruplets of 4 items (supporting distinct types!)
 * @param <T1> first item type
 * @param <T2> second item type
 * @param <T3> third item type
 * @param <T4> fourth item type
 * @author Rachel Lowe
 */
public interface IQuadruplet <T1, T2, T3, T4> extends ITriplet<T1, T2, T3>, Serializable {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Obtains the 4th item from the IQuadruplet
     * @return whatever the 4th item was
     */
    T4 getFourth();

    /**
     * Convenient wrapper for creating a quadruplet of the 4 specified items
     * @param a first value
     * @param b second value
     * @param c third value
     * @param d fourth value
     */
    static <T1, T2, T3, T4> IQuadruplet<T1, T2, T3, T4> of(final T1 a, final T2 b, final T3 c, final T4 d){
        return new Quadruplet<>(a,b,c,d);
    }

    /**
     * Makes a quadruplet from a triplet and something else
     * @param tri triplet containing first 3 elements
     * @param d 4th element
     * @return quadruplet containing triplet contents + 4th
     */
    static <T1, T2, T3, T4> IQuadruplet<T1, T2, T3, T4> of(final ITriplet<T1, T2, T3> tri, final T4 d){
        return new Quadruplet<>(tri, d);
    }
}
