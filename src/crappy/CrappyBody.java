package crappy;

import crappy.math.*;
import crappy.shapes.A_CrappyShape;
import crappy.shapes.Crappy_AABB;

/**
 * A rigidbody class used by Crappy
 */
public class CrappyBody implements I_CrappyBody, I_View_CrappyBody, CrappyBody_Shape_Interface, CrappyBody_Connector_Interface {

    /**
     * Current position of this CrappyBody
     */
    Vect2D position;

    /**
     * Temporarily stores the displacement between the original position and the position of this body mid-timestep
     */
    protected final M_Vect2D tempDisplacement = M_Vect2D.GET();

    /**
     * Position of this body mid-timestep
     */
    protected final M_Vect2D tempPosition = M_Vect2D.GET();

    /**
     * Current velocity of this CrappyBody
     */
    Vect2D velocity;

    /**
     * Temporarily stores the change in velocity experienced by this object between its original velocity and
     * the velocity of it mid-timestep
     */
    protected final M_Vect2D tempVelChange = M_Vect2D.GET();

    /**
     * Velocity of this body mid-timestep
     */
    protected final M_Vect2D tempVel = M_Vect2D.GET();

    /**
     * Current rotation of this CrappyBody
     */
    Rot2D rotation;

    /**
     * The rotation of this body mid-timestep
     */
    protected final M_Rot2D tempRot = M_Rot2D.GET();

    /**
     * The change in the rotation of this body mid-timestep.
     */
    protected final M_Rot2D tempRotChange = M_Rot2D.GET();

    A_CrappyShape shape;


    /**
     * Current angular velocity of this CrappyBody
     */
    double angVelocity;

    /**
     * Temporarily stores how much the angular velocity will be changed by mid-timestep
     */
    protected double tempAngVelocityChange;

    /**
     * Temporarily stores the angular velocity of this body mid-timestep
     */
    protected double tempAngVelocity;

    /**
     * Current torque being applied to this body
     */
    protected double torque = 0;

    final double mass;

    double inertia = 0;


    /**
     * All the forces to apply to this body which remain constant throughout current timestep
     */
    protected final M_Vect2D pending_forces_this_timestep = M_Vect2D.GET();


    /**
     * All the torques to apply to this body which remain constant throughout current timestep
     */
    protected double pending_torque_this_timestep = 0;

    /**
     * All the forces to apply to this body which may change mid-timestep
     */
    protected final M_Vect2D pending_forces_mid_timestep = M_Vect2D.GET();

    /**
     * All the torques to apply to this body which may change mid-timestep
     */
    protected double pending_torque_mid_timestep = 0;

    public final CRAPPY_BODY_TYPE bodyType;

    /**
     * This is a view of the CrappyBody's transform mid-timestep.
     */
    public final I_Transform intermediateTransform = new I_CrappyBody_Temp_Transform(this);

    /**
     * How much drag is this CrappyBody experiencing?
     */
    protected double linearDrag = 0;

    /**
     * How much angular drag this CrappyBody has
     */
    protected double angularDrag = 0;

    /**
     * the restitution of this object
     */
    protected double restitution = 0.9;

    protected boolean tangible = true;

    public CrappyBody(final double mass, final CRAPPY_BODY_TYPE bodyType) {
        this.bodyType = bodyType;
        if (bodyType == CRAPPY_BODY_TYPE.STATIC){
            this.mass = 0;
        } else {
            this.mass = mass;
        }
        inertia = 0;
    }

    public void setShape(final A_CrappyShape shape){
        this.shape = shape;
    }

    /**
     * What type of body is this?
     */
    public static enum CRAPPY_BODY_TYPE{
        STATIC,
        DYNAMIC,
        KINEMATIC
    }

    /**
     * Used to define where a force is coming from.
     * This only really affects Kinematic bodies,
     * as they ignore all forces applied by the engine.
     */
    public static enum FORCE_SOURCE{
        ENGINE,
        MANUAL
    }



    @Override
    public void setMomentOfInertia(final double moment) {
        this.inertia = moment;
    }

    @Override
    public Vect2D getPos() {
        return position;
    }

    @Override
    public I_Vect2D getTempPos(){
        return tempPosition;
    }


    @Override
    public Vect2D getVel() {
        return velocity;
    }

    @Override
    public I_Vect2D getTempVel(){
        return tempVel;
    }

    @Override
    public Rot2D getRot() {
        return rotation;
    }

    public I_Rot2D getTempRot(){
        return tempRot;
    }

    @Override
    public double getAngVel() {
        return angVelocity;
    }

    @Override
    public double getTempAngVel(){ return tempAngVelocity; }


    /**
     * Cannot apply forces to static bodies, and cannot apply ENGINE forces to kinematic bodies
     * @param source the source of this force
     * @return whether or not we can apply it to this body
     */
    private boolean canApplyThisForce(final FORCE_SOURCE source){
        switch (bodyType){
            case STATIC:
                return false;
            case KINEMATIC:
                return source != FORCE_SOURCE.ENGINE;
            default:
                return true;
        }

    }

    /**
     * Internal method for applying a force (considered constant throughout the timestep) to a local position on a body
     * THIS WILL BE IGNORED BY KINEMATIC BODIES!
     * @param force the force
     * @param localForcePos local position to apply it to
     */
    public void applyForce(final I_Vect2D force, final I_Vect2D localForcePos){

        applyForce(force, localForcePos, FORCE_SOURCE.ENGINE);
    }

    /**
     * Use this to apply any forces that are considered constant throughout a timestep,
     * and apply it to a specific local position on this body.
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     */
    public void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final FORCE_SOURCE source){
        if (canApplyThisForce(source)){
            pending_forces_this_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));

            pending_torque_this_timestep += M_Vect2D.GET(localForcePos).rotate(rotation).cross_discard(force);
        }
    }

    /**
     * Internal method for applying a force (considered constant throughout the timestep) to origin of body
     * @param force the force
     */
    @Override
    public void applyForce(final I_Vect2D force){
        applyForce(force, FORCE_SOURCE.ENGINE);
    }

    /**
     * Use this to apply any forces that are considered constant throughout a timestep,
     * and apply it to centroid of the body
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     * @param force the force expressed as a vector
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     */
    public void applyForce(final I_Vect2D force, final FORCE_SOURCE source){

        if (canApplyThisForce(source)) {

            pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
        }
    }

    /**
     * Use this to apply any forces that depend on the distance between a point on this body and a point elsewhere,
     * and apply it to a specific local position on this body.
     * THIS WILL BE IGNORED BY KINEMATIC BODIES!
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     */
    @Override
    public void applyDistanceForce(final I_Vect2D force, final I_Vect2D localForcePos) {

        applyDistanceForce(force, localForcePos, FORCE_SOURCE.ENGINE);
    }

    /**
     * Use this to manually apply any forces that depend on the distance between a point on this body
     * and a point elsewhere, and apply it to a specific local position on this body.
     * Will always be ignored by static bodies, and will only be taken into account by a kinematic body if
     * FORCE_SOURCE is ENGINE.
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     * @param source Where the force is coming from. If you're moving something yourself, please specify
     *               FORCE_SOURCE.MANUAL, otherwise any Kinematic bodies will ignore it.
     */
    public void applyDistanceForce(final I_Vect2D force, final I_Vect2D localForcePos, final FORCE_SOURCE source){

        if (canApplyThisForce(source)) {
            pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));

            pending_torque_mid_timestep += M_Vect2D.GET(localForcePos).rotate(rotation).cross_discard(force);
        }

    }

    /**
     * Use this to apply a force that depends on the distance between this body and somewhere else,
     * but applied to (0, 0) on this body
     * @param force the force to apply to point (0, 0) on this body.
     */
    public void applyDistanceForce(final I_Vect2D force){
        applyDistanceForce(force, FORCE_SOURCE.ENGINE);
    }

    /**
     * Use this to apply a force that depends on the distance between this body and somewhere else,
     * but applied to (0, 0) on this body
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     * @param force the force to apply to point (0, 0) on this body.
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     */
    public void applyDistanceForce(final I_Vect2D force,final FORCE_SOURCE source){
        if (canApplyThisForce(source)){
            pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
        }
    }




    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public Crappy_AABB getAABB() {
        return shape.getBoundingBox();
    }

    @Override
    public A_CrappyShape getShape() {
        return shape;
    }

    @Override
    public double getMomentOfInertia() {
        return inertia;
    }

    public double getRestitution(){ return restitution; }

    @Override
    public CRAPPY_BODY_TYPE getBodyType() {
        return bodyType;
    }

    /**
     * Call this to perform the first euler sub-update on this body.
     * Will automatically call euler_substep(deltaT_divided_by_substeps) afterwards.
     * @param deltaT_divided_by_substeps deltaT but pre-divided by the number of euler substeps.
     * @param gravity constant force of gravity for these substeps.
     */
    void first_euler_sub_update(final double deltaT_divided_by_substeps, final I_Vect2D gravity){

        switch (bodyType){
            case STATIC:
                // if it's static, it won't be updating, simple
                return;
            case DYNAMIC:
                // we apply gravity to the body
                pending_forces_this_timestep.add(gravity);
                if (linearDrag != 0){
                    // and we apply the drag to the body if appropriate
                    pending_forces_this_timestep.add_discardOther(M_Vect2D.GET(velocity).mult(-linearDrag));
                }
                if (angularDrag != 0){
                    // also applying angular drag if appropriate
                    pending_torque_this_timestep += (angVelocity * -angularDrag);
                }
            default:

                pending_forces_this_timestep.mult(1/mass);

                pending_torque_this_timestep = (inertia == 0) ? 0 : pending_torque_this_timestep/inertia;


                tempVel.set(velocity);
                tempPosition.set(position);
                tempRot.set(rotation);
                tempAngVelocity = angVelocity;

                euler_substep(deltaT_divided_by_substeps);
                break;
        }

    }

    void euler_substep(final double delta_T_divided_by_substeps){
        if (bodyType == CRAPPY_BODY_TYPE.STATIC){
            return;
        }

        tempPosition.addScaled(
                tempVel, delta_T_divided_by_substeps
        );

        tempRot.rotateBy(tempAngVelocity * delta_T_divided_by_substeps);

        tempVel.addScaled(pending_forces_this_timestep, delta_T_divided_by_substeps);
        tempVel.addScaled(pending_forces_mid_timestep.mult(1/mass), delta_T_divided_by_substeps);

        pending_forces_mid_timestep.reset();

        if (inertia != 0) {
            tempAngVelocity += (pending_torque_this_timestep * delta_T_divided_by_substeps);
            tempAngVelocity += ((pending_torque_mid_timestep / inertia) * delta_T_divided_by_substeps);
        }

        pending_torque_mid_timestep = 0;

    }

    private void post_euler_update_cleanup(){
        pending_forces_mid_timestep.reset();
        pending_torque_mid_timestep = 0;
        pending_forces_this_timestep.reset();
        pending_torque_this_timestep = 0;
        velocity = new Vect2D(tempVel);
        position = new Vect2D(tempPosition);
        angVelocity = tempAngVelocity;
        rotation = new Rot2D(tempRot);

        //and now making sure the temps are 100% identical to the

        tempVel.set(velocity);
        tempPosition.set(position);
        tempRot.set(rotation);
        tempAngVelocity = angVelocity;
    }



    @Override
    public I_Transform getTempTransform() {
        return intermediateTransform;
    }
}




