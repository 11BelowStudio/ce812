package crappy.utils.bitmasks;

/**
 * An interface that consumes 'IHaveBitmask' instances.
 */
@FunctionalInterface
public interface BitmaskConsumer{

    /**
     * Consumes an IHaveBitmask.
     * INTENDED TO HAVE SIDE EFFECTS ON THIS OBJECT!
     * @param bitmaskHaver the IHaveBitmask to consume.
     */
    void consumeBitmask(final IHaveBitmask bitmaskHaver);
}