package crappy.utils.containers;

/**
 * An equally crappy implementation of the IPair interface
 * @param <T1> type of 'first' object
 * @param <T2> type of 'second' object
 * @author Rachel Lowe
 */
public class Pair<T1, T2> implements IPair<T1, T2> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * The first item in the pair
     */
    public final T1 first;

    /**
     * The second item in the pair
     */
    public final T2 second;

    /**
     * Creates a new pair holding items F, S
     * @param f first item of pair
     * @param s second item of pair
     */
    public Pair(final T1 f, final T2 s){
        first = f;
        second = s;
    }

    /**
     * get first item
     * @return the first item from the pair
     */
    @Override
    public T1 getFirst() {
        return first;
    }

    /**
     * get second item
     * @return the second item from the pair
     */
    @Override
    public T2 getSecond(){
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
