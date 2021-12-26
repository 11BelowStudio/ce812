package crappy.utils.bitmasks;

import java.util.function.Predicate;

public interface BitmaskPredicate extends IHaveBitmask, Predicate<IHaveBitmask> {

    /**
     * Evaluates this predicate on the given argument, by seeing if this.getBitmask & other.getBitmask
     * preserves the initial value of this.getBitmask
     *
     * In other words, if the true bits of this object are true in the bits of the other object, return true.
     * Default implementation doesn't give a fuck about falses.
     *
     * Please feel free to override if you would prefer to give a fuck about falses.
     *
     * @param other the input argument
     *
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     */
    @Override
    default boolean test(final IHaveBitmask other){
        return (this.getBitmask() & other.getBitmask()) == getBitmask();
    }
}
