package crappy.utils.bitmasks;

/**
 * Interface that combines IHaveBitmask and BitmaskConsumer,
 * and also provides a method to see if there are any matches when trying
 * to perform an AND operation on this object and another IHaveBitmask instance
 * @author Rachel Lowe
 */
public interface IHaveAndConsumeBitmask extends IHaveBitmask, BitmaskConsumer {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Do any bits in this object's bitmask and the other object's bitmask match?
     * @param other the other object with a bitmask
     * @return true if there are any 1s in the result of anding this bitmask with the other one.
     */
    default boolean anyMatchInBitmasks(final IHaveBitmask other){
        return anyMatchInBitmasks(other.getBitmask());
    }
    /**
     * Do any bits in this object's bitmask and the other given bitmask match?
     * @param otherbm the other bitmask
     * @return true if there are any 1s in the result of anding this bitmask with the other one.
     */
    default boolean anyMatchInBitmasks(final int otherbm){
        return (getBitmask() & otherbm) > 0;
    }
}
