package crappy;

import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.I_Vect2D;
import crappy.utils.PendingStateChange;

/**
 * CrappyBody interface for the CrappyWorld
 * @author Rachel Lowe
 */
public interface I_CrappyBody_CrappyWorld_Interface extends I_CrappyBody, I_View_CrappyBody, I_ManipulateCrappyBody, CrappyBody_Shape_Interface{
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Call this to tell this I_CrappyBody to call the {@link CrappyCallbackHandler#acceptCollidedWithBitmaskAfterAllCollisions(int)} method
     * in its {@link CrappyCallbackHandler}.
     * @see CrappyBody#callbackHandler
     */
    void performPostCollisionBitmaskCallback();

    /**
     * Resolve any changes to this body's 'active' state
     * @return change made to the body's active state
     */
    PendingStateChange resolveActiveChange();

    /**
     * Resolve any potential changes to this body's removal state
     * @return true if it needs to be removed
     */
    boolean resolveRemovalChange();

    /**
     * Resolve any potential changes needed to this body's position lock change
     * @return change made to position lock
     */
    PendingStateChange resolvePositionLockChange();

    /**
     * Resolve any potential changes needed to this body's rotation lock change
     * @return change made to rotation lock
     */
    PendingStateChange resolveRotationLockChange();

    /**
     * Resolve any potential changes needed to this body's tangibility
     * @return change made to tangibility.
     */
    PendingStateChange resolveTangibilityChange();

    /**
     * Call this before the first euler substep.
     * Will immediately call {@link #euler_substep(double)} with subDeltaT
     * @param deltaT raw deltaT.
     * @param gravity constant gravitational vector (a constant linear acceleration)
     * @param subDelta substep deltaT.
     */
    void first_euler_sub_update(final double deltaT, final I_Vect2D gravity, final double subDelta);

    /**
     * Handles the individual euler-substep stuff
     * @param subDelta substep deltaT
     */
    void euler_substep(final double subDelta);

    void clearAllPendingForces();

    void applyAllTempChanges();

    void crappilyRenderBody(I_CrappilyDrawStuff renderer);

    void updateDrawables();

    void doneEulers();

    void handleStuffBeforeFirstEulerUpdate();

    void resolveStuffAfterLastEulerUpdate();

    /**
     * DO NOT CALL THIS YOURSELF EVER.
     *
     * this is here so CRAPPY can tell a body that it's getting rid of it.
     */
    void $_$_$__discard_INTERNAL_USE_ONLY_DO_NOT_USE_YOURSELF_EVER_SERIOUSLY_DONT_GRRR();
}
