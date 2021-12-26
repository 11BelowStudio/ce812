package crappy.utils.bitmasks;

/**
 * An interface providing a view of a bitmask belonging to a class
 */
@FunctionalInterface
public interface IHaveBitmask {

    /**
     * Obtains the bitmask of the object of the implementing class
     * @return the bitmask of this instance
     */
    int getBitmask();

}

