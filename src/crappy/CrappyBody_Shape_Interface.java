package crappy;

import crappy.math.Vect2D;
import crappy.shapes.Crappy_AABB;

public interface CrappyBody_Shape_Interface extends I_CrappyBody, I_Transform {

    void setAABB(final Crappy_AABB aabb);



}
