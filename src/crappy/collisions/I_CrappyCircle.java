package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.Vect2D;

public interface I_CrappyCircle {

    CrappyBody_Shape_Interface getBody();

    I_Transform getBodyTransform();

    double getRestitution();

    double getMass();

    double getRadius();

    Vect2D getPos();

    Vect2D getVel();

}
