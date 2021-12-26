package crappy.collisions;

import crappy.CrappyBody;
import crappy.CrappyBody_Shape_Interface;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

public final class CrappyCollisionMath {

    /**
     * no constructing >:(
     */
    private CrappyCollisionMath(){}

    public static void COLLIDE_CIRCLE_CIRCLE(final I_CrappyCircle a, final I_CrappyCircle b, final double deltaT){

        // we find out when the collision actually happened
        final double t = GET_EXACT_COLLISION_TIME_CIRCLE_CIRCLE(a, b);

        // if the collision didn't happen in this timestep, we ignore it.
        if (t > 0 || t < -deltaT){
            return;
        }

        // move a back to where it was when it collided
        final Vect2D aCollidePos = a.getPos().addScaled(a.getVel(),t);

        // move b back to where it was when it collided
        final Vect2D bCollidePos = b.getPos().addScaled(b.getVel(),t);

        // calculate the AB vector (a to b)
        final Vect2D aToB = Vect2DMath.VECTOR_BETWEEN(aCollidePos, bCollidePos);

        // normalizing aToB to get collision normal
        final Vect2D norm = aToB.norm();

        // finding the relative size of the radius of a compared to the radius of b,
        // as a fraction of aRadius/totalRadii
        final double aRadiusRatio = a.getRadius() / (a.getRadius() + b.getRadius());

        // jb = (e+1) * (Ua.norm - Ub.norm) / (1/Ma + 1/Mb)

        // e: average of the restitutions of a and b

        final double jb =
                (
                    (
                        ((a.getRestitution() + b.getRestitution())/2.0)+1
                    ) * (
                        a.getVel().dot(norm) - b.getVel().dot(norm)
                    )
                ) / ((1/a.getMass()) + (1/b.getMass()));

        // vb = ub + norm*(jb/mb)
        //b.setVel(b.getVel().addScaled(norm, jb/b.getMass()));
        b.getBody().applyForce(
                b.getVel().addScaled(norm, jb),
                Vect2D.ZERO.lerp(norm, -1 + aRadiusRatio),
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        // va = ua + norm * (-jb/ma)
        //a.setVel(a.getVel().addScaled(norm, -jb/a.getMass()));

        a.getBody().applyForce(
                a.getVel().addScaled(norm, -jb),
                Vect2D.ZERO.lerp(norm, aRadiusRatio),
                CrappyBody.FORCE_SOURCE.ENGINE
        );


    }


    /**
     * Finds exact timestep where CrappyCircle A and CrappyCircle B actually collide with each other
     * @param a the first CrappyCircle
     * @param b the other CrappyCircle
     * @return the exact T where a collides with b
     */
    public static double GET_EXACT_COLLISION_TIME_CIRCLE_CIRCLE(final I_CrappyCircle a, final I_CrappyCircle b){

        // A moves according to  x = xa + Va(t)
        // B moves according to  x = xb + Vb(t)
        // A->B: (xb - xa) + (Vb - Va)t
        //     : c + vt
        //         c = xb - xa
        //         v = vb - va
        // ||c + vt|| = d
        //      where d = radius a + radius b
        //      find t.

        // ||c + vt|| = d
        // (c+vt).(c+vt) = d^2
        // c.c + (2c.v)t + (v.v)t^2 = d^2
        // (v.v)t^2 + (2c.v)t + (c.c - d^2) = 0

        // find t with quadratic formula (choosing negative root), substituting in
        //  a (v.v)
        //  b (c.v)
        //  c (c.c - d^2)

        // (-b +- sqrt(b^2 - 4ac))/2a


        final Vect2D c = Vect2DMath.VECTOR_BETWEEN(a.getPos(), b.getPos());
        final Vect2D v = Vect2DMath.MINUS(b.getVel(), a.getVel());
        final double d = a.getRadius() + b.getRadius();

        final double vv = v.dot(v);
        final double cv = c.dot(v);
        final double ccd = c.dot(c) - Math.pow(d,2);

        // sqrt(b^2 - 4ac)
        final double theThingThatHasThePlusMinus = Math.sqrt(Math.pow(cv,2) - (vv * ccd));

        return Math.min(
                (-cv - theThingThatHasThePlusMinus)/vv,
                (-cv + theThingThatHasThePlusMinus)/vv
        );
    }


    public static void COLLIDE_CIRCLE_EDGE(final I_CrappyCircle circle, final CrappyEdge edge){
        // TODO: this.
    }


    public static void COLLIDE_TWO_LOCAL_POINTS(
            final Vect2D aPoint, final CrappyBody_Shape_Interface aBody,
            final Vect2D bPoint, final CrappyBody_Shape_Interface bBody,
            final Vect2D aToB_world
    ){

        // TODO finish this

    }


    public static void COLLIDE_CIRCLE_LINE(final CrappyCircle c, final CrappyLine l) {

        final Vect2D ap = Vect2DMath.MINUS(c.getBodyTransform().getPos(), l.getWorldStart());

        // we obtain the signed distance between the line itself and the circle
        double temp = ap.dot(l.getWorldNorm());

        final boolean flipNormal = (temp < 0);

        // and also remove the sign
        final double distBetweenCircleMidpointAndLine = Math.abs(temp);

        if (distBetweenCircleMidpointAndLine > c.getRadius()){
            return; // if the circle's too far away, we stop what we're doing.
        }

        // here's a copy of the tangent for future reference
        final Vect2D tangent = l.getWorldProj().norm();

        // distance down the line of where the circle and the line collide
        temp = ap.dot(tangent);

        if (temp < 0 || temp > l.getLength()){
            return; // if it's out of bounds, skip it and move on
        }

        // we get a copy of the line's normal, but ensuring it points 'out' of the wall.
        final Vect2D tempNorm = M_Vect2D.GET(l.getWorldNorm()).mult(
                flipNormal? -1 : 1
        ).finished();





        //Points P (x,y) on a line defined by two points P1 (x1,y1) and P2 (x2,y2) is described by
        //P = P1 + u (P2 - P1)
        //
        //or in each coordinate
        //x = x1 + u (x2 - x1)
        //y = y1 + u (y2 - y1)
        //
        //A sphere centered at P3 (x3,y3) with radius r is described by
        //(x - x3)2 + (y - y3)2  = r2
        //
        //Substituting the equation of the line into the sphere gives a quadratic equation of the form
        //a u2 + b u + c = 0
        //
        //where:
        //a = (x2 - x1)2 + (y2 - y1)2
        //
        //b = 2[ (x2 - x1) (x1 - x3) + (y2 - y1) (y1 - y3)]
        //
        //c = x32 + y32 + x12 + y12 - 2[x3 x1 + y3 y1] - r2
        //
        // can be solved like a quadratic
        //
        // The exact behaviour is determined by the expression within the square root
        //
        //  b * b - 4 * a * c
        //
        //    If this is less than 0 then the line does not intersect the sphere.
        //
        //    If it equals 0 then the line is a tangent to the sphere intersecting it at one point, namely at u = -b/2a.
        //
        //    If it is greater then 0 the line intersects the sphere at two points.
        //
        //To apply this to two dimensions, that is, the intersection of a line and a circle simply remove the z component from the above mathematics.


        // For a line segment between P1 and P2 there are 5 cases to consider.
        //
        //    Line segment doesn't intersect and on outside of sphere,
        //    in which case both values of u will either be less than 0 or greater than 1.
        //
        //    Line segment doesn't intersect and is inside sphere,
        //    in which case one value of u will be negative and the other greater than 1.
        //
        //    Line segment intersects at one point,
        //    in which case one value of u will be between 0 and 1 and the other not.
        //
        //    Line segment intersects at two points,
        //    in which case both values of u will be between 0 and 1.
        //
        //    Line segment is tangential to the sphere,
        //    in which case both values of u will be the same and between 0 and 1.
        //
        //When dealing with a line segment it may be more efficient to first determine
        // whether the line actually intersects the sphere or circle.
        // This is achieved by noting that the closest point on the line through P1P2 to
        // the point P3 is along a perpendicular from P3 to the line.
        //
        // In other words if P is the closest point on the line then
        //(P3 - P) dot (P2 - P1) = 0
        //
        //Substituting the equation of the line into this
        //[P3 - P1 - u(P2 - P1)] dot (P2 - P1) = 0
        //
        //Solving the above for u =
        //(x3 - x1)(x2 - x1) + (y3 - y1)(y2 - y1)
        //-----------------------------------------------------------
        //(x2 - x1)(x2 - x1) + (y2 - y1)(y2 - y1)
        //
        //If u is not between 0 and 1 then the closest point is not between P1 and P2
        // Given u, the intersection point can be found, it must also be less than the radius r.
        //
        // If these two tests succeed then the earlier calculation of the actual intersection point can be applied.


        final Vect2D lineStart = l.getWorldStart();
        final Vect2D lineEnd = l.getWorldEnd();



    }
}
