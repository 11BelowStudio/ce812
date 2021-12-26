package crappy;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

public interface CrappyBody_Connector_Interface extends I_Transform{

    Vect2D getPos();

    I_Vect2D getTempPos();

    Vect2D getVel();

    I_Vect2D getTempVel();

    Rot2D getRot();

    I_Rot2D getTempRot();

    double getAngVel();

    double getTempAngVel();

    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);


}
