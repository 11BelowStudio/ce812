package crappy.utils.bitmasks;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * An interface that consumes 'IHaveBitmask' instances.
 *
 * @author Rachel Lowe
 */
@FunctionalInterface
public interface BitmaskConsumer extends Consumer<IHaveBitmask> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Consumes an IHaveBitmask.
     * INTENDED TO HAVE SIDE EFFECTS ON THIS OBJECT!
     * @param bitmaskHaver the IHaveBitmask to consume.
     */
    void accept(final IHaveBitmask bitmaskHaver);


}