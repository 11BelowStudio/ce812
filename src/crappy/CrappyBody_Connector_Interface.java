package crappy;

import crappy.graphics.DrawableCrappyShape;
import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

public interface CrappyBody_Connector_Interface extends I_Transform, IHaveIdentifier{

    Vect2D getPos();

    I_Vect2D getTempPos();

    Vect2D getVel();

    I_Vect2D getTempVel();

    Rot2D getRot();

    I_Rot2D getTempRot();

    double getAngVel();

    double getTempAngVel();

    void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final CrappyBody.FORCE_SOURCE source);

    boolean isActive();

    void __addConnector_internalPlsDontUseManually(final CrappyConnector c);

    void __removeConnector_internalPlsDontUseManually(final CrappyConnector c);


}
