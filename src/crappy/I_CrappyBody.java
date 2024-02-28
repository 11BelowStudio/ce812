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

    /**
     * Use this to apply a MANUAL force that depends on the distance between this body and somewhere else,
     * but applied to (0, 0) on this body.
     * Will be ignored by static bodies.
     * @param force the force to apply to point (0, 0) on this body.
     */
    default void applyMidTimestepForce(final I_Vect2D force){
        applyMidTimestepForce(force, CrappyBody.FORCE_SOURCE.MANUAL);
    }

    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final CrappyBody.FORCE_SOURCE source);

    /**
     * Like {@link #applyTorque(double, CrappyBody.FORCE_SOURCE)} but source is manual. Please use this.
     * @param torque torque to apply throughout timestep
     */
    default void applyTorque(final double torque){
        applyTorque(torque, CrappyBody.FORCE_SOURCE.MANUAL);
    }

    void applyTorque(final double torque, final CrappyBody.FORCE_SOURCE source);

    /**
     * Applies MANUAL torque to this object during sub-timestep.
     * @param torque torque to apply
     */
    default void applyMidTimestepTorque(final double torque){
        applyMidTimestepTorque(torque, CrappyBody.FORCE_SOURCE.MANUAL);
    }

    void applyMidTimestepTorque(final double torque, final CrappyBody.FORCE_SOURCE source);


    /**
     * Applies appropriate forces for when this hit something static
     * @param localCollidePos local position (on this object) of the collision
     * @param norm normal vector for the collision
     * @param jImpulse Collision impulse (to apply to this)
     */
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
