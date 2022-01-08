package crappy.utils.bitmasks;

import java.util.function.Predicate;

/**
 * Wrapper for a thing with a bitmask that is also a predicate on other things that have bitmasks.
 *
 * @author Rachel Lowe
 */
public interface BitmaskPredicate extends IHaveBitmask, Predicate<IHaveBitmask> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

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
