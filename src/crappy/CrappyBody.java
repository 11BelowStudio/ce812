package crappy;

import crappy.math.M_Rot2D;
import crappy.math.M_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.shapes.A_CrappyShape;
import crappy.shapes.Crappy_AABB;

/**
 * A rigidbody class used by Crappy
 */
public class CrappyBody implements I_CrappyBody, CrappyBody_Shape_Interface {


    protected Vect2D position;

    protected final M_Vect2D tempDisplacement = M_Vect2D._GET_RAW();

    protected final M_Vect2D tempPosition = M_Vect2D._GET_RAW();

    protected Vect2D velocity;

    protected final M_Vect2D tempVelChange = M_Vect2D._GET_RAW();

    protected final M_Vect2D tempVel = M_Vect2D._GET_RAW();

    protected Rot2D rotation;

    protected final M_Rot2D tempRot = M_Rot2D._GET_RAW();

    protected final M_Rot2D tempRotChange = M_Rot2D._GET_RAW();

    protected A_CrappyShape shape;

    protected Crappy_AABB boundingBox;

    protected double angVelocity;

    protected double tempAngVelocityChange;

    protected double mass;

    protected double torque;

    protected double inertia;

    protected final M_Vect2D pending_forces = M_Vect2D._GET_RAW();

    protected final M_Vect2D pending_dist_based_forces = M_Vect2D._GET_RAW();


    @Override
    public void setAABB(Crappy_AABB aabb) {
        boundingBox.update_aabb(aabb);
    }

    @Override
    public Vect2D getPos() {
        return position;
    }

    @Override
    public Vect2D getVel() {
        return velocity;
    }

    @Override
    public Rot2D getRot() {
        return rotation;
    }

    @Override
    public double getAngVel() {
        return angVelocity;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public Crappy_AABB getAABB() {
        return boundingBox;
    }

    @Override
    public A_CrappyShape getShape() {
        return shape;
    }

    @Override
    public double getInertia() {
        return inertia;
    }
}




