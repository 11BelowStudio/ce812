package crappy;

import crappy.math.Rot2D;
import crappy.math.Vect2D;

/**
 * An interface that just has a getter for world position, world rotation, world velocity, and world angular velocity.
 * @author Rachel Lowe
 */
public interface I_Transform {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Get world position of centre of mass
     * @return position
     */
    Vect2D getPos();

    /**
     * Get world rotation of body
     * @return rotation
     */
    Rot2D getRot();

    /**
     * Get linear velocity of body
     * @return linear velocity
     */
    Vect2D getVel();


    /**
     * Get angular velocity of body
     * (expressed as Z axis of this as a 3D vector, aka parallel to axis of rotation, aka the actually important bit)
     * @return angular velocity
     */
    double getAngVel();

    /**
     * This method can be overridden to return a different I_Transform instance
     * which is intended to return a view of the thing this transform is attached to mid-timestep.
     * Default behaviour just returns this transform as-is.
     * Should not be overridden by a temp transform.
     * @return a view of this transform, mid timestep.
     */
    default I_Transform getTempTransform(){ return this; }

    /**
     * Intended to be overridden by a tempTransform to get the 'root' transform of it,
     * being the version of this transform from the last full timestep.
     *
     * Default behaviour returns this transform as-is. Should not be overridden unless by a temp transform.
     * @return a view of this transform, pre timestep.
     */
    default I_Transform getRootTransform(){ return this; }

}
