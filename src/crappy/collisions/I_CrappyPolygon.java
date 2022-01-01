package crappy.collisions;

import crappy.math.Vect2D;

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
     * (largest circle from centroid which is full enclosed within the shape)
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

}
