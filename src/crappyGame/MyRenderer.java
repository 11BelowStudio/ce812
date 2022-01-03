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
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.util.Arrays;

public class MyRenderer implements I_CrappilyDrawStuff {

    private I_GraphicsTransform gTransform;

    private VolatileImage img;

    private Graphics2D g;

    private JComponent view;

    MyRenderer(JComponent v){

        view = v;
        generateImage();
    }

    MyRenderer(I_GraphicsTransform gt){
        gTransform = gt;
    }


    void generateImage(){

        img = view.createVolatileImage(view.getWidth(), view.getHeight());

        g = img.createGraphics();

    }

    void prepareToRender(Graphics2D g0){
        g = g0;
    }

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
    public void drawPolygon(I_Vect2D pos, I_Vect2D[] vertices, Color col) {

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
    public void drawFilledPolygon(I_Vect2D pos, I_Vect2D[] vertices, Color col) {

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

    @Override
    public void drawRectangle(I_Vect2D lb, I_Vect2D wh, Color col) {
        System.out.println("MyRenderer.drawRectangle");
        System.out.println("lb = " + lb + ", wh = " + wh + ", col = " + col);

        g.setColor(col);
        g.draw(
                new Rectangle2D.Double(lb.getX(), lb.getY(), wh.getX(), wh.getY())
        );
    }

    public void drawRectangle(
            final double x, final double y, final double w, final double h, final Color col
    ){
        g.setColor(col);
        g.draw(
                new Rectangle2D.Double(x,y,w,h)
        );
    }
}
