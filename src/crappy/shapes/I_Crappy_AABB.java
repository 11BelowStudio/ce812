package crappy.shapes;

import crappy.math.I_Vect2D;
import crappy.utils.IPair;

public interface I_Crappy_AABB extends IPair<I_Vect2D, I_Vect2D> {

    I_Vect2D getMax();

    I_Vect2D getMin();

    /**
     * Abuses the compareTo method of I_Vect2D to see if v is within this AABB's boundaries.
     * @param v the I_Vect2D we're trying to check
     * @return true if the I_Vect2D is in bounds, false otherwise.
     */
    default boolean check_if_in_bounds(final I_Vect2D v){
        return v.isGreaterThanOrEqualTo(getMin()) && getMin().isGreaterThanOrEqualTo(v);
    }

    default boolean check_bb_intersect(final I_Crappy_AABB other){
        return check_if_in_bounds(other.getMax()) || check_if_in_bounds(other.getMin());
    }

    default boolean check_bb_intersect(final IPair<? extends I_Vect2D, ? extends I_Vect2D> other){
        return check_if_in_bounds(other.getFirst()) || check_if_in_bounds(other.getSecond());
    }

}
