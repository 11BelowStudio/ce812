package crappy;

import crappy.shapes.Crappy_AABB;

public interface CrappyBody_Shape_Interface extends I_View_CrappyBody, I_Transform, I_CrappyBody {

    void setMomentOfInertia(final double moment);

}
