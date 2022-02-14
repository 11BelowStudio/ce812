/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.utils.containers.IPair;


import java.util.function.Predicate;

/**
 * interface for a Crappy_AABB
 *
 * @author Rachel Lowe
 */
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
        return v.isGreaterThanOrEqualTo(getMin()) && v.isLessThanOrEqualTo(getMax());
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
        //return check_bb_intersect(otherAABB);
        return I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(this, otherAABB);
    }

    /**
     * Obtains a pair holding (width, height) of this AABB
     * @return width, height
     */
    default IPair<Double, Double> getWidthHeight(){
        return getMax().addScaled(getMin(), -1);
    }

    /**
     * Checks if these two bounding boxes overlap at any point
     * @param a first bounding box
     * @param b other bounding box
     * @return true if they overlap.
     */
    static boolean DO_THESE_BOUNDING_BOXES_OVERLAP(final I_Crappy_AABB a, final I_Crappy_AABB b){

        return a.__innerBBOverlapCheck(b) || b.__innerBBOverlapCheck(a);
    }

    default boolean __innerBBOverlapCheck(final I_Crappy_AABB other){
        return getMin().isLessThanOrEqualTo(other.getMax()) && getMax().isGreaterThanOrEqualTo(other.getMin());
    }

}


