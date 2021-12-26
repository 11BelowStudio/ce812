package crappy;

import crappy.math.I_Vect2D;
import crappy.utils.bitmasks.IHaveAndConsumeBitmask;

public interface I_CrappyBody extends I_View_CrappyBody, IHaveAndConsumeBitmask {


    void applyForce(final I_Vect2D force, final I_Vect2D localForcePos);

    void applyForce(final I_Vect2D force);

    void applyDistanceForce(final I_Vect2D force, final I_Vect2D localForcePos);

    void applyDistanceForce(final I_Vect2D force);
}
