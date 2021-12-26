package crappy.collisions;

import crappy.math.I_Vect2D;

import java.util.Set;

/**
 * An interface for each 'node' of a given AABB QuadTree
 */
public interface AABBQuadTreeNode {

    I_Vect2D getComparisonPoint();

    Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWith(final I_Crappy_AABB attemptBoundingBox);

}
