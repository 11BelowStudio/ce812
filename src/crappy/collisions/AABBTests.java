/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.math.Vect2D;
import org.junit.Test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class AABBTests {

    final Vect2D[][] v = new Vect2D[6][6];

    {
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 6; y++) {
                v[x][y] = new Vect2D(x, y);
            }
        }
    }

    Vect2D v(int a, int b){
        return v[a][b];
    }

    I_Crappy_AABB bb(int a, int b, int c, int d){
        return new Crappy_AABB(v(a,b), v(c,d));
    }


    boolean overlap(int a, int b, int c, int d, int e, int f, int g, int h){
        return I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(bb(a,b,c,d), bb(e,f,g,h));
    }

    @Test
    public void testOverlaps(){


        
        assertTrue(overlap(0,0,4,4,1,1,3,3));

        assertTrue(overlap(1,1,3,3,1,1,3,3));

        assertTrue(overlap(0,2,5,4,2,0,4,5));

        assertTrue(overlap(0,0,5,3,2,2,4,4));

        assertTrue(overlap(0,0,4,4,3,3,5,5));

        assertFalse(overlap(0,0,1,1,4,4,5,5));
        assertFalse(overlap(4,4,5,5,0,0,1,1));

        assertFalse(overlap(3,0,5,2,0,3,2,5));

        assertFalse(overlap(0,2,3,3,4,2,5,4));




        



    }
}
