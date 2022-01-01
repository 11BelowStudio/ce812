package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.graphics.DrawableCrappyShape;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

/**
 * An interface for a CrappyCircle. This just provides a default implementation for isPointInBounds.
 */
public interface I_CrappyCircle extends I_CrappyShape {


    /**
     * Works out whether a given point is in bounds or not
     *
     * @param worldPoint world position of point
     *
     * @return whether or not it's in bounds.
     * Works this out by finding the vector between the worldPoint and this shape's centroid,
     * then compares the SQUARE magnitude of that to the SQUARE radius of this
     * (so we don't have to faff around with square roots)
     */
    @Override
    default boolean isPointInShape(final I_Vect2D worldPoint) {
        return Vect2DMath.VECTOR_BETWEEN(worldPoint, getCentroid()).magSquared() < getRadiusSquared();
    }
}
