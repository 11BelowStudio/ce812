package crappy.utils;

/**
 * An equally crappy implementation of the IPair interface
 * @param <T1> type of 'first' object
 * @param <T2> type of 'second' object
 */
public class Pair<T1, T2> implements IPair<T1, T2> {

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


    @Override
    public T1 getFirst() {
        return first;
    }

    @Override
    public T2 getSecond(){
        return second;
    }
}
