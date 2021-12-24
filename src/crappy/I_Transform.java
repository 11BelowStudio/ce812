package crappy;

import crappy.math.Rot2D;
import crappy.math.Vect2D;

/**
 * An interface that just has a getter for world position, world rotation, world velocity, and world angular velocity.
 */
public interface I_Transform {

    /**
     * Get world position of centre of mass
     * @return position
     */
    Vect2D getPos();

    /**
     * Get world rotation of body
     * @return rotation
     */
    Rot2D getRot();

    /**
     * Get linear velocity of body
     * @return linear velocity
     */
    Vect2D getVel();

    /**
     * Get angular velocity of body
     * (expressed as Z axis of this as a 3D vector, aka parallel to axis of rotation, aka the actually important bit)
     * @return angular velocity
     */
    double getAngVel();

}
