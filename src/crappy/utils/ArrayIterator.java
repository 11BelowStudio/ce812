package crappy.utils;

import java.util.Iterator;

/**
 * An iterator that iterates over a primitive array.
 * @param <T> datatype held in aforementioned array
 */
public class ArrayIterator<T> implements Iterator<T> {

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
