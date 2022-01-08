package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyWorld;
import crappy.I_View_CrappyBody;
import crappy.collisions.CrappyCircle;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.models.IRecieveDebris;

import static crappyGame.GameObjects.Payload.BALL_STATE.BEING_TOWED;
import static crappyGame.GameObjects.Payload.BALL_STATE.DED;

public class Payload implements CrappyCallbackHandler, Respawnable, GameObject {



    public enum BALL_STATE{
        WAITING,
        BEING_TOWED,
        DROPPED,
        DED,
        SUCCESS
    }

    private BALL_STATE state = BALL_STATE.DED;

    private CrappyBody body;

    private final Vect2D startPos;

    private Vect2D dedPos;

    private static final int collidesWithBits = BodyTagEnum.COMBINE_BITMASKS(BodyTagEnum.WORLD, BodyTagEnum.FINISH_LINE, BodyTagEnum.SHIP);

    final IRecieveDebris debrisGoesHere;

    public Payload(Vect2D startPos, CrappyWorld w, IRecieveDebris d){
        this.startPos = startPos;
        dedPos = startPos;
        respawn(w);
        debrisGoesHere = d;
    }

    @Override
    public boolean respawn(CrappyWorld w) {

        if (state != BALL_STATE.DED || (body != null && !body.isDiscarded())){
            body.setMarkForRemoval(true);
            body.setActive(false);
            w.removeBody(body);
        }
        body = new CrappyBody(
                startPos,
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0.1,
                0.8,
                0,
                0.00025,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                BodyTagEnum.PAYLOAD.bitmask,
                collidesWithBits,
                this,
                new Object(),
                "payload",
                false,
                false,
                false
        );
        new CrappyCircle(body, 0.2);

        w.addBody(body);
        state = BALL_STATE.WAITING;
        return true;

    }

    public void setBeingTowed(final boolean towed){
        if (state == BALL_STATE.DED){
            return;
            // can't do shit when this is already dead
        } else if (towed){
            body.setFrozen(false);
            body.setTangibility(true);
            body.overwriteVelocityAfterCollision(Vect2D.ZERO, 0, CrappyBody.FORCE_SOURCE.MANUAL);
            state = BEING_TOWED;
        } else if (state == BEING_TOWED) {
            state = BALL_STATE.DROPPED;
        }
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

    public BALL_STATE getState(){
        return state;
    }

    public void setState(final BALL_STATE s){
        state = s;
    }

    public boolean isAlive(){
        return state != BALL_STATE.DED;
    }

    public boolean canBeLerpedTo(){
        switch (state){
            case SUCCESS:
            case DROPPED:
            case BEING_TOWED:
            case DED:
                return true;
            default:
                return false;
        }
    }

    public Vect2D getLerpPos(){
        if (state == DED){
            return dedPos;
        }
        return getPos();
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
        if (BodyTagEnum.FINISH_LINE.anyMatchInBitmasks(collidedWithBits)){
            this.state = BALL_STATE.SUCCESS;
        } else if (state != BALL_STATE.DED && state != BALL_STATE.SUCCESS && BodyTagEnum.WORLD.anyMatchInBitmasks(collidedWithBits)){
            debrisGoesHere.addDebris(getPos(), getRot(), getVel(), 3 + (int)(Math.random() * 5), IRecieveDebris.DebrisSource.PAYLOAD);
            this.state = BALL_STATE.DED;
            this.dedPos = getPos();
            body.setMarkForRemoval(true);
        } else if (state == BALL_STATE.WAITING && BodyTagEnum.SHIP.anyMatchInBitmasks(collidedWithBits)){
            state = BALL_STATE.DROPPED;
            body.setFrozen(false);
            body.setTangibility(true);
            body.overwriteVelocityAfterCollision(Vect2D.ZERO, 0, CrappyBody.FORCE_SOURCE.MANUAL);
        }
    }

}
