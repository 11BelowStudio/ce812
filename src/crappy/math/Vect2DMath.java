package crappy.math;

import crappy.I_Transform;
import crappy.utils.containers.IPair;
import crappy.utils.containers.IQuadruplet;
import crappy.utils.containers.ITriplet;

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
    public static Vect2D MINUS(final Vect2D v1, final Vect2D v2) { return v2.addScaled(v1, -1); }

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D MINUS(final I_Vect2D v1, final Vect2D v2){ return v2.addScaled(v1, -1); }

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
        return ADD_SCALED_M(v2, v1, -1);
    }

    /**
     * Returns the vector between start and end {@code start->end}. Or, in other words, {@code start-end}.
     * @param start where we're starting from
     * @param end where we're going
     * @return vector from start to end.
     */
    public static Vect2D VECTOR_BETWEEN(final I_Vect2D start, final I_Vect2D end){
        return MINUS(end, start);
    }

    /**
     * Returns the vector between start and end {@code start->end}. Or, in other words, {@code start-end}.
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
        return M_Vect2D.GET(start).lerp(end, lerpScale).finished();
    }

    /**
     * Returns the distance between vectors A and B
     * @param a the first vector
     * @param b the second vector
     * @return scalar distance between A and B
     */
    public static double DIST(final I_Vect2D a, final I_Vect2D b){
        return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
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
        return IPair.of(min.finished(), max.finished());
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
            out[i] = locals[i].localToWorldCoordinates(pos, rot);
            if (out[i].x < min.x){
                min.x = out[i].x;
            } else if (out[i].x > max.x) {
                max.x = out[i].x;
            }
            if (out[i].y < min.y){
                min.y = out[i].y;
            } else if (out[i].y > max.y) {
                max.y = out[i].y;
            }
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
            out[i] = locals[i].localToWorldCoordinates(pos, rot);
        }
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
    public static Vect2D DIVIDE(final I_Vect2D v, final double d){
        return new Vect2D(v.getX()/d, v.getY()/d);
    }

    /**
     * Divides vector v by d, returns result in a mutable vector
     * @param v the vector to divide
     * @param d how much to divide it by
     * @return an M_Vect2D holding the result of v/d
     */
    public static M_Vect2D DIVIDE_M(final I_Vect2D v, final double d){ return M_Vect2D.GET(v.getX()/d, v.getY()/d);}

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
     * @see <a href=https://iq.opengenus.org/area-of-polygon-shoelace/>https://iq.opengenus.org/area-of-polygon-shoelace/</a>
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
     * Attempts to find the moment of inertia for an arbitrary polygon with corners defined by the 'corners' list,
     * based on the moment of inertia algorithm on Wikipedia for:
     * 'Plane polygon with vertices P1, P2, P3, ..., PN and mass m uniformly distributed on its interior,
     * rotating about an axis perpendicular to the plane and passing through the origin.'
     * @param mass mass of that polygon
     * @param corners list of vectors describing the shape in question
     * @return moment of inertia about (0,0) for that shape BEFORE BEING MULTIPLIED BY MASS!
     * @throws IllegalArgumentException if fewer than 3 corners given
     */
    public static double POLYGON_MOMENT_OF_INERTIA_ABOUT_ZERO(final double mass, final Vect2D... corners){
        if (corners.length < 3){
            throw new IllegalArgumentException(
                    "I can't find the moment of inertia for a polygon with fewer than 3 corners! " +
                            "You only gave me " + corners.length + " corners!"
            );
        }

        double numerator = 0;
        double denominator = 0;

        final M_Vect2D current = M_Vect2D.GET(corners[corners.length-1]);

        for (Vect2D next: corners) {

            final double cXn = current.cross(next);

            numerator += (
                    cXn + current.dot(current) + current.dot(next) + next.dot(next)
            );

            denominator += cXn;

            current.set(next);
        }

        current.finished();

        return mass * numerator / (6 * denominator);

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




}







