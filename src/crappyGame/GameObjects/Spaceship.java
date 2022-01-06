package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyWorld;
import crappy.I_View_CrappyBody;
import crappy.collisions.CrappyPolygon;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.Controller.IAction;
import crappyGame.assets.SoundManager;

public class Spaceship implements CrappyCallbackHandler, Respawnable, GameObject {


    private final CrappyBody.CrappyBodyCreator spaceshipMaker = new CrappyBody.CrappyBodyCreator();

    public CrappyBody body;

    final Vect2D startPos;

    final static int SPACESHIP_CAN_COLLIDE_WITH = BodyTagEnum.COMBINE_BITMASKS(BodyTagEnum.WORLD, BodyTagEnum.FINISH_LINE, BodyTagEnum.PAYLOAD);

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


    private static final Vect2D[] SHIP_SHAPE = new Vect2D[]{
            new Vect2D(0, 0.25), new Vect2D(-0.25, -0.25),
            new Vect2D(0, -0.1), new Vect2D( 0.25, -0.25)
    };

    public Spaceship(Vect2D startPos, CrappyWorld world){

        this.startPos = startPos;
        respawn(world);

    }

    public SHIP_STATE getState(){
        return state;
    }

    public void setState(SHIP_STATE s){
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


    public void update(IAction act){

        switch (state){
            case JUST_RESPAWNED:
                if (act.pressedAny()){
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
                    body.applyTorque(body.getMomentOfInertia() * STEER_RATE);
                }
                if (act.isRightHeld()){
                    body.applyTorque(body.getMomentOfInertia() * -STEER_RATE);
                }

                if (act.isUpHeld()){
                    body.applyForce(THRUST_FORCE.rotate(body.getRot()).mult(body.getMass()));
                }

                SoundManager.togglePlayThrusters(act.isLeftHeld() | act.isRightHeld() | act.isUpHeld());
                break;
            case FREEFALL_OF_SHAME:
                SoundManager.togglePlayThrusters(false);
            case DEAD:
                if (justDied){
                    SoundManager.playBoom();
                    justDied = false;
                }
        }


    }

    public void respawn(final CrappyWorld w){

        if (state == SHIP_STATE.FREEFALL_OF_SHAME){
            body.setMarkForRemoval(true);
            w.removeBody(body);
        } else if (state != SHIP_STATE.DEAD){
            return;
        }
        body = new CrappyBody(
                startPos,
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                2,
                1,
                0.0000001,
                0.00075,
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
        w.addBody(body);
        state = SHIP_STATE.JUST_RESPAWNED;
        stillAlive = true;
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
        CrappyCallbackHandler.super.collidedWith(otherBody);
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
        if ((BodyTagEnum.WORLD.bitmask & collidedWithBits) > 0){
            body.setMarkForRemoval(true);
        }
    }

    /**
     * CrappyBody calls this if it gets removed from the physics world.
     */
    @Override
    public void bodyNoLongerExists() {
        stillAlive = false;
    }
}
