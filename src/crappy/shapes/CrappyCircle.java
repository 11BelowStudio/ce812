package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.Vect2DMath;
import crappy.math.Vect2D;

public class CrappyCircle extends A_CrappyShape {


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
        aabb.update_aabb_circle(rootTransform.getPos().toVect2D(), radius);
        return aabb;
    }

    @Override
    public void updateFinalWorldVertices() {
        finalWorldVertices[0] = body.getPos();
    }
}
