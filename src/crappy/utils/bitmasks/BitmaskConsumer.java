package crappy.utils.bitmasks;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * An interface that consumes 'IHaveBitmask' instances.
 *
 */
@FunctionalInterface
public interface BitmaskConsumer extends Consumer<IHaveBitmask> {

    /**
     * Consumes an IHaveBitmask.
     * INTENDED TO HAVE SIDE EFFECTS ON THIS OBJECT!
     * @param bitmaskHaver the IHaveBitmask to consume.
     */
    void accept(final IHaveBitmask bitmaskHaver);


}