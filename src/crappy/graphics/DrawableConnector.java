/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.graphics;

import crappy.math.Vect2D;

/**
 * A drawable interface for the CrappyConnector
 *
 * @author Rachel Lowe
 */
public interface DrawableConnector {

    /**
     * obtains the drawable position of the end of the connector connected to Body B
     * @return drawable position of the end of the connector connected to Body B
     */
    Vect2D getDrawableAPos();

    /**
     * obtains the drawable position of the end of the connector connected to Body A
     * @return drawable position of the end of the connector connected to Body A
     */
    Vect2D getDrawableBPos();

    /**
     * Returns the natural length of this connector
     * @return connector natural length
     */
    double getNaturalLength();

    /**
     * Updates the drawablePosA and drawablePosB to results of
     * {@link crappy.CrappyConnector#bodyAWorldPos()} and {@link crappy.CrappyConnector#bodyBWorldPos()}
     */
    void updateDrawables();
}
