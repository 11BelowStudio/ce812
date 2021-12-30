package crappy;

/**
 * An interface for the CrappyShapes to access the CrappyBody with.
 */
public interface CrappyBody_Shape_Interface extends I_View_CrappyBody, I_Transform, I_CrappyBody {

    /**
     * Called to set the moment of inertia after the shape is made.
     * @param moment the moment of inertia.
     */
    void setMomentOfInertia(final double moment);

    /**
     * Called to notify the CrappyBody that it has, in fact, collided with something.
     * @param collidedWith the body it collided with.
     */
    void notifyAboutCollision(final I_View_CrappyBody collidedWith);

    /**
     * Returns manipulatable interface of this body.
     * @return I_ManipulateCrappyBody view of this object CrappyBody
     */
    I_ManipulateCrappyBody getManipulatable();

}
