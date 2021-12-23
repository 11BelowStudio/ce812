package crappy;

import crappy.math.Rot2D;
import crappy.math.Vect2D;

/**
 * An interface that just has a getter for a position and a velocity
 */
public interface I_Transform {

    Vect2D getPos();

    Rot2D getRot();

}
