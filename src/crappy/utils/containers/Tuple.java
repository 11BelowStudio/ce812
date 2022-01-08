package crappy.utils.containers;

import crappy.internals.CrappyWarning;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Some sort of terrible implementation of a data class that effectively holds an unmodifiable list
 * @param <T> type of the object held in that list
 * @author Rachel Lowe
 */
public class Tuple<T> implements Iterable<T>, Serializable {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * The tuple itself
     */
    private final T[] data;

    /**
     * How long the tuple is
     */
    private final int length;

    /**
     * Constructs the tuple, holding the given contents
     * @param contents the stuff that needs to be held in the tuple.
     * @throws IllegalArgumentException if contents is empty
     */
    @SafeVarargs
    public Tuple(T... contents){
        length = contents.length;
        if (length <= 0){
            throw new IllegalArgumentException("Cannot create a tuple of less than 1 element!");
        }
        //noinspection unchecked
        data = (T[]) Array.newInstance( contents[0].getClass(), length);
        System.arraycopy(contents, 0, data, 0, length);
    }

    /**
     * Obtains the length of the tuple
     * @return tuple length
     */
    public int getLength(){
        return length;
    }

    /**
     * Obtains the item at the given index of the tuple
     * @param index which index is data needed from?
     * @return data[index]
     */
    public T get(final int index){
        return data[index];
    }

    @Override
    public Iterator<T> iterator() {
        return new TupleIterator<T>(this);
    }

    /**
     * Returns a clone of the data array. Internally uses .clone() method of the array itself.
     * @return a clone of the data array.
     */
    public T[] getDataClone() {
        return data.clone();
    }

    /**
     * Returns the data list as-is.
     * ONLY USE THIS IF YOU KNOW WHAT YOU'RE DOING!
     * @return the data list as-is.
     * @deprecated not actually deprecated,
     * this tag is here to discourage you from using it/nag you about it if you are using it.
     */
    @Deprecated
    @CrappyWarning("Please don't use this, unless you are absolutely sure that you know what you're doing.")
    public T[] __getData(){
        return data;
    }

    /**
     * An iterator for these tuples
     * @param <T> data type held within the tuples
     */
    private static class TupleIterator<T> implements Iterator<T>{

        /**
         * a counter
         */
        private int counter = -1;

        /**
         * The tuple that's being iterated over
         */
        private final Tuple<T> tup;

        /**
         * Creates a TupleIterator that iterates over a given tuple
         * @param theTuple tuple to iterate over
         */
        private TupleIterator(final Tuple<T> theTuple){ tup = theTuple; }

        @Override
        public boolean hasNext() {
            return counter < tup.length-1;
        }

        @Override
        public T next() {
            counter++;
            return tup.get(counter);
        }
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "data=" + Arrays.toString(data) +
                ", length=" + length +
                '}';
    }
}
