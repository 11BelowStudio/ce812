package crappy.math;

import crappy.utils.containers.IPair;
import java.awt.geom.Point2D;

/**
 * A read-only interface for 2D vectors.
 */
public interface I_Vect2D extends IPair<Double, Double>, Comparable<I_Vect2D> {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    /**
     * Obtains the X component of this vector
     * @return x component
     */
    double getX();

    /**
     * Obtains the Y component of this vector
     * @return the y component
     */
    double getY();


    /**
     * Obtains the magnitude of this vector
     * @return magnitude (length) of this vector
     */
    default double mag(){ return Math.hypot(getX(), getY()); }

    /**
     * Obtains magnitude squared. Cheaper than mag because no square roots are involved.
     * @return magnitude^2
     */
    default double magSquared(){
        return (getX() * getX()) + (getY() * getY());
    }

    /**
     * The angle of this vector
     * @return the angle of this vector
     */
    default double angle(){ return Math.atan2(getY(), getX()); }

    /**
     * Dot product of this vector and the other vector
     * @param v the other vector
     * @return this.v
     */
    default double dot(final I_Vect2D v){ return getX() * v.getX() + getY() * v.getY(); }

    /**
     * Cross product (as scalar) of this vector and the other vector
     * @param v the other vector
     * @return the non-zero bit of the cross product of this X v
     */
    default double cross(final I_Vect2D v){
        return getX() * v.getY() - getY() * v.getX();
    }

    /**
     * Cross product (as Vect3D) of this vector and the other vector
     * @param v the other vector
     * @return (0, 0, this x v)
     */
    default Vect3D cross3D(final I_Vect2D v){ return new Vect3D(this.cross(v)); }


    default Double getFirst(){
        return getX();
    }

    default Double getSecond(){
        return getY();
    }


    /**
     * Method that constructs a new Vect2D that is a copy of this vector
     * (for when immutability is guaranteed)
     * @return a new Vect2D holding the same value as this.
     */
    default Vect2D toVect2D(){ return new Vect2D(this); }

    /**
     * Converts this into a Point2D.Double
     * @return a new Point2D.Double holding the same stuff as this vector held.
     */
    default Point2D.Double toPoint2D(){ return new Point2D.Double(getX(), getY()); }





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
                Vect2DMath.COMPARE_DOUBLES_CRAPPILY(getX(), o.getX()),
                Vect2DMath.COMPARE_DOUBLES_CRAPPILY(getY(), o.getY())
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

    default boolean isLessThanOrEqualTo(final I_Vect2D o){

        return (getX() <= o.getX()) && (getY() <= o.getY());
    }

    /**
     * Comparison operation.
     * Returns result of comparing x values, then attempts to compare y values if x values are identical.
     * @param o the other I_Vect2D to compare it to
     * @return 1 if x > o.x, -1 if x < o.x, else 1 if y > o.y, -1 if y < o.y, else 0.
     */
    @Override
    default int compareTo(final I_Vect2D o) {
        final int x_comp = Vect2DMath.COMPARE_DOUBLES_EPSILON(getX(), o.getX());
        if (x_comp == 0){
            return Vect2DMath.COMPARE_DOUBLES_EPSILON(getY(), o.getY());
        }
        return x_comp;
    }

    /**
     * Quick check for whether or not this vector is zero by looking at magSquared
     * @return true if magSquared is greater than 0 (as, if it's 0, that means this is zero).
     */
    default boolean isNotZero(){
        return magSquared() > 0;
    }


    /**
     * Checks if both parts of this are finite
     * @return true if x and y are finite.
     */
    default boolean isFinite(){
        return Double.isFinite(getX()) && Double.isFinite(getY());
    }


}


