package crappy;

import crappy.math.I_Vect2D;
import crappy.utils.bitmasks.IHaveAndConsumeBitmask;

/**
 * An interface for the CrappyBody,
 * offering methods to apply forces to it.
 */
public interface I_CrappyBody extends I_View_CrappyBody, IHaveAndConsumeBitmask {


    void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyForce(final I_Vect2D force, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final CrappyBody.FORCE_SOURCE source);

    void applyTorque(final double torque, final CrappyBody.FORCE_SOURCE source);

    void applyTorque(final double torque);

    void applyMidTimestepTorque(final double torque, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepTorque(final double torque);
}
