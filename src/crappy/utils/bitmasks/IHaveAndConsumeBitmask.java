package crappy.utils.bitmasks;

/**
 * Interface that combines IHaveBitmask and BitmaskConsumer,
 * and also provides a method to see if there are any matches when trying
 * to perform an AND operation on this object and another IHaveBitmask instance
 */
public interface IHaveAndConsumeBitmask extends IHaveBitmask, BitmaskConsumer {

    /**
     * Do any bits in this object's bitmask and the other object's bitmask match?
     * @param other the other object with a bitmask
     * @return true if there are any 1s in the result of anding this bitmask with the other one.
     */
    default boolean anyMatchInBitmasks(final IHaveBitmask other){
        return (getBitmask() & other.getBitmask()) > 0;
    }
}
