package crappy.utils.containers;

/**
 * 4 items of generic types in one object!
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @author Rachel Lowe
 */
public class Quadruplet<T1, T2, T3, T4> implements IQuadruplet<T1, T2, T3, T4>{
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * First item
     */
    public final T1 first;

    /**
     * Second item
     */
    public final T2 second;

    /**
     * Third item
     */
    public final T3 third;

    /**
     * Fourth item
     */
    public final T4 fourth;

    /**
     * Public constructor, initializing the items in this quadruplet
     * @param a first value
     * @param b second value
     * @param c third value
     * @param d fourth value
     */
    public Quadruplet(final T1 a, final T2 b, final T3 c, final T4 d){
        first = a;
        second= b;
        third = c;
        fourth = d;
    }

    /**
     * Creates a quadruplet holding the 3 items held in a triplet along with a 4th item
     * @param tri triplet holding the first 3 items to use
     * @param d 4th item
     */
    public Quadruplet(final ITriplet<T1, T2, T3> tri, final T4 d){
        first = tri.getFirst();
        second= tri.getSecond();
        third = tri.getThird();
        fourth = d;
    }



    /**
     * get first item
     *
     * @return the first item from the quadruplet
     */
    @Override
    public T1 getFirst() {
        return first;
    }

    /**
     * Get second item
     *
     * @return the second item from the quadruplet
     */
    @Override
    public T2 getSecond() {
        return second;
    }

    /**
     * Obtains the third item from the quadruplet
     *
     * @return third item from the quadruplet.
     */
    @Override
    public T3 getThird() {
        return third;
    }

    /**
     * Obtains the 4th item
     *
     * @return whatever the 4th item was
     */
    @Override
    public T4 getFourth() {
        return fourth;
    }

    @Override
    public String toString() {
        return "Quadruplet{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", fourth=" + fourth +
                '}';
    }
}
