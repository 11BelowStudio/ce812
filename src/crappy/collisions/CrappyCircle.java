package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.graphics.DrawableCrappyShape;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.Vect2DMath;
import crappy.math.Vect2D;

public class CrappyCircle extends A_CrappyShape implements I_CrappyCircle, DrawableCrappyShape.DrawableCircle {



    public CrappyCircle(final CrappyBody_Shape_Interface body, final double radius) {
        super(CRAPPY_SHAPE_TYPE.CIRCLE, Vect2D.ZERO, body, radius);
        this.radius = radius;

        body.setMomentOfInertia(Vect2DMath.CIRCLE_MOMENT_OF_INERTIA(radius, body.getMass()));

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
        thisFrameAABB.update_aabb_circle(rootTransform.getPos().toVect2D(), getRadius());
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


}
