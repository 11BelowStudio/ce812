/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.graphics;

import crappy.CrappyBody;

/**
 * A drawable interface for the CrappyBody
 * @author Rachel Lowe
 */
public interface DrawableBody {

    /**
     * Obtains {@link CrappyBody.CRAPPY_BODY_TYPE} of body
     * @return body type
     */
    CrappyBody.CRAPPY_BODY_TYPE getBodyType();

    /**
     * Renders the crappybody with the given renderer
     * @param renderer the renderer which will be used to draw the crappybody
     */
    void drawCrappily(I_CrappilyDrawStuff renderer);

    /**
     * Updates the 'drawable' info about the body
     */
    void updateDrawables();

    /**
     * Obtains the full CrappyBody (in case end-user wants to do something wacky)
     * @return this object as a CrappyBody
     */
    CrappyBody getBody();

    /**
     * The Drawable version of the body's shape
     * @return the drawable shape of the body
     */
    DrawableCrappyShape getShape();
}
