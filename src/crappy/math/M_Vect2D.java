package crappy.math;

import crappy.internals.CrappyWarning;
import crappy.utils.IPair;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A mutable Vect2D.
 *
 * Intended for the sole purpose of internal calculations, so it's not public,
 * instead being kept far away from anyone else who may misuse it.
 */
public final class M_Vect2D implements I_Vect2D {

    /**
     * X component of this vector.
     * Not final.
     */
    public double x;
    /**
     * Y component of this vector.
     * Not final.
     */
    public double y;

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
    public static M_Vect2D _GET_RAW(){
        final M_Vect2D candidate = POOL.poll();
        if (candidate!=null){
            return candidate;
        }
        return new M_Vect2D();
    }

    /**
     * Obtains a new M_Vect2D which wasn't from the pool (here to reduce the risk of stuff leaking from the pool)
     * ONLY USE THIS IF YOU ARE ABSOLUTELY SURE THAT YOU'RE MAKING SOMETHING THAT WON'T EVER BE PUT BACK INTO THE POOL!
     * @return a new, empty, M_Vect2D
     * @deprecated Intentionally misusing the 'deprecated' tag here
     */
    @Deprecated
    @CrappyWarning("PLEASE ONLY USE THIS IF YOU ARE ABSOLUTELY SURE YOU KNOW WHAT YOU'RE DOING!")
    public static M_Vect2D __GET_NONPOOLED(){
        return new M_Vect2D();
    }

    /**
     * Obtain an M_Vect2D with value (0,0)
     * @return an M_Vect2D with the value (0,0)
     */
    public static M_Vect2D GET(){
        return _GET_RAW().reset();
    }


    /**
     * Obtains an M_Vect2D with x and y initialized to a certain value.
     * @param x x value for this M_Vect2D
     * @param y y value for this M_Vect2D
     * @return an M_Vect2D with given x and y value.
     */
    public static M_Vect2D GET(final double x, final double y){
        return _GET_RAW().set(x, y);
    }

    /**
     * Obtains an M_Vect2D with same x and y values as the given I_Vect2D
     * @param v the I_Vect2D to copy the values of
     * @return an M_Vect2D, with the same x and y values as that vector.
     */
    public static M_Vect2D GET(final I_Vect2D v){
        return _GET_RAW().set(v);
    }


    /**
     * Obtains an M_Vect2D that's a copy of the given pair
     * @param p the pair to copy
     * @return an M_Vect2D that's equal to M_Vect2D(p.first, p.second)
     */
    public static M_Vect2D GET(final IPair<Double, Double> p){ return _GET_RAW().set(p.getFirst(), p.getSecond()); }

    /**
     * Obtains an M_Vect2D with same x and y values as the given Vect3D
     * @param v the Vect3D to copy the values of
     * @return an M_Vect2D, with the same x and y values as that Vect3D. Z is discarded.
     */
    public static M_Vect2D GET(final Vect3D v){ return _GET_RAW().set(v.x, v.y); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        M_Vect2D m_vect2D = (M_Vect2D) o;
        return Double.compare(m_vect2D.x, x) == 0 && Double.compare(m_vect2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Reject modernity, return to 0
     * @return this vector except it'll be (0,0) instead.
     */
    public M_Vect2D reset(){
        x = 0;
        y = 0;
        return this;
    }

    /**
     * Discard this M_Vect2D, putting it back in the pool.
     * DO NOT ATTEMPT RE-USING THIS M_VECT2D AFTER DISCARDING IT!
     */
    public final void discard(){
        POOL.add(this);
    }

    /**
     * Like discard, but it first creates an immutable Vect2D with the same value as this M_Vect2D
     * before putting it back in the pool. Then it returns that immutable copy of this M_Vect2D
     * @return a new, immutable, Vect2D with the value that this M_Vect2D had when this was called.
     */
    public Vect2D finished(){
        final Vect2D v = new Vect2D(this);
        discard();
        return v;
    }

    /**
     * basically a wrapper for {@link #finished()} but returning it as an I_Vect2D instead of a Vect2D,
     * but end result is basically that the value becomes immutable (and this M_Vect2D is put back in the pool)
     * @return an I_Vect2D view of a new immutable Vect2D holding the value held by this M_Vect2D (which gets discarded)
     */
    @Override
    public I_Vect2D to_I_Vect2D(){
        return finished();
    }

    /**
     * Use this to set this M_Vect2D to have a given x,y value
     * @param x new x value
     * @param y new y value
     * @return this M_Vect2D with the updated x, y values.
     */
    public M_Vect2D set(final double x, final double y){
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * use this to make this M_Vect2D copy the value of an I_Vect2D
     * @param v the I_Vect2D to copy the value of.
     * @return this except it has the same value as that I_Vect2D.
     */
    public M_Vect2D set(final I_Vect2D v){
        this.x = v.getX();
        this.y = v.getY();
        return this;
    }

    /**
     * use this to make this M_Vect2D copy the value of an IPair of Doubles.
     * @param p the IPair to copy the value of.
     * @return this except it has the same values as that IPair.
     */
    public M_Vect2D set(final IPair<Double, Double> p){
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
    public M_Vect2D add(final I_Vect2D other){
        this.x += other.getX();
        this.y += other.getY();
        return this;
    }

    /**
     * Adds the other M_Vect2D to this, and promptly discards the other M_Vect2D, returning this updated M_Vect2D
     * @param other the other M_Vect2D to add to this and discard
     * @return this + other
     */
    public M_Vect2D add_discardOther(final M_Vect2D other){
        this.x += other.x;
        this.y += other.y;
        other.discard();
        return this;
    }

    /**
     * Add the value of all of the other I_Vect2Ds to this M_Vect2D
     * @param others a list of I_Vect2Ds to be added to this.
     * @return this + sum(others)
     */
    public M_Vect2D sum(final I_Vect2D... others){
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
    public static M_Vect2D POLAR(final I_Rot2D rot, final double mag){
        return GET(-mag * rot.get_sin(), mag * rot.get_cos());
    }

    /**
     * Calculates the magnitude of this vector.
     * @return the magnitude of this vector.
     */
    @Override
    public double mag(){return Math.hypot(x, y);}

    /**
     * Calculates magnitude, and discards this M_Vect2D
     * @return magnitude
     */
    public double mag_discard(){
        final double m = mag();
        discard();
        return m;
    }

    /**
     * Obtains magnitude squared
     * @return magnitude^2
     */
    @Override
    public double magSquared(){return (x * x) + (y * y);}

    /**
     * Calculates magSquared, and discards this
     * @return mag squared
     */
    public double magSquared_discard(){
        final double m = magSquared();
        discard();
        return m;
    }

    /**
     * Forces this vector to have a length of 1 (or will continue to have a length of 0 if it currently is (0,0)
     * @return this vector but with a length of 1 instead.
     */
    public M_Vect2D norm(){
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
    public M_Vect2D addScaled(final I_Vect2D other, final double scale){
        this.x += (other.getX() * scale);
        this.y += (other.getY() * scale);
        return this;
    }

    /**
     * Multiplies this vector by given scale
     * @param scale how much should this vector be multiplied by?
     * @return this * scale
     */
    public M_Vect2D mult(final double scale){
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
    public M_Vect2D rotate(final I_Rot2D rot) {
        final double old_x = x;
        //final double old_y = y;
        x = old_x * rot.get_cos() - y * rot.get_sin();
        y = old_x * rot.get_sin() + y * rot.get_cos();
        return this;
    }

    /**
     * Dot product of this vector and other vector
     * @param v other vector
     * @return this.v
     */
    public double dot(final I_Vect2D v){ return x * v.getX() + y * v.getY(); }

    /**
     * Dot product of this vector and other vector, also discards this vector
     * @param v other vector
     * @return this.v
     */
    public double dot_discard(final I_Vect2D v){
        final double d = dot(v);
        discard();
        return d;
    }

    /**
     * Returns the scalar cross product of this vector and the other vector
     * @param v the other vector
     * @return this x v
     */
    @Override
    public double cross(final I_Vect2D v){ return x * v.getY() - y * v.getX();}

    /**
     * Returns the scalar cross product of this vector and the other vector, and discards this vector
     * @param v the other vector
     * @return this x v
     */
    public double cross_discard(final I_Vect2D v){
        final double d = cross(v);
        discard();
        return d;
    }

    /**
     * Returns the angle of this vector
     * @return angle of this vector
     */
    @Override
    public double angle(){ return Math.atan2(y, x); }

    /**
     * Returns the angle of this vector and discards this vector
     * @return angle of this vector
     */
    public double angle_discard(){
        final double a = angle();
        discard();
        return a;
    }

    /**
     * Obtains the cross product of this and a 3D vector of (0, 0, z), and overwrites the value of this with the result.
     * This method handles this X z and z X this, please use 'me_first' to indicate which one you want to use.
     * @param z the Z coordinate of the 3D vector of (0,0,z)
     * @param me_first if true, {@code this X z}. Else, {@code z X this}.
     * @return {@code this X z} if me_first, else {@code z X this}.
     */
    @SuppressWarnings("BooleanParameter")
    public M_Vect2D cross(final double z, final boolean me_first){
        if (me_first){
            final double new_x = -z * y;
            y = z * x;
            x = new_x;
        } else {
            final double new_y = -z * x;
            x = z * y;
            y = new_y;
        }
        return this;
    }

    /**
     * Inverts this vector and returns it
     * @return this but with x and y multiplied by -1
     */
    public M_Vect2D invert(){
        x *= -1;
        y *= -1;
        return this;
    }



}
