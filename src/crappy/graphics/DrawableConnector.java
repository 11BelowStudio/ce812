package crappy.graphics;

import crappy.math.Vect2D;

/**
 * A drawable interface for the CrappyConnector
 *
 * @author Rachel Lowe
 */
public interface DrawableConnector {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    Vect2D getDrawableAPos();

    Vect2D getDrawableBPos();

    double getNaturalLength();

    void updateDrawables();
}
