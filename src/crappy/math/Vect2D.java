package crappy.math;

import crappy.I_Transform;
import crappy.utils.CrappyWarning;
import crappy.utils.IPair;
import crappy.utils.Pair;
import org.junit.Test;

import java.io.Serializable;
import java.util.Objects;

/**
 * The immutable 2D vector used by CRAPPY.
 *
 * Based on implementions provided for the CE812 and CE218 courses at the University of Essex,
 * with extra utility methods added later on.
 */
public final class Vect2D implements Serializable, I_Vect2D {

    /**
     * Immutable X component
     */
    public final double x;

    /**
     * Immutable Y component
     */
    public final double y;

    /**
     * A zero vector, here so I don't need to keep instantiating a new empty vector every time a zero vector is needed.
     */
    public static final Vect2D ZERO = new Vect2D(0,0);

    /**
     * Creates a null vector (0,0).
     * Please use ZERO instead of trying to create yet another Vect2D with value (0,0).
     */
    private Vect2D(){ this(0, 0); }

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
    public Vect2D(final I_Vect2D v){
        this(v.getX(), v.getY());
    }


    /**
     * Creates a new immutable vector which is a copy of the argument M_Vect2D
     * @param v the M_Vect2D to copy
     */
    public Vect2D(final M_Vect2D v){ this(v.x, v.y); }

    /**
     * Whether this is equal to another object.
     * True if the other object is a Vect2D  with same x and y
     * @param o other object
     * @return whether or not this is equal to the other object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vect2D vect2D = (Vect2D) o;
        return Double.compare(vect2D.x, x) == 0 && Double.compare(vect2D.y, y) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * magnitude
     * @return magnitude of this vector
     */
    @Override
    public double mag() {
        return Math.hypot(x, y);
    }

    /**
     * The angle of this vector
     * @return the angle of this vector
     */
    @Override
    public double angle() {return Math.atan2(y, x);}

    /**
     * angle of difference vector between this vector and other vector
     * @param other other vector
     * @return angle between them
     */
    public double angle(final Vect2D other) {
        return Math.atan2(other.y - y, other.x - x);
    }

    /**
     * Angle between this vector and the other vector
     * @param other other vector
     * @return angle from this to other
     */
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
    public double dot(final Vect2D v) {
        return x * v.x + y * v.y;
    }

    /**
     * Dot product of this vector and another one
     * @param v other vector
     * @return this dot v
     */
    @Override
    public double dot(final I_Vect2D v){
        return x * v.getX() + y * v.getY();
    }

    @Override
    public double cross(final I_Vect2D v){
        return x * v.getY() + y * v.getX();
    }

    /**
     * Obtains the cross product of this and a 3D vector of (0, 0, z)
     * This method handles this X z and z X this, please use 'me_first' to indicate which one you want to use.
     * @param z the Z coordinate of the 3D vector of (0,0,z)
     * @param me_first if true, {@code this X z}. Else, {@code z X this}.
     * @return {@code this X z} if me_first, else {@code z X this}.
     */
    @SuppressWarnings("BooleanParameter")
    public Vect2D cross(final double z, final boolean me_first){
        if (me_first) {
            return new Vect2D(
                    z * y,
                    -z * x
            );
        } else {
            return new Vect2D(
                    -z * y,
                    z * x
            );
        }
    }

    /**
     * Normalization
     * @return normalized version of this vector. if this vector is 0,0, returns this vector.
     */
    public Vect2D norm() {
        final double len = mag();
        if (len == 0){
            return this;
        }
        return new Vect2D(x/len, y/len);
    }



    /**
     * Turns this Vect2D into an M_Vect2D
     * @return an M_Vect2D copy of this Vect2D
     */
    M_Vect2D copy_to_mutable(){
        return M_Vect2D.GET(this);
    }

    /**
     * Linearly interpolates from this vector to the other vector
     * @param other lerp goes towards here
     * @param lerpScale how much to lerp by (0: return this. 1: return other. 0.5: midpoint)
     * @return vector that's lerpScale of the way between start and end
     */
    public Vect2D lerp(final Vect2D other, final double lerpScale){
        return addScaled(
                Vect2DMath.MINUS(other, this), // vector o->this
                lerpScale // how much to scale (o->this) by
        );
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


    public Vect2D localToWorldCoordinates(final I_Transform trans){
        return localToWorldCoordinates(trans.getPos(), trans.getRot());
    }

    /**
     * Turns this local coordinate vector into a world coordinate vector.
     * @param bodyPos the world position of the body this local coordinate is attached to
     * @param bodyRot the current rotation of the body which this local coordinate is attached to
     * @return this local coordinate translated into a world coordinate
     */
    public Vect2D localToWorldCoordinates(final I_Vect2D bodyPos, final I_Rot2D bodyRot){
        return M_Vect2D.GET(this).rotate(bodyRot).add(bodyPos).finished();
    }

    /**
     * Obtains the world velocity of this local coordinate.
     * @param trans the I_Transform describing the body which this local coordinate belongs to
     * @return velCOM + angVel x worldCoord
     */
    public Vect2D getWorldVelocityOfLocalCoordinate(final I_Transform trans){
        return M_Vect2D.GET(this)
                .rotate(trans.getRot()) // rotation
                .add(trans.getPos()) // moving to world pos
                .cross(trans.getAngVel(), false) // angVel X r
                .add(trans.getVel()) // adding main body vel
                .finished(); // aaand done!
    }


    @Override
    public Vect2D toVect2D(){ return this; }

    /**
     * Returns the absolute value of this Vect2D
     * @return this Vect2D if x and y >= 0, else returns a new Vect2D with math.abs(x) and math.abs(y)
     */
    public Vect2D abs(){
        if (isGreaterThanOrEqualTo(Vect2D.ZERO)){
            return this;
        }
        return new Vect2D(Math.abs(x), Math.abs(y));
    }


}

