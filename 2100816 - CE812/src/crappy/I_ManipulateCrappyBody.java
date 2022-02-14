package crappy;

/**
 * This interface can be used by a callback handler to manipulate certain aspects of a CrappyBody.
 * @author Rachel Lowe
 */
public interface I_ManipulateCrappyBody extends
        I_View_CrappyBody, I_CrappyBody, CrappyBody_Shape_Interface, CrappyBody_Connector_Interface {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    /**
     * Do you want this body to be 'active' or not? Inactive bodies don't have collision and don't recieve forces.
     * Will update at start of next update loop.
     * @param beActive whether or not you want this body to be 'active'
     */
    void setActive(final boolean beActive);

    /**
     * Do you want to mark this CrappyBody for removal at the next possible frame?
     * Will update at start of next update loop.
     * @param markForRemoval whether or not you're marking it for removal.
     *                       If true, this object will be removed at the next frame.
     */
    void setMarkForRemoval(final boolean markForRemoval);

    /**
     * Should this rigidbody be tangible or not? Intangible objects have no collisions, can recieve forces.
     * Will update at start of next update loop.
     * @param pendingTangibility true if it should become tangible, false if intangible.
     */
    void setTangibility(final boolean pendingTangibility);


    /**
     * Should this rigidbody be allowed to move linearly?
     * @param posLocked true if no, false if yes
     */
    void setPositionLocked(final boolean posLocked);

    /**
     * Should this rigidbody be allowed to turn?
     * @param rotLocked true if no, false if yes
     */
    void setRotationLocked(final boolean rotLocked);

    /**
     * Should this rigidbody be frozen? (Still collidable, just immovable).
     * CALLING THIS WILL OVERRIDE PENDING POSITION LOCKED AND ROTATION LOCKED!
     * @param isFrozen true to freeze, false to let it move.
     */
    default void setFrozen(final boolean isFrozen){
        setPositionLocked(isFrozen);
        setRotationLocked(isFrozen);
    }
}
