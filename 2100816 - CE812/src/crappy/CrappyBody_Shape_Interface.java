package crappy;

import crappy.graphics.DrawableBody;
import crappy.math.I_Vect2D;

/**
 * An interface for the CrappyShapes to access the CrappyBody with.
 *
 * @author Rachel Lowe
 */
public interface CrappyBody_Shape_Interface extends I_View_CrappyBody, I_Transform, I_CrappyBody, DrawableBody {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */




    /**
     * Called to notify the CrappyBody that it has, in fact, collided with something.
     * @param collidedWith the body it collided with.
     */
    void notifyAboutCollision(final I_View_CrappyBody collidedWith);

    /**
     * Call this to tell this I_CrappyBody to call the {@link CrappyCallbackHandler#acceptCollidedWithBitmaskAfterAllCollisions(int)} method
     * in its {@link CrappyCallbackHandler}.
     * @see CrappyBody#callbackHandler
     */
    void performPostCollisionBitmaskCallback();

    /**
     * Returns manipulatable interface of this body.
     * @return I_ManipulateCrappyBody view of this object CrappyBody
     */
    I_ManipulateCrappyBody getManipulatable();



}
