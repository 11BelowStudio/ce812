package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CrappyCircle extends A_CrappyShape {


    public CrappyCircle(final CrappyBody_Shape_Interface body, final double radius) {
        super(CRAPPY_SHAPE_TYPE.CIRCLE, body);
        this.radius = radius;

        updateShape();

    }

    @Override
    public Crappy_AABB getBoundingBox() {
        return aabb;
    }

    @Override
    public Crappy_AABB updateShape() {
        aabb.update_aabb_circle(body.getPos(), radius);
        return aabb;
    }
}
