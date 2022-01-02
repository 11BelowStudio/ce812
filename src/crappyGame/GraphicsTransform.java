package crappyGame;

import crappy.graphics.I_GraphicsTransform;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.utils.containers.IPair;

public class GraphicsTransform implements I_GraphicsTransform {


    final IPair<Double, Double> worldToScreenScale;

    final IPair<Double, Double> worldToScreenCorrectionOffset;

    Vect2D viewportCorner = Vect2D.ZERO;


    GraphicsTransform(double viewableWorldWidth, double viewableWorldHeight, double screenX, double screenY){

        worldToScreenScale = IPair.of(viewableWorldWidth/screenX, viewableWorldHeight/screenY);

        worldToScreenCorrectionOffset = IPair.of(0.0, -screenY);

    }


    /**
     * Obtain the raw scale for 'world length to screen length'/'world height to screen height'.
     * <p>
     * In the form {@code world visible in viewport/screen pixels in viewport}
     * <p>
     * THIS DOES NOT INCLUDE ANY Y FLIPPING, VIEWPORT SCROLLING, ETC THAT MAY NEED TO BE DONE TO CONVERT A WORLD
     * COORDINATE TO A SCREEN COORDINATE!
     *
     * @return pair of {@code <screen X scale, screen Y scale>}
     */
    @Override
    public IPair<Double, Double> getScreenScale() {
        return worldToScreenScale;
    }

    /**
     * Where in world coordinates is (0,0) in the viewport?
     *
     * @return where in world coordinates the origin of the viewport is
     */
    @Override
    public I_Vect2D getViewportOrigin() {
        return viewportCorner;
    }

    /**
     * Obtains an (x, y) pair to be added to the scaled viewported screen coords, to make them actually appear
     * correctly.
     *
     * @return the appropriate (x, y) pair
     *
     * @implNote For a viewport with rendering origin (0,0) in the top-left corner, this should return (x,-y)
     */
    @Override
    public IPair<Double, Double> screenCoordsCorrectionOffset() {
        return worldToScreenCorrectionOffset;
    }
}
