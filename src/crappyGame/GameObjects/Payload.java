package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyWorld;
import crappy.I_View_CrappyBody;
import crappy.collisions.CrappyCircle;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

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

    private static final int collidesWithBits = BodyTagEnum.COMBINE_BITMASKS(BodyTagEnum.WORLD, BodyTagEnum.FINISH_LINE);

    public Payload(Vect2D startPos, CrappyWorld w){
        this.startPos = startPos;
        respawn(w);
    }

    @Override
    public void respawn(CrappyWorld w) {

        if (state != BALL_STATE.DED){
            body.setMarkForRemoval(true);
            w.removeBody(body);
        }
        body = new CrappyBody(
                startPos,
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0.3,
                0,
                0,
                0.75,
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

    }

    public void setBeingTowed(final boolean towed){
        if (towed){
            body.setFrozen(false);
            state = BALL_STATE.BEING_TOWED;
        } else if (state != BALL_STATE.DED){
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
        } else if (BodyTagEnum.WORLD.anyMatchInBitmasks(collidedWithBits)){
            this.state = BALL_STATE.DED;
            body.setMarkForRemoval(true);
        }
    }

}
