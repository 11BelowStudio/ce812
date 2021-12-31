package crappy;

import crappy.math.I_Vect2D;
import crappy.utils.PendingStateChange;

public interface I_CrappyBody_CrappyWorld_Interface extends I_CrappyBody, I_View_CrappyBody, I_ManipulateCrappyBody{

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

    void euler_substep(final double subDelta);

    void clearAllPendingForces();

    void applyAllTempChanges();
}
