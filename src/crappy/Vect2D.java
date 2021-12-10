package crappy;

import java.io.Serializable;

/**
 * The immutable 2D vector used by CRAPPY.
 *
 * Based on implementions provided for the CE812 and CE218 courses at the University of Essex,
 * with extra utility methods added later on.
 */
public final class Vect2D implements Serializable {

    public final double x, y;

    /**
     * A zero vector, here so I don't need to keep instantiating a new empty vector every time a zero vector is needed.
     */
    public static final Vect2D ZERO = new Vect2D();

    /**
     * Creates a null vector (0,0)
     */
    public Vect2D() {
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
     * @param angle the angle to rotate this Vect2D by
     * @return this Vect2D, rotated by angle radians.
     */
    public Vect2D rotate(final double angle) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        return new Vect2D(
                x * cos - y * sin,
                x * sin + y * cos
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

    /**
     * Rotates the current vector 90 degrees anticlockwise, doesn't modify this vector
     * @return a copy of this vector, rotated 90 degrees anticlockwise
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vect2D rotate90degreesAnticlockwise() {
        return new Vect2D(-y,x);
    }

    /**
     * Returns cartesian version of the polar vector with given angle and magnitude
     * @param angleRadians angle for the vector (radians)
     * @param mag magnitude for the vector
     * @return a vector with given angle and magnitude
     */
    public static Vect2D POLAR(final double angleRadians, final double mag){
        return new Vect2D(-mag*Math.sin(angleRadians), mag*Math.cos(angleRadians));
    }

    // some methods which treat a 2-index array of xy as a substitute for a mutable vector.
    public double[] toArray(){
        return new double[]{x, y};
    }

    public Vect2D(final double[] xy){
        x = xy[0];
        y = xy[1];
    }

    public static double[] ADD_ARRAY(final double[] out_xy, final Vect2D v){
        out_xy[0] += v.x;
        out_xy[1] += v.y;
        return out_xy;
    }

    public static double[] ADD_SCALED_ARRAY(final double[] out_xy, final Vect2D v, final double scale){
        out_xy[0] += (v.x * scale);
        out_xy[1] += (v.y * scale);
        return out_xy;
    }

    public static double[] SCALE_ARRAY(final double[] out_xy, final double scale){
        out_xy[0] *= scale;
        out_xy[1] *= scale;
        return out_xy;
    }

    public static double MAGNITUDE_ARRAY(final double[] xy){
        return Math.hypot(xy[0], xy[1]);
    }

    public static double[] NORMALIZE_ARRAY(final double[] out_xy){
        final double mag = MAGNITUDE_ARRAY(out_xy);
        if (mag != 0){
            out_xy[0] /= mag;
            out_xy[1] /= mag;
        }
        return out_xy;
    }
}
