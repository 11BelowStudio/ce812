package crappy.utils;

import java.util.Iterator;

/**
 * An iterator that iterates over a primitive array.
 *
 * @param <T> datatype held in aforementioned array
 *
 * @author Rachel Lowe
 */
public class ArrayIterator<T> implements Iterator<T> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * The items to iterate over
     */
    private final T[] items;

    /**
     * A cursor
     */
    private int cursor = 0;

    /**
     * Creates this ArrayIterator
     * @param arr the array that this will be iterating over.
     */
    public ArrayIterator(T[] arr){
        items = arr;
    }

    @Override
    public boolean hasNext() {
        return cursor < items.length;
    }

    @Override
    public T next() {
        return items[cursor++];
    }
}
