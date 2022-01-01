package crappyGame;

import crappy.graphics.I_CrappilyDrawStuff;
import crappy.graphics.I_GraphicsTransform;
import crappy.math.I_Vect2D;

import java.awt.*;

public class MyRenderer implements I_CrappilyDrawStuff {

    private I_GraphicsTransform gTransform;

    private Graphics2D g;

    public void getCurrentGraphics(Graphics2D g){
        // TODO: work out how this is going to actually be able to use the graphics object.
        //  could take a really stupid approach and have it make a long queue of
        //  lambdas that apply a draw operation to a Graphics2D to be iterated through and done in the draw method or something
    }

    /**
     * You can use this to set the I_GraphicsTransform which will be used to work out where all the vertices and such
     * need to go
     *
     * @param g the I_GraphicsTransform
     */
    @Override
    public void setGraphicsTransform(final I_GraphicsTransform g) {

    }

    /**
     * Please write this getter for the I_GraphicsTransform which was supposed to be set with {@link
     * #setGraphicsTransform(I_GraphicsTransform)}.
     *
     * @return the current I_GraphicsTransform
     */
    @Override
    public I_GraphicsTransform getGraphicsTransform() {
        return gTransform;
    }

    /**
     * Please draw this OUTLINE OF A circle.
     *
     * @param pos     The position of this circle (IN SCREEN COORDINATES!)
     * @param radiusX radius scaled by the appropriate x amount (IN SCREEN SPACE!)
     * @param radiusY radius scaled by the appropriate y scale (IN SCREEN SPACE!)
     * @param col     Draw it in this colour.
     */
    @Override
    public void drawCircle(final I_Vect2D pos, final double radiusX, final double radiusY, final Color col) {

    }

    /**
     * Please draw this FILLED-IN circle.
     *
     * @param pos     The position of this circle (IN SCREEN COORDINATES!)
     * @param radiusX radius scaled by the appropriate x amount (IN SCREEN SPACE!)
     * @param radiusY radius scaled by the appropriate y scale (IN SCREEN SPACE!)
     * @param col     Draw it in this colour.
     */
    @Override
    public void drawFilledCircle(I_Vect2D pos, double radiusX, double radiusY, Color col) {

    }

    /**
     * Please draw this OUTLINE OF A polygon.
     *
     * @param pos      position of polygon (IN SCREEN COORDINATES!)
     * @param vertices the corners of the polygon (IN SCREEN COORDINATES!)
     * @param col      draw it in this colour.
     */
    @Override
    public void drawPolygon(I_Vect2D pos, I_Vect2D[] vertices, Color col) {

    }

    /**
     * Please draw this FILLED-IN polygon.
     *
     * @param pos      position of polygon (IN SCREEN COORDINATES!)
     * @param vertices the corners of the polygon (IN SCREEN COORDINATES!)
     * @param col      draw it in this colour.
     */
    @Override
    public void drawFilledPolygon(I_Vect2D pos, I_Vect2D[] vertices, Color col) {

    }

    /**
     * please draw this line
     *
     * @param a   first line corner
     * @param b   second line corner
     * @param col line colour
     */
    @Override
    public void drawLine(I_Vect2D a, I_Vect2D b, Color col) {

    }
}
