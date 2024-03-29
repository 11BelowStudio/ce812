package crappyGame;

import crappy.graphics.I_CrappilyDrawStuff;
import crappy.graphics.I_GraphicsTransform;
import crappy.graphics.Vect2DGraphics;
import crappy.math.I_Vect2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * The implementation of I_CrappilyDrawStuff being used for
 * A Scientific Interpretation of Daily Life in the Space Towing Industry circa 3052 CE.
 */
public class MyRenderer implements I_CrappilyDrawStuff {

    /**
     * The transformation being used for the graphics
     */
    private final I_GraphicsTransform gTransform;

    /**
     * The graphics2D object that this will be drawing stuff to.
     */
    private transient Graphics2D g;

    /**
     * Should bounding boxes be drawn?
     *
     * @return true if you want to render bounding boxes.
     */
    @Override
    public boolean RENDERING_BOUNDING_BOXES() {
        return false;
    }

    /**
     * Should velocity lines be drawn?
     *
     * @return true if you want to render visible velocities.
     */
    @Override
    public boolean RENDERING_VELOCITIES() {
        return false;
    }

    /**
     * Should rotation lines be drawn?
     *
     * @return true if you want to render visible rotations.
     */
    @Override
    public boolean RENDERING_ROTATIONS() {
        return false;
    }

    /**
     * Should polygon incircles be drawn?
     *
     * @return true if you want to render polygon incircles.
     */
    @Override
    public boolean RENDERING_INCIRCLES() {
        return false;
    }


    /**
     * Should line/edge normal vectors be drawn?
     *
     * @return true if you want to render line/edge normal vectors
     */
    @Override
    public boolean RENDERING_NORMALS() {
        return false;
    }


    /**
     * Constructs this, with the given GraphicsTransform
     * @param gt the GraphicsTransform we're using.
     */
    public MyRenderer(final I_GraphicsTransform gt){
        gTransform = gt;
    }


    /**
     * Gives this the Graphics2D object that this will need to draw stuff to
     * @param g0 the Graphics2D object being used.
     */
    void prepareToRender(final Graphics2D g0){
        g = g0;
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

        g.setColor(col);

        g.draw(
                new Ellipse2D.Double(pos.getX()-radiusX, pos.getY()-radiusY, 2*radiusX, 2*radiusY)
        );
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

        g.setColor(col);

        g.fill(
                new Ellipse2D.Double(pos.getX()-radiusX, pos.getY()-radiusY, 2*radiusX, 2*radiusY)
        );
    }

    /**
     * Please draw this OUTLINE OF A polygon.
     *
     * @param pos      position of polygon (IN SCREEN COORDINATES!)
     * @param vertices the corners of the polygon (IN SCREEN COORDINATES!)
     * @param col      draw it in this colour.
     */
    @Override
    public void drawPolygon(final I_Vect2D pos, final I_Vect2D[] vertices, final Color col) {

        g.setColor(col);
        g.draw(
                Vect2DGraphics.VECTS_TO_DOUBLE_PATH(vertices)
        );
    }

    /**
     * Please draw this FILLED-IN polygon.
     *
     * @param pos      position of polygon (IN SCREEN COORDINATES!)
     * @param vertices the corners of the polygon (IN SCREEN COORDINATES!)
     * @param col      draw it in this colour.
     */
    @Override
    public void drawFilledPolygon(final I_Vect2D pos, final I_Vect2D[] vertices, final Color col) {

        g.setColor(col);
        g.fill(
                Vect2DGraphics.VECTS_TO_DOUBLE_PATH(vertices)
        );
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


        g.setColor(col);
        g.draw(
                new Line2D.Double(a.getX(), a.getY(),b.getX(), b.getY())
        );
    }


    /**
     * Please draw this rectangle
     * @param x top-left x
     * @param y top-left y
     * @param w width
     * @param h height
     * @param col colour
     */
    @Override
    public void drawRectangle(
            final double x, final double y, final double w, final double h, final Color col
    ){
        g.setColor(col);
        g.draw(
                new Rectangle2D.Double(x,y,w,h)
        );
    }
}
