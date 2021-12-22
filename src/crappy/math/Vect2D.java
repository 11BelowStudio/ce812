package crappy.math;

import crappy.utils.IPair;
import crappy.utils.Pair;

import java.io.Serializable;

/**
 * The immutable 2D vector used by CRAPPY.
 *
 * Based on implementions provided for the CE812 and CE218 courses at the University of Essex,
 * with extra utility methods added later on.
 */
public final class Vect2D implements Serializable, I_Vect2D {

    public final double x, y;

    /**
     * A zero vector, here so I don't need to keep instantiating a new empty vector every time a zero vector is needed.
     */
    public static final Vect2D ZERO = new Vect2D(0,0);

    /**
     * Creates a null vector (0,0).
     * Please use ZERO instead of trying to create yet another Vect2D with value (0,0).
     */
    private Vect2D() {
        this(0, 0);
    }

    /**
     * create vector with given coordinates
     * @param x x coordinate
     * @param y y coordinate.
     */
    public Vect2D(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new vector that's a copy of the argument vector
     * @param v vector to copy
     */
    public Vect2D(final Vect2D v) {
        this(v.x, v.y);
    }


    /**
     * Creates a new immutable vector which is a copy of the argument I_Vect2D.
     * @param v an I_Vect2D to copy
     */
    Vect2D(final I_Vect2D v){
        this(v.getX(), v.getY());
    }


    /**
     * Whether this is equal to another object.
     * True if the other object is a Vect2D  with same x and y
     * @param o other object
     * @return whether or not this is equal to the other object
     */
    public boolean equals(final Object o) {
        if (o instanceof Vect2D) {
            final Vect2D v = (Vect2D) o;
            return x == v.x && y == v.y;
        }
        return false;
    }

    /**
     * magnitude
     * @return magnitude of this vector
     */
    public double mag() {
        return Math.hypot(x, y);
    }

    /**
     * The angle of this vector
     * @return the angle of this vector
     */
    public double angle() {
        return Math.atan2(y, x);
    }

    /**
     * angle of difference vector between this vector and other vector
     * @param other other vector
     * @return angle between them
     */
    public double angle(final Vect2D other) {
        return Math.atan2(other.y - y, other.x - x);
    }

    double angle(final I_Vect2D other){ return Math.atan2(other.getY() - y, other.getX() - x); }

    public String toString() {
        return "(" + String.format("%.01f", x) + "," + String.format("%.01f", y)
                + ")";
    }

    /**
     * Adds a vector equal to this + v
     * @param v vector being added to a copy of this vector
     * @return a vector equal to this+v
     */
    public Vect2D add(final Vect2D v) {
        return new Vect2D(x + v.x, y + v.y);
    }


    /**
     * Adding a scalar d to x and y
     * @param d scalar to add to x and y
     * @return this but x and y have both been increased by d.
     */
    public Vect2D add(final double d){ return new Vect2D(x + d, y + d); }

    /**
     * Adds a vector equal to this + v
     * @param v vector being added to a copy of this vector
     * @return a vector equal to this+v
     */
    Vect2D add(final I_Vect2D v){ return new Vect2D(x + v.getX(), y + v.getY()); }

    /**
     * Finds the sum of these vectors, returning the result as a Vect2D.
     * @param vects all of the vectors to add together
     * @return Sum of all of those vectors.
     */
    public static Vect2D sum(final I_Vect2D... vects){
        double x = 0;
        double y = 0;
        for (final I_Vect2D v: vects){
            x += v.getX();
            y += v.getY();
        }
        return new Vect2D(x, y);
    }

    /**
     * Scaled addition of two vectors.
     * note: vector subtraction can be expressed as scaled addition with factor (-1)
     * @param v the other vector
     * @param fac how much that other vector will be scaled by
     * @return a vector equal to this + (v * fac)
     */
    public Vect2D addScaled(final Vect2D v, final double fac) {
        return new Vect2D(x + (v.x * fac), y + (v.y * fac));
    }

    /**
     * Scaled addition of two vectors.
     * note: vector subtraction can be expressed as scaled addition with factor (-1)
     * @param v the other vector
     * @param fac how much that other vector will be scaled by
     * @return a vector equal to this + (v * fac)
     */
    Vect2D addScaled(final I_Vect2D v, final double fac){
        return new Vect2D(x + (v.getX() * fac), y + (v.getY() * fac));
    }

    /**
     * Returns a new vector equal to this*fac
     * @param fac how much to multiply this vector by
     * @return result of this * fac
     */
    public Vect2D mult(final double fac) {
        return new Vect2D(this.x * fac,this.y * fac);
    }

    /**
     * rotate by angle given in radians
     * (basically scalar rotation)
     * @param rot the rotation to rotate this Vect2D by
     * @return this Vect2D, rotated by angle radians.
     */
    public Vect2D rotate(final I_Rot2D rot) {
        return new Vect2D(
                x * rot.get_cos() - y * rot.get_sin(),
                x * rot.get_sin() + y * rot.get_cos()
        );
    }

    /**
     * Dot product of this vector and another one
     * @param v other vector
     * @return this dot v
     */
    public double scalarProduct(final Vect2D v) {
        return x * v.x + y * v.y;
    }

    /**
     * Dot product of this vector and another one
     * @param v other vector
     * @return this dot v
     */
    double scalarProduct(final I_Vect2D v){
        return x * v.getX() + y * v.getY();
    }

    /**
     * Normalization
     * @return normalized version of this vector. if this vector is 0,0, returns this vector.
     */
    public Vect2D normalise() {
        final double len = mag();
        if (len == 0){
            return this;
        }
        return new Vect2D(x/len, y/len);
    }

    /**
     * returns a vector equal to v1 - v2
     * @param v1 the initial vector
     * @param v2 the vector being subtracted
     * @return a vector equal to v1 - v2
     */
    public static Vect2D minus(final Vect2D v1, final Vect2D v2) {
        // returns v1-v2
        return v1.addScaled(v2, -1);
    }


    static Vect2D minus(final Vect2D v1, final I_Vect2D v2){
        return v1.addScaled(v2, -1);
    }

    M_Vect2D copy_to_mutable(){
        return M_Vect2D.GET(this);
    }

    /**
     * Rotates the current vector 90 degrees anticlockwise, doesn't modify this vector
     * @return a copy of this vector, rotated 90 degrees anticlockwise
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vect2D rotate90degreesAnticlockwise() {
        return new Vect2D(-y,x);
    }

    /**
     * Returns cartesian version of the polar vector with given rotation and magnitude
     * @param rot rotation for the vector
     * @param mag magnitude for the vector
     * @return a vector with given angle and magnitude
     */
    public static Vect2D POLAR(final I_Rot2D rot, final double mag){
        return new Vect2D(-mag*rot.get_sin(), mag*rot.get_cos());
    }

    public double getX() { return x; }

    public double getY(){ return y; }

    public static Vect2D lower_bound(final I_Vect2D a, final I_Vect2D b){
        return M_Vect2D.lower_bound(a, b).finished();
    }

    public static Vect2D upper_bound(final I_Vect2D a, final I_Vect2D b){
        return M_Vect2D.upper_bound(a, b).finished();
    }

    /**
     * Turns this local coordinate vector into a world coordinate vector.
     * @param bodyPos the world position of the body this local coordinate is attached to
     * @param bodyRotation the current rotation of the body which this local coordinate is attached to
     * @return this local coordinate translated into a world coordinate
     */
    public Vect2D localToWorldCoordinates(final Vect2D bodyPos, final Rot2D bodyRotation){
        return M_Vect2D.GET(this).rotate(bodyRotation).add(bodyPos).finished();
    }

    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'locals'
     * list, and outputs them into the given 'out' list.
     * @param bodyPos position of the body centroid
     * @param bodyRotation position of the body's rotation
     * @param locals local positions of everything in the body
     * @param out the list which the world positions of everything in the body will be put into
     */
    public static void localToWorldCoordinatesForBodyToOut(
            final Vect2D bodyPos,
            final Rot2D bodyRotation,
            final Vect2D[] locals,
            final Vect2D[] out
    ){
        for (int i = locals.length-1; i >= 0; i--) {
            out[i] = locals[i].localToWorldCoordinates(bodyPos, bodyRotation);
        }
    }

    /**
     * Performs the 'local coordinates to world coordinates' transformation on the coordinates in the given 'locals'
     * list, and outputs them into the given 'out' list, and also returns a pair with the bounds of the translated vectors
     * @param bodyPos position of the body centroid
     * @param bodyRotation position of the body's rotation
     * @param locals local positions of everything in the body
     * @param out the list which the world positions of everything in the body will be put into
     */
    public static IPair<Vect2D, Vect2D> localToWorldCoordinatesForBodyToOutAndGetBounds(
            final Vect2D bodyPos,
            final Rot2D bodyRotation,
            final Vect2D[] locals,
            final Vect2D[] out
    ){
        out[0] = locals[0].localToWorldCoordinates(bodyPos, bodyRotation);
        final M_Vect2D min = M_Vect2D.GET(out[0]);
        final M_Vect2D max = M_Vect2D.GET(min);
        for (int i = 1; i < locals.length; i++){
            out[i] = locals[i].localToWorldCoordinates(bodyPos, bodyRotation);
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
        return new Pair<>(min.finished(), max.finished());
    }


}