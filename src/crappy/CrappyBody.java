package crappy;

import crappy.math.*;
import crappy.collisions.A_CrappyShape;
import crappy.collisions.Crappy_AABB;
import crappy.utils.bitmasks.IHaveBitmask;

/**
 * A rigidbody class used by Crappy
 */
public class CrappyBody implements I_CrappyBody, I_View_CrappyBody, CrappyBody_Shape_Interface, CrappyBody_Connector_Interface {

    //TODO https://www.myphysicslab.com/explain/physics-engine-en.html hmmmmm

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

    /**
     * How much mass does this body have?
     */
    final double mass;

    /**
     * What's the moment of inertia of this body?
     * If 0, object cannot be rotated.
     * This should be set by the CrappyShape which will be attached to this body.
     */
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

    /**
     * What sort of body is this?
     */
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

    /**
     * Set this to 'false' if this body isn't supposed to have any physics impact when colliding
     */
    protected boolean tangible = true;

    /**
     * Whether or not this body is currently allowed to move
     */
    protected boolean canMove = true;

    /**
     * Set this to 'true' if this CrappyBody needs to be removed from the physics engine
     */
    protected boolean pendingRemoval = false;

    /**
     * Basically a bitmask to indicate what 'tags' the objects this object collided with had.
     */
    int combinedBitsOfOtherObjectsCollidedWith = 0;

    /**
     * This is a bitmask, as some sort of arbitrary 'tag' system for bodies.
     */
    final int myBodyTagBits;

    /**
     * A bitmask indicating what object tags this object can collide with.
     * Set this to -1 to indicate that it can collide with anything.
     */
    final int tagsICanCollideWithBitmask;

    // TODO: basically call this after done with euler updates, give collidedWithBitmask,
    //  intended for external objects to see what this collided with this timestep, and to handle it appropriately.
    final CrappyCollisionCallbackHandler callbackHandler;

    /**
     *
     */
    final Object userData;

    public CrappyBody(
            final Vect2D pos,
            final Vect2D vel,
            final Rot2D rot,
            final double angVel,
            final double mass,
            final CRAPPY_BODY_TYPE bodyType,
            final int bodyTagBits,
            final int tagsICanCollideWithBits,
            final CrappyCollisionCallbackHandler callback,
            final Object userData
    ) {
        this.bodyType = bodyType;
        if (bodyType == CRAPPY_BODY_TYPE.STATIC){
            this.mass = 0;
        } else {
            this.mass = mass;
        }

        inertia = 0;
        myBodyTagBits = bodyTagBits;
        tagsICanCollideWithBitmask = tagsICanCollideWithBits;
        callbackHandler = callback;
        this.userData = userData;
    }

    public void setShape(final A_CrappyShape shape){
        this.shape = shape;
    }

    /**
     * Returns {@link #myBodyTagBits} (the bitmask representing the 'tags' of this object)
     * @return {@link #myBodyTagBits}
     */
    @Override
    public int getBitmask() {
        return myBodyTagBits;
    }

    /**
     * Combines current {@link #combinedBitsOfOtherObjectsCollidedWith} to be the result of
     * ORing it with the other object's bitmask (updating {@link #combinedBitsOfOtherObjectsCollidedWith})
     * @param other the other object with a bitmask
     */
    @Override
    public void accept(final IHaveBitmask other) {
        combinedBitsOfOtherObjectsCollidedWith |= other.getBitmask();
    }

    /**
     * COMPARES THIS OBJECT'S 'tagsICanCollideWith' BITMASK TO THE OTHER OBJECT'S BITMASK!
     * Also returns true if 'tagsICanCollideWith' is -1
     * @param other the other object with a bitmask
     * @return true if tagsICanCollideWithBitmask is -1,
     * or if the result of tagsICanCollideWithBitmask & other.getBitmask() is greater than 0.
     * @see #tagsICanCollideWithBitmask
     */
    public boolean anyMatchInBitmasks(final IHaveBitmask other){
        return tagsICanCollideWithBitmask == -1 || ((tagsICanCollideWithBitmask & other.getBitmask()) > 0);
    }

    /**
     * What type of body is this?
     */
    public static enum CRAPPY_BODY_TYPE{
        STATIC,
        DYNAMIC,
        KINEMATIC;


    }

    /**
     * Used to define where a force is coming from.
     * This only really affects Kinematic bodies,
     * as they ignore all forces applied by the engine.
     */
    public static enum FORCE_SOURCE{
        ENGINE,
        MANUAL;
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
                return canMove;
        }

    }

    /**
     * External method for applying a force (considered constant throughout the timestep) to a local position on a body.
     *
     * You should use this to apply forces to your bodies for your games.
     * THIS WILL BE IGNORED BY STATIC BODIES!
     * @param force the force
     * @param localForcePos local position to apply it to
     * @see #applyForce(I_Vect2D, I_Vect2D, FORCE_SOURCE)
     */
    public void applyForce(final I_Vect2D force, final I_Vect2D localForcePos){

        applyForce(force, localForcePos, FORCE_SOURCE.MANUAL);
    }

    /**
     * Use this to apply any forces that are considered constant throughout a timestep,
     * and apply it to a specific local position on this body.
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     *
     * If you're not doing stuff within CRAPPY, please use {@link #applyForce(I_Vect2D, I_Vect2D)} instead
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     * @see #applyMidTimestepForce(I_Vect2D, I_Vect2D)
     * @see #canApplyThisForce(FORCE_SOURCE)
     */
    public void applyForce(final I_Vect2D force, final I_Vect2D localForcePos, final FORCE_SOURCE source){
        if (canApplyThisForce(source)){
            pending_forces_this_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));

            pending_torque_this_timestep += M_Vect2D.GET(localForcePos).rotate(rotation).cross_discard(force);
        }
    }

    /**
     * Public method for applying a force (considered constant throughout the timestep) to origin of body.
     * All forces added through this are considered to be manually applied, instead of being applied by the engine.
     *
     * If you're reading this and not working on CRAPPY itself, use this.
     * @param force the force
     * @see #applyForce(I_Vect2D, FORCE_SOURCE)
     */
    public void applyForce(final I_Vect2D force){
        applyForce(force, FORCE_SOURCE.MANUAL);
    }

    /**
     * Use this to apply any forces that are considered constant throughout a timestep,
     * and apply it to centroid of the body
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     *
     * If you're not doing stuff within CRAPPY, please use {@link #applyForce(I_Vect2D)} instead.
     * @param force the force expressed as a vector
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     */
    @Override
    public void applyForce(final I_Vect2D force, final FORCE_SOURCE source){

        if (canApplyThisForce(source)) {

            pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
        }
    }

    /**
     * Use this to apply any forces that depend on the distance between a point on this body and a point elsewhere,
     * and apply it to a specific local position on this body.
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     */
    public void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos) {

        applyMidTimestepForce(force, localForcePos, FORCE_SOURCE.MANUAL);
    }

    /**
     * Use this to manually apply any forces that depend on the distance between a point on this body
     * and a point elsewhere, and apply it to a specific local position on this body.
     * Will always be ignored by static bodies, and will only be taken into account by a kinematic body if
     * FORCE_SOURCE is ENGINE.
     * Anyway, if you're not doing stuff within CRAPPY itself, please use
     * {@link #applyMidTimestepForce(I_Vect2D, I_Vect2D)} instead.
     * @param force the force expressed as a vector
     * @param localForcePos local position of where this force is being applied
     * @param source Where the force is coming from. If you're moving something yourself, please specify
     *               FORCE_SOURCE.MANUAL, otherwise any Kinematic bodies will ignore it.
     * @see #applyMidTimestepForce(I_Vect2D, I_Vect2D)
     */
    @Override
    public void applyMidTimestepForce(final I_Vect2D force, final I_Vect2D localForcePos, final FORCE_SOURCE source){

        if (canApplyThisForce(source)) {
            pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));

            pending_torque_mid_timestep += M_Vect2D.GET(localForcePos).rotate(rotation).cross_discard(force);
        }

    }

    /**
     * Use this to apply a force that depends on the distance between this body and somewhere else,
     * but applied to (0, 0) on this body.
     * Will be ignored by static bodies.
     * @param force the force to apply to point (0, 0) on this body.
     */
    public void applyMidTimestepForce(final I_Vect2D force){
        applyMidTimestepForce(force, FORCE_SOURCE.MANUAL);
    }

    /**
     * Use this to apply a force that depends on the distance between this body and somewhere else,
     * but applied to (0, 0) on this body
     * THIS WILL BE IGNORED BY KINEMATIC BODIES unless specified as manual.
     * @param force the force to apply to point (0, 0) on this body.
     * @param source if not MANUAL, this force will be ignored by kinematic bodies.
     */
    @Override
    public void applyMidTimestepForce(final I_Vect2D force, final FORCE_SOURCE source){
        if (canApplyThisForce(source)){
            pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
        }
    }


    /**
     * Applies a torque which remains constant throughout the timestep to this object.
     * You should use {@link #applyTorque(double)} instead.
     * @param torque torque force to apply
     * @param source source of it. You should specify it as manual.
     * @see #applyTorque(double)
     */
    public void applyTorque(final double torque, final FORCE_SOURCE source){

        if (canApplyThisForce(source)){
            pending_torque_this_timestep += torque;
        }

    }

    /**
     * Like {@link #applyTorque(double, FORCE_SOURCE)} but source is manual. Please use this.
     * @param torque torque to apply throughout timestep
     */
    public void applyTorque(final double torque){
        applyTorque(torque, FORCE_SOURCE.MANUAL);
    }

    /**
     * Applies torque to this object for sub-timestep.
     * You, reading this, should use {@link #applyMidTimestepTorque(double)} instead.
     * @param torque torque to apply
     * @param source is it coming from engine or being done manually?
     * @see #applyMidTimestepTorque(double)
     */
    public void applyMidTimestepTorque(final double torque, final FORCE_SOURCE source){

        if (canApplyThisForce(source)){
            pending_torque_mid_timestep += torque;
        }
    }

    /**
     * Applies torque to this object for sub-timestep.
     * @param torque torque to apply
     */
    public void applyMidTimestepTorque(final double torque){
        applyMidTimestepTorque(torque, FORCE_SOURCE.MANUAL);
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

    @Override
    public Object getUserData() {
        return userData;
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

   void post_euler_update_cleanup(){
        pending_forces_mid_timestep.reset();
        pending_torque_mid_timestep = 0;
        pending_forces_this_timestep.reset();
        pending_torque_this_timestep = 0;
        velocity = new Vect2D(tempVel);
        position = new Vect2D(tempPosition);
        angVelocity = tempAngVelocity;
        rotation = new Rot2D(tempRot);

        //and now making sure the temps are 100% identical to the actuals

        tempVel.set(velocity);
        tempPosition.set(position);
        tempRot.set(rotation);
        tempAngVelocity = angVelocity;
    }

    @Override
    public I_Transform getTempTransform() {
        return intermediateTransform;
    }


    public static CrappyBodyCreator GET_CREATOR(){
        return new CrappyBodyCreator();
    }
}

/**
 * Inspired somewhat by JBox2D's BodyDef class,
 * intended to make it slightly less painful to create a CrappyBody.
 */
class CrappyBodyCreator {


    public CrappyWorld world = null;

    public Vect2D pos = Vect2D.ZERO;
    public Vect2D vel = Vect2D.ZERO;

    public Rot2D angle = Rot2D.IDENTITY;

    public double angVel = 0;

    public double mass = 1;

    public CrappyBody.CRAPPY_BODY_TYPE bodyType = null;

    public boolean tangible = true;

    public double linearDrag = 0.9;

    public double angularDrag = 0.9;

    public static int DEFAULT_THIS_BODY_TAGS_BITMASK = 0;

    public int thisBodyTagsAsBitmask = DEFAULT_THIS_BODY_TAGS_BITMASK;

    public int tagsOfBodiesThatCanBeCollidedWith = 0;

    public CrappyCollisionCallbackHandler parentObject;

    public Object userData;

    public CrappyBodyCreator(){}



}


