package crappy.collisions;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

/**
 * Interface for CrappyPolygons
 */
public interface I_CrappyPolygon extends I_CrappyShape, Iterable<I_CrappyEdge> {

    /**
     * Obtains the 'whiskers' from the centroid in world rotation
     * @return the rotated 'whiskers' between centroid and vertices
     */
    public Vect2D[] getWorldWhiskers();

    /**
     * Obtains the normalized 'whiskers' in world rotation
     * @return the normalized versions of the whiskers
     */
    public Vect2D[] getWorldNormalWhiskers();

    /**
     * Get world whisker at given index
     * @param i index of world whisker to get
     * @return whisker at index i of getWorldWhiskers
     */
    default Vect2D getWorldWhisker(final int i){
        return getWorldWhiskers()[i];
    }

    /**
     * Get normalized world whisker at given index
     * @param i index of normalized world whisker to get
     * @return whisker at index i of getWorldNormalWhiskers
     */
    default Vect2D getWorldNormalWhisker(final int i){
        return getWorldNormalWhiskers()[i];
    }

    /**
     * Returns vertex count
     * @return number of vertices
     */
    public int getVertexCount();

    /**
     * Returns the incircle of this polygon
     * (largest circle from centroid which is fully enclosed within the shape)
     * @return the incircle.
     */
    I_CrappyCircle getIncircle();

    /**
     * obtains radius of the incircle
     * @return incircle radius
     */
    double getIncircleRadius();

    /**
     * Obtains a copy of world vertices
     * @return array with copy of world vertices in it
     */
    Vect2D[] getWorldVertices();

    /**
     * checks if a given point in world coordinates is within this polygon's (real, polygonal) bounds as described
     * by {@link #getWorldVertices()}
     * @param p the point
     * @return true if that point is within the shape described by {@link #getWorldVertices()}
     */
    default boolean isWorldPointInPolyBounds(final I_Vect2D p){
        return Vect2DMath.IS_POINT_IN_POLYGON(p, getWorldVertices());
    }


    /**
     * Get a copy of the local vertices but rotated to match the given rotation (but not translated)
     * @param rot how much to rotate them by about (0,0)?
     * @return rotated version of locals list
     */
    Vect2D[] getRotatedLocals(final I_Rot2D rot);


    /**
     * Gets a circle representation of this polygon, from local (0, 0) to wherever in local coords worldCollisionPos is
     * @param worldCollisionPos
     * @return
     */
    I_CrappyCircle getCircleForWorldCollisionPos(final I_Vect2D worldCollisionPos);

    /**
     * Returns a circle representing this polygon, but only extends as far as localPos from the origin.
     * @param localPos
     * @return
     */
    I_CrappyCircle getCircleForLocalCollisionPos(final I_Vect2D localPos);

}
