/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

/**
 * Interface for the CrappyEdges
 * @author Rachel Lowe
 */
public interface I_CrappyEdge extends I_CrappyShape {

    Vect2D getLocalStart();

    Vect2D getWorldStart();

    Vect2D getWorldProj();

    Vect2D getWorldNorm();

    Vect2D getWorldTang();

    double getLength();

    Vect2D getLocalTang();

    default Vect2D getWorldEnd(){
        return getWorldStart().add(getWorldProj());
    }

    I_CrappyCircle getEndPointCircle();

    /**
     * Edges don't really contain points, but checks if the point is close enough
     * @param worldPoint the point we're checking
     * @return true if the point is close enough
     */
    @Override
    default boolean isPointInShape(final I_Vect2D worldPoint){
        return Vect2DMath.IS_POINT_ON_LINE_PROJ(worldPoint, getWorldStart(), getWorldProj());
    }

    /**
     * Obtains the size of the 'buffer zone' behind it.
     * If null, NaN, or Infinity are returned, that means depth is infinite.
     */
    default double getDepth(){
        // TODO: implement this!!!!!!!

        return 0.0;
    }
}
