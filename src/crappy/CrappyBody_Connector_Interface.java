package crappy;

import crappy.math.Rot2D;
import crappy.math.Vect2D;

public interface CrappyBody_Connector_Interface extends I_Transform {

    Vect2D getPos();

    Vect2D getVel();

    Rot2D getRot();

    double getAngVel();

    void applyDistanceForce(final Vect2D force, final Vect2D localForcePos);
}
