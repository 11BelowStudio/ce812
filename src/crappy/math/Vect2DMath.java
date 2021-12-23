package crappy.math;

import crappy.I_Transform;
import crappy.utils.IPair;
import crappy.utils.Pair;

/**
 * A utility class holding static Vect2D math-related methods.
 *
 * Can statically import any necessary methods from here on a per-method basis.
 */
public final class Vect2DMath {

    /**
     * No constructing.
     */
    private Vect2DMath(){}

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D MINUS(final Vect2D v1, final Vect2D v2) {
        // returns v1-v2
        return v1.addScaled(v2, -1);
    }

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D MINUS(final Vect2D v1, final I_Vect2D v2){
        return v1.addScaled(v2, -1);
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
     * Obtains the midpoint between the given Vect2Ds when we know which one is the lower bound and which one
     * is the upper bound
     * @param min lower bound Vect2D
     * @param max upper bound Vect2D
     * @return midpoint of min and max
     */
    public static Vect2D MIDPOINT_MIN_MAX(final Vect2D min, final Vect2D max){
        return max.lerp(min, 0.5);
    }

    /**
     * Linearly interpolates from start to end
     * @param start start from here
     * @param end go to here
     * @param lerpScale how much to lerp by (0: return start. 1: return end. 0.5: midpoint)
     * @return vector that's lerpScale of the way between start and end
     */
    public static Vect2D LERP(final Vect2D start, final Vect2D end, final double lerpScale){
        return start.lerp(end, lerpScale);
    }


    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'locals'
     * list, and outputs them into the given 'out' list.
     * @param bodyTransform the transform of the body
     * @param locals local positions of everything in the body
     * @param localNorms local normal vectors of everything in the body
     * @param out the list which the world positions of everything in the body will be put into
     * @param outNorms world normal vectors of everything in the body
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
     */
    public static void LOCAL_TO_WORLD_FOR_BODY_TO_OUT(
            final Vect2D bodyPos,
            final Rot2D bodyRotation,
            final Vect2D[] locals,
            final Vect2D[] localNorms,
            final Vect2D[] out,
            final Vect2D[] outNorms
    ){
        for (int i = locals.length-1; i >= 0; i--) {
            out[i] = locals[i].localToWorldCoordinates(bodyPos, bodyRotation);
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
     */
    public static IPair<Vect2D, Vect2D> LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
            final Vect2D bodyPos,
            final Rot2D bodyRotation,
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
            outCoords[i] = localCoords[i].localToWorldCoordinates(bodyPos, bodyRotation);
            outNormals[i] = localNormals[i].rotate(bodyRotation);
            if (outCoords[i].x < min.x){
                min.x = outCoords[i].x;
            } else if (outCoords[i].x > max.x) {
                max.x = outCoords[i].x;
            }
            if (outCoords[i].y < min.y){
                min.y = outCoords[i].y;
            } else if (outCoords[i].y > max.y) {
                max.y = outCoords[i].y;
            }
        }
        return new Pair<>(min.finished(), max.finished());
    }



    /**
     * Subtracts V1 from V2, returning as an M_Vect2D
     * @param v1 first vector
     * @param v2 second vector
     * @return v1-v2
     */
    public static M_Vect2D MINUS_MUT(final I_Vect2D v1, final I_Vect2D v2){
        return M_Vect2D.GET(v1).addScaled(v2, -1);
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
        return new Pair<>(min.finished(), max.finished());
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
     * @param s the scalar (the non-zero component of V X (result of this)
     * @return res -> v X res = s
     */
    public static Vect2D CROSS(final I_Vect2D v, final double s){
        return new Vect2D(-s * v.getY(), s * v.getX());
    }

    /**
     * Divides vector v by d
     * @param v the vector to divide by d
     * @param d denominator for division
     * @return v/d
     */
    public static Vect2D DIVIDE(final I_Vect2D v, final double d){
        return new Vect2D(v.getX()/d, v.getY()/d);
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

}
