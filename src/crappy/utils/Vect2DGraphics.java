package crappy.utils;

import crappy.math.I_Vect2D;
import crappy.math.Vect2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.geom.*;

/**
 * Class holding some graphics-related utility methods,
 * regarding the conversion between a Path2D.Double and an array of I_Vect2D objects.
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
}
