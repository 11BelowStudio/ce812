package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyWorld;
import crappy.I_View_CrappyBody;
import crappy.collisions.CrappyPolygon;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappyGame.Controller.IAction;
import crappyGame.assets.SoundManager;
import crappyGame.models.IRecieveDebris;

public class Spaceship implements CrappyCallbackHandler, Respawnable, GameObject {


    public CrappyBody body;

    final Vect2D startPos;

    Vect2D dedPos;

    Vect2D safePos;

    final IRecieveDebris debrisGoesHere;

    final static int SPACESHIP_CAN_COLLIDE_WITH = BodyTagEnum.COMBINE_BITMASKS(BodyTagEnum.WORLD, BodyTagEnum.PAYLOAD);

    final static Vect2D THRUST_FORCE = new Vect2D(0, 7.6);

    final static double STEER_RATE = 2 * Math.PI;

    boolean stillAlive = true;

    boolean justDied = false;

    public enum SHIP_STATE{
        JUST_RESPAWNED,
        GOING_IN,
        TOWING,
        DONE,
        FREEFALL_OF_SHAME,
        DEAD
    }

    SHIP_STATE state = SHIP_STATE.DEAD;


    private static final Vect2D[] SHIP_SHAPE = Vect2DMath.OFFSET_VECTORS_SO_CENTROID_IS_AT_ZERO_INTO_NEW_LIST(
            new Vect2D(0, 0.2), new Vect2D(-0.2, -0.2),
            new Vect2D(0, -0.1), new Vect2D( 0.2, -0.2)
    );


    /*
            new Vect2D[]{
            new Vect2D(0, 0.2), new Vect2D(-0.2, -0.2),
            new Vect2D(0, -0.1), new Vect2D( 0.2, -0.2)
    };
     */

    public Spaceship(Vect2D startPos, CrappyWorld world, IRecieveDebris d){


        this.startPos = startPos;
        respawn(world);
        debrisGoesHere = d;
        dedPos = startPos;
        safePos = startPos;
    }

    public SHIP_STATE getState(){
        return state;
    }

    public void setState(final SHIP_STATE s){
        state = s;
    }

    public Vect2D getPos(){

        return body.getPos();
    }

    @Override
    public Rot2D getRot() {
        return body.getRot();
    }

    public CrappyBody getBody(){
        return body;
    }

    @Override
    public Vect2D getVel() {
        return body.getVel();
    }

    public boolean isStillAlive(){
        return stillAlive;
    }

    public Vect2D getStartPos(){
        return startPos;
    }

    public Vect2D getPos_safe(){
        return body.getDrawableShape().getDrawablePos();
    }

    public Vect2D getDedPos(){
        return dedPos;
    }

    public void update(IAction act){

        switch (state){
            case JUST_RESPAWNED:
                if (act.anyDirectionPressed()){
                    state = SHIP_STATE.GOING_IN;
                    body.setFrozen(false);
                }
                break;
            case GOING_IN:
            case TOWING:
                if (!stillAlive){
                    state = SHIP_STATE.DEAD;
                    SoundManager.togglePlayThrusters(false);
                }
                if (act.isLeftHeld()){
                    body.applyTorque(STEER_RATE * body.getMomentOfInertia());
                }
                if (act.isRightHeld()){
                    body.applyTorque(-STEER_RATE * body.getMomentOfInertia());
                }

                if (act.isUpHeld()){
                    body.applyForce(THRUST_FORCE.rotate(body.getRot()));
                }

                SoundManager.togglePlayThrusters(act.isLeftHeld() | act.isRightHeld() | act.isUpHeld());
                break;
            case FREEFALL_OF_SHAME:
                SoundManager.togglePlayThrusters(false);
            case DEAD:
                SoundManager.togglePlayThrusters(false);
                if (justDied){
                    justDied = false;
                }
        }



    }

    public boolean respawn(final CrappyWorld w){

        if (state != SHIP_STATE.DEAD || (body != null && !body.isDiscarded())){
            return false;
        }
        body = new CrappyBody(
                startPos,
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                1,
                0,
                0.0001,
                0.0001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                BodyTagEnum.SHIP.bitmask,
                SPACESHIP_CAN_COLLIDE_WITH,
                this,
                new Object(),
                "ship",
                true,
                false,
                false
        );
        new CrappyPolygon(body, SHIP_SHAPE);
        //CrappyPolygon.POLYGON_FACTORY_REGULAR(body, 3, 0.25);
        w.addBody(body);
        state = SHIP_STATE.JUST_RESPAWNED;
        stillAlive = true;
        return true;
    }

    /**
     * The crappybody will call this with info about the other body when it notices that the other body has been
     * collided with.
     *
     * @param otherBody the other body.
     *
     * @implNote default implementation does nothing.
     */
    @Override
    public void collidedWith(I_View_CrappyBody otherBody) {
    }

    /**
     * The CrappyBody will call this after all collisions have been detected/computed. It gives the combined bitmask
     * bits for the bodies that the attached body collided with this frame
     *
     * @param collidedWithBits bitmask bits for all bodies that the attached body collided with
     *
     * @implNote default implementation does nothing
     */
    @Override
    public void acceptCollidedWithBitmaskAfterAllCollisions(int collidedWithBits) {
        if ((BodyTagEnum.WORLD.bitmask & collidedWithBits) > 0 || (BodyTagEnum.PAYLOAD.bitmask & collidedWithBits) > 0){
            if (stillAlive){
                debrisGoesHere.addDebris(getPos(), getRot(), getVel(), 2 + (int)(Math.random()*5), IRecieveDebris.DebrisSource.SHIP);
                dedPos = getPos();
                stillAlive = false;
                state = SHIP_STATE.DEAD;
            }
            body.setMarkForRemoval(true);
            stillAlive = false;
        }
    }

    public final void destroy(){
        if (body != null && !body.isDiscarded()) {
            body.setMarkForRemoval(true);
            body = null;
        }
    }

    /**
     * CrappyBody calls this if it gets removed from the physics world.
     */
    @Override
    public void bodyNoLongerExists() {
        stillAlive = false;
        state = SHIP_STATE.DEAD;
    }
}
