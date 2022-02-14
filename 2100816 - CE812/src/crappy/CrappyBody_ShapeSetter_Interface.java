package crappy;

import crappy.collisions.A_CrappyShape;

/**
 * Interface used only in the constructors for the CrappyShapes
 * so they can give themselves to the bodies they're meant to attach to.
 *
 * @author Rachel Lowe
 */
public interface CrappyBody_ShapeSetter_Interface extends CrappyBody_Shape_Interface{
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    /**
     * Call this once to set the shape of this object
     * @param shape the shape of this object
     */
    void __setShape__internalDoNotCallYourselfPlease(final A_CrappyShape shape, final double momentOfInertia);
}
