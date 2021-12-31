package crappy.graphics;


import crappy.math.I_Vect2D;

import java.awt.*;

public interface I_CrappilyRenderStuff {


    /**
     * Please use this to set the I_GraphicsTransform which will be used to
     * work out where all the vertices and such need to go
     * @param g the I_GraphicsTransform
     */
    void setGraphicsTransform(I_GraphicsTransform g);

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

    // TODO: methods that accept shapes, calculate appropriate vertices for them, and call these draw methods

}
