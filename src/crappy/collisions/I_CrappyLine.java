package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

/**
 * Interface for the CrappyLine, with methods to get both of the overlapping CrappyEdges.
 */
public interface I_CrappyLine extends I_CrappyShape, Iterable<I_CrappyEdge>{

    /**
     * Returns the first crappyedge
     * @return the first crappyedge
     */
    public I_CrappyEdge getEdgeA();

    /**
     * Returns the second crappyedge
     * @return the second crappyedge
     */
    public I_CrappyEdge getEdgeB();

    Vect2D getWorldStart();

    Vect2D getWorldEnd();

    /**
     * Lines don't really contain points, but checks if the point is close enough
     * @param worldPoint the point we're checking
     * @return true if the point is close enough
     */
    @Override
    default boolean isPointInShape(final I_Vect2D worldPoint){
        return Vect2DMath.IS_POINT_ON_LINE_END(worldPoint, getWorldStart(), getWorldEnd());
    }
}
