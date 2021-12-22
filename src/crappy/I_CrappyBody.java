package crappy;

import crappy.math.Rot2D;
import crappy.shapes.Crappy_AABB;

public interface I_CrappyBody {

    Vect2D getPosition();

    Vect2D getVelocity();

    Rot2D getRotation();

    Crappy_AABB getBoundingBox();
}
