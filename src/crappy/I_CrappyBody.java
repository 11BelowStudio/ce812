package crappy;

public interface I_CrappyBody {

    Vect2D getPosition();

    Vect2D getVelocity();

    Rot2D getRotation();

    Crappy_AABB getBoundingBox();
}
