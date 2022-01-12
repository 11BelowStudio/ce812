package crappy;

import crappy.collisions.CrappyEdge;
import crappy.collisions.CrappyLine;
import crappy.graphics.DrawableBody;
import crappy.graphics.DrawableCrappyShape;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.*;
import crappy.collisions.A_CrappyShape;
import crappy.collisions.Crappy_AABB;
import crappy.utils.lazyFinal.LazyFinal;
import crappy.utils.PendingStateChange;
import crappy.utils.bitmasks.IHaveBitmask;

import java.util.*;

/**
 * A rigidbody class used by Crappy
 * @author Rachel Lowe
 */
public class CrappyBody implements
        I_CrappyBody,
        I_View_CrappyBody,
        CrappyBody_Shape_Interface,
        CrappyBody_Connector_Interface,
        I_ManipulateCrappyBody,
        I_CrappyBody_CrappyWorld_Interface,
        DrawableBody,
        CrappyBody_ShapeSetter_Interface {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    /**
     * Current position of this CrappyBody
     */
    Vect2D position;

    /**
     * Previous position of this CrappyBody
     */
    Vect2D lastPosition;

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
    Vect2D velocity = Vect2D.ZERO;

    /**
     * Temporarily stores the change in velocity experienced by this object between its original velocity and
     * the velocity of it mid-timestep
     */
    protected transient final M_Vect2D tempVelChange = M_Vect2D.GET();

    /**
     * Velocity of this body mid-timestep
     */
    protected final M_Vect2D tempVel = M_Vect2D.GET();

    /**
     * Current rotation of this CrappyBody
     */
    Rot2D rotation = Rot2D.IDENTITY;

    /**
     * The rotation of this body mid-timestep
     */
    protected final M_Rot2D tempRot = M_Rot2D.GET();

    /**
     * The change in the rotation of this body mid-timestep.
     */
    protected transient final M_Rot2D tempRotChange = M_Rot2D.GET();

    /**
     * Previous rotation.
     */
    Rot2D lastRotation = Rot2D.IDENTITY;

    //A_CrappyShape shape;

    public final LazyFinal<A_CrappyShape> shape = new LazyFinal<>();

    /**
     * Current angular velocity of this CrappyBody
     */
    double angVelocity = 0;

    /**
     * Temporarily stores how much the angular velocity will be changed by mid-timestep
     */
    protected double tempAngVelocityChange = 0;

    /**
     * Temporarily stores the angular velocity of this body mid-timestep
     */
    protected double tempAngVelocity = 0;

    /**
     * Current torque being applied to this body
     */
    protected double torque = 0;

    /**
     * How much mass does this body have?
     */
    public final double mass;

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
     * For forces that need to remain sustained throughout all of the update iterations
     */
    protected final M_Vect2D pending_external_forces = M_Vect2D.GET();


    /**
     * All the torques to apply to this body which remain constant throughout current timestep
     */
    protected double pending_torque_this_timestep = 0;

    /**
     * All the forces to apply to this body which may change mid-timestep
     */
    protected transient final M_Vect2D pending_forces_mid_timestep = M_Vect2D.GET();

    /**
     * All the torques to apply to this body which may change mid-timestep
     */
    protected transient double pending_torque_mid_timestep = 0;

    /**
     * For torques that need to remain sustained throughout all of the update iterations
     */
    protected double pending_external_torque = 0;

    /**
     * What sort of body is this?
     */
    public final CRAPPY_BODY_TYPE bodyType;

    /**
     * This is a view of the CrappyBody's transform mid-timestep.
     */
    public final I_CrappyBody_Temp_Transform intermediateTransform = new I_CrappyBody_Temp_Transform(this);

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
    protected double restitution = 1;

    /**
     * Whether or not this body is considered 'active'.
     */
    protected boolean active = true;

    /**
     * pending change to activity
     */
    protected PendingStateChange pendingActive = PendingStateChange.SAME_AS_IT_EVER_WAS;

    /**
     * Set this to 'false' if this body isn't supposed to have any physics impact when colliding
     */
    protected boolean tangible = true;

    /**
     * pending change to being (in)tangible
     */
    protected PendingStateChange pendingIntangbility = PendingStateChange.SAME_AS_IT_EVER_WAS;

    /**
     * Whether or not this body is currently allowed to move
     */
    protected boolean canMoveLinearly = true;

    /**
     * Whether or not this body is currently allowed to rotate
     */
    protected boolean canRotate = true;

    /**
     * Set this to true if this should stop being able to move linearly
     */
    protected PendingStateChange pendingCanMoveLinearly = PendingStateChange.SAME_AS_IT_EVER_WAS;

    /**
     * Set this to true if this should stop being able to rotate
     */
    protected PendingStateChange pendingCanRotate = PendingStateChange.SAME_AS_IT_EVER_WAS;

    /**
     * Set this to 'true' if this CrappyBody needs to be removed from the physics engine
     */
    protected boolean pendingRemoval = false;


    /**
     * Basically a bitmask to indicate what 'tags' the objects this object collided with had.
     */
    transient int combinedBitsOfOtherObjectsCollidedWith = 0;

    /**
     * This is a bitmask, as some sort of arbitrary 'tag' system for bodies.
     */
    public final int myBodyTagBits;

    /**
     * A bitmask indicating what object tags this object can collide with.
     * Set this to -1 to indicate that it can collide with anything.
     */
    public final int tagsICanCollideWithBitmask;

    /**
     * intended for external objects to see what this collided with this timestep, and to handle it appropriately.
     */
    public final CrappyCallbackHandler callbackHandler;

    /**
     * any user-defined data
     */
    public final Object userData;

    /**
     * Also has a string name, for ease of use.
     */
    public final String name;

    /**
     * A unique identifier.
     */
    public final UUID uniqueIdentifier;


    /**
     * A set with all of the connectors attached to this body.
     */
    final Set<CrappyConnectorBodyInterface> myConnections = new HashSet<>();

    /**
     * The identified bodies that this object cannot collide with.
     */
    private final Map<UUID, IHaveIdentifier> idsICannotCollideWith = Collections.synchronizedMap(new HashMap<>());


    private boolean discarded = false;

    /**
     * Constructs a new CrappyBody
     * @param pos initial world position
     * @param vel initial world velocity
     * @param rot initial world rotation
     * @param angVel initial world angular velocity
     * @param mass initial world mass
     * @param bodyType initial world body type
     * @param bodyTagBits initial world 'tag' bits {@link #myBodyTagBits}
     * @param tagsICanCollideWithBits a bitmask indicating which 'tagged' objects
     *                                this object can collide with
     *                                (-1 to collide with everything)
     *                                {@link #tagsICanCollideWithBitmask}
     * @param callback A callback handler to call when this body gets collided with
     * @param userData an object of arbitrary user data, use at your own risk!
     * @param id a name for this body.
     * @param tangible is it tangible?
     * @param canMoveLinearly can it move x y position?
     * @param canRotate can it rotate?
     */
    public CrappyBody(
            final Vect2D pos,
            final Vect2D vel,
            final Rot2D rot,
            final double angVel,
            final double mass,
            final double restitution,
            final double angularDrag,
            final double linearDrag,
            final CRAPPY_BODY_TYPE bodyType,
            final int bodyTagBits,
            final int tagsICanCollideWithBits,
            final CrappyCallbackHandler callback,
            final Object userData,
            final String id,
            final boolean tangible,
            final boolean canMoveLinearly,
            final boolean canRotate
    ) {
        this.bodyType = bodyType;
        if (bodyType == CRAPPY_BODY_TYPE.STATIC){
            this.mass = 0;
        } else if (Double.isFinite(mass)){
            double aMass = Math.abs(mass);
            if (aMass > 0){
                this.mass = aMass;
            } else {
                throw new IllegalArgumentException("Cannot give a non-static body a mass of zero!");
            }
        } else {
            throw new IllegalArgumentException("Cannot use a mass of infinity/NaN!");
        }
        this.position = pos;
        lastPosition = pos;
        tempPosition.set(pos);
        this.velocity = vel;
        tempVel.set(vel);
        this.rotation = rot;
        lastRotation = rot;
        tempRot.set(rot);
        this.angVelocity = angVel;
        this.tempAngVelocity = angVel;
        this.restitution = restitution;
        this.angularDrag = angularDrag;
        this.linearDrag = linearDrag;
        inertia = 0;
        myBodyTagBits = bodyTagBits;
        tagsICanCollideWithBitmask = tagsICanCollideWithBits;
        callbackHandler = callback;
        this.userData = userData;
        name = id;
        intermediateTransform.update();
        eus = EULER_UPDATE_STATE.NOTHING;

        uniqueIdentifier = UUID.randomUUID();
        this.tangible = tangible;
        this.canMoveLinearly = canMoveLinearly;
        this.canRotate = canRotate;
    }

    /**
     * This is called to set the shape of this object
     * @param shape the shape of this object
     */
    public void __setShape__internalDoNotCallYourselfPlease(final A_CrappyShape shape, final double momentOfInertia){
        this.shape.set(shape);
        if (bodyType != CRAPPY_BODY_TYPE.STATIC) {
            this.inertia = momentOfInertia;
        }
        shape.timestepStartUpdate();
        shape.midTimestepUpdate();
        shape.timestepEndUpdate();
        shape.updateShape(this);
        shape.updateShape(this);
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
     * Checks if this object is actually allowed to collide with the other object
     * @param other the other object
     * @return true if they can collide, otherwise false.
     */
    public boolean allowedToCollideWith(final I_View_CrappyBody other){
        if (idsICannotCollideWith.containsKey(other.getID())){
            return false;
        } else {
            return tagsICanCollideWithBitmask < 0 || anyMatchInBitmasks(other);
        }

    }

    @Override
    public boolean isDiscarded() {
        return discarded;
    }

    /**
     * COMPARES THIS OBJECT'S 'tagsICanCollideWith' BITMASK TO THE OTHER OBJECT'S BITMASK!
     * Also returns true if 'tagsICanCollideWith' is -1
     * Also returns true if 'tagsICanCollideWith' is -1
     * @param other the other object with a bitmask
     * @return true if tagsICanCollideWithBitmask is -1,
     * or if the result of tagsICanCollideWithBitmask & other.getBitmask() is greater than 0.
     * @see #tagsICanCollideWithBitmask
     */
    @Override
    public boolean anyMatchInBitmasks(final IHaveBitmask other){
        return tagsICanCollideWithBitmask < 0 || ((tagsICanCollideWithBitmask & other.getBitmask()) > 0);
    }

    /**
     * Do you want this body to be 'active' or not? Inactive bodies don't have collision and don't recieve forces. Will
     * update at start of next update loop.
     *
     * @param beActive whether or not you want this body to be 'active'
     */
    @Override
    public void setActive(final boolean beActive) {
        pendingActive = PendingStateChange.TO_PENDING_STATE_CHANGE(active, beActive);
    }

    /**
     * Do you want to mark this CrappyBody for removal at the next possible frame? Will update at start of next update
     * loop.
     *
     * @param markForRemoval whether or not you're marking it for removal. If true, this object will be removed at the
     *                       next frame.
     * @implNote WILL COMPLAIN INSTEAD OF REMOVE IF YOU'RE TRYING TO REMOVE STATIC STUFF!
     */
    @Override
    public void setMarkForRemoval(final boolean markForRemoval) {
        if (markForRemoval && bodyType == CRAPPY_BODY_TYPE.STATIC) {
            System.out.println("pls no");
        } else {
            this.pendingRemoval = markForRemoval;
        }
    }

    /**
     * Should this rigidbody become intangible? No collisions, can recieve forces. Will update at start of next update
     * loop.
     *
     * @param pendingTangibility true if it should become intangible.
     */
    @Override
    public void setTangibility(final boolean pendingTangibility) {
        this.pendingIntangbility = PendingStateChange.TO_PENDING_STATE_CHANGE(tangible, pendingTangibility);
    }

    /**
     * Should this rigidbody be allowed to move linearly?
     *
     * @param posLocked true if no, false if yes
     */
    @Override
    public void setPositionLocked(final boolean posLocked) {
        pendingCanMoveLinearly = PendingStateChange.TO_PENDING_STATE_CHANGE(canMoveLinearly, !posLocked);
    }

    /**
     * Should this rigidbody be allowed to turn?
     *
     * @param rotLocked true if no, false if yes
     */
    @Override
    public void setRotationLocked(boolean rotLocked) {
        pendingCanRotate = PendingStateChange.TO_PENDING_STATE_CHANGE(canRotate, !rotLocked);
    }

    /**
     * Call this to tell this I_CrappyBody to call the {@link CrappyCallbackHandler#acceptCollidedWithBitmaskAfterAllCollisions(int)}
     * method in its {@link CrappyCallbackHandler}.
     *
     * @see CrappyBody#callbackHandler
     */
    @Override
    public void performPostCollisionBitmaskCallback() {
        callbackHandler.acceptCollidedWithBitmaskAfterAllCollisions(combinedBitsOfOtherObjectsCollidedWith);
    }

    /**
     * Resolve any changes to this body's 'active' state
     *
     * @return change made to the body's active state
     */
    @Override
    public PendingStateChange resolveActiveChange() {
        final PendingStateChange psc = pendingActive;
        this.active = psc.performStateChange(this.active);
        pendingActive = PendingStateChange.SAME_AS_IT_EVER_WAS;
        return psc;
    }

    /**
     * Resolve any potential changes to this body's removal state
     *
     * @return true if it needs to be removed
     */
    @Override
    public boolean resolveRemovalChange() {
        if (pendingRemoval){
            callbackHandler.bodyNoLongerExists();
        }
        return pendingRemoval;
    }

    /**
     * Resolve any potential changes needed to this body's position lock change
     *
     * @return change made to position lock
     */
    @Override
    public PendingStateChange resolvePositionLockChange() {
        final PendingStateChange psc = pendingCanMoveLinearly;
        canMoveLinearly = psc.performStateChange(canMoveLinearly);
        pendingCanMoveLinearly = PendingStateChange.SAME_AS_IT_EVER_WAS;
        return psc;
    }

    /**
     * Resolve any potential changes needed to this body's rotation lock change
     *
     * @return change made to rotation lock
     */
    @Override
    public PendingStateChange resolveRotationLockChange() {
        final PendingStateChange psc = pendingCanRotate;
        canRotate = psc.performStateChange(canRotate);
        pendingCanRotate = PendingStateChange.SAME_AS_IT_EVER_WAS;
        return psc;
    }

    /**
     * Resolve any potential changes needed to this body's tangibility
     *
     * @return change made to tangibility.
     */
    @Override
    public PendingStateChange resolveTangibilityChange() {
        final PendingStateChange psc = pendingIntangbility;
        tangible = psc.performStateChange(tangible);
        pendingIntangbility = PendingStateChange.SAME_AS_IT_EVER_WAS;
        return psc;
    }

    /**
     * Sets the new linear drag for this object.
     * Please keep it between 0 (no drag) and 1 (all the drag).
     * (but if you want to give it a weird value, feel free)
     * @param newDrag new linear drag force.
     */
    public void setLinearDrag(double newDrag){
        linearDrag = newDrag;
    }

    /**
     * Sets the new angular drag for this object.
     * Please keep it between 0 (no drag) and 1 (all the drag).
     * (but if you want to give it a weird value, feel free)
     * @param newDrag new angular drag force.
     */
    public void setAngularDrag(double newDrag){
        angularDrag = newDrag;
    }



    /**
     * What type of body is this?
     */
    public static enum CRAPPY_BODY_TYPE {
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


    /**
     * internal enum for keeping track of internal state with euler updates and sub-updates (which one happened last)
     */
    private enum EULER_UPDATE_STATE{
        NOTHING,
        PRIOR_SUB_UPDATE,
        SUB_UPDATING,
        DONE;
    }

    /**
     * what euler update state is this in?
     */
    private EULER_UPDATE_STATE eus = EULER_UPDATE_STATE.NOTHING;



    /**
     * Called to notify the CrappyBody that it has, in fact, collided with something.
     *
     * @param collidedWith the body it collided with.
     */
    @Override
    public void notifyAboutCollision(final I_View_CrappyBody collidedWith) {
        accept(collidedWith);
        callbackHandler.collidedWith(collidedWith);
    }

    /**
     * Returns manipulatable interface of this body.
     *
     * @return I_ManipulateCrappyBody view of this object CrappyBody
     */
    @Override
    public I_ManipulateCrappyBody getManipulatable() {
        return this;
    }


    @Override
    public Vect2D getPos() {
        return position;
    }

    /**
     * Obtains previous position of the body, before most recent getPos position.
     * @return prior position
     */
    public Vect2D getLastPos(){
        return lastPosition;
    }

    /**
     * Obtains previous rotation of the body, before most recent getRot rotation
     *
     * @return prior rotation
     */
    @Override
    public I_Rot2D getLastRot() {
        return lastRotation;
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
     * mass
     *
     * @return the mass
     */
    @Override
    public double getMass() {
        return mass;
    }

    /**
     * collision bounding box
     *
     * @return bounding box
     */
    @Override
    public Crappy_AABB getAABB() {
        return shape.getAssert().getBoundingBox();
    }

    /**
     * Collision shape
     *
     * @return collision shape
     */
    @Override
    public A_CrappyShape getShape() {
        return shape.getAssert();
    }

    /**
     * Moment of inertia
     *
     * @return moment of inertia
     */
    @Override
    public double getMomentOfInertia() {
        return inertia;
    }

    /**
     * body type
     *
     * @return static? dynamic? kinematic?
     */
    @Override
    public CRAPPY_BODY_TYPE getBodyType() {
        return bodyType;
    }


    /**
     * Coefficient of restitution for crappybody
     *
     * @return restitution
     */
    @Override
    public double getRestitution() {
        return restitution;
    }

    /**
     * Obtains user data object of CrappyBody
     *
     * @return user data
     */
    @Override
    public Object getUserData() {
        return userData;
    }


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

            switch (source){
                case ENGINE:
                    //System.out.println("CrappyBody.applyForce");
                    //System.out.println("force = " + force + ", localForcePos = " + localForcePos + ", source = " + source);

                    //System.out.println("pending_forces_this_timestep = " + pending_forces_this_timestep);
                    //pending_forces_this_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
                    pending_forces_this_timestep.add(force);
                    //System.out.println("pending_forces_this_timestep = " + pending_forces_this_timestep);

                    //System.out.println("pending_torque_this_timestep = " + pending_torque_this_timestep);
                    pending_torque_this_timestep += M_Vect2D.GET(localForcePos).rotate(rotation).cross_discard(force);
                    //System.out.println("pending_torque_this_timestep = " + pending_torque_this_timestep);
                    break;
                case MANUAL:
                    pending_external_forces.add(force);
                    pending_external_torque +=  M_Vect2D.GET(localForcePos).rotate(rotation).cross_discard(force);
                    break;
            }


        }
    }

    private void applyGravityForce(final I_Vect2D grav){

        if (canApplyThisForce(FORCE_SOURCE.ENGINE)){

            pending_forces_this_timestep.add(Vect2DMath.MULTIPLY(grav, getMass()));

            pending_torque_this_timestep += inertia * M_Vect2D.GET(getShape().getLocalCentroid()).rotate(rotation).cross_discard(grav);

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
            switch (source){
                case ENGINE:
                    pending_forces_this_timestep.add(force);
                    break;
                case MANUAL:
                    pending_external_forces.add(force);
                    break;
            }
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

        if (canApplyThisForce(source) && force.isNotZero()) {
            switch (source){
                case ENGINE:
                    pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));

                    pending_torque_mid_timestep += M_Vect2D.GET(localForcePos).cross_discard(force);
                    break;
                case MANUAL:
                    pending_external_forces.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));

                    pending_external_torque += M_Vect2D.GET(localForcePos).cross_discard(force);
                    break;
            }

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
        if (canApplyThisForce(source) && force.isNotZero()){
            switch (source){
                case ENGINE:
                    pending_forces_mid_timestep.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
                case MANUAL:
                    pending_external_forces.add_discardOther(Vect2DMath.DIVIDE_M(force, mass));
            }
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

        if (canApplyThisForce(source) && inertia > 0){
            switch (source){
                case ENGINE:
                    pending_torque_this_timestep += torque;
                    break;
                case MANUAL:
                    pending_external_torque += torque;
                    break;
            }
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

        if (canApplyThisForce(source) && inertia > 0){
            switch (source){
                case ENGINE:
                    pending_torque_mid_timestep += torque;
                    break;
                case MANUAL:
                    pending_external_torque += torque;
                    break;
            }
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
    public void applyHitSomethingStatic(I_Vect2D localCollidePos, I_Vect2D norm, double jImpulse) {
        if (canApplyThisForce(FORCE_SOURCE.ENGINE)) {
            //System.out.println("CrappyBody.applyHitSomethingStatic");
            //System.out.println("localCollidePos = " + localCollidePos + ", norm = " + norm + ", jImpulse = " + jImpulse);

            //System.out.println("tempVel = " + velocity);
            if (canMoveLinearly) {
                velocity.addScaled(norm, jImpulse / getMass());
            }
            //System.out.println("tempVel = " + velocity);
            //System.out.println("tempAngVelocity = " + angVelocity);
            if (canRotate) {
                angVelocity += (jImpulse * localCollidePos.cross(norm)) / getMomentOfInertia();
            }
            //System.out.println("tempAngVelocity = " + angVelocity);
        }
    }


    /**
     * Obtains name of crappybody
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Is this actually tangible (able to give/receive collision forces)? intangible objects can still collide, but not
     * with forces.
     *
     * @return true if yes, false if no
     */
    @Override
    public boolean isTangible() {
        return tangible;
    }

    /**
     * Is this actually active? (ie not dormant, still affected by all physics, can collide)
     *
     * @return true if is active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void __addConnector_internalPlsDontUseManually(final CrappyConnectorBodyInterface c) {
        myConnections.add(c);
        if (!c.canBodiesCollide()) {
            refreshBodiesICannotCollideWith();
        }
    }

    @Override
    public void __removeConnector_internalPlsDontUseManually(final CrappyConnectorBodyInterface c) {
        myConnections.remove(c);
        if (!c.canBodiesCollide()) {
            refreshBodiesICannotCollideWith();
        }
    }

    /**
     * Refreshes the map of UUIDs for the bodies that this object cannot collide with.
     */
    private void refreshBodiesICannotCollideWith(){
        synchronized (idsICannotCollideWith) {
            idsICannotCollideWith.clear();
            for (CrappyConnectorBodyInterface c : myConnections) {
                if (!c.canBodiesCollide()) {
                    final IHaveIdentifier o = c.getOtherBody(this);
                    idsICannotCollideWith.putIfAbsent(o.getID(), o);
                }
            }
        }
    }

    public DrawableCrappyShape getDrawableShape() {
        return getShape();
    }


    /**
     * Call this before the first euler substep.
     * Will immediately call {@link #euler_substep(double)} with subDeltaT
     * @param deltaT raw deltaT.
     * @param gravity constant gravitational vector (a constant linear acceleration)
     * @param subDeltaT substep deltaT.
     */
    public void first_euler_sub_update(final double deltaT, final I_Vect2D gravity, final double subDeltaT){


        switch (eus){
            case NOTHING:
            case DONE:
                eus = EULER_UPDATE_STATE.PRIOR_SUB_UPDATE;
                break;
            default:
                System.out.println(this);
                throw new AssertionError(
                        "Expected euler update state of 'NOTHING' or 'FINALIZED', was in " + eus + "!"
                );
        }

        switch (bodyType){
            case STATIC:
                // if it's static, it won't be updating, simple
                return;
            case DYNAMIC:
                // we apply gravity to the body
                //pending_forces_this_timestep.add(gravity);
                //applyForce(Vect2DMath.MULTIPLY(gravity, mass), getShape().getCentroid(), FORCE_SOURCE.ENGINE);
                applyGravityForce(gravity);

                //pending_torque_this_timestep += angVelocity;

            default:


                tempPosition.set(position);
                tempRot.set(rotation);

                if (canMoveLinearly) {
                    pending_forces_this_timestep.mult(1/mass);
                    tempVel.set(velocity);
                    if (linearDrag != 0) {
                        // and we apply the drag to the body if appropriate
                        tempVel.mult(1 - linearDrag);
                    }
                    tempVel.addScaled(pending_forces_this_timestep, deltaT);
                } else {
                    tempVel.reset();
                }
                if (canRotate) {
                    pending_torque_this_timestep = Vect2DMath.RETURN_X_IF_NOT_FINITE(
                            pending_torque_this_timestep/inertia, 0
                    );
                    tempAngVelocity = angVelocity * (1-angularDrag);
                    tempAngVelocity += (pending_torque_this_timestep * deltaT);
                } else {
                    tempAngVelocity = 0;
                }

                break;
        }

        shape.getAssert().timestepStartUpdate();

        euler_substep(subDeltaT);
    }

    public void euler_substep(final double substepDeltaT){

        switch (eus){
            case PRIOR_SUB_UPDATE:
                eus = EULER_UPDATE_STATE.SUB_UPDATING;
            case SUB_UPDATING:
                break;
            default:
                throw new AssertionError(
                        "Expected euler update state of 'PRIOR_SUB_UPDATE' or 'SUB_UPDATING', was in " + eus + "!"
                );
        }

        if (bodyType == CRAPPY_BODY_TYPE.STATIC){
            return;
        }

        if (canMoveLinearly) {
            tempPosition.addScaled(
                    tempVel, substepDeltaT
            );
            tempVel.addScaled(pending_forces_this_timestep, substepDeltaT);
            tempVel.addScaled(pending_forces_mid_timestep.mult(1/mass), substepDeltaT);
            tempVel.addScaled(pending_external_forces, substepDeltaT);
        }

        pending_forces_mid_timestep.reset();


        if (canRotate) {
            tempRot.rotateBy(tempAngVelocity * substepDeltaT);

            if (inertia != 0) {
                tempAngVelocity += (pending_torque_this_timestep * substepDeltaT);
                tempAngVelocity += ((pending_torque_mid_timestep / inertia) * substepDeltaT);
                tempAngVelocity += (pending_external_torque * substepDeltaT);
            }

            pending_torque_mid_timestep = 0;
        }

        intermediateTransform.update();

        shape.getAssert().midTimestepUpdate();

    }

    public void doneEulers(){
        eus = EULER_UPDATE_STATE.DONE;
    }

    @Override
    public void handleStuffBeforeFirstEulerUpdate() {
        pending_external_torque /= inertia;
        pending_external_forces.divide(mass);
    }

    @Override
    public void resolveStuffAfterLastEulerUpdate() {
        pending_external_forces.reset();
        pending_external_torque = 0;
    }

    @Override
    public void $_$_$__discard_INTERNAL_USE_ONLY_DO_NOT_USE_YOURSELF_EVER_SERIOUSLY_DONT_GRRR() {
        discarded = true;
    }


    public void clearAllPendingForces(){

        pending_forces_mid_timestep.reset();
        pending_torque_mid_timestep = 0;
        pending_forces_this_timestep.reset();
        pending_torque_this_timestep = 0;
    }


    /**
     * Makes all the temporary changes to velocity and position (done during sub-updates) not temporary.
     *
     */
    public void applyAllTempChanges(){

        if (eus == EULER_UPDATE_STATE.SUB_UPDATING) {
            eus = EULER_UPDATE_STATE.DONE;
        } else {
            throw new AssertionError("Expected state of 'SUB_UPDATING', was in " + eus + "!");
        }

        //System.out.println("name = " + name);
        //System.out.println("tempAngVelocity = " + tempAngVelocity);
        //System.out.println("angVelocity = " + angVelocity);
        //System.out.println("\n");
        if (tempAngVelocity != 0){
            double a = 0;
            double b = a+3;
        }

        velocity = new Vect2D(tempVel);
        lastPosition = position;
        position = new Vect2D(tempPosition);
        angVelocity = tempAngVelocity;
        lastRotation = rotation;
        rotation = new Rot2D(tempRot);

        //and now making sure the temps are 100% identical to the actuals

        tempVel.set(velocity);
        tempPosition.set(position);
        tempRot.set(rotation);
        tempAngVelocity = angVelocity;

        // and clearing any non-applied forces

        clearAllPendingForces();

        intermediateTransform.update();

        shape.getAssert().timestepEndUpdate();


    }

    @Override
    public void crappilyRenderBody(I_CrappilyDrawStuff renderer) {
        shape.getAssert().drawCrappily(renderer);
    }

    @Override
    public void updateDrawables() {
        shape.getAssert().updateDrawables();
    }

    @Override
    public void drawCrappily(I_CrappilyDrawStuff renderer) {
        shape.getAssert().drawCrappily(renderer);
    }

    @Override
    public CrappyBody getBody() {
        return this;
    }

    @Override
    public I_Transform getTempTransform() {
        return intermediateTransform;
    }

    public boolean wasClicked(){ return callbackHandler.wasClicked(); }

    @Override
    public String toString() {
        return "CrappyBody{" +
                "position=" + position +
                ", tempDisplacement=" + tempDisplacement +
                ", tempPosition=" + tempPosition +
                ", velocity=" + velocity +
                ", tempVelChange=" + tempVelChange +
                ", tempVel=" + tempVel +
                ", rotation=" + rotation +
                ", tempRot=" + tempRot +
                ", tempRotChange=" + tempRotChange +
                ", shape=" + shape +
                ", angVelocity=" + angVelocity +
                ", tempAngVelocityChange=" + tempAngVelocityChange +
                ", tempAngVelocity=" + tempAngVelocity +
                ", torque=" + torque +
                ", mass=" + mass +
                ", inertia=" + inertia +
                ", pending_forces_this_timestep=" + pending_forces_this_timestep +
                ", pending_torque_this_timestep=" + pending_torque_this_timestep +
                ", pending_forces_mid_timestep=" + pending_forces_mid_timestep +
                ", pending_torque_mid_timestep=" + pending_torque_mid_timestep +
                ", bodyType=" + bodyType +
                ", intermediateTransform=" + intermediateTransform +
                ", linearDrag=" + linearDrag +
                ", angularDrag=" + angularDrag +
                ", restitution=" + restitution +
                ", active=" + active +
                ", pendingActive=" + pendingActive +
                ", tangible=" + tangible +
                ", pendingIntangbility=" + pendingIntangbility +
                ", canMoveLinearly=" + canMoveLinearly +
                ", canRotate=" + canRotate +
                ", pendingCanMoveLinearly=" + pendingCanMoveLinearly +
                ", pendingCanRotate=" + pendingCanRotate +
                ", pendingRemoval=" + pendingRemoval +
                ", combinedBitsOfOtherObjectsCollidedWith=" + combinedBitsOfOtherObjectsCollidedWith +
                ", myBodyTagBits=" + myBodyTagBits +
                ", tagsICanCollideWithBitmask=" + tagsICanCollideWithBitmask +
                ", callbackHandler=" + callbackHandler +
                ", userData=" + userData +
                ", name='" + name + '\'' +
                ", eus=" + eus +
                '}';
    }

    public static CrappyBodyCreator GET_CREATOR(){
        return new CrappyBodyCreator();
    }


    public void overwriteVelocityAfterCollision(final I_Vect2D newVel, final double newAngVel, final FORCE_SOURCE overwriteSource){



        if (canApplyThisForce(overwriteSource)) {
            velocity = newVel.toVect2D();
            tempVel.set(newVel);
            if (Double.isFinite(angVelocity) && inertia > 0) {
                angVelocity = newAngVel;
                tempAngVelocity = newAngVel;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrappyBody that = (CrappyBody) o;
        return Double.compare(that.getMass(), getMass()) == 0 && Double.compare(that.inertia, inertia) == 0 && myBodyTagBits == that.myBodyTagBits && tagsICanCollideWithBitmask == that.tagsICanCollideWithBitmask && getShape().equals(that.getShape()) && getBodyType() == that.getBodyType() && getName().equals(that.getName()) && uniqueIdentifier.equals(that.uniqueIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShape(), getMass(), inertia, getBodyType(), myBodyTagBits, tagsICanCollideWithBitmask, getName(), uniqueIdentifier);
    }

    @Override
    public UUID getID() {
        return uniqueIdentifier;
    }



    /**
     * Inspired somewhat by JBox2D's BodyDef class,
     * intended to make it slightly less painful to create a CrappyBody.
     */
    public static class CrappyBodyCreator {

        public CrappyWorld world = null;

        public Vect2D pos = Vect2D.ZERO;
        public Vect2D vel = Vect2D.ZERO;

        public Rot2D angle = Rot2D.IDENTITY;

        public double angVel = 0;

        public double mass = 1;

        public CrappyBody.CRAPPY_BODY_TYPE bodyType = null;

        public double linearDrag = 0.9;

        public double angularDrag = 0.9;

        public double restitution = 1.2;

        public static int DEFAULT_THIS_BODY_TAGS_BITMASK = 0;

        public int thisBodyTagsAsBitmask = DEFAULT_THIS_BODY_TAGS_BITMASK;

        public int tagsOfBodiesThatCanBeCollidedWith = -1;

        public static final CrappyCallbackHandler defaultCallbackHandler = new DEFAULT_CALLBACK_HANDLER();

        public CrappyCallbackHandler handler = defaultCallbackHandler;

        public Object userData = new Object();

        public String name = "";

        public boolean isTangible = true;
        public boolean canMoveLinearly = true;
        public boolean canRotate = true;

        public CrappyBodyCreator(){}


        public static class DEFAULT_CALLBACK_HANDLER implements CrappyCallbackHandler { }


        public CrappyBody makeBody(){

            return new CrappyBody(
                    pos,
                    vel,
                    angle,
                    angVel,
                    mass,
                    restitution,
                    angularDrag,
                    linearDrag,
                    bodyType,
                    thisBodyTagsAsBitmask,
                    tagsOfBodiesThatCanBeCollidedWith,
                    handler,
                    userData,
                    name,
                    isTangible,
                    canMoveLinearly,
                    canRotate
            );

        }

    }

    public static CrappyBody EDGE_BODY_MAKER(
            final Vect2D start, final Vect2D end, final double depth,
            final double restitution, final int myTags, final int collideWithTags,
            final CrappyCallbackHandler callback, final Object userData, final String name
    ){
        final CrappyBody c = new CrappyBody(
                start,
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                restitution,
                0,
                0,
                CRAPPY_BODY_TYPE.STATIC,
                myTags,
                collideWithTags,
                callback,
                userData,
                name,
                true,
                false,
                false
                );
        new CrappyEdge(c, Vect2D.ZERO, Vect2DMath.MINUS(end, start), depth);
        return c;

    }

    public static CrappyBody LINE_BODY_MAKER(
            final Vect2D start, final Vect2D end,
            final double restitution, final int myTags, final int collideWithTags,
            final CrappyCallbackHandler callback, final Object userData, final String name
    ){
        final CrappyBody c = new CrappyBody(
                start,
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                restitution,
                0,
                0,
                CRAPPY_BODY_TYPE.STATIC,
                myTags,
                collideWithTags,
                callback,
                userData,
                name,
                true,
                false,
                false
        );
        new CrappyLine(c, start, end);
        return c;

    }

}





