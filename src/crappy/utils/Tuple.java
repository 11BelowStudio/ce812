package crappy.utils;

import crappy.internals.CrappyWarning;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Some sort of terrible implementation of a data class that effectively holds an unmodifiable list
 * @param <T> type of the object held in that list
 */
public class Tuple<T> implements Iterable<T> {

    /**
     * The tuple itself
     */
    final T[] data;

    /**
     * How long the tuple is
     */
    final int length;

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
}
