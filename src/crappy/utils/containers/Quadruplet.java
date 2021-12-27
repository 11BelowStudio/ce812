package crappy.utils.containers;

public class Quadruplet<T1, T2, T3, T4> implements IQuadruplet<T1, T2, T3, T4>{

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
}
