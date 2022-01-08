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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.geom.*;

/**
 * Class holding some graphics-related utility methods,
 * regarding the conversion between a Path2D.Double and an array of I_Vect2D objects.
 *
 * @author Rachel Lowe
 */
public final class Vect2DGraphics {

    /**
     * no constructing. only static.
     */
    private Vect2DGraphics(){}

    /**
     * Turns an array of vectors into a Path2D.Double object
     * @param vects array of I_Vect2Ds describing vertices
     * @return a Path2D.Double describing those vertices
     */
    public static Path2D.Double VECTS_TO_DOUBLE_PATH(final I_Vect2D... vects){

        final Path2D.Double path = new Path2D.Double();
        path.moveTo(vects[vects.length-1].getX(), vects[vects.length-1].getY());
        for (final I_Vect2D v: vects) {
            path.lineTo(v.getX(), v.getY());
        }
        path.closePath();
        return path;
    }


    /**
     * Attempts to turn a Path2D.Double into a list of vectors.
     * Assumes that no quad/cubic line segments are contained in the path
     * @param thePath the path to turn into vectors
     * @return the path as a list of Vect2Ds.
     */
    public static List<Vect2D> DOUBLE_PATH_TO_VECT_LIST(final Path2D.Double thePath){

        final double[] outs = new double[6];

        final ArrayList<Vect2D> vects = new ArrayList<>();

        for (final PathIterator iter = thePath.getPathIterator(null); !iter.isDone(); iter.next()) {
            final int seg = iter.currentSegment(outs);
            if (seg == PathIterator.SEG_LINETO){
                vects.add(new Vect2D(outs[0], outs[1]));
            } else if (seg == PathIterator.SEG_CUBICTO || seg==PathIterator.SEG_QUADTO){
                throw new IllegalArgumentException(
                        "Please do not call this with any Path2Ds containing cubics or quads! Encountered " +
                                (((seg==PathIterator.SEG_CUBICTO)? "SEG_CUBICTO" : "SEG_QUADTO") + "!")
                );
            }
        }
        vects.trimToSize();
        return Collections.unmodifiableList(vects);
    }

    /**
     * Turns an I_Vect2D into a Point2D.Double
     * @param v the vector to turn into a Point2D.Double
     * @return this vector as a Point2D.Double
     */
    public static Point2D.Double VECT_TO_POINT(final I_Vect2D v){
        return new Point2D.Double(v.getX(), v.getY());
    }

    /**
     * Converts an M_Vect2D into a Point2D.Double, promptly discarding that M_Vect2D,
     * and then returning that Point2D.Double
     * @param v the mutable vector to turn into a point
     * @return a point2D.Double representing what the state of that M_Vect2D was.
     */
    public static Point2D.Double VECT_TO_POINT_DISPOSE(final M_Vect2D v){
        final Point2D.Double p = new Point2D.Double(v.x, v.y);
        v.discard();
        return p;
    }

    /**
     * Turns an IPair of Numbers into a Point2D.Double
     * @param p the pair of numbers to turn into a Point2D.Double
     * @return a Point2D.Double representing the values in that pair
     */
    public static Point2D.Double PAIR_TO_POINT(final IPair<Number, Number> p){
        return new Point2D.Double((Double) p.getFirst(), (Double) p.getSecond());
    }

    /**
     * Converts a Point2D into a Vect2D
     * @param p the Point2D
     * @return a Vect2D representing the same thing as the Point2D did.
     */
    public static Vect2D POINT_TO_VECT(final Point2D p){
        return new Vect2D(p.getX(), p.getY());
    }

    /**
     * Converts a Point2D into an M_Vect2D
     * @param p the Point2D
     * @return an M_Vect2D holding the same thing that the point held
     */
    public static M_Vect2D POINT_TO_VECT_M(final Point2D p){
        return M_Vect2D.GET(p.getX(), p.getY());
    }


}
