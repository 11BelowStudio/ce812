package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.utils.containers.IPair;

import java.util.function.Predicate;

public interface I_Crappy_AABB extends IPair<Vect2D, Vect2D>, Cloneable, Predicate<I_Crappy_AABB> {

    /**
     * Obtain the max corner
     * @return vector describing the max xy corner of the I_Crappy_AABB
     */
    Vect2D getMax();

    /**
     * Obtain the min corner
     * @return vector describing the min xy corner of the I_Crappy_AABB
     */
    Vect2D getMin();

    /**
     * Obtain the midpoint
     * @return vector describing the midpoint of the I_Crappy_AABB
     */
    Vect2D getMidpoint();

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

    /**
     * Checks if this bounding box intersects with the other bounding box
     * @param other the other bounding box
     * @return true if the other AABB intersects with this AABB, false otherwise
     */
    default boolean check_bb_intersect(final Crappy_AABB other){
        return check_if_in_bounds(other.getMax()) || check_if_in_bounds(other.getMin());
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param otherAABB the input argument
     *
     * @return {@code true} if the this intersects with the other CrappyAABB, otherwise {@code false}
     */
    @Override
    default boolean test(final I_Crappy_AABB otherAABB) {
        return check_bb_intersect(otherAABB);
    }

}
