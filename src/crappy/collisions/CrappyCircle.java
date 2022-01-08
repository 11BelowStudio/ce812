package crappy.collisions;

import crappy.CrappyBody_ShapeSetter_Interface;
import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.graphics.DrawableCrappyShape;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.M_Vect2D;
import crappy.math.Vect2DMath;
import crappy.math.Vect2D;

public class CrappyCircle extends A_CrappyShape implements I_CrappyCircle, DrawableCrappyShape.DrawableCircle {


    private Vect2D drawableRot;

    public CrappyCircle(final CrappyBody_ShapeSetter_Interface body, final double radius) {
        super(CRAPPY_SHAPE_TYPE.CIRCLE, Vect2D.ZERO, body, radius);


        body.__setShape__internalDoNotCallYourselfPlease(
                this, Vect2DMath.CIRCLE_MOMENT_OF_INERTIA(radius, body.getMass())
        );
        updateShape(body);

        System.out.println("CrappyCircle.CrappyCircle: " + body.getName());
        System.out.println("getPos() = " + getPos());

        timestepStartUpdate();
        midTimestepUpdate();
        timestepEndUpdate();
        timestepStartUpdate();
        midTimestepUpdate();
        timestepEndUpdate();


    }

    /**
     * Constructor for circles within other bodies (Does not attempt to set itself inside the body)
     * @param radius radius
     * @param s body
     */
    CrappyCircle(final double radius, final CrappyBody_Shape_Interface s){
        super(CRAPPY_SHAPE_TYPE.CIRCLE, Vect2D.ZERO, s, radius);
        updateShape(body);
    }

    @Override
    public CRAPPY_SHAPE_TYPE getShapeType() {
        return CRAPPY_SHAPE_TYPE.CIRCLE;
    }


    @Override
    public Crappy_AABB getBoundingBox() {
        return aabb;
    }

    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {
        thisFrameAABB.update_aabb_circle(rootTransform.getPos(), getRadius());
        return thisFrameAABB;
    }



    @Override
    public void drawCrappily(I_CrappilyDrawStuff renderer) {
        renderer.acceptCircle(this);
    }

    @Override
    public CrappyBody_Shape_Interface getBody(){
        return body;
    }

    @Override
    public Vect2D getPos() {
        return getBodyTransform().getPos();
    }

    @Override
    public Vect2D getVel() {
        return getBodyTransform().getVel();
    }

    public void updateDrawables() {
        super.updateDrawables();
        synchronized (drawableSyncer) {
            drawableRot = M_Vect2D.GET(getBodyTransform().getRot()).finished();
        }
    }

    public Vect2D getDrawableRot(){
        synchronized (drawableSyncer){
            return drawableRot;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
