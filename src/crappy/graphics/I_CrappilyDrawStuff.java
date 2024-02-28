/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.graphics;


import crappy.CrappyBody;
import crappy.collisions.*;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

import java.awt.*;


/**
 * Loosely inspired by Box2D's debug draw method, because I'm lazy.
 * This particular implementation of it, however, is completely my own work.
 *
 * Feel free to override the default methods in here!
 *
 * @author Rachel Lowe
 */
public interface I_CrappilyDrawStuff {


    /**
     * You can use this to set the I_GraphicsTransform which will be used to
     * work out where all the vertices and such need to go
     * @param g the I_GraphicsTransform
     */
    default void setGraphicsTransform(I_GraphicsTransform g) {}

    /**
     * Please write this getter for the I_GraphicsTransform which was
     * supposed to be set with {@link #setGraphicsTransform(I_GraphicsTransform)}.
     * @return the current I_GraphicsTransform
     */
    I_GraphicsTransform getGraphicsTransform();

    /**
     * Please draw this OUTLINE OF A circle.
     * @param pos The position of this circle (IN SCREEN COORDINATES!)
     * @param radiusX radius scaled by the appropriate x amount (IN SCREEN SPACE!)
     * @param radiusY radius scaled by the appropriate y scale (IN SCREEN SPACE!)
     * @param col Draw it in this colour.
     */
    void drawCircle(
            final I_Vect2D pos, final double radiusX,
            final double radiusY, final Color col
    );

    /**
     * Please draw this FILLED-IN circle.
     * @param pos The position of this circle (IN SCREEN COORDINATES!)
     * @param radiusX radius scaled by the appropriate x amount (IN SCREEN SPACE!)
     * @param radiusY radius scaled by the appropriate y scale (IN SCREEN SPACE!)
     * @param col Draw it in this colour.
     */
    void drawFilledCircle(
            final I_Vect2D pos, final double radiusX,
            final double radiusY, final Color col
    );

    /**
     * Please draw this OUTLINE OF A polygon.
     * @param pos position of polygon (IN SCREEN COORDINATES!)
     * @param vertices the corners of the polygon (IN SCREEN COORDINATES!)
     * @param col draw it in this colour.
     */
    void drawPolygon(
            final I_Vect2D pos, final I_Vect2D[] vertices,
            final Color col
    );

    /**
     * Please draw this FILLED-IN polygon.
     * @param pos position of polygon (IN SCREEN COORDINATES!)
     * @param vertices the corners of the polygon (IN SCREEN COORDINATES!)
     * @param col draw it in this colour.
     */
    void drawFilledPolygon(
            final I_Vect2D pos, final I_Vect2D[] vertices,
            final Color col
    );

    /**
     * please draw this line
     * @param a first line corner
     * @param b second line corner
     * @param col line colour
     */
    void drawLine(
            final I_Vect2D a, final I_Vect2D b,
            final Color col
    );



    /**
     * Please draw this rectangle
     * @param x top-left x
     * @param y top-left y
     * @param w width
     * @param h height
     * @param col colour
     */
    void drawRectangle(
            final double x, final double y, final double w, final double h, final Color col
    );

    /**
     * Should bounding boxes be drawn?
     * @return true if you want to render bounding boxes.
     */
    default boolean RENDERING_BOUNDING_BOXES(){
        return true;
    }
    /**
     * Should velocity lines be drawn?
     * @return true if you want to render visible velocities.
     */
    default boolean RENDERING_VELOCITIES(){
        return true;
    }
    /**
     * Should rotation lines be drawn?
     * @return true if you want to render visible rotations.
     */
    default boolean RENDERING_ROTATIONS(){
        return true;
    }
    /**
     * Should polygon incircles be drawn?
     * @return true if you want to render polygon incircles.
     */
    default boolean RENDERING_INCIRCLES(){
        return true;
    }
    /**
     * Should line/edge normal vectors be drawn?
     * @return true if you want to render line/edge normal vectors
     */
    default boolean RENDERING_NORMALS(){
        return true;
    }


    /**
     * Use this to draw the axis-aligned bounding box of a DrawableCrappyShape.
     * Feel free to override this default implementation!
     * @param d the DrawableCrappyShape we want to render the AABB of
     */
    default void acceptAABB(DrawableCrappyShape d){
        final I_Crappy_AABB b = d.getBody().getAABB();
        final Vect2D min = getGraphicsTransform().TO_SCREEN_COORDS_V(b.getMin());
        final Vect2D max = getGraphicsTransform().TO_SCREEN_COORDS_V(b.getMax());
        final Vect2D wh = getGraphicsTransform().TO_RAW_SCREEN_SCALE_M(b.getWidthHeight()).finished();
        //drawRectangle(min, getGraphicsTransform().TO_RAW_SCREEN_SCALE_M(b.getWidthHeight()), Color.WHITE);

        drawRectangle(min.x, max.y, wh.x, wh.y, Color.WHITE);
    }

    /**
     * Use this to draw the circle of a DrawableCrappyShape.DrawableCircle.
     * Feel free to override this default implementation!
     * @param c the DrawableCrappyShape.DrawableCircle we want to render
     */
    default void acceptCircle(final DrawableCrappyShape.DrawableCircle c){

        
        double yRad = getGraphicsTransform().convertWorldHeightToScreenHeight(c.getRadius());
        double xRad = getGraphicsTransform().convertWorldLengthToScreenLength(c.getRadius());

        final Vect2D screenPos = getGraphicsTransform().TO_SCREEN_COORDS_V(c.getDrawablePos());

        if (RENDERING_BOUNDING_BOXES()) {
            acceptAABB(c);
        }

        drawFilledCircle(screenPos, xRad, yRad, DEFAULT_COLORS.SELECT_COLOR_BODY(c.getShapeType(), c.getBody().getBodyType(), true));
        drawCircle(screenPos, xRad, yRad, DEFAULT_COLORS.SELECT_COLOR_BODY(c.getShapeType(), c.getBody().getBodyType(), false));
        if (RENDERING_ROTATIONS()) {
            drawLine(screenPos, getGraphicsTransform().TO_SCREEN_COORDS_V(c.getDrawablePos().add(Vect2DMath.INVERT_X(c.getDrawableRot()))), Color.CYAN);
        }
        if (RENDERING_VELOCITIES()) {
            drawLine(screenPos, getGraphicsTransform().TO_SCREEN_COORDS_V(c.getDrawablePos().add(c.getDrawableVel())), Color.RED);
        }
    }


    /**
     * Use this to draw the polygon of a DrawableCrappyShape.DrawablePolygon.
     * Feel free to override this default implementation!
     * @param p the DrawableCrappyShape.DrawablePolygon we want to render
     */
    default void acceptPolygon(final DrawableCrappyShape.DrawablePolygon p){

        final Vect2D screenPos = getGraphicsTransform().TO_SCREEN_COORDS_V(p.getDrawablePos());

        final Vect2D[] screenVertices = p.getDrawableVertices();

        for (int i = p.getVertexCount()-1; i >= 0; i--){
            screenVertices[i] = getGraphicsTransform().TO_SCREEN_COORDS_V(screenVertices[i]);
        }

        if (RENDERING_BOUNDING_BOXES()) {
            acceptAABB(p);
        }

        drawFilledPolygon(screenPos, screenVertices, DEFAULT_COLORS.SELECT_COLOR_BODY(p.getShapeType(), p.getBody().getBodyType(), true));

        if (RENDERING_INCIRCLES()) {
            acceptCircle(p.getDrawableIncircle());
        }
        drawPolygon(screenPos, screenVertices, DEFAULT_COLORS.SELECT_COLOR_BODY(p.getShapeType(), p.getBody().getBodyType(), false));

        if (RENDERING_ROTATIONS()) {
            drawLine(screenPos, getGraphicsTransform().TO_SCREEN_COORDS_V(p.getDrawablePos().add(Vect2DMath.INVERT_X(p.getDrawableRot()))), Color.CYAN);
        }
        if (RENDERING_VELOCITIES()) {
            drawLine(screenPos, getGraphicsTransform().TO_SCREEN_COORDS_V(p.getDrawablePos().add(p.getDrawableVel())), Color.RED);
        }
    }

    /**
     * Use this to draw the edge of a DrawableCrappyShape.DrawableEdge.
     * Feel free to override this default implementation!
     * @param e the DrawableCrappyShape.DrawableEdge we want to render
     */
    default void acceptEdge(DrawableCrappyShape.DrawableEdge e){

        if (RENDERING_BOUNDING_BOXES()) {
            acceptAABB(e);
        }

        final Vect2D screenStart = getGraphicsTransform().TO_SCREEN_COORDS_V(e.getDrawableStart());
        final Vect2D screenEnd = getGraphicsTransform().TO_SCREEN_COORDS_V(e.getDrawableEnd());

        if (RENDERING_NORMALS()) {
            final Vect2D screenNorm = getGraphicsTransform().TO_SCREEN_COORDS_V(e.getDrawableNorm());
            drawLine(getGraphicsTransform().TO_SCREEN_COORDS_V(e.getDrawableCentroid()), screenNorm, DEFAULT_COLORS.SELECT_COLOR_BODY(e.getShapeType(), e.getBody().getBodyType(), true));
        }

        acceptCircle(e.getDrawableEndCircle());
        drawLine(screenStart, screenEnd, DEFAULT_COLORS.SELECT_COLOR_BODY(e.getShapeType(), e.getBody().getBodyType(), false));
    }

    /**
     * Use this to draw the line of a DrawableCrappyShape.DrawableLine.
     * Feel free to override this default implementation!
     * @param l the DrawableCrappyShape.DrawableLine we want to render
     */
    default void acceptLine(DrawableCrappyShape.DrawableLine l){

        if (RENDERING_BOUNDING_BOXES()) {
            acceptAABB(l);
        }

        final Vect2D screenStart = getGraphicsTransform().TO_SCREEN_COORDS_V(l.getDrawableStart());
        final Vect2D screenEnd = getGraphicsTransform().TO_SCREEN_COORDS_V(l.getDrawableEnd());


        acceptCircle(l.getDrawableEndCircle());
        acceptCircle(l.getDrawableOtherEndCircle());

        if (RENDERING_NORMALS()) {
            final Vect2D screenNormA = getGraphicsTransform().TO_SCREEN_COORDS_V(l.getDrawableNorm());
            final Vect2D screenNormB = getGraphicsTransform().TO_SCREEN_COORDS_V(l.getDrawableNormEnd());
            drawLine(screenNormA, screenNormB, DEFAULT_COLORS.SELECT_COLOR_BODY(l.getShapeType(), l.getBody().getBodyType(), true));
        }
        drawLine(screenStart, screenEnd, DEFAULT_COLORS.SELECT_COLOR_BODY(l.getShapeType(), l.getBody().getBodyType(), false));
    }

    /**
     * Use this to draw the visuals of a DrawableConnector.
     * Feel free to override this default implementation!
     * @param c the DrawableConnector we want to render
     */
    default void acceptConnector(final DrawableConnector c){

        final Vect2D p1 = c.getDrawableAPos();

        final Vect2D p2 = c.getDrawableBPos();

        // the hue of the connector will be based on the colour of the shape.
        // more stretched = red, more compressed = green, at natural length = yellow
        double h = Vect2DMath.DIST(p1, p2)/ Vect2DMath.RETURN_1_IF_0(c.getNaturalLength());
        if (h < 0.5){
            h = 0.5;
        } else if (h > 1.5){
            h = 1.5;
        }
        h = (h - 0.5)/3f;

        drawLine(
                getGraphicsTransform().TO_SCREEN_COORDS_V(p1),
                getGraphicsTransform().TO_SCREEN_COORDS_V(p2),
                Color.getHSBColor(
                        (float) h,
                        1f,
                        1f
                )
        );
    }


    /**
     * Uses this renderer to draw the given body
     * @param d DrawableBody we want to draw
     */
    default void drawThisBody(final DrawableBody d){ d.drawCrappily(this); }

    /**
     * Wrapper for the default colours used by the default renderer methods
     */
    public static enum DEFAULT_COLORS{
        ;

        static final Color STATIC_FILLED = new Color(210, 200, 200, 128);
        static final Color STATIC_HOLLOW = new Color(220, 210, 210, 255);
        static final Color DYN_CIRCLE_FILLED = new Color(150, 200, 150, 128);
        static final Color DYN_CIRCLE_HOLLOW = new Color(150, 250, 200, 255);
        static final Color DYN_POLY_FILLED = new Color(150, 150, 200, 128);
        static final Color DYN_POLY_HOLLOW = new Color(200, 150, 255, 255);
        static final Color DYN_LINE_FILLED = new Color(200, 200, 100, 128);
        static final Color DYN_LINE_HOLLOW = new Color(250, 250, 75, 255);
        static final Color KIN_CIRCLE_FILLED = new Color(75, 75, 200, 128);
        static final Color KIN_CIRCLE_HOLLOW = new Color(75, 75, 255, 255);
        static final Color KIN_POLY_FILLED = new Color(200, 150, 150, 128);
        static final Color KIN_POLY_HOLLOW = new Color(255, 100, 100, 255);
        static final Color KIN_LINE_FILLED = new Color(100, 200, 100, 128);
        static final Color KIN_LINE_HOLLOW = new Color(55, 200, 55, 255);


        public static Color SELECT_COLOR_BODY(I_CrappyShape.CRAPPY_SHAPE_TYPE stype, CrappyBody.CRAPPY_BODY_TYPE btype, boolean filled){

            switch (btype){
                case STATIC:
                    if (filled){
                        return STATIC_FILLED;
                    } else {
                        return STATIC_HOLLOW;
                    }
                case DYNAMIC:
                    switch (stype){
                        case CIRCLE:
                            if (filled){
                                return DYN_CIRCLE_FILLED;
                            } else {
                                return DYN_CIRCLE_HOLLOW;
                            }
                        case POLYGON:
                            if (filled){
                                return DYN_POLY_FILLED;
                            } else {
                                return DYN_POLY_HOLLOW;
                            }
                        case EDGE:
                        case LINE:
                            if (filled){
                                return DYN_LINE_FILLED;
                            } else {
                                return DYN_LINE_HOLLOW;
                            }
                    }
                case KINEMATIC:
                    switch (stype){
                        case CIRCLE:
                            if (filled){
                                return KIN_CIRCLE_FILLED;
                            } else {
                                return KIN_CIRCLE_HOLLOW;
                            }
                        case POLYGON:
                            if (filled){
                                return KIN_POLY_FILLED;
                            } else {
                                return KIN_POLY_HOLLOW;
                            }
                        case LINE:
                        case EDGE:
                            if (filled){
                                return KIN_LINE_FILLED;
                            } else {
                                return KIN_LINE_HOLLOW;
                            }
                    }
            }
            return Color.MAGENTA;
        }
    }


}
