package crappy.math;

import crappy.I_Transform;
import crappy.utils.containers.IPair;
import crappy.utils.containers.IQuadruplet;

import java.util.Arrays;

/**
 * A utility class holding static Vect2D math-related methods.
 *
 * Can statically import any necessary methods from here on a per-method basis.
 */
public final class Vect2DMath {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * No constructing.
     */
    private Vect2DMath(){}


    /**
     * A helper method to compare doubles.
     * Like {@link Double#compare(double, double)} but omits the double to long bits stuff
     * and just returns 0 if neither are found to be bigger than each other, to save time.
     * @param a first double
     * @param b second double
     * @return +1 if a > b, -1 if a < b, otherwise 0.
     * @see Double#compare(double, double)
     */
    public static int COMPARE_DOUBLES_CRAPPILY(final double a, final double b){
        if (a > b){
            return 1;
        } else if (a < b){
            return -1;
        }
        return 0;
    }

    /**
     * The epsilon value used for {@link #COMPARE_DOUBLES_EPSILON(double, double)}
     * @see #COMPARE_DOUBLES_EPSILON(double, double)
     * @see #COMPARE_DOUBLES_EPSILON(double, double, double)
     */
    public final static double EPSILON = 0.000001d;

    /**
     * Compares d1 to d2, except using a 'close enough' value of {@link #EPSILON}.
     * @param d1 first double
     * @param d2 second double
     * @return 0 if {@code |d1-d2| < {@link #EPSILON}}, -1 if {@code d1 < d2}, and 1 if {@code d1 > d2}
     * @see #COMPARE_DOUBLES_EPSILON(double, double, double)
     */
    public static int COMPARE_DOUBLES_EPSILON(final double d1, final double d2){
        return COMPARE_DOUBLES_EPSILON(d1, d2, EPSILON);
    }

    /**
     * Compares d1 to d2.
     * @param d1 first double
     * @param d2 second double
     * @param epsilon a 'close enough' value.
     *                If the difference between d1 and d2 is no larger than epsilon, they're considered equal enough.
     * @return 0 if {@code |d1-d2| < epsilon}, -1 if {@code d1 < d2}, and 1 if {@code d1 > d2}
     */
    public static int COMPARE_DOUBLES_EPSILON(final double d1, final double d2, final double epsilon){
        final double diff = d1 - d2;
        if (Math.abs(diff) <= epsilon){
            return 0;
        } else if (diff > 0){
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Checks if v1 and v2 are approximately equal to each other,
     * using predefined 'close enough' value of {@link #EPSILON}
     * @param v1 first vector
     * @param v2 other vector
     * @return true if v1's components are 'close enough' to v2's
     * @see #EQUALS_EPSILON(I_Vect2D, I_Vect2D, double)
     * @see #EPSILON
     */
    public static boolean EQUALS_EPSILON(final I_Vect2D v1, final I_Vect2D v2){
        return EQUALS_EPSILON(v1, v2, EPSILON);
    }

    /**
     * Checks if vectors v1 and v2 are approximately equal (if x and y of each are within epsilon of other x and y)
     * @param v1 first vector
     * @param v2 second vector
     * @param epsilon 'close enough' value
     * @return true if components of v1 and v2 are 'close enough' to each other
     * @see #COMPARE_DOUBLES_EPSILON(double, double, double)
     */
    public static boolean EQUALS_EPSILON(final I_Vect2D v1, final I_Vect2D v2, final double epsilon){

        return COMPARE_DOUBLES_EPSILON(v1.getX(), v2.getX(), epsilon) == 0
                && COMPARE_DOUBLES_EPSILON(v1.getY(), v2.getY(), epsilon) == 0;

    }

    /**
     * Multiplies vector V by S, returns mutable
     * @param v vector
     * @param s scalar
     * @return v*s
     */
    public static M_Vect2D MULTIPLY_M(final I_Vect2D v, final double s){
        return M_Vect2D.GET(v).mult(s);
    }

    /**
     * Multiplies vector V by S, returns immutable
     * @param v vector
     * @param s scalar
     * @return v*s
     */
    public static Vect2D MULTIPLY(final I_Vect2D v, final double s){
        return MULTIPLY_M(v,s).finished();
    }

    /**
     * Multiplies this vector componentwise by given scale
     * @param v vector to multiply
     * @param xScale how much should x be multiplied by?
     * @param yScale how much should y be multiplied by?
     * @return MUTABLE copy of V but with each element multiplied by the appropriate scale.
     */
    public static M_Vect2D MULTIPLY_M(final I_Vect2D v, final double xScale, final double yScale){
        return M_Vect2D.GET(v).mult(xScale, yScale);
    }

    /**
     * Multiplies this vector componentwise by given scale, returns mutable
     * @param v vector to multiply
     * @param xyScale pair of {@code <x scale, y scale> }
     * @return MUTABLE copy of V but with each element multiplied by the appropriate scale.
     */
    public static M_Vect2D MULTIPLY_M(final I_Vect2D v, final IPair<? extends Number, ? extends Number> xyScale){
        return MULTIPLY_M(v, (Double) xyScale.getFirst(), (Double) xyScale.getSecond());
    }

    /**
     * Multiplies this vector componentwise by given scale, returns immutable
     * @param v vector to multiply
     * @param xyScale pair of {@code <x scale, y scale> }
     * @return immutable copy of V but with each element multiplied by the appropriate scale.
     */
    public static Vect2D MULTIPLY(final I_Vect2D v, final IPair<? extends Number, ? extends Number> xyScale){
        return MULTIPLY_M(v, xyScale).finished();
    }

    /**
     * Multiplies this vector componentwise by given scale
     * @param v vector to multiply by
     * @param xScale how much should x be multiplied by?
     * @param yScale how much should y be multiplied by?
     * @return IMMUTABLE copy of V but with each element multiplied by the appropriate scale.
     */
    public static Vect2D MULTIPLY(final I_Vect2D v, final double xScale, final double yScale){
        return MULTIPLY_M(v, xScale, yScale).finished();
    }

    /**
     * Returns the MUTABLE result of v.mult(self.v(other)), for ease of use.
     * @param v the vector we're scaling
     * @param other the other vector being used in the dot product
     * @return MUTABLE result of (v * v.other)
     */
    public static M_Vect2D MULT_DOT_OTHER(final I_Vect2D v, final I_Vect2D other){
        return M_Vect2D.GET(v).mult(v.dot(other));
    }

    /**
     * Adds v1 to v2, returns mutable
     * @param v1 vector
     * @param v2 other vector
     * @return v1 + v2
     */
    public static M_Vect2D ADD_M(final I_Vect2D v1, final I_Vect2D v2){
        return M_Vect2D.GET(v1).add(v2);
    }

    /**
     * Adds v1 to v2, returns immutable
     * @param v1 vector
     * @param v2 other vector
     * @return v1 + v2
     */
    public static Vect2D ADD(final I_Vect2D v1, final I_Vect2D v2){
        return ADD_M(v1, v2).finished();
    }

    /**
     * Adds d to both components of v1, returns mutable
     * @param v1 vector
     * @param d add this to x and y of v1
     * @return v1 + (d, d)
     */
    public static M_Vect2D ADD_M(final I_Vect2D v1, final double d){
        return M_Vect2D.GET(v1).addScaled(Vect2D.ONES, d);
    }

    /**
     * Adds d to both components of v1, returns immutable
     * @param v1 vector
     * @param d add this to x and y of v1
     * @return v1 + (d, d)
     */
    public static Vect2D ADD(final I_Vect2D v1, final double d){
        return ADD_M(v1, d).finished();
    }

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D MINUS(final Vect2D v1, final Vect2D v2) { return v1.addScaled(v2, -1); }

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D MINUS(final Vect2D v1, final I_Vect2D v2){ return v1.addScaled(v2, -1); }

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D MINUS(final I_Vect2D v1, final I_Vect2D v2){
        return new Vect2D(v1.getX() - v2.getX(), v1.getY()-v2.getY());
    }

    /**
     * Vector subtraction but with a mutable result
     * @param v1 first vector
     * @param v2 second vector
     * @return v1 - v2 but the result is mutable.
     */
    public static M_Vect2D MINUS_M(final I_Vect2D v1, final I_Vect2D v2){
        return ADD_SCALED_M(v1, v2, -1);
    }

    /**
     * Returns the vector between start and end {@code start->end}. Or, in other words, {@code end-start}.
     * @param start where we're starting from
     * @param end where we're going
     * @return vector from start to end.
     */
    public static Vect2D VECTOR_BETWEEN(final I_Vect2D start, final I_Vect2D end){
        return MINUS(end, start);
    }

    /**
     * Returns the vector between start and end {@code start->end}. Or, in other words, {@code end-start}.
     * @param start where we're starting from
     * @param end where we're going
     * @return MUTABLE vector from start to end.
     */
    public static M_Vect2D VECTOR_BETWEEN_M(final I_Vect2D start, final I_Vect2D end){
        return MINUS_M(end, start);
    }



    /**
     * Like ADD_SCALED but ensures we don't accidentally modify any mutable M_Vect2Ds we may be using here
     * @param v1 first vector
     * @param v2 vector to scale and add to the other one
     * @param scale how much to scale v2 by
     * @return v1 + (v2 * scale)
     */
    public static Vect2D ADD_SCALED(final I_Vect2D v1, final I_Vect2D v2, final double scale){
        return new Vect2D(
                v1.getX() + (v2.getX() * scale),
                v1.getY() + (v2.getY() * scale)
        );
    }

    /**
     * Like ADD_SCALED but with a mutable result
     * @param v1 first vector
     * @param v2 second vector
     * @param scale how much to scale v2 by
     * @return v1 + (v2 * scale)
     */
    public static M_Vect2D ADD_SCALED_M(final I_Vect2D v1, final I_Vect2D v2, final double scale){
        return M_Vect2D.GET(v1).addScaled(v2, scale);
    }

    /**
     * Finds the sum of these vectors, returning the result as a Vect2D.
     * @param vects all the vectors to add together
     * @return Sum of all of those vectors.
     */
    public static Vect2D SUM(final I_Vect2D... vects){
        double x = 0;
        double y = 0;
        for (final I_Vect2D v: vects){
            x += v.getX();
            y += v.getY();
        }
        return new Vect2D(x, y);
    }

    /**
     * Creates a new Vect2D holding the min x and min y values of a and b
     * @param a first vector
     * @param b second vector
     * @return a Vect2D with (min(a.x, b.x), min(a.y, b.y))
     */
    public static Vect2D LOWER_BOUND(final I_Vect2D a, final I_Vect2D b){
        return LOWER_BOUND_M(a, b).finished();
    }

    /**
     * Creates a new Vect2D holding the max x and max y values of a and b
     * @param a first vector
     * @param b second vector
     * @return a Vect2D with (max(a.x, b.x), max(a.y, b.y))
     */
    public static Vect2D UPPER_BOUND(final I_Vect2D a, final I_Vect2D b){
        return UPPER_BOUND_M(a, b).finished();
    }

    /**
     * Obtains the midpoint between the given Vect2Ds
     * @param a first Vect2D
     * @param b second Vect2D
     * @return midpoint of a and b
     */
    public static Vect2D MIDPOINT(final I_Vect2D a, final I_Vect2D b){
        return MIDPOINT_MIN_MAX(LOWER_BOUND(a, b), UPPER_BOUND(a, b));
    }

    /**
     * Given 3 vectors, returns the vector that's in the middle of them
     * @param a first vector
     * @param b second vector
     * @param c third vector
     * @return the vector out of a, b, c that's in the middle of the others (via compareTo)
     */
    public static I_Vect2D GET_MIDDLE_VECTOR(final I_Vect2D a, final I_Vect2D b, final I_Vect2D c){

        if (a.compareTo(b) > 0){
            if (b.compareTo(c) > 0){
                return b;
            } else if (c.compareTo(a) > 0){
                return a;
            } else{
                return c;
            }
        } else if (c.compareTo(b) > 0){
            return b;
        } else if (a.compareTo(c) > 0){
            return a;
        } else {
            return c;
        }
    }

    /**
     * Obtains the midpoint between the given Vect2Ds when we know which one is the lower bound and which one
     * is the upper bound
     * @param min lower bound Vect2D
     * @param max upper bound Vect2D
     * @return midpoint of min and max
     */
    public static Vect2D MIDPOINT_MIN_MAX(final Vect2D min, final I_Vect2D max){
        return min.lerp(max, 0.5);
    }

    public static Vect2D MIDPOINT_MIN_MAX(final I_Vect2D min, final I_Vect2D max){
        return LERP(min, max, 0.5);
    }

    /**
     * Linearly interpolates from start to end, but when 'start' is actually a Vect2D.
     * @param start start from here
     * @param end go to here
     * @param lerpScale how much to lerp by (0: return start. 1: return end. 0.5: midpoint)
     * @return vector that's lerpScale of the way between start and end
     * @see Vect2D#lerp(I_Vect2D, double)
     */
    public static Vect2D LERP(final Vect2D start, final I_Vect2D end, final double lerpScale){
        return start.lerp(end, lerpScale);
    }

    /**
     * Linearly interpolates from start to end, but for I_Vect2D objects
     * (when we don't know if start is immutable or not).
     * @param start start from here
     * @param end go to here
     * @param lerpScale how much to lerp by (0: return start. 1: return end. 0.5: midpoint)
     * @return vector that's lerpScale of the way between start and end
     */
    public static Vect2D LERP(final I_Vect2D start, final I_Vect2D end, final double lerpScale){
        return LERP_M(start, end, lerpScale).finished();
    }

    /**
     * Linearly interpolates from start to end, but returns an immutable result.
     * @param start start from here
     * @param end go to here
     * @param lerpScale how much to lerp by (0: return start. 1: return end. 0.5: midpoint)
     * @return vector that's lerpScale of the way between start and end
     */
    public static M_Vect2D LERP_M(final I_Vect2D start, final I_Vect2D end, final double lerpScale){
        return M_Vect2D.GET(start).lerp(end, lerpScale);
    }

    /**
     * Returns the distance between vectors A and B
     * @param a the first vector
     * @param b the second vector
     * @return scalar distance between A and B
     */
    public static double DIST(final I_Vect2D a, final I_Vect2D b){
        return Math.hypot(b.getX() - a.getX(), b.getY() - a.getY());
    }

    /**
     * Returns the square of the distance between A and B
     * (allowing for proximity checks without needing any square roots)
     * @param a first vector
     * @param b second vector
     * @return square of the magnitude of a->b
     */
    public static double DIST_SQUARED(final I_Vect2D a, final I_Vect2D b){
        return Math.pow(b.getX()-a.getX(), 2) + Math.pow(b.getX() - a.getX(), 2);
    }


    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'locals'
     * list, and outputs them into the given 'out' list.
     * @param bodyTransform the transform of the body
     * @param locals local positions of everything in the body
     * @param localNorms local normal vectors of everything in the body
     * @param out the list which the world positions of everything in the body will be put into
     * @param outNorms world normal vectors of everything in the body
     * @throws ArrayIndexOutOfBoundsException if out is smaller than locals, if outNorms is smaller than localNorms,
     * or if localNorms is smaller than locals
     */
    public static void LOCAL_TO_WORLD_FOR_BODY_TO_OUT(
            final I_Transform bodyTransform,
            final Vect2D[] locals,
            final Vect2D[] localNorms,
            final Vect2D[] out,
            final Vect2D[] outNorms
    ){
        LOCAL_TO_WORLD_FOR_BODY_TO_OUT(
                bodyTransform.getPos(),
                bodyTransform.getRot(),
                locals,
                localNorms,
                out,
                outNorms
        );
    }

    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'locals'
     * list, and outputs them into the given 'out' list.
     * @param bodyPos position of the body centroid
     * @param bodyRotation position of the body's rotation
     * @param locals local positions of everything in the body
     * @param localNorms local normal vectors of everything in the body
     * @param out the list which the world positions of everything in the body will be put into
     * @param outNorms world normal vectors of everything in the body
     * @throws ArrayIndexOutOfBoundsException if out is smaller than locals, if outNorms is smaller than localNorms,
     * or if localNorms is smaller than locals
     */
    public static void LOCAL_TO_WORLD_FOR_BODY_TO_OUT(
            final I_Vect2D bodyPos,
            final I_Rot2D bodyRotation,
            final Vect2D[] locals,
            final Vect2D[] localNorms,
            final Vect2D[] out,
            final Vect2D[] outNorms
    ){
        for (int i = locals.length-1; i >= 0; i--) {
            out[i] = LOCAL_TO_WORLD_M(locals[i], bodyPos, bodyRotation).finished();
            outNorms[i] = localNorms[i].rotate(bodyRotation);
        }
    }

    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'localCoords'
     * list, and outputs them into the given 'out' list, and also returns a pair with the bounds of the translated vectors
     * @param bodyTransform the transform for the body
     * @param localCoords local positions of everything in the body
     * @param localNormals local normals of the edges in the body
     * @param outCoords the list which the world positions of everything in the body will be put into
     * @param outNormals the list which the world normals of each edge in the body will be put into
     * @throws ArrayIndexOutOfBoundsException if outCoords is shorter than localCoords,
     * if outNormals is shorter than localNormals, or if localNormals is shorter than localCoords
     */
    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final I_Transform bodyTransform,
            final Vect2D[] localCoords,
            final Vect2D[] localNormals,
            final Vect2D[] outCoords,
            final Vect2D[] outNormals
    ){
        return LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                bodyTransform.getPos(), bodyTransform.getRot(), localCoords, localNormals, outCoords, outNormals
        );
    }

    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'localCoords'
     * list, and outputs them into the given 'out' list, and also returns a pair with the bounds of the translated vectors
     * @param bodyPos position of the body centroid
     * @param bodyRotation position of the body's rotation
     * @param localCoords local positions of everything in the body
     * @param localNormals local normals of the edges in the body
     * @param outCoords the list which the world positions of everything in the body will be put into
     * @param outNormals the list which the world normals of each edge in the body will be put into
     * @throws ArrayIndexOutOfBoundsException if outCoords is shorter than localCoords,
     *      * if outNormals is shorter than localNormals, or if localNormals is shorter than localCoords
     */
    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final I_Vect2D bodyPos,
            final I_Rot2D bodyRotation,
            final Vect2D[] localCoords,
            final Vect2D[] localNormals,
            final Vect2D[] outCoords,
            final Vect2D[] outNormals
    ){
        outCoords[0] = localCoords[0].localToWorldCoordinates(bodyPos, bodyRotation);
        outNormals[0] = localNormals[0].rotate(bodyRotation);
        final M_Vect2D min = M_Vect2D.GET(outCoords[0]);
        final M_Vect2D max = M_Vect2D.GET(min);
        for (int i = 1; i < localCoords.length; i++){
            innerUpdateCoordsAndNormAndMinMax(
                    bodyPos, bodyRotation, localCoords,
                    localNormals, outCoords, outNormals,
                    min, max, i
            );
        }
        return IPair.of(min.finished(), max.finished());
    }

    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'localCoords'
     * list, and outputs them into the given 'out' list, and also returns a pair with the bounds of the translated vectors
     * @param bodyTransform the transform for the body
     * @param localCoords local positions of everything in the body
     * @param localProj local projections in the body
     * @param localNorm local normals of the edges in the body
     * @param outCoords the list which the world positions of everything in the body will be put into
     * @param outProj list where rotated projections will be put into
     * @param outNorm the list which the rotated world normals will be put into
     * @throws ArrayIndexOutOfBoundsException if outCoords is shorter than localCoords,
     * if outNormals is shorter than localNormals, or if localNormals is shorter than localCoords
     */
    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final I_Transform bodyTransform,
            final Vect2D[] localCoords,
            final Vect2D[] localProj,
            final Vect2D[] localNorm,
            final Vect2D[] outCoords,
            final Vect2D[] outProj,
            final Vect2D[] outNorm
    ){
        return LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                bodyTransform.getPos(), bodyTransform.getRot(), localCoords, localProj, localNorm, outCoords, outProj, outNorm
        );
    }

    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final I_Vect2D bodyPos,
            final I_Rot2D bodyRotation,
            final Vect2D[] localCoords,
            final Vect2D[] localProj,
            final Vect2D[] localNorm,
            final Vect2D[] outCoords,
            final Vect2D[] outProj,
            final Vect2D[] outNorm
    ){
        outCoords[0] = localCoords[0].localToWorldCoordinates(bodyPos, bodyRotation);
        outProj[0] = localProj[0].rotate(bodyRotation);
        outNorm[0] = localNorm[0].rotate(bodyRotation);
        final M_Vect2D min = M_Vect2D.GET(outCoords[0]);
        final M_Vect2D max = M_Vect2D.GET(min);
        for (int i = 1; i < localCoords.length; i++){
            outProj[i] = localProj[i].rotate(bodyRotation);
            innerUpdateCoordsAndNormAndMinMax(bodyPos, bodyRotation, localCoords, localNorm, outCoords, outNorm, min, max, i);
        }
        return IPair.of(min.finished(), max.finished());
    }

    /**
     * Actually updates coordinates lists and recalculates min/max
     * @param bodyPos body position
     * @param bodyRotation body rotation
     * @param localCoords input list of local coords (to rotate + translate)
     * @param localNorm input list of local normals (to rotate)
     * @param outCoords output list of world coords (to be written to)
     * @param outNorm output list of world normals (to be written to)
     * @param min ongoing record of lower bound of world coords
     * @param max ongoing record of upper bound of world coords
     * @param i cursor
     */
    private static void innerUpdateCoordsAndNormAndMinMax(
            final I_Vect2D bodyPos,
            final I_Rot2D bodyRotation,
            final Vect2D[] localCoords,
            final Vect2D[] localNorm,
            final Vect2D[] outCoords,
            final Vect2D[] outNorm,
            final M_Vect2D min,
            final M_Vect2D max,
            final int i
    ) {
        outCoords[i] = LOCAL_TO_WORLD_M(localCoords[i], bodyPos, bodyRotation).finished();
        outNorm[i] = localNorm[i].rotate(bodyRotation);
        updateMinAndMaxWithNewValue(min, max, outCoords[i]);
    }

    /**
     * Helper method to update OUT_MIN and OUT_MAX (M_Vect2Ds) to hold updated x and y values
     * to reflect new known upper/lower bounds when given newValue
     * @param OUT_MIN M_Vect2D holding the lower bound
     * @param OUT_MAX M_Vect2D holding the upper bound
     * @param newValue new value to potentially update the upper/lower bounds with
     */
    private static void updateMinAndMaxWithNewValue(
            final M_Vect2D OUT_MIN,
            final M_Vect2D OUT_MAX,
            final Vect2D newValue
    ){
        if (newValue.x < OUT_MIN.x){
            OUT_MIN.x = newValue.x;
        } else if (newValue.x > OUT_MAX.x) {
            OUT_MAX.x = newValue.x;
        }
        if (newValue.y < OUT_MIN.y){
            OUT_MIN.y = newValue.y;
        } else if(newValue.y > OUT_MAX.y) {
            OUT_MAX.y = newValue.y;
        }
    }


    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final I_Transform trans,
            final Vect2D[] locals,
            final Vect2D[] out
    ){
        return LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(trans.getPos(), trans.getRot(), locals, out);
    }

    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final I_Vect2D pos,
            final I_Rot2D rot,
            final Vect2D[] locals,
            final Vect2D[] out
    ){
        out[0] = locals[0].localToWorldCoordinates(pos, rot);
        final M_Vect2D min = M_Vect2D.GET(out[0]);
        final M_Vect2D max = M_Vect2D.GET(min);
        for (int i = locals.length-1; i > 0; i--) {
            out[i] = LOCAL_TO_WORLD_M(locals[i], pos, rot).finished();
            updateMinAndMaxWithNewValue(min, max, out[i]);
        }
        return IPair.of(min.finished(), max.finished());
    }

    /**
     * Uses the given transform to convert the given local Vect2Ds into world coordinates
     * @param trans transformation for the Vect2Ds
     * @param locals local coordinates
     * @param out world coordinates (will be overwritten!)
     */
    public static void LOCAL_TO_WORLD_FOR_BODY_TO_OUT(
            final I_Transform trans,
            final Vect2D[] locals,
            final Vect2D[] out
    ){
        LOCAL_TO_WORLD_FOR_BODY_TO_OUT(trans.getPos(), trans.getRot(), locals, out);
    }

    /**
     * Uses the given transform to convert the given local Vect2Ds into world coordinates
     * @param pos origin of local coords in world
     * @param rot rotation of local coords in world
     * @param locals local coordinates
     * @param out world coordinates (will be overwritten!)
     */
    public static void LOCAL_TO_WORLD_FOR_BODY_TO_OUT(
            final I_Vect2D pos,
            final I_Rot2D rot,
            final Vect2D[] locals,
            final Vect2D[] out
    ){
        for (int i = locals.length-1; i >= 0; i--) {
            out[i] = LOCAL_TO_WORLD_M(locals[i], pos, rot).finished();
        //locals[i].localToWorldCoordinates(pos, rot);
        }
    }

    /**
     * Moves a local coordinate to world coordinates
     * @param localPos local coordinate in body
     * @param trans transform describing world position/rotation of body
     * @return world position of that local position.
     */
    public static M_Vect2D LOCAL_TO_WORLD_M(I_Vect2D localPos, I_Transform trans){
        return LOCAL_TO_WORLD_M(localPos, trans.getPos(), trans.getRot());
    }

    /**
     * Moves a local coordinate to world coordinates
     * @param localPos local coordinate in body
     * @param worldPos world position of body
     * @param worldRot world rotation of body
     * @return world position of that local position.
     */
    public static M_Vect2D LOCAL_TO_WORLD_M(I_Vect2D localPos, I_Vect2D worldPos, I_Rot2D worldRot){
        return M_Vect2D.GET(localPos).rotate(worldRot).add(worldPos);
    }

    /**
     * Turns a world coordinate into a local coordinate of a body
     * @param worldPos initial world coordinate
     * @param trans transform of that body
     * @return the worldPos but as a local coordinate of the body with the transform described by trans.
     */
    public static M_Vect2D WORLD_TO_LOCAL_M(I_Vect2D worldPos, I_Transform trans){
        return WORLD_TO_LOCAL_M(worldPos, trans.getPos(), trans.getRot());
    }

    /**
     * Turns a world coordinate into a local coordinate of a body
     * @param worldPos initial world coordinate
     * @param bodyWorldPos world position of body
     * @param bodyWorldRot world rotation of body
     * @return the worldPos but as a local coordinate of the body described by bodyWorldPos and bodyWorldRot
     */
    public static M_Vect2D WORLD_TO_LOCAL_M(I_Vect2D worldPos, I_Vect2D bodyWorldPos, I_Rot2D bodyWorldRot){
        return M_Vect2D.GET(worldPos).sub(bodyWorldPos).rotate_opposite(bodyWorldRot);
    }

    /**
     * Finds the world velocity of a local coordinate when we already know that aforementioned local coordinate has been rotated.
     * @param rotatedPos the rotated local coordinate
     * @param trans the transform it's attached to
     * @return the world velocity of that local coordinate which was already rotated when we got it.
     */
    public static M_Vect2D WORLD_VEL_OF_ROTATED_LOCAL_COORD_M(
            final I_Vect2D rotatedPos, final I_Transform trans
    ){
        return WORLD_VEL_OF_ROTATED_LOCAL_COORD_M(rotatedPos, trans.getVel(), trans.getAngVel());
    }

    /**
     * Finds the world velocity of a local coordinate when we already know that aforementioned local coordinate has been rotated.
     * @param rotatedPos the rotated local coordinate
     * @param velCOM the linear velocity of the body it is attached to
     * @param angVel the angular velocity of the body it is attached to
     * @return the world velocity of that local coordinate which was already rotated when we got it.
     */
    public static M_Vect2D WORLD_VEL_OF_ROTATED_LOCAL_COORD_M(
            final I_Vect2D rotatedPos, final IPair<Double, Double> velCOM, final double angVel
    ){
        return M_Vect2D.GET(rotatedPos)
                .cross(angVel, false)
                .add(velCOM);
    }

    /**
     * Obtains the world velocity of local coordinate localPos on a body with transform trans
     * @param localPos local coordinate
     * @param trans transform of that body
     * @return world velocity of that local coordinate
     */
    public static M_Vect2D WORLD_VEL_OF_LOCAL_COORD_M(final I_Vect2D localPos, final I_Transform trans){
        return WORLD_VEL_OF_LOCAL_COORD_M(localPos, trans.getVel(), trans.getAngVel(), trans.getRot());
    }



    /**
     * Obtains the world velocity of local coordinate localPos on a body described by all the other arguments here
     *
     * vCOM + angVel x r
     *
     * @param localPos local pos of coord on body
     * @param velCOM linear velocity of body's centre of mass
     * @param angVel angular velocity of body
     * @param worldRot rotation of the body
     * @return the velocity, in world scale, of the local position on that body
     */
    public static M_Vect2D WORLD_VEL_OF_LOCAL_COORD_M(
            final I_Vect2D localPos,
            final IPair<Double, Double> velCOM,
            final double angVel,
            final I_Rot2D worldRot
    ){
        return M_Vect2D.GET(localPos)
                .rotate(worldRot)
                .cross(angVel, false) // angVel X r
                .add(velCOM); // adding main body vel
    }

    /**
     * Obtain the lower bound of a couple of I_Vect2Ds, outputting them into the given M_Vect2D
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @param out the M_Vect2D to overwrite the result into
     * @return minimum x and minimum y of the given I_Vect2Ds
     */
    public static M_Vect2D LOWER_BOUND_TO_OUT_M(final I_Vect2D a, final I_Vect2D b, final M_Vect2D out){
        out.x = a.getX() < b.getX() ? a.getX() : b.getX();
        out.y = a.getY() < b.getY() ? a.getY() : b.getY();
        return out;
    }

    /**
     * Attempts to find the minimum x and y values in the given list of I_Vect2D objects.
     * @param out the M_Vect2D to output the result into
     * @param vects the list of I_Vect2D objects we're comparing
     * @return an M_Vect2D with the minimum x and y values from that list.
     */
    public static M_Vect2D LOWER_BOUND_TO_OUT_VARARGS_M(final M_Vect2D out, final I_Vect2D... vects){
        if (vects.length == 0){
            throw new IllegalArgumentException("Can't find the minimum of 0 items!");
        }
        out.set(vects[0]);
        for (int i = vects.length-1; i > 0; i--) {
            if (out.x > vects[i].getX()){
                out.x = vects[i].getX();
            }
            if (out.y > vects[i].getY()){
                out.y = vects[i].getY();
            }
        }
        return out;
    }

    /**
     * Returns an M_Vect2D containing the lowest X and lowest Y values from that list.
     * @param vects a list of vectors to find the minimum X and Y values from
     * @return an M_Vect2D with lowest X and lowest Y values from that list.
     */
    public static M_Vect2D LOWER_BOUND_VARARGS_M(final I_Vect2D... vects){
        return LOWER_BOUND_TO_OUT_VARARGS_M(M_Vect2D._GET_RAW(), vects);
    }

    /**
     * Like LOWER_BOUND_TO_OUT_M, but without a pre-specified output vector
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @return a new M_Vect2D with the min x and min y from a and b
     */
    public static M_Vect2D LOWER_BOUND_M(final I_Vect2D a, final I_Vect2D b){
        return LOWER_BOUND_TO_OUT_M(a, b, M_Vect2D._GET_RAW());
    }

    /**
     * Obtain the lower bound of a couple of I_Vect2Ds, outputting them into the given M_Vect2D
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @param out the M_Vect2D to overwrite the result into
     * @return max x and max y of the given I_Vect2Ds
     */
    public static M_Vect2D UPPER_BOUND_TO_OUT_M(final I_Vect2D a, final I_Vect2D b, final M_Vect2D out){
        out.x = a.getX() > b.getX() ? a.getX() : b.getX();
        out.y = a.getY() > b.getY() ? a.getY() : b.getY();
        return out;
    }

    /**
     * Like UPPER_BOUND_TO_OUT_M, but outputting into a new M_Vect2D.
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @return a new M_Vect2D with the max x and max y from a and b
     */
    public static M_Vect2D UPPER_BOUND_M(final I_Vect2D a, final I_Vect2D b){
        return UPPER_BOUND_TO_OUT_M(a, b, M_Vect2D._GET_RAW());
    }

    /**
     * Puts the maximum x and maximum y from the vects in the vects list into the out M_Vect2D
     * @param out output will go in here
     * @param vects list of vectors
     * @return vector with (min x, min y)
     */
    public static M_Vect2D UPPER_BOUND_TO_OUT_VARARGS_M(final M_Vect2D out, final I_Vect2D... vects){
        if (vects.length == 0){
            throw new IllegalArgumentException("Can't find the maximum of 0 items!");
        }
        out.set(vects[0]);
        for (int i = vects.length-1; i > 0; i--) {
            if (out.x < vects[i].getX()){
                out.x = vects[i].getX();
            }
            if (out.y < vects[i].getY()){
                out.y = vects[i].getY();
            }
        }
        return out;
    }

    /**
     * Like UPPER_BOUND_TO_OUT_VARARGS_M but without a specified out vector
     * @param vects vectors to find the upper bounds of
     * @return M_Vect2D with (max x, max y)
     */
    public static M_Vect2D UPPER_BOUND_VARARGS_M(final I_Vect2D... vects){
        return UPPER_BOUND_TO_OUT_VARARGS_M(M_Vect2D._GET_RAW(), vects);
    }


    /**
     * Returns a pair holding a vector with the minimum x and y values, and another one with the maximum x and y values,
     * obtained from the I_Vect2D objects in the vects list.
     * @param vects the list of I_Vect2D objects which we're looking through
     * @return Pair of (Min vector, max vector).
     * @throws IllegalArgumentException if vects has length of 0.
     */
    @SafeVarargs
    public static IPair<I_Vect2D, I_Vect2D> GET_BOUNDS_VARARGS(final IPair<Double, Double>... vects){
        if (vects.length == 0){
            throw new IllegalArgumentException("How do you expect me to find the minimum and maximum from an empty list???");
        }
        final M_Vect2D min = M_Vect2D.GET(vects[0]);
        final M_Vect2D max = M_Vect2D.GET(min);
        for (int i = vects.length-1; i > 0 ; i--) {
            final double x = vects[i].getFirst();
            final double y = vects[i].getSecond();
            if (x < min.x){
                min.x = x;
            } else if (x > max.x){
                max.x = x;
            }
            if (y < min.y){
                min.y = y;
            } else if (y > max.y){
                max.y = y;
            }
        }
        return IPair.of(min.finished(), max.finished());
    }


    /**
     * Obtains the angle between vectors V1 and V2
     * @param v1 first vector
     * @param v2 second vector
     * @return angle between vectors V1 and V2
     */
    public static double ANGLE(final I_Vect2D v1, final I_Vect2D v2){
        return Math.atan2(v2.getY() - v1.getY(), v2.getX() - v1.getX());
    }

    /**
     * Obtains dot product of vectors V1 and V2
     * @param v1 first vector
     * @param v2 second vector
     * @return v1.v2
     */
    public static double DOT(final I_Vect2D v1, final I_Vect2D v2){
        return (v1.getX() * v2.getX()) + (v1.getY() * v2.getY());
    }

    /**
     * Obtains scalar cross product of v1 and v2
     * @param v1 the first vector
     * @param v2 second vector
     * @return v1 X v2
     */
    public static double CROSS(final I_Vect2D v1, final I_Vect2D v2){
        return v1.cross(v2);
    }

    /**
     * Obtains the vector cross product of V and S
     * @param v the vector
     * @param s the scalar (the Z component of the 3d vector)
     * @return v X s = res
     */
    public static Vect2D CROSS(final I_Vect2D v, final double s){
        return new Vect2D(s * v.getY(), -s * v.getX());
    }

    /**
     * Obtains the vector cross product of S and V
     * @param s the scalar (the Z component of the 3d vector)
     * @param v the vector
     * @return s X v = res
     */
    public static Vect2D CROSS(final double s, final I_Vect2D v){
        return new Vect2D(-s * v.getY(), s * v.getX());
    }

    /**
     * Divides vector v by d
     * @param v the vector to divide by d
     * @param d denominator for division
     * @return v/d
     */
    public static Vect2D DIVIDE(final I_Vect2D v, final double d){ return DIVIDE_M(v,d).finished(); }

    /**
     * Divides vector v by d, returns result in a mutable vector
     * @param v the vector to divide
     * @param d how much to divide it by
     * @return an M_Vect2D holding the result of v/d
     */
    public static M_Vect2D DIVIDE_M(final I_Vect2D v, final double d){ return DIVIDE_M(v, d, d);}

    /**
     * Divides vector v componentwise by p, returns result mutable
     * @param v the vector
     * @param p pair of {@literal (x divisor, y divisor)}
     * @return v but with components divided by the appropriate amounts
     */
    public static M_Vect2D DIVIDE_M(final I_Vect2D v, final IPair<Double, Double> p){
        return DIVIDE_M(v, p.getFirst(), p.getSecond());
    }

    /**
     * Divides vector v componentwise by dx and dy, returns result mutable
     * @param v the vector
     * @param dx x divisor
     * @param dy y divisor
     * @return copy of v but with x/=dx and y/=dy
     */
    public static M_Vect2D DIVIDE_M(final I_Vect2D v, final double dx, final double dy){
        return M_Vect2D.GET(v).divide(dx, dy);
    }

    /**
     * Creates a random polar vector at a random angle with a magnitude in the given range
     * @param min_mag minimum magnitude
     * @param max_mag maximum magnitude
     * @return a random polar vector
     */
    public static Vect2D RANDOM_POLAR_VECTOR(final double min_mag, final double max_mag){
        return Vect2D.POLAR(Rot2D.FROM_DEGREES(Math.random()*360), min_mag + (Math.random() * max_mag - min_mag));
    }

    /**
     * Obtains the maximum magnitude of the vectors in the given list of vectors
     * @param vects the vectors we're trying to get the longest magnitude from
     * @return biggest magnitude in vects
     */
    public static double MAX_MAGNITUDE(final I_Vect2D... vects){
        double max_mag = vects[0].mag();
        for (int i = vects.length-1; i > 0; i--) {
            max_mag = Math.max(max_mag, vects[i].mag());
        }
        return max_mag;
    }

    public static IPair<Double, Double> MIN_MAX_MAGNITUDE(final I_Vect2D... vects){
        double minSquared = vects[0].magSquared();
        double maxSquared = minSquared;

        for (int i = vects.length-1; i > 0; i--) {
            double m = vects[i].magSquared();
            if (m > maxSquared){
                maxSquared = m;
            } else if (m < minSquared){
                minSquared = m;
            }
        }
        return IPair.of(Math.sqrt(minSquared), Math.sqrt(maxSquared));
    }

    public static IPair<Double, Double> MIN_MAX_MAGNITUDE_OFFSET(final I_Vect2D offset, final I_Vect2D... vects){
        final M_Vect2D temp = M_Vect2D.GET(vects[0]).sub(offset);
        double minSquared = temp.magSquared();
        double maxSquared = minSquared;

        for (int i = vects.length-1; i > 0; i--) {
            final double m = temp.set(vects[i]).sub(offset).magSquared();
            if (m > maxSquared){
                maxSquared = m;
            } else if (m < minSquared){
                minSquared = m;
            }
        }
        temp.discard();
        return IPair.of(Math.sqrt(minSquared), Math.sqrt(maxSquared));
    }

    /**
     * Obtains the radius of the incircle from the centroid,
     * as well as the distance between the furthest vector from the centroid and the centroid.
     * @param centroid position of the centroid within local coordinates
     * @param vects corners of the polygon in local coordinates
     * @return pair of {@code <incircle radius, max magnitude>}
     */
    public static IPair<Double, Double> INCIRCLE_AND_MAX_MAGNITUDE_OFFSET(final I_Vect2D centroid, final I_Vect2D... vects){

        final M_Vect2D last = M_Vect2D.GET(vects[vects.length-1]).sub(centroid);
        final M_Vect2D current = M_Vect2D._GET_RAW();
        double minIncircle = last.magSquared();
        double maxMag = 0;
        for (int i = 0; i < vects.length; i++) {

            current.set(vects[i]).sub(centroid);

            final double thisMag = current.mag();
            if (thisMag > maxMag){
                maxMag = thisMag;
            }

            last.set(
                    VECTOR_BETWEEN_M(current, last).finished()
            );
            // given triangle with sides ab, ac, bc:
            //
            //     C __
            // bc  |   \__ ac
            //     B ------\A
            //        ab
            // height = ab * sin(A)
            // https://www.mathsisfun.com/algebra/trig-area-triangle-without-right-angle.html
            //
            final double currentHeight = thisMag * Math.sin(ANGLE(last, current));

            if (currentHeight < minIncircle){
                minIncircle = currentHeight;
            }

            last.set(current);
        }

        current.discard();
        last.discard();

        return IPair.of(minIncircle, maxMag);
    }

    public static Vect2D CLOSEST_POINT_ON_LINE_SEGMENT(I_Vect2D lineStart, I_Vect2D lineEnd, I_Vect2D observer){

        Vect2D s = Vect2DMath.VECTOR_BETWEEN(lineStart, observer);
        Vect2D proj = Vect2DMath.VECTOR_BETWEEN(lineStart, lineEnd);

        double toEndSquared = proj.magSquared();
        double sDotE = s.dot(proj);
        double dist = sDotE/toEndSquared;

        if (dist <= 0) {
            return lineStart.toVect2D();
        } if (dist >= 1){
            return lineEnd.toVect2D();
        } else{
            return ADD_SCALED(lineStart, proj, dist);
        }
    }



    /**
     * Obtains the radius of the incircle from the centroid,
     * as well as the distance between the furthest vector from the centroid and the centroid
     * @param centroid position of the centroid within local coordinates
     * @param vects corners of the polygon in local coordinates
     * @param out array which will hold centroid->localCorner vectors.
     * @return pair of {@literal  <incircle radius, max magnitude>}
     */
    public static IPair<Double, Double> INCIRCLE_AND_MAX_MAGNITUDE_OFFSET_ALSO_CENTROID_TO_CORNERS_TO_OUT(
            final I_Vect2D centroid,
            final I_Vect2D[] vects,
            final Vect2D[] out
    ){
        final M_Vect2D last = M_Vect2D.GET(vects[vects.length-1]).sub(centroid);
        final M_Vect2D current = M_Vect2D._GET_RAW();
        double minIncircle = last.magSquared();
        double maxMagSquared = 0;

        double lastMag = last.mag();

        for (int i = 0; i < vects.length; i++) {

            current.set(vects[i]).sub(centroid);

            out[i] = new Vect2D(current);


            final double thisMag = current.magSquared();
            if (thisMag > maxMagSquared){
                maxMagSquared = thisMag;
            }

            double thisClosestDist = DIST(
                    CLOSEST_POINT_ON_LINE_SEGMENT(last, current, Vect2D.ZERO), Vect2D.ZERO
            );


            if (thisClosestDist < minIncircle){
                minIncircle = thisClosestDist;
            }


            last.set(current);
        }

        current.discard();
        last.discard();

        return IPair.of(minIncircle, Math.sqrt(maxMagSquared));

    }

    /**
     * This uses the shoelace formula to compute the area of an arbitrary polygon defined by some Vect2Ds.
     * Heavily based on the C++ implementation found here:
     * <a href=https://iq.opengenus.org/area-of-polygon-shoelace/>https://iq.opengenus.org/area-of-polygon-shoelace/</a>,
     * except returning the signed area, not unsigned.
     * In short, it effectively calculates the sum of the areas of the triangles between the midpoint and each outside
     * edge of the polygon, and apparently works with self-intersects and such as well which is pretty nice I guess.
     * Probably more efficient to use {@link  #AREA_AND_CENTROID_OF_VECT2D_POLYGON(Vect2D...)} instead though.
     * @param corners list of corners of a polygon
     * @return the area of the polygon described by the Vect2Ds. If positive, that means the corners are ordered
     * anticlockwise 'about the normal'. If negative, that means the corners are clockwise.
     * @throws IllegalArgumentException if fewer than 3 corners.
     * @see <a href="https://iq.opengenus.org/area-of-polygon-shoelace/">https://iq.opengenus.org/area-of-polygon-shoelace/</a>
     * @see <a href="http://paulbourke.net/geometry/polygonmesh/centroid.pdf">http://paulbourke.net/geometry/polygonmesh/centroid.pdf</a>
     * for the area/centroid polygon maths
     * @see #AREA_AND_CENTROID_OF_VECT2D_POLYGON(Vect2D...)
     */
    public static double AREA_OF_VECT2D_POLYGON(final Vect2D... corners){

        if(corners.length < 3){
            throw new IllegalArgumentException(
                    "How do you expect me to calculate the area of a polygon with less than 3 corners???" +
                            "You only gave me " + corners.length + " corners!"
            );
        }
        final M_Vect2D prev = M_Vect2D.GET(corners[corners.length-1]);

        double area = 0;
        for (Vect2D v: corners) {
            area += (v.x + prev.x) * (v.y - prev.y);
            prev.set(v);
        }


        prev.discard();
        return area/2.0;

    }


    /**
     * Calculates the (signed) area and the centroid of an arbitrary polygon,
     * described by a list of Vect2Ds for the corners of it.
     *
     * The maths for the centroid and the corner stuff can be found in the 'see also' links at the end of this
     * javadoc comment, but basically it works for any polygon as long as it doesn't self-intersect.
     *
     * The implementation for these calculations are heavily based on the C++ implementation of the area calculation
     * found here:
     * <a href=https://iq.opengenus.org/area-of-polygon-shoelace/>https://iq.opengenus.org/area-of-polygon-shoelace/</a>,
     * except returning the signed area, not unsigned.
     * In short, it effectively calculates the sum of the areas of the triangles between the midpoint and each outside
     * edge of the polygon, and apparently works with self-intersects and such as well which is pretty nice I guess.
     *
     *
     * @param corners list of Vect2Ds which are the corners of the 2D polygon
     * @return a pair holding {@code [signed area, centroid]} for the given polygon. If area is positive, that means the
     * points are ordered anticlockwise 'about the normal', otherwise, that means they're clockwise
     * @throws IllegalArgumentException if less than 3 corners are given (because polygons need at least 3 corners)
     * @see <a href="http://paulbourke.net/geometry/polygonmesh/">http://paulbourke.net/geometry/polygonmesh/</a>
     * for a lot of polygon maths
     * @see <a href="http://paulbourke.net/geometry/polygonmesh/centroid.pdf">http://paulbourke.net/geometry/polygonmesh/centroid.pdf</a>
     * for the area/centroid maths
     * @see <a href="https://iq.opengenus.org/area-of-polygon-shoelace/">https://iq.opengenus.org/area-of-polygon-shoelace/</a>
     * for the C++ implementation for area calculations which this implementation is based on.
     */
    public static IPair<Double, Vect2D> AREA_AND_CENTROID_OF_VECT2D_POLYGON(final Vect2D... corners){

        if (corners.length < 3){
            throw new IllegalArgumentException(
                    "I can't find the area and centroid of a polygon with fewer than 3 corners! " +
                            "You only gave me " + corners.length + " corners!"
            );
        }


        final M_Vect2D centroid = M_Vect2D.GET();
        // we'll be storing the centroid in here.

        final M_Vect2D current = M_Vect2D.GET(corners[corners.length-1]);
        // we're basically starting from i-1, instead of i=0, but the end result is the same
        // (but the code is more elegant!)
        // also using this as an M_Vect2D because that way we can just declare this local variable as final
        // and then discard this (putting it back in the pool) at the end, minimizing garbage collection.

        double area = 0;

        for (Vect2D next: corners) {

            //final double current_area_calc = (current.x * next.y) - (next.x * current.y);
            // x(i)y(i+1) − x(i+1)y(i)

            final double current_area_calc = current.cross(next);

            area += current_area_calc; // area is the sum of current_area_calc results

            centroid.x += (current.x + next.x) * current_area_calc;
            // (x(i) + x(i+1)) * (x(i)y(i+1) − x(i+1)y(i))

            centroid.y += (current.y + next.y) * current_area_calc;
            // (y(i) + y(i+1)) (x(i) y(i+1) − x(i+1) y(i))

            current.set(next);
        }

        current.discard(); // we're done with 'current', so we discard it.

        area /= 2; // we need to halve area

        centroid.mult(1.0/(6.0 * area)); // centroid needs to be multiplied by 1/6A

        return IPair.of(area, centroid.finished()); // and that's us done!

    }


    /**
     * A more conclusive 'polygon moment of inertia' solver,
     * given a polygon with known mass and area, centre of mass at centroid,
     * corners described by the list of vertices, but rotating about (0,0)
     * @param mass total mass of polygon
     * @param area area of polygon
     * @param centroid centroid (centre of mass) of polygon
     * @param verts corners of the polygon
     * @return moment of inertia of this polygon about (0,0)
     */
    public static double POLYGON_MOMENT_OF_INERTIA_ABOUT_ZERO_GIVEN_CENTROID_MASS_AND_AREA(
            final double mass, final double area, final I_Vect2D centroid, final Vect2D... verts
    ){

        if (verts.length < 3){
            throw new IllegalArgumentException(
                    "That's not a polygon, polygons need at least 3 corners, you only gave " + verts.length + "!"
            );
        }
        double centroidMoment = 0;

        Vect2D prev = verts[verts.length-1];

        for (final Vect2D current: verts) {

            Vect2D currCentroid = GET_TRIANGLE_CENTROID(centroid, prev, current);

            double currAreaMass = mass * AREA_OF_ARBITRARY_TRIANGLE(centroid, prev, current) / area;

            centroidMoment += (DIST_SQUARED(centroid, currCentroid) * currAreaMass);

            prev = current;



        }

        return centroidMoment + (mass * centroid.magSquared());


    }

    /**
     * Obtains the centroid of a triangle.
     * @param a corner A
     * @param b corner B
     * @param c corner C
     * @return centroid of those corners.
     */
    public static Vect2D GET_TRIANGLE_CENTROID(final I_Vect2D a, final I_Vect2D b, final I_Vect2D c){
        return SUM(a, b, c).divide(3.0);
    }

    /**
     * Cross product of 2 2D vectors is the same as the area of the parallelogram between them.
     * So, by abusing that and the 'triangle = base+height/2' formula, we can get triangle area pretty easily
     * @param a first vector
     * @param b second vector
     * @param c third vector
     * @return area of triangle ABC.
     */
    public static double AREA_OF_ARBITRARY_TRIANGLE(final I_Vect2D a, final I_Vect2D b, final I_Vect2D c){
        return MINUS_M(b, a).cross_discardBoth(MINUS_M(c, a))/2.0;
    }

    /**
     * Obtains the centroid of a 2D polygon described by a list of corners when the area is already known.
     * See {@link #AREA_AND_CENTROID_OF_VECT2D_POLYGON(Vect2D...)} for the maths. Probably more efficient to use
     * that method instead tbh.
     * @param area the known area of the 2D polygon
     * @param corners the list of Vect2Ds which this 2D polygon consists of
     * @return the centroid of this 2D polygon with a known area.
     * @see #AREA_AND_CENTROID_OF_VECT2D_POLYGON(Vect2D...)
     */
    public static Vect2D CENTROID_OF_VECT2D_POLYGON_GIVEN_AREA(final double area, final Vect2D... corners){
        if (corners.length < 3){
            throw new IllegalArgumentException(
                    "I can't find the centroid of a polygon with fewer than 3 corners! " +
                            "You only gave me " + corners.length + " corners!"
            );
        }

        final M_Vect2D centroid = M_Vect2D.GET();
        // we'll be storing the centroid in here.

        final M_Vect2D current = M_Vect2D.GET(corners[corners.length-1]);
        // we're basically starting from i-1, instead of i=0, but the end result is the same
        // (but the code is more elegant!)
        // also using this as an M_Vect2D because that way we can just declare this local variable as final
        // and then discard this (putting it back in the pool) at the end, minimizing garbage collection.

        for (Vect2D next: corners) {

            final double current_area_calc = current.cross(next);
            // x(i)y(i+1) − x(i+1)y(i)

            centroid.x += (current.x + next.x) * current_area_calc;
            // (x(i) + x(i+1)) * (x(i)y(i+1) − x(i+1)y(i))

            centroid.y += (current.y + next.y) * current_area_calc;
            // (y(i) + y(i+1)) (x(i) y(i+1) − x(i+1) y(i))

            current.set(next);
        }
        current.discard(); // we're done with 'current', so we discard it.


        return centroid.mult(1.0/(6.0 * area)).finished(); // centroid needs to be multiplied by 1/6A
    }

    /**
     * Given the polygon described by in_points, copies it into out_points, but shifted such that the centroid of
     * that polygon is now (0,0), and also returns the signed area and original centroid of that polygon.
     * @param in_points The points describing the original polygon
     * @param out_points The polygon shifted to have centroid (0,0)
     * @return an IPair of {@code [signed area, original centroid]} for the polygon described by in_points
     * @throws IllegalArgumentException if in_points has a length below 3, or if out_points is smaller than in_points
     * @see #AREA_AND_CENTROID_OF_VECT2D_POLYGON(Vect2D...)
     */
    public static IPair<Double, Vect2D> TRANSLATE_POLYGON_TO_SHIFT_CENTROID_TO_ORIGIN_AND_RETURN_AREA_AND_DISPLACEMENT(
            final Vect2D[] in_points,
            final Vect2D[] out_points
    ){

        if (in_points.length > out_points.length){
            throw new IllegalArgumentException("out_points list cannot be smaller than in_points list!");
        }

        IPair<Double, Vect2D> centroid_area = AREA_AND_CENTROID_OF_VECT2D_POLYGON(in_points);

        for (int i = 0; i < in_points.length; i++) {
            out_points[i] = MINUS(in_points[i], centroid_area.getSecond());
        }

        return centroid_area;

    }


    /**
     * Returns moment of inertia for a circle about (0, 0)
     * Uses formula (1/2) * mass * r^2
     * @param radius radius of this circle
     * @param mass mass of this circle
     * @return the moment of inertia for a circle with given radius about (0, 0)
     */
    public static double CIRCLE_MOMENT_OF_INERTIA(final double radius, final double mass){
        return (mass * radius * radius)/2.0;
    }

    /**
     * Returns moment of inertia for a line between 'start' and 'end', with given mass, assuming uniform mass,
     * about axis (0,0), using formula sum(massK, distK^2)
     * @param start first end of the line
     * @param end other end of the line
     * @param mass mass of the line
     * @return moment of inertia for that line
     */
    public static double LINE_MOMENT_OF_INERTIA(final I_Vect2D start, final I_Vect2D end, final double mass){



        return mass * (start.magSquared() + end.magSquared())/2.0;

        // m: total mass
        // I: inertia
        // a: start dist
        // b: end dist
        //
        // n = m/2 [mass of each point]
        // x = a^2
        // y = b^2
        //
        // I = nx + ny
        // 2I = mx + my
        // 2I/m = x + y
        // I/m = (x + y)/2
        // I = m * (x+y)/2
    }

    /**
     * Returns moment of inertia for a line from 'start' with midpoint 'mid', with given mass, assuming uniform mass,
     * with centre of mass at the midpoint of that line, but turning about axis (0,0), using the parallel axis theorem.
     * @param start first end of the line
     * @param mid midpoint of that line
     * @param mass mass of the line
     * @return moment of inertia for that line turning about (0,0)
     */
    public static double LINE_START_CENTROID_MOMENT_OF_INERTIA(final I_Vect2D start, final I_Vect2D mid, final double mass){


        // moment of inertia for axis through midpoint + (mass * dist to rotation place)
        return (mass * mid.magSquared()) + (mass * Vect2DMath.ADD(start, mid).magSquared());

    }

    /**
     * Gets a random Vect2D within these bounds
     * @param min lower bound
     * @param max upper bound
     * @return random Vect2D in these bounds
     */
    public static Vect2D RANDOM_VECTOR_IN_BOUNDS(final I_Vect2D min, final I_Vect2D max){

        return new Vect2D(
                min.getX() + (Math.random() * (max.getX() - min.getX())),
                min.getY() + (Math.random() * (max.getY() - min.getY()))
        );

    }


    /**
     * Basically returns the quarter midpoints between the min and the max
     * @param min min(x,y) of region
     * @param max max(x,y) of region
     * @return the points that describe the midpoints between (min, midpoint) and (midpoint, max)
     */
    public static IPair<Vect2D, Vect2D> QUARTER_MIDPOINTS(final I_Vect2D min, final I_Vect2D max){
        final Vect2D quarterDiff = VECTOR_BETWEEN_M(min, max).divide(4).finished();
        return IPair.of(
                SUM(min, quarterDiff),
                MINUS(max, quarterDiff)
        );
    }

    /**
     * Knowing the min bounds and a known original midpoint, we can generate the midpoints for each quarter region
     * around the knownMidpoint
     * @param min the lower bound of the outer region
     * @param knownMidpoint our known midpoint
     * @return quadruplet with: -x-y, +x+y, +x-y, -y+x regions
     */
    public static IQuadruplet<Vect2D, Vect2D, Vect2D, Vect2D> ALL_QUARTER_MIDPOINTS_FROM_KNOWN_MIDPOINT(final Vect2D min, final Vect2D knownMidpoint){
        final Vect2D min_midpoint = min.lerp(knownMidpoint, 0.5);
        return IQuadruplet.of(
                min_midpoint,
                INVERT_RELATIVE_TO(min_midpoint, knownMidpoint),
                INVERT_X_RELATIVE_TO(min_midpoint, knownMidpoint.x),
                INVERT_Y_RELATIVE_TO(min_midpoint, knownMidpoint.y)
        );
    }

    /**
     * Returns a Quadruplet of the boundaries of all the sub-regions of this given region
     * @param min lower bound of outer region
     * @param max upper bound of outer region
     * @param mid midpoint of outer region
     * @return quadruplet of (lower bound) upper bound pairs for each region. Order is (-x-y, +x+y, -x+y, -y+x)
     */
    public static IQuadruplet<
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>
    > ALL_QUARTER_REGIONS_BOUNDS(final Vect2D min, final Vect2D max, final Vect2D mid){
        return IQuadruplet.of(
                IPair.of(min, mid),
                IPair.of(mid, max),
                IPair.of(new Vect2D(min.x, mid.y), new Vect2D(mid.x, max.y)),
                IPair.of(new Vect2D(mid.x, min.y), new Vect2D(max.x, mid.y))
        );
    }


    /**
     * Returns a Quadruplet of (lower bound, midpoint) pairs of all the sub-regions of this given region
     * @param minMid pair of (lower bound, midpoint) for parent region
     * @return quadruplet of (lower bound, midpoint) pairs for each region. Order is (-x-y, +x+y, -x+y, +x-y)
     * @see #ALL_QUARTER_REGIONS_LOWERBOUND_MIDPOINTS(Vect2D, Vect2D)
     */
    public static IQuadruplet<
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>
            > ALL_QUARTER_REGIONS_LOWERBOUND_MIDPOINTS(IPair<Vect2D, Vect2D> minMid){
        return ALL_QUARTER_REGIONS_LOWERBOUND_MIDPOINTS(minMid.getFirst(), minMid.getSecond());
    }

    /**
     * Returns a Quadruplet of (lower bound, midpoint) pairs of all the sub-regions of this given region
     * @param min lower bound of outer region
     * @param mid midpoint of outer region
     * @return quadruplet of (lower bound, midpoint) pairs for each region. Order is (-x-y, +x+y, -x+y, +x-y)
     */
    public static IQuadruplet<
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>,
            IPair<Vect2D, Vect2D>
            > ALL_QUARTER_REGIONS_LOWERBOUND_MIDPOINTS(final Vect2D min, final Vect2D mid){
        final Vect2D offset = MINUS_M(mid, min).mult(0.5).finished(); // offset of child midpoints from their min
        return IQuadruplet.of(
                PAIR_OF_MIN_AND_OFFSET_MIN(min, offset),
                PAIR_OF_MIN_AND_OFFSET_MIN(mid, offset),
                PAIR_OF_MIN_AND_OFFSET_MIN(new Vect2D(min.x, mid.y), offset),
                PAIR_OF_MIN_AND_OFFSET_MIN(new Vect2D(mid.x, min.y), offset)
        );
    }

    /**
     * Returns a pair of (v, v + offset)
     * @param v initial vector
     * @param offset vector to add to V and put in second
     * @return pair of (v, v+offset)
     */
    private static IPair<Vect2D, Vect2D> PAIR_OF_MIN_AND_OFFSET_MIN(final Vect2D v, final Vect2D offset){
        return IPair.of(v, v.add(offset));
    }

    /**
     * Invert y axis of vector relative to iY
     * @param v the vector we're inverting
     * @param iY inverting x relative to here
     * @return v but the x value is reflected about iY on y axis
     */
    public static Vect2D INVERT_Y_RELATIVE_TO(final I_Vect2D v, final double iY){
        return new Vect2D(
                v.getX(),
                v.getY() + ((iY - v.getY()) * 2)
        );
    }

    public static Vect2D INVERT_X(final I_Vect2D v){
        return new Vect2D(
                -v.getX(),
                v.getY()
        );
    }

    /**
     * Invert x axis of vector relative to iX
     * @param v the vector we're inverting
     * @param iX inverting x relative to here
     * @return v but the x value is reflected about the iX on x axis
     */
    public static Vect2D INVERT_X_RELATIVE_TO(final I_Vect2D v, final double iX){
        return new Vect2D(
                v.getX() + ((iX - v.getX()) * 2),
                v.getY()
        );
    }

    public static Vect2D INVERT_Y(final I_Vect2D v){
        return new Vect2D(
                v.getX(),
                -v.getY()
        );
    }


    /**
     * Returns a vector that's a copy of the current one but with X and Y inverted in relation to invertAround
     * @param v the vector we're reflecting
     * @param invertAround reflecting it around this point
     * @return v but x and y are inverted relative to invertAround
     */
    public static Vect2D INVERT_RELATIVE_TO(final I_Vect2D v, final I_Vect2D invertAround){
        return ADD_SCALED(
                v,
                VECTOR_BETWEEN(v, invertAround),
                2
        );
    }


    /**
     * Once again, somewhat based on
     * <a href="https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/">https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/</a>
     * @param line1start start of first line
     * @param line1proj projection of first line
     * @param line2start start of second line
     * @param line2proj projection of second line
     * @return true if the two lines intersect, false otherwise
     * @see <a href="https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/">https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/</a>
     */
    public static boolean DO_LINES_INTERSECT(
            final I_Vect2D line1start, final I_Vect2D line1proj,
            final I_Vect2D line2start, final I_Vect2D line2proj
    ){

        final double toOtherStart = ANGLE_BETWEEN_LINE_PROJ_AND_POINT(line1start, line1proj, line2start);
        final double toOtherEnd = ANGLE_BETWEEN_LINE_PROJ_AND_POINT(
                line1start, line1proj, Vect2DMath.ADD(line2start, line2proj)
        );

        return Vect2DMath.COMPARE_DOUBLES_EPSILON(toOtherStart, 0) == 0 ||
                Vect2DMath.COMPARE_DOUBLES_EPSILON(toOtherEnd, 0) == 0 ||
                (toOtherStart < 0 ^ toOtherEnd < 0);

    }

    /**
     * Performs {@link #DO_LINES_INTERSECT(I_Vect2D, I_Vect2D, I_Vect2D, I_Vect2D)} but in both directions (treating
     * each line as line1 and also as line2), but it's an OR thing so chances are the second check won't be needed
     * @param line1start where line 1 starts
     * @param line1proj projection of line 1
     * @param line2start where line 2 starts
     * @param line2proj projection of line 2
     * @return true if both lines intersect
     */
    public static boolean DO_LINES_INTERSECT_CHECK_BOTH(
            final I_Vect2D line1start, final I_Vect2D line1proj,
            final I_Vect2D line2start, final I_Vect2D line2proj
    ){
        return DO_LINES_INTERSECT(line1start, line1proj, line2start, line2proj) ||
                DO_LINES_INTERSECT(line2start, line2proj, line1start, line1proj);
    }

    /**
     * Obtains the point where lines 1 and 2 intersect.
     *
     * Uses maths from <a href="http://paulbourke.net/geometry/pointlineplane/">http://paulbourke.net/geometry/pointlineplane/</a>
     * @param line1start where line 1 starts
     * @param line1proj projection of line 1
     * @param line2start where line 2 starts
     * @param line2proj projection of line 2
     * @return point (world coords) where lines 1 and 2 intersect.
     * @see <a href="http://paulbourke.net/geometry/pointlineplane/">http://paulbourke.net/geometry/pointlineplane/</a>
     */
    public static Vect2D GET_INTERSECTION_POINT(
            final I_Vect2D line1start, final I_Vect2D line1proj,
            final I_Vect2D line2start, final I_Vect2D line2proj
    ){

        final double denominator = line1proj.cross(line2proj);

        if (Vect2DMath.COMPARE_DOUBLES_EPSILON(denominator, 0) == 0){
            // if cross product is 0, we just get a point that isn't at the very end of the lines.
            return Vect2DMath.GET_MIDDLE_VECTOR(line2start, ADD(line2start, line2proj), ADD(line1start, line1proj)).toVect2D();
        }

        final Vect2D start1MinusStart2 = Vect2DMath.MINUS(line1start, line2start);

        return Vect2DMath.MULTIPLY_M(
                line1proj,
                line2proj.cross(start1MinusStart2) / denominator,
                line1proj.cross(start1MinusStart2) / denominator
        ).add(line1start).finished();

    }

    /**
     * Given a line going in lineProj from lineStart along with a random point, find the angle between
     * the end of the line and that random point, as viewed from the start of the line
     * @param lineStart where the line starts
     * @param lineProj projection of the line
     * @param point the point we're trying to get the angle of
     * @return angle between lineStart->lineProj and lineStart->point
     */
    public static double ANGLE_BETWEEN_LINE_PROJ_AND_POINT(final I_Vect2D lineStart, final I_Vect2D lineProj, final I_Vect2D point){
        return lineProj.cross(Vect2DMath.MINUS(point, lineStart));
    }


    /**
     * Attempts to see if a given point is within a given polygon.
     * Based on the algorithm explained here: <a href="https://jeffreythompson.org/collision-detection/poly-point.php">https://jeffreythompson.org/collision-detection/poly-point.php</a>
     * @param point the point we are checking
     * @param verts vertices describing the polygon
     * @return true if the point is within the shape described by verts.
     * @see <a href="https://jeffreythompson.org/collision-detection/poly-point.php">https://jeffreythompson.org/collision-detection/poly-point.php</a>
     */
    public static boolean IS_POINT_IN_POLYGON(final I_Vect2D point, final I_Vect2D... verts){

        boolean collided = false;

        final M_Vect2D p = M_Vect2D.GET(point);
        final M_Vect2D prev = M_Vect2D.GET(verts[verts.length-1]);
        final M_Vect2D curr = M_Vect2D._GET_RAW();

        for (int i = 0; i < verts.length; i++) {
            curr.set(verts[i]);

            if (
                    ((prev.y >= p.y && curr.y < p.y) || (prev.y < p.y && curr.y >= p.y)) &&
                    p.x < (curr.x - prev.x) * (p.y - prev.y) / (curr.y - prev.y) + prev.x
            ){
                collided =! collided;
            }
        }
        p.discard();
        prev.discard();
        curr.discard();
        return collided;

    }

    /**
     * Given a line from (0,0), and a polygon with origin (0,0) described by a list of 2D vectors,
     * attempts to find the point on the polygon's edge
     * where the fromOriginLine intersects the edges of the polygon.
     * @param fromOriginLine line from origin towards somewhere that isn't the origin
     * @param corners corners of the polygon
     * @return point on the polygon's perimeter where the fromOriginLine intersects with it. Returns 0,0 if not found.
     */
    public static Vect2D GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(final I_Vect2D fromOriginLine, final Vect2D... corners){

        Vect2D prev = corners[corners.length-1];

        for (final Vect2D current: corners){

            final Vect2D cProj = VECTOR_BETWEEN(current, prev);

            if (DO_LINES_INTERSECT(Vect2D.ZERO, fromOriginLine, current, cProj)){
                return GET_INTERSECTION_POINT(Vect2D.ZERO, cProj, current, cProj);
            }
            prev = current;
        }

        return Vect2D.ZERO;
    }

    /**
     * Checks if lower <= p <= upper (component-wise)
     * @param p the vector we're checking
     * @param upperBound upper bound (p must not have a component greater than this)
     * @param lowerBound lower bound (p must not have a component smaller than this)
     * @return true if {@code lower <= p <= upper}.
     */
    public static boolean IS_IN_BOUNDS(final I_Vect2D p, final I_Vect2D upperBound, final I_Vect2D lowerBound){
        return p.isGreaterThanOrEqualTo(lowerBound) && upperBound.isGreaterThanOrEqualTo(p);
    }

    /**
     * Wrapper for {@link #IS_IN_BOUNDS(I_Vect2D, I_Vect2D, I_Vect2D)} for when
     * we don't know which is upper which is lower
     * @param p the point
     * @param a one of the bounds
     * @param b the other bound
     * @return whether or not p is within the bounds described by a and b, whichever way round they are.
     */
    public static boolean IS_IN_BOUNDS_UNKNOWN_ORDERING(final I_Vect2D p, final I_Vect2D a, final I_Vect2D b){
        if (a.isGreaterThanOrEqualTo(b)){
            return IS_IN_BOUNDS(p, a, b);
        } else {
            return IS_IN_BOUNDS(p, b, a);
        }
    }


    /**
     * Works out if a point is on a line (given line projection)
     * @param point the point
     * @param lineStart start of the line
     * @param lineProj projection of the line
     * @return true if point is on that line (or at very least within epsilon distance of it)
     */
    public static boolean IS_POINT_ON_LINE_PROJ(final I_Vect2D point, final I_Vect2D lineStart, final I_Vect2D lineProj){

        // I did attempt making an implementation that wouldn't rely on .mag because, y'know,
        // square roots can be slow-ish, but the workaround was almost definitely slower, so sod it,
        // here's the .mag one.

        return COMPARE_DOUBLES_EPSILON(
                DIST(lineStart, point) +
                        // point-start = relPoint (point relative to start)
                        // relPoint-proj = proj->relPoint == dist from end to point
                        M_Vect2D.GET(point).sub(lineStart).sub(lineProj).mag_discard(),
                lineProj.mag()
        ) == 0;


    }
    /**
     * Works out if a point is on a line (given line end)
     * @param point the point
     * @param lineStart start of the line
     * @param lineEnd end point of the line
     * @return true if point is on that line (or at very least within epsilon distance of it)
     */
    public static boolean IS_POINT_ON_LINE_END(final I_Vect2D point, final I_Vect2D lineStart, final I_Vect2D lineEnd){
        return COMPARE_DOUBLES_EPSILON(
                DIST(lineStart, point) + DIST(lineEnd, point),
                DIST(lineStart, lineEnd)
        ) == 0;
    }

    /**
     * EXPECTS STUFF TO HAVE BEEN POSITIONED RELATIVE TO THE START OF THE LINE!
     * Checks if a point (POSITIONED RELATIVE TO THE START OF THE LINE!) has the same angle as the line's projection,
     * then makes sure that the point is between 0 and the projection (thus being on the projection)
     * @param projPoint point relative to the line projection
     * @param proj line projection
     * @return true if projPoint is on line proj.
     */
    public static boolean IS_POINT_ON_LINE_LOCAL(final I_Vect2D projPoint, final I_Vect2D proj){
        return COMPARE_DOUBLES_EPSILON(proj.cross(projPoint), 0) == 0 &&
                IS_IN_BOUNDS_UNKNOWN_ORDERING(projPoint, proj, Vect2D.ZERO);
    }


    /**
     * Returns 1 if given 0, else returns v as-is.
     * @param v the value which may or may not be 0
     * @return v, or 1 if v is 0.
     */
    public static double RETURN_1_IF_0(final double v){
        if (Double.compare(v, 0) == 0){
            return 1;
        } else {
            return v;
        }
    }

    public static Vect2D INVERT(final I_Vect2D v){
        return new Vect2D(-v.getX(), -v.getY());
    }


    /**
     * Creates a regular polygon with specified number of vertices + radius, in the form of an array of Vect2Ds.
     * @param vertices number of vertices
     * @param radius 'radius' of the polygon (dist between each vertex and the middle)
     */
    public static Vect2D[] MAKE_REGULAR_POLYGON(final int vertices, final double radius){

        final Vect2D[] vects = new Vect2D[vertices];
        MAKE_REGULAR_POLYGON_TO_OUT(vertices, radius, vects);
        return vects;

    }

    /**
     * Creates a regular polygon with specified number of vertices + radius, puts it in out
     * @param vertices number of vertices
     * @param radius 'radius' of the polygon (dist between each vertex and the middle)
     * @param out premade array to put the result in. please make sure it's big enough.
     */
    public static void MAKE_REGULAR_POLYGON_TO_OUT(final int vertices, final double radius, final Vect2D[] out){
        final M_Rot2D rot = M_Rot2D._GET_RAW();
        final double deg = Math.toRadians(360.0/(double)vertices);
        for (int i = 0; i < vertices; i++) {
            out[i] = Vect2D.POLAR(rot.set(i*deg), radius);
        }
        rot.dispose();
    }

    public static Vect2D[] OFFSET_VECTORS_INTO_NEW_LIST(final Vect2D offset, final Vect2D... vects){
        final Vect2D[] out = new Vect2D[vects.length];
        for (int i = vects.length-1; i >= 0 ; i--) {
            out[i] = vects[i].add(offset);
        }
        return out;
    }

    public static Vect2D[] OFFSET_VECTORS_SO_CENTROID_IS_AT_ZERO_INTO_NEW_LIST(final Vect2D... vects){
        return OFFSET_VECTORS_INTO_NEW_LIST(AREA_AND_CENTROID_OF_VECT2D_POLYGON(vects).getSecond().invert(), vects);
    }

    public static void OFFSET_VECTORS_TO_OUT(final Vect2D offset, final Vect2D[] in, final Vect2D[] out){
        for (int i = in.length-1; i >= 0 ; i--) {
            out[i] = in[i].add(offset);
        }
    }

    public static void OFFSET_VECTORS_IN_PLACE(final Vect2D offset, final Vect2D[] in){
        for (int i = in.length-1; i >= 0 ; i--) {
            in[i] = in[i].add(offset);
        }
    }

    /**
     * Returns n only if it's finite and also a number
     * @param n default value
     * @param x backup value if n is infinite/not a number
     * @return n, or x if n is infinite/not a number
     */
    public static double RETURN_X_IF_NOT_FINITE(final double n, final double x){
        if (Double.isFinite(n)){
            return n;
        } else {
            return x;
        }
    }

    /**
     * Returns n only if a finite number which isn't zero.
     * @param n default value
     * @param x backup value if n is infinite/not a number/zero
     * @return n, or x if n is infinite/not a number/zero
     */
    public static double RETURN_X_IF_NOT_FINITE_OR_IF_ZERO(final double n, final double x){
        if (Double.isFinite(n) && Double.compare(n, 0) != 0){
            return n;
        }
        return x;
    }


}







