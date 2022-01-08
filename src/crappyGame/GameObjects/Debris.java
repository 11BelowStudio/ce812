package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.collisions.CrappyPolygon;
import crappy.math.I_Rot2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

public class Debris implements GameObject, Updatable {

    final static int COLLIDES_WITH = BodyTagEnum.COMBINE_BITMASKS(BodyTagEnum.WORLD, BodyTagEnum.DECORATIVE_PARTICLE);

    final CrappyBody body;

    double despawnTimer = 2 + (Math.random() * 3);

    boolean stillExists = true;

    public Debris(Vect2D pos, Vect2D vel, I_Rot2D rot, double angVel){

        stillExists = true;
        body = new CrappyBody(
                pos,
                vel,
                rot.toRot2D(),
                angVel,
                0.15 + (Math.random() * 0.5),
                0.95,
                0.001,
                0.0001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                BodyTagEnum.DECORATIVE_PARTICLE.getBitmask(),
                COLLIDES_WITH,
                CrappyBody.CrappyBodyCreator.defaultCallbackHandler,
                new Object(),
                "debris",
                true,
                true,
                true
        );

        CrappyPolygon.POLYGON_FACTORY_REGULAR(
                body,3 + (int)(Math.random() * 4.0), 0.05 + (Math.random() * 0.1)
        );

    }

    @Override
    public Vect2D getPos() {
        return body.getPos();
    }

    @Override
    public Rot2D getRot() {
        return body.getRot();
    }

    @Override
    public CrappyBody getBody() {
        return body.getBody();
    }

    @Override
    public Vect2D getVel() {
        return body.getVel();
    }

    @Override
    public void update(double delta) {
        despawnTimer -= delta;
        if (despawnTimer < 0){
            body.setMarkForRemoval(true);
            stillExists = false;
        }
    }

    public boolean isStillExists(){
        return stillExists;
    }
}
