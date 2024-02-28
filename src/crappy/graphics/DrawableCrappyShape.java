/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.graphics;

import crappy.CrappyBody;
import crappy.CrappyBody_Shape_Interface;
import crappy.collisions.I_CrappyShape;
import crappy.math.Vect2D;

/**
 * A (group of) read-only interface(s) that only expose copies of certain parts of the shape,
 * which are synchronized and usable mostly risk-free in the rendering loop
 * @author Rachel Lowe
 */
public interface DrawableCrappyShape {

    /**
     * 'drawable' pos of the shape
     * @return 'drawable' pos of the shape
     */
    Vect2D getDrawablePos();

    /**
     * location of the centroid of the 'drawable' shape
     * @return 'drawable' shape centroid world pos
     */
    Vect2D getDrawableCentroid();

    /**
     * current velocity of the 'drawable' shape
     * @return the 'drawable' shape's velocity
     */
    Vect2D getDrawableVel();

    /**
     * Obtains the body attached to the shape, juuuust in case someone needs that for some reason
     * @return the body
     */
    CrappyBody_Shape_Interface getBody();

    /**
     * What type of shape is this?
     * @return the shape type of this shape
     */
    I_CrappyShape.CRAPPY_SHAPE_TYPE getShapeType();

    /**
     * What type of body is this shape attached to?
     * @return body type of this shape
     */
    default CrappyBody.CRAPPY_BODY_TYPE getBodyType(){ return getBody().getBodyType(); }

    /**
     * Specialized implementation for circles
     */
    public interface DrawableCircle extends DrawableCrappyShape {

        /**
         * Circle radius
         * @return radius of the drawable circle
         */
        double getRadius();
        /**
         * Returns drawable rotation
         * @return current rotation of shape
         */
        Vect2D getDrawableRot();

    }
    /**
     * Specialized implementation for edges
     */
    interface DrawableEdge extends DrawableCrappyShape{

        /**
         * World pos of 'drawable' line start
         * @return world pos of where to start drawing the line
         */
        Vect2D getDrawableStart();

        /**
         * World pos of 'drawable' line end
         * @return world pos of where to stop drawing the line
         */
        Vect2D getDrawableEnd();

        /**
         * World pos of 'drawable' normal vector (line from drawablestart to here)
         * @return world pos of where to stop drawing the line which indicates the edge's normal vector
         */
        Vect2D getDrawableNorm();

        /**
         * The drawable 'end circle' around the end of the line (for collision handling purposes)
         * @return drawable 'end circle'
         */
        DrawableCircle getDrawableEndCircle();

    }
    /**
     * Specialized implementation for lines
     */
    interface DrawableLine extends DrawableCrappyShape, DrawableEdge{

        /**
         * World pos of the other 'drawable' normal vector (line from drawableend to here)
         * @return world pos of where to stop drawing the line which indicates the edge's other normal vector
         */
        Vect2D getDrawableNormEnd();

        /**
         * The other 'end circle' around the other end of the line (again, collision handling purposes)
         * @return the other drawable 'end circle'
         */
        DrawableCircle getDrawableOtherEndCircle();
    }
    /**
     * Specialized implementation for polygons
     */
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

        /**
         * Returns drawable rotation
         * @return current rotation of shape
         */
        Vect2D getDrawableRot();
    }


}
