package crappy;

/**
 * Implement this interface to let your CrappyBodies notify you about any collisions they've done!
 * Both methods have a default implementation that does nothing, please override whichever you'd prefer
 * to use.
 */
public interface CrappyCallbackHandler {

    /**
     * The crappybody will call this with info about the other body when it notices that the
     * other body has been collided with.
     * @param otherBody the other body.
     * @implNote default implementation does nothing.
     */
    default void collidedWith(final I_View_CrappyBody otherBody){}

    /**
     * The CrappyBody will call this after all collisions have been detected/computed.
     * It gives the combined bitmask bits for the bodies that the attached body collided with this frame
     * @param collidedWithBits bitmask bits for all bodies that the attached body collided with
     * @implNote default implementation does nothing
     */
    default void acceptCollidedWithBitmaskAfterAllCollisions(final int collidedWithBits){}

    /**
     * CrappyBody calls this if it gets removed from the physics world.
     */
    default void bodyNoLongerExists(){}

}
