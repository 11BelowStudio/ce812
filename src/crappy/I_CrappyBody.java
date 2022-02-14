package crappy;

import crappy.math.I_Vect2D;
import crappy.utils.bitmasks.IHaveAndConsumeBitmask;

/**
 * An interface for the CrappyBody,
 * offering methods to apply forces to it.
 * @author Rachel Lowe
 */
public interface I_CrappyBody extends I_View_CrappyBody, IHaveAndConsumeBitmask {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyForce(final I_Vect2D force, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final CrappyBody.FORCE_SOURCE source);

    void applyTorque(final double torque, final CrappyBody.FORCE_SOURCE source);

    void applyTorque(final double torque);

    void applyMidTimestepTorque(final double torque, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepTorque(final double torque);

    void applyHitSomethingStatic(final I_Vect2D localCollidePos, final I_Vect2D norm, final double jImpulse);

    /**
     * Overwrites velocity after a collision happens
     * @param newVel the new linear velocity
     * @param newAngVel the new angular velocity
     * @param overwriteSource the source of this overwrite operation
     */
    void overwriteVelocityAfterCollision(final I_Vect2D newVel, final double newAngVel, final CrappyBody.FORCE_SOURCE overwriteSource);

    boolean wasClicked();
}
