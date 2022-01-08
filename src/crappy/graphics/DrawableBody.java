package crappy.graphics;

import crappy.CrappyBody;

/**
 * A drawable interface for the CrappyBody
 * @author Rachel Lowe
 */
public interface DrawableBody {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    CrappyBody.CRAPPY_BODY_TYPE getBodyType();

    void drawCrappily(I_CrappilyDrawStuff renderer);

    void updateDrawables();

    CrappyBody getBody();

    DrawableCrappyShape getShape();
}
