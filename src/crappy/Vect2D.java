package crappy;

import crappy.utils.IPair;
import crappy.utils.Pair;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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




}

/**
 * Fuck it, I've just realized that I'm probably going to be able to abuse this
 * to simplify the whole 'getting an N log N collision handling data structure' stuff.
 * So I'm enshrining these funny numbers in an enum so I don't forget to abuse this further.
 */
enum I_Vect2D_Comp_Enum {

    // TODO: abuse the shit out of this to get quadtrees/other things to divide the physics objects
    //  up into smaller groups for easier collision handling

    // +5 +2 -1
    // +3  0 -3
    // +1 -2 -5

    X_BIGGER_Y_BIGGER(5),
    X_BIGGER_Y_SAME(3),
    X_BIGGER_Y_SMALLER(1),
    X_SAME_Y_BIGGER(2),
    X_SAME_Y_SAME(0),
    X_SAME_Y_SMALLER(-2),
    X_SMALLER_Y_BIGGER(-1),
    X_SMALLER_Y_SAME(-3),
    X_SMALLER_Y_SMALLER(-5);

    private static final int y_diff_int = 2;
    private static final int x_diff_int = 3;

    public final int out_val;

    private I_Vect2D_Comp_Enum(final int int_value){
        out_val = int_value;
    }

    /**
     * Obtains the appropriate I_Vect2D_Comp_Enum value from the results of comparing the x and y values of
     * two I_Vect2Ds a and b
     * @param x_comp_res result of comparing the x values of vector a and vector b
     * @param y_comp_res result of comparing the y values of vector a and vector b
     * @return the appropriate I_Vect2D_Comp_Enum describing the relative positions of vectors a and b
     */
    public static I_Vect2D_Comp_Enum fromCompResults(final int x_comp_res, final int y_comp_res){
        return fromInt((x_comp_res * x_diff_int) + (y_diff_int * y_comp_res));
    }


    public static I_Vect2D_Comp_Enum fromInt(final int input){
        switch (input){
            case 5:
                return X_BIGGER_Y_BIGGER;
            case 3:
                return X_BIGGER_Y_SAME;
            case 1:
                return X_BIGGER_Y_SMALLER;
            case 2:
                return X_SAME_Y_BIGGER;
            case 0:
                return X_SAME_Y_SAME;
            case -2:
                return X_SAME_Y_SMALLER;
            case -1:
                return X_SMALLER_Y_BIGGER;
            case -3:
                return X_SMALLER_Y_SAME;
            case -5:
                return X_SMALLER_Y_SMALLER;
            default:
                throw new IllegalArgumentException("Invalid input!");
        }
    }

}



interface I_Vect2D extends IPair<Double, Double>, Comparable<I_Vect2D> {

    double getX();

    double getY();

    static double angle(final I_Vect2D v1, final I_Vect2D v2){
        return Math.atan2(v2.getY() - v1.getY(), v2.getX() - v1.getX());
    }

    static double dot(final I_Vect2D v1, final I_Vect2D v2){
        return (v1.getX() * v2.getX()) + (v1.getY() * v2.getY());
    }

    static double mag(final I_Vect2D v){
        return Math.hypot(v.getX(), v.getY());
    }

    static double cross(final I_Vect2D v1, final I_Vect2D v2){
        return v1.getX() * v2.getY() + v1.getY() * v2.getX();
    }

    static Vect2D cross(final I_Vect2D v, final double s){
        return new Vect2D(-s * v.getY(), s * v.getX());
    }


    default Double getFirst(){
        return getX();
    }

    default Double getSecond(){
        return getY();
    }

    /**
     * A helper method to compare doubles.
     * Like Double.compare(double d1, double d2) but omits the double to long bits stuff
     * and just returns 0 if neither are found to be bigger than each other, to save time.
     * @param a first double
     * @param b second double
     * @return +1 if a > b, -1 if a < b, otherwise 0.
     */
    static int COMPARE_DOUBLES(final double a, final double b){
        if (a > b){
            return 1;
        } else if (a < b){
            return -1;
        }
        return 0;
    }

    /**
     * Returns a pair holding a vector with the minimum x and y values, and another one with the maximum x and y values,
     * obtained from the I_Vect2D objects in the vects list.
     * @param vects the list of I_Vect2D objects which we're looking through
     * @return Pair of (Min vector, max vector).
     * @throws IllegalArgumentException if vects has length of 0.
     */
    @SafeVarargs
    static IPair<I_Vect2D, I_Vect2D> min_and_max_varargs(final IPair<Double, Double>... vects){
        return M_Vect2D.min_and_max_varargs(vects);
    }

    /**
     * This is going to be abused by the axis-aligned bounding boxes AND A WHOLE LOAD OF EXTRA THINGS BESIDES THAT!
     * @param o the other I_Vect2D
     * @return it will either return 5, 3, 2, 1, 0, -1, -2, -3, or -5, depending on where this is in relation to o.
     * <html>
     *     <code><br>
     *      -1 |+2 |+5 <br>
     *      ---|---|---<br>
     *      -3 | 0 |+3 <br>
     *      ---|---|---<br>
     *      -5 |-2 |+1 <br>
     *      </code>
     *
     * </html>
     *
     */
    default I_Vect2D_Comp_Enum relative_pos_compare(final I_Vect2D o){
        // +5 +2 -1
        // +3  0 -3
        // +1 -2 -5
        return I_Vect2D_Comp_Enum.fromCompResults(
                COMPARE_DOUBLES(getX(), o.getX()),
                COMPARE_DOUBLES(getY(), o.getY())
        );
    }

    /**
     * Is this I_Vect2D greater than the other one? (this.x > o.x && this.y > o.y)
     * @param o other I_Vect2D
     * @return true if X and Y of this are not less than X and Y of other
     */
    default boolean isGreaterThanOrEqualTo(final I_Vect2D o){
        return (getX() >= o.getX()) && (getY() >= o.getY());
    }

    /**
     * Comparison operation.
     * Returns result of comparing x values, then attempts to compare y values if x values are identical.
     * @param o the other I_Vect2D to compare it to
     * @return 1 if x > o.x, -1 if x < o.x, else 1 if y > o.y, -1 if y < o.y, else 0.
     */
    @Override
    default int compareTo(final I_Vect2D o) {
        final int x_comp = COMPARE_DOUBLES(getX(), o.getX());
        if (x_comp == 0){
            return COMPARE_DOUBLES(getY(), o.getY());
        }
        return x_comp;
    }
}

/**
 * A mutable Vect2D.
 *
 * Intended for the sole purpose of internal calculations, so it's not public,
 * instead being kept far away from anyone else who may misuse it.
 */
final class M_Vect2D implements I_Vect2D {

    /**
     * X component of this vector.
     * Not final.
     */
    double x;
    /**
     * Y component of this vector.
     * Not final.
     */
    double y;

    /**
     * A pool of M_Vect2D objects,
     * the intent being that these can just be recycled instead of needing to instantiate a lot of these.
     */
    private static final Queue<M_Vect2D> POOL = new ConcurrentLinkedQueue<>();

    static{
        // initially set up the pool with 10 M_Vect2D objects (should hopefully be enough)
        for(int i = 10; i > 0; i--){
            POOL.add(new M_Vect2D());
        }
    }

    /**
     * Yep, the constructor is private.
     * This is because you're not meant to be creating new instances of these,
     * instead, you're meant to use the GET method to get a premade instance.
     * The garbage collector's going to be busy enough with the immutable vectors,
     * may as well try to avoid overwhelming it, y'know?
     */
    private M_Vect2D(){
        x = 0;
        y = 0;
    }

    /**
     * Obtains a M_Vect2D (in the state it was in earlier on).
     * @return an M_Vect2D, from the pool. Will be in the state it was in when it was put in the pool.
     */
    static M_Vect2D _GET_RAW(){
        final M_Vect2D candidate = POOL.poll();
        if (candidate!=null){
            return candidate;
        }
        return new M_Vect2D();
    }

    /**
     * Obtain an M_Vect2D with value (0,0)
     * @return an M_Vect2D with the value (0,0)
     */
    static M_Vect2D GET(){
        return _GET_RAW().reset();
    }


    /**
     * Obtains an M_Vect2D with x and y initialized to a certain value.
     * @param x x value for this M_Vect2D
     * @param y y value for this M_Vect2D
     * @return an M_Vect2D with given x and y value.
     */
    static M_Vect2D GET(final double x, final double y){
        return _GET_RAW().set(x, y);
    }

    /**
     * Obtains an M_Vect2D with same x and y values as the given I_Vect2D
     * @param v the I_Vect2D to copy the values of
     * @return an M_Vect2D, with the same x and y values as that vector.
     */
    static M_Vect2D GET(final I_Vect2D v){
        return _GET_RAW().set(v);
    }


    static M_Vect2D GET(final IPair<Double, Double> p){ return _GET_RAW().set(p.getFirst(), p.getSecond()); }

    /**
     * Reject modernity, return to 0
     * @return this vector except it'll be (0,0) instead.
     */
    M_Vect2D reset(){
        x = 0;
        y = 0;
        return this;
    }

    /**
     * Discard this M_Vect2D, putting it back in the pool.
     * DO NOT ATTEMPT RE-USING THIS M_VECT2D AFTER DISCARDING IT!
     */
    final void discard(){
        POOL.add(this);
    }

    /**
     * Like discard, but it first creates an immutable Vect2D with the same value as this M_Vect2D
     * before putting it back in the pool. Then it returns that immutable copy of this M_Vect2D
     * @return a new, immutable, Vect2D with the value that this M_Vect2D had when this was called.
     */
    Vect2D finished(){
        final Vect2D v = new Vect2D(this);
        discard();
        return v;
    }

    /**
     * Use this to set this M_Vect2D to have a given x,y value
     * @param x new x value
     * @param y new y value
     * @return this M_Vect2D with the updated x, y values.
     */
    M_Vect2D set(final double x, final double y){
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * use this to make this M_Vect2D copy the value of an I_Vect2D
     * @param v the I_Vect2D to copy the value of.
     * @return this except it has the same value as that I_Vect2D.
     */
    M_Vect2D set(final I_Vect2D v){
        this.x = v.getX();
        this.y = v.getY();
        return this;
    }

    /**
     * use this to make this M_Vect2D copy the value of an IPair of Doubles.
     * @param p the IPair to copy the value of.
     * @return this except it has the same values as that IPair.
     */
    M_Vect2D set(final IPair<Double, Double> p){
        this.x = p.getFirst();
        this.y = p.getSecond();
        return this;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    /**
     * Adds the value of the other I_Vect2D to this M_Vect2D
     * @param other the I_Vect2D to add to this M_Vect2D
     * @return this + other
     */
    M_Vect2D add(final I_Vect2D other){
        this.x += other.getX();
        this.y += other.getY();
        return this;
    }

    /**
     * Add the value of all of the other I_Vect2Ds to this M_Vect2D
     * @param others a list of I_Vect2Ds to be added to this.
     * @return this + sum(others)
     */
    M_Vect2D sum(final I_Vect2D... others){
        for (I_Vect2D v: others){
            this.x += v.getX();
            this.y += v.getY();
        }
        return this;
    }

    /**
     * Obtains an M_Vect2D as a polar vector, with given rotation and magnitude
     * @param rot rotation for this vector
     * @param mag magnitude for this vector
     * @return a mutable polar vector.
     */
    static M_Vect2D POLAR(final I_Rot2D rot, final double mag){
        return GET(-mag * rot.get_sin(), mag * rot.get_cos());
    }

    /**
     * Calculates the magnitude of this vector.
     * @return the magnitude of this vector.
     */
    double mag(){
        return Math.hypot(x, y);
    }

    /**
     * Forces this vector to have a length of 1 (or will continue to have a length of 0 if it currently is (0,0)
     * @return this vector but with a length of 1 instead.
     */
    M_Vect2D normalize(){
        final double mag = mag();
        if (mag != 0){
            this.x /= mag;
            this.y /= mag;
        }
        return this;
    }

    /**
     * Adds another vector, multiplied by scale, to this M_Vect2D
     * @param other other vector to add to this M_Vect2D
     * @param scale what to multiply that other vector by before adding it
     * @return this + (other * scale)
     */
    M_Vect2D addScaled(final I_Vect2D other, final double scale){
        this.x += (other.getX() * scale);
        this.y += (other.getY() * scale);
        return this;
    }

    /**
     * Multiplies this vector by given scale
     * @param scale how much should this vector be multiplied by?
     * @return this * scale
     */
    M_Vect2D mult(final double scale){
        this.x *= scale;
        this.y *= scale;
        return this;
    }

    /**
     * Rotates this M_Vect2D 90 degrees anticlockwise
     * @return this M_Vect2D, but rotated 90 degrees clockwise.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public M_Vect2D rotate90degreesAnticlockwise() {
        final double old_x = x;
        x = -y;
        y = old_x;
        return this;
    }

    /**
     * Rotates this M_Vect2D by the given rotation
     * @param rot how much should this M_Vect2D be rotated by?
     * @return this M_Vect2D rotated by the given rot.
     */
    M_Vect2D rotate(final I_Rot2D rot) {
        final double old_x = x;
        //final double old_y = y;
        x = old_x * rot.get_cos() - y * rot.get_sin();
        y = old_x * rot.get_sin() + y * rot.get_cos();
        return this;
    }

    double scalarProduct(final I_Vect2D v){ return x * v.getX() + y * v.getY(); }

    M_Vect2D cross(final double s){
        final double new_y = s * x;
        x = -s * y;
        y = new_y;
        return this;
    }


    /**
     * Obtain the lower bound of a couple of I_Vect2Ds, outputting them into the given M_Vect2D
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @param out the M_Vect2D to overwrite the result into
     * @return minimum x and minimum y of the given I_Vect2Ds
     */
    static M_Vect2D lower_bound_to_out(final I_Vect2D a, final I_Vect2D b, final M_Vect2D out){
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
    static M_Vect2D min_to_out_varargs(final M_Vect2D out, final I_Vect2D... vects){
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
    static M_Vect2D min_varargs(final I_Vect2D... vects){
        return min_to_out_varargs(_GET_RAW(), vects);
    }

    /**
     * Like min_to_out, but outputting into a new M_Vect2D.
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @return a new M_Vect2D with the min x and min y from a and b
     */
    static M_Vect2D lower_bound(final I_Vect2D a, final I_Vect2D b){
        return lower_bound_to_out(a, b, _GET_RAW());
    }

    /**
     * Obtain the lower bound of a couple of I_Vect2Ds, outputting them into the given M_Vect2D
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @param out the M_Vect2D to overwrite the result into
     * @return max x and max y of the given I_Vect2Ds
     */
    static M_Vect2D upper_bound_to_out(final I_Vect2D a, final I_Vect2D b, final M_Vect2D out){
        out.x = a.getX() > b.getX() ? a.getX() : b.getX();
        out.y = a.getY() > b.getY() ? a.getY() : b.getY();
        return out;
    }

    /**
     * Like max_to_out, but outputting into a new M_Vect2D.
     * @param a first I_Vect2D
     * @param b second I_Vect2D
     * @return a new M_Vect2D with the max x and max y from a and b
     */
    static M_Vect2D upper_bound(final I_Vect2D a, final I_Vect2D b){
        return upper_bound_to_out(a, b, _GET_RAW());
    }

    static M_Vect2D max_to_out_varargs(final M_Vect2D out, final I_Vect2D... vects){
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

    static M_Vect2D max_varargs(final I_Vect2D... vects){
        return max_to_out_varargs(_GET_RAW(), vects);
    }


    /**
     * Returns a pair holding a vector with the minimum x and y values, and another one with the maximum x and y values,
     * obtained from the I_Vect2D objects in the vects list.
     * @param vects the list of I_Vect2D objects which we're looking through
     * @return Pair of (Min vector, max vector).
     * @throws IllegalArgumentException if vects has length of 0.
     */
    @SafeVarargs
    static IPair<I_Vect2D, I_Vect2D> min_and_max_varargs(final IPair<Double, Double>... vects){
        if (vects.length == 0){
            throw new IllegalArgumentException("How do you expect me to find the minimum and maximum from an empty list???");
        }
        M_Vect2D min = GET(vects[0]);
        M_Vect2D max = GET(min);
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
        return new Pair<>(min, max);

    }


}
