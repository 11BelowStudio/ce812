package crappy.graphics;

import crappy.CrappyBody;
import crappy.CrappyBody_Shape_Interface;
import crappy.collisions.I_CrappyShape;
import crappy.math.Vect2D;

import java.util.Vector;

/**
 * A (group of) read-only interface(s) that only expose copies of certain parts of the shape,
 * which are synchronized and usable mostly risk-free in the rendering loop
 */
public interface DrawableCrappyShape {


    Vect2D getDrawablePos();

    Vect2D getDrawableCentroid();

    Vect2D getDrawableVel();

    /**
     * Obtains the body attached to the shape, juuuust in case someone needs that for some reason
     * @return the body
     */
    CrappyBody_Shape_Interface getBody();

    I_CrappyShape.CRAPPY_SHAPE_TYPE getShapeType();

    default CrappyBody.CRAPPY_BODY_TYPE getBodyType(){ return getBody().getBodyType(); }

    public interface DrawableCircle extends DrawableCrappyShape {

        double getRadius();

        Vect2D getDrawableRot();

    }

    interface DrawableEdge extends DrawableCrappyShape{
        Vect2D getDrawableStart();

        Vect2D getDrawableEnd();

        Vect2D getDrawableNorm();

        DrawableCircle getDrawableEndCircle();

    }

    interface DrawableLine extends DrawableCrappyShape, DrawableEdge{

        Vect2D getDrawableNormEnd();

        DrawableCircle getDrawableOtherEndCircle();
    }

    interface DrawablePolygon extends DrawableCrappyShape{

        /**
         * The drawable incircle
         * @return the drawable inner circle
         */
        DrawableCircle getDrawableIncircle();

        /**
         * Get drawable world vertices
         * @return the array of drawable world vertices
         */
        Vect2D[] getDrawableVertices();

        /**
         * Returns vertex count
         * @return number of vertices
         */
        int getVertexCount();

        Vect2D getDrawableRot();
    }


}
