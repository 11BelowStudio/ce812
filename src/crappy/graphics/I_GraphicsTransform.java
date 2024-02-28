/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.graphics;

import crappy.math.I_Vect2D;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.utils.containers.IPair;

import java.awt.geom.Point2D;

/**
 * An interface representing a transformation between world coordinates and screen coordinates.
 * @author Rachel Lowe
 */
public interface I_GraphicsTransform {

    /**
     * Because Java draws stuff from the top-left corner, not the bottom-left,
     * we need to invert Y. We can easily do that when performing the world-screen transformations,
     * by multiplying the part of the equation where Y needs to be inverted by this pair of doubles.
     */
    public static final IPair<Double, Double> ORIGIN_TOP_LEFT_CORRECTION = IPair.of((double)1,(double) -1);

    /**
     * Obtain the raw scale for 'world length to screen length'/'world height to screen height'.
     *
     * In the form {@code world visible in viewport/screen pixels in viewport}
     *
     * THIS DOES NOT INCLUDE ANY Y FLIPPING, VIEWPORT SCROLLING, ETC THAT MAY NEED TO BE DONE TO CONVERT A WORLD
     * COORDINATE TO A SCREEN COORDINATE!
     * @return pair of {@code <screen X scale, screen Y scale>}
     */
    public IPair<Double, Double> getScreenScale();

    default double convertWorldLengthToScreenLength(double wLen){
        return wLen / getScreenScale().getFirst();
    }

    default double convertWorldHeightToScreenHeight(double wHeight){
        return wHeight / getScreenScale().getSecond();
    }

    /**
     * Where in world coordinates is (0,0) in the viewport?
     * @return where in world coordinates the origin of the viewport is
     */
    public I_Vect2D getViewportOrigin();

    /**
     * Obtains an (x, y) pair to be added to the scaled viewported screen coords, to make them actually
     * appear correctly.
     * @return the appropriate (x, y) pair
     * @implNote For a viewport with rendering origin (0,0) in the top-left corner,
     * this should return (x,-y)
     */
    public IPair<Double, Double> screenCoordsCorrectionOffset();

    /**
     * Obtains an (x, y) pair to scale the scaled viewported screen coords by, to make them actually
     * appear correctly.
     * @return the appropriate (x, y) pair
     * @implNote For a viewport with rendering origin (0,0) in the top-left corner,
     * this should return (1,-1)
     */
    default IPair<Double, Double> screenCoordsCorrectionScale(){
        return ORIGIN_TOP_LEFT_CORRECTION;
    }

    default Vect2D TO_SCREEN_COORDS_V(final IPair<Double, Double> worldCoord){
        return TO_VIEWPORT_CORRECTED_SCREEN_COORDS_M(worldCoord).finished();
    }

    default M_Vect2D TO_RAW_SCREEN_SCALE_M(final IPair<Double, Double> worldCoord){
        return M_Vect2D.GET(worldCoord).divide(getScreenScale());
    }

    default M_Vect2D TO_CORRECTED_SCREEN_SCALE_M(final IPair<Double, Double> worldCoord){
        return TO_RAW_SCREEN_SCALE_M(worldCoord).add(screenCoordsCorrectionOffset()).mult(screenCoordsCorrectionScale());
    }

    default M_Vect2D TO_VIEWPORT_CORRECTED_SCREEN_COORDS_M(final IPair<Double, Double> worldCoord){
        return M_Vect2D.GET(worldCoord)
                .sub(getViewportOrigin())
                .divide(getScreenScale())
                .add(screenCoordsCorrectionOffset())
                .mult(screenCoordsCorrectionScale());
    }

    default Point2D.Double TO_SCREEN_COORDS_P(final IPair<Double, Double> worldCoord){
        return TO_VIEWPORT_CORRECTED_SCREEN_COORDS_M(worldCoord).toPoint2D_discard();
    }

    default M_Vect2D TO_WORLD_COORDS_BUT_VIEWPORT_OFFSET_M(final IPair<Double, Double> v){
        return M_Vect2D.GET(v)
                .divide(screenCoordsCorrectionScale())
                .sub(screenCoordsCorrectionOffset())
                .mult(getScreenScale());
    }

    default M_Vect2D TO_WORLD_COORDS_M(final IPair<Double, Double> v){
        return TO_WORLD_COORDS_BUT_VIEWPORT_OFFSET_M(v)
                .add(getViewportOrigin());
    }

    default Vect2D TO_WORLD_COORDS_V(final IPair<Double, Double> screenCoord){
        return TO_WORLD_COORDS_M(screenCoord).finished();
    }



}
