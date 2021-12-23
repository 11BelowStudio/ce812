package crappy;

import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.shapes.A_CrappyShape;
import crappy.shapes.Crappy_AABB;

public interface I_CrappyBody extends I_Transform {

    Vect2D getVel();

    double getAngVel();

    double getMass();

    Crappy_AABB getAABB();

    A_CrappyShape getShape();

    double getInertia();
}
