package crappy;

import crappy.collisions.A_CrappyShape;

public interface CrappyBody_ShapeSetter_Interface extends CrappyBody_Shape_Interface{

    /**
     * Call this once to set the shape of this object
     * @param shape the shape of this object
     */
    void __setShape__internalDoNotCallYourselfPlease(final A_CrappyShape shape, final double momentOfInertia);
}
