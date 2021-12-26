package crappy.utils;

/**
 * A crappy (LITERALLY) implementation of a read-only 'pair' datatype
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

    /**
     * Creates a new IPair of the two given items
     * @param f the first item to include in the pair
     * @param s the second item to include in the pair
     * @param <T1> type of first item
     * @param <T2> type of second item
     * @return new pair holding items f, s.
     */
    public static  <T1, T2> IPair<T1, T2> of(final T1 f, final T2 s){
        return new Pair<>(f, s);
    }
}
