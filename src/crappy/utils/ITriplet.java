package crappy.utils;

public interface ITriplet <T1, T2, T3> extends IPair<T1, T2>{

    T1 getFirst();

    T2 getSecond();

    /**
     * Obtains the third item from the triplet
     * @return third item from the triplet.
     */
    T3 getThird();
}
