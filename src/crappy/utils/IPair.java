package crappy.utils;

/**
 * A crappy implementation of a read-only 'pair' datatype
 * @param <T1> first item in the pair
 * @param <T2> second item in the pair
 */
public interface IPair<T1, T2>{

    /**
     * get first item
     * @return the first item from the pair
     */
    T1 getFirst();

    /**
     * Get second item
     * @return the second item from the pair
     */
    T2 getSecond();
}
