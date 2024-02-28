package crappy;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

/**
 * An interface which the CrappyConnectors can use to look at the CrappyBodies.
 * @author Rachel Lowe
 */
public interface CrappyBody_Connector_Interface extends I_Transform, IHaveIdentifier{
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Obtains position of this body mid-timestep
     * @return mid-timestamp pos of body
     */
    I_Vect2D getTempPos();

    /**
     * Obtains mid-timestep velocity of this body
     * @return mid-timestep velocity.
     */
    I_Vect2D getTempVel();

    /**
     * Obtains mid-timestep rotation of this body
     * @return mid-timestep rotation.
     */
    I_Rot2D getTempRot();


    /**
     * Obtains mid-timestep angular velocity of this body
     * @return mid-timestep angular velocity.
     */
    double getTempAngVel();

    /**
     * Use this to apply any forces that are considered constant throughout a timestep,
     * and apply it to a specific local position on this body.
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     *
     * If you're not doing stuff within CRAPPY, please use {@link CrappyBody#applyForce(I_Vect2D, I_Vect2D)} instead
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     * @see CrappyBody#applyMidTimestepForce(I_Vect2D, I_Vect2D)
     * @see CrappyBody#canApplyThisForce(CrappyBody.FORCE_SOURCE)
     */
    void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    /**
     * Use this to manually apply any forces that depend on the distance between a point on this body
     * and a point elsewhere, and apply it to a specific local position on this body.
     * Will always be ignored by static bodies, and will only be taken into account by a kinematic body if
     * FORCE_SOURCE is ENGINE.
     * Anyway, if you're not doing stuff within CRAPPY itself, please use
     * {@link CrappyBody#applyMidTimestepForce(I_Vect2D, I_Vect2D)} instead.
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     * @param source Where the force is coming from. If you're moving something yourself, please specify
     *               FORCE_SOURCE.MANUAL, otherwise any Kinematic bodies will ignore it.
     * @see CrappyBody#applyMidTimestepForce(I_Vect2D, I_Vect2D)
     */
    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    /**
     * Is this actually active? (ie not dormant, still affected by all physics, can collide)
     *
     * @return true if is active.
     */
    boolean isActive();

    /**
     * Funny internal engine method for adding a CrappyConnector to a CrappyBody.
     * Do not use this if you are not working within CRAPPY.
     * @param c the CrappyConnector (as CrappyConnectorBodyInterface) being added to this CrappyBody
     */
    void __addConnector_internalPlsDontUseManually(final CrappyConnectorBodyInterface c);

    /**
     * Funny internal engine method for removing a CrappyConnector from a CrappyBody.
     * Do not use this if you are not working within CRAPPY.
     * @param c the CrappyConnector (as CrappyConnectorBodyInterface) being removed from this CrappyBody
     */
    void __removeConnector_internalPlsDontUseManually(final CrappyConnectorBodyInterface c);


}
