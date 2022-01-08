/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

/**
 * An interface for the crappyshape within the axis-aligned bounding box quadtree
 *
 * @author Rachel Lowe
 */
public interface CrappyShape_QuadTree_Interface {

    I_Crappy_AABB getBoundingBox();

    I_CrappyShape getShape();

}
