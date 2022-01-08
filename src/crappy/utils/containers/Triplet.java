package crappy.utils.containers;

/**
 * An implementation of the triplet class
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @author Rachel Lowe
 */
public class Triplet<T1, T2, T3> implements ITriplet<T1, T2, T3> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    public final T1 first;

    public final T2 second;

    public final T3 third;

    /**
     * Creates triplet holding these items
     * @param a first
     * @param b second
     * @param c third
     */
    public Triplet(final T1 a, final T2 b, final T3 c){
        first = a;
        second = b;
        third = c;
    }

    @Override
    public T1 getFirst() {
        return first;
    }

    @Override
    public T2 getSecond() {
        return second;
    }

    @Override
    public T3 getThird() {
        return third;
    }

    @Override
    public String toString() {
        return "Triplet{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
