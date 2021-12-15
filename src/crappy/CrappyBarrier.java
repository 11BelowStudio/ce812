package crappy;

import crappy.utils.Pair;

/**
 * A static barrier of some description.
 */
public abstract class CrappyBarrier {


    public abstract boolean doBoundingBoxesOverlap(final I_Crappy_AABB aabb);

    public abstract Pair<Vect2D, Rot2D> calculateStateAfterCollision(final CrappyBody otherBody, final double delta);
}
