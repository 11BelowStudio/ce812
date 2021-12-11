package crappy.utils;

public class Pair<T1, T2> implements IPair<T1, T2> {

    public final T1 first;

    public final T2 second;

    public Pair(final T1 t1, final T2 t2){
        first = t1;
        second = t2;
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
