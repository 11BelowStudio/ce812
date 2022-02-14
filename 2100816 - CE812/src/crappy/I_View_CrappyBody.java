/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.collisions.A_CrappyShape;
import crappy.collisions.Crappy_AABB;
import crappy.utils.bitmasks.IHaveBitmask;

/**
 * A view-only interface of the CrappyBody.
 *
 * @author Rachel Lowe
 */
public interface I_View_CrappyBody extends I_Transform, IHaveBitmask, IHaveIdentifier {

    /**
     * mid-timestep position
     * @return mid-timestep position
     */
    I_Vect2D getTempPos();

    /**
     * Obtains previous position of the body, before most recent getPos position.
     * @return prior position
     */
    I_Vect2D getLastPos();

    /**
     * Obtains previous rotation of the body, before most recent getRot rotation
     * @return prior rotation
     */
    I_Rot2D getLastRot();

    /**
     * mid-timestep rotation
     * @return mid-timestep rotation
     */
    I_Rot2D getTempRot();

    /**
     * mid-timestep linear velocity
     * @return mid-timestep linear velocity
     */
    I_Vect2D getTempVel();

    /**
     * mid-timestep angular velocity
     * @return mid-timestep angular velocity
     */
    double getTempAngVel();

    /**
     * mass
     * @return the mass
     */
    double getMass();

    /**
     * collision bounding box
     * @return bounding box
     */
    Crappy_AABB getAABB();

    /**
     * Collision shape
     * @return collision shape
     */
    A_CrappyShape getShape();

    /**
     * Moment of inertia
     * @return moment of inertia
     */
    double getMomentOfInertia();

    /**
     * body type
     * @return static? dynamic? kinematic?
     */
    CrappyBody.CRAPPY_BODY_TYPE getBodyType();

    /**
     * Coefficient of restitution for crappybody
     * @return restitution
     */
    double getRestitution();

    /**
     * Obtains user data object of CrappyBody
     * @return user data
     */
    Object getUserData();

    /**
     * Obtains name of crappybody
     * @return name
     */
    String getName();

    /**
     * Is this actually tangible (able to give/receive collision forces)?
     * intangible objects can still collide, but not with forces.
     * @return true if yes, false if no
     */
    boolean isTangible();

    /**
     * Is this actually active? (ie not dormant, still affected by all physics, can collide)
     * @return true if is active.
     */
    boolean isActive();

    /**
     * Checks if this object is actually allowed to collide with the other object
     * @param other the other object
     * @return true if they can collide, otherwise false.
     */
    boolean allowedToCollideWith(final I_View_CrappyBody other);

    /**
     * Whether or not this body has been discarded.
     * DISCARDED BODIES SHOULD NEVER BE USED AGAIN!
     * @return whether or not you should run away from this body ASAP.
     */
    boolean isDiscarded();



    class I_CrappyBody_Temp_Transform implements I_Transform{

        private final I_View_CrappyBody cb;

        private Vect2D tempPos;

        private Vect2D tempVel;

        private Rot2D tempRot;

        private double tempAngVel;

        I_CrappyBody_Temp_Transform(final I_View_CrappyBody cb){

            this.cb = cb;
            update();
        }

        void update(){
            tempPos = cb.getTempPos().toVect2D();
            tempVel = cb.getTempVel().toVect2D();
            tempRot = cb.getTempRot().toRot2D();
            tempAngVel = cb.getTempAngVel();
        }

        @Override
        public Vect2D getPos() {
            return tempPos;
        }

        @Override
        public Rot2D getRot() {
            return tempRot;
        }

        @Override
        public Vect2D getVel() {
            return tempVel;
        }

        @Override
        public double getAngVel() {
            return tempAngVel;
        }

        /**
         * Returns the 'root' transform of the crappybody this is a temp transform of
         * @return the 'root' transform of the attached crappybody.
         */
        @Override
        public I_Transform getRootTransform(){
            return cb;
        }

        @Override
        public String toString() {
            return "I_CrappyBody_Temp_Transform{" +
                    ", tempPos=" + tempPos +
                    ", tempVel=" + tempVel +
                    ", tempRot=" + tempRot +
                    ", tempAngVel=" + tempAngVel +
                    '}';
        }
    }
}
