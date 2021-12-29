package crappy.collisions;

import crappy.CrappyBody;
import crappy.CrappyBody_Shape_Interface;
import crappy.math.I_Vect2D;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

import java.util.Iterator;

public final class CrappyCollisionMath {

    /**
     * no constructing >:(
     */
    private CrappyCollisionMath(){}


    private static double AVG_RESTITUTION(final I_HaveRestitution a, final I_HaveRestitution b){
        return (a.getRestitution() + b.getRestitution())/2.0;
    }

    /**
     * Calculates the impulse denominator (for angular + linear velocity) for a given shape.
     * equal to
     * (1/m) + (((r x n) x r)/mInertia).norm
     * If mass/inertia are 0, replaces that part of the sum with 0 (returning 0 if they're both 0)
     * @param s the shape
     * @param rotatedLocalPos the r vector (collision location relative to world coords of local origin)
     * @param cNorm the collision normal vector
     * @return (1/m) + (((r x n) x r)/mInertia).norm
     */
    private static double CALCULATE_IMPULSE_DENOMINATOR_FOR_SHAPE(
            final I_CrappyShape s, final I_Vect2D rotatedLocalPos,  final I_Vect2D cNorm
    ){
        double massBit = 0;

        if (s.getMass() > 0){
            massBit = 1/ s.getMass();
        }

        double angleBit = 0;

        if (s.getMInertia() > 0){

            angleBit = M_Vect2D.GET(rotatedLocalPos)
                    .cross(
                            M_Vect2D.GET(rotatedLocalPos).cross_discard(cNorm), false
                    ).divide(s.getMInertia())
                    .dot_discard(cNorm);

        }

        return massBit + angleBit;
    }

    /**
     * Returns 1 if given 0, else returns v as-is.
     * @param v the value which may or may not be 0
     * @return v, or 1 if v is 0.
     */
    private static double RETURN_1_IF_0(final double v){
        if (Double.compare(v, 0) == 0){
            return 1;
        } else {
            return v;
        }
    }

    /**
     * We calculate the impulses to apply apply to each body, and actually apply them.
     * @param a first body
     * @param aLocalPos local pos in a of where the collision happened
     * @param b other body
     * @param bLocalPos local pos in b of where the collision happened
     * @param norm the collision normal
     */
    private static void CALCULATE_AND_APPLY_IMPULSE(
            final I_CrappyShape a, final I_Vect2D aLocalPos,
            final I_CrappyShape b, final I_Vect2D bLocalPos,
            final I_Vect2D norm
    ){

        // Both of these are equal to  Ԧ𝑣𝐶𝑂𝑀 + 𝜔 × Ԧ𝑟
        final M_Vect2D aLocalWorldVel = Vect2DMath.WORLD_VEL_OF_LOCAL_COORD_M(aLocalPos, a.getBodyTransform());
        final M_Vect2D bLocalWorldVel = Vect2DMath.WORLD_VEL_OF_LOCAL_COORD_M(bLocalPos, b.getBodyTransform());

        // jb = (e+1) * (Ua.norm - Ub.norm) / (1/Ma + 1/Mb)



        // jb =
        //      (e+1) * (A(Ԧ𝑣𝐶𝑂𝑀 + 𝜔 × Ԧ𝑟).norm - B(Ԧ𝑣𝐶𝑂𝑀 + 𝜔 × Ԧ𝑟).norm) /
        //      (1/Ma + 1/Mb) + (((ra x n) x ra)/aInertia).norm + (((rb x n) x rb)/bInertia).norm

        // e: average of the restitutions of a and b
        // we substitute the denominator with 1 if we get a value of 0 because overall masses/velocities are 0

        final double jb =(
            (
                    (
                            AVG_RESTITUTION(a,b)+1// (e+1)
                    ) * (
                            // (Ua.norm - Ub.norm)
                            //a.getVel().dot(norm) - b.getVel().dot(norm)
                            aLocalWorldVel.dot(norm) - bLocalWorldVel.dot(norm)
                    )
            ) / RETURN_1_IF_0(
                    CALCULATE_IMPULSE_DENOMINATOR_FOR_SHAPE(a, aLocalPos, norm) +
                    CALCULATE_IMPULSE_DENOMINATOR_FOR_SHAPE(b, bLocalPos, norm)
            )
        );




        // vb = ub + norm*(jb/mb)

        // HOWEVER, the division by mb happens within the applyForce method, so we don't do it here.
        // (also the applyForce method applies the appropriate force amount to hopefully cause the appropriate change
        // to the centre of mass' velocity)

        b.getBody().applyForce(
                Vect2DMath.MULTIPLY(norm, jb),
                //Vect2DMath.ADD_SCALED(bLocalWorldVel, norm, jb),
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        b.getBody().applyTorque(
                bLocalPos.cross(norm) * jb,
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        // va = ua + norm * (-jb/ma)
        //a.setVel(a.getVel().addScaled(norm, -jb/a.getMass()));

        a.getBody().applyForce(
                Vect2DMath.MULTIPLY(norm, -jb),
                //Vect2DMath.ADD_SCALED(aLocalWorldVel, norm, -jb),
                //Vect2D.ZERO.lerp(norm, aRadiusRatio),
                //aLocalPos,
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        a.getBody().applyTorque(
                aLocalPos.cross(norm) * -jb,
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        bLocalWorldVel.discard();
        aLocalWorldVel.discard();
    }


    public static boolean COLLIDE_CIRCLE_CIRCLE(final I_CrappyShape a, final I_CrappyShape b, final double deltaT) {

        // we find out when the collision actually happened
        final double t = GET_EXACT_COLLISION_TIME_CIRCLE_CIRCLE(a, b);

        // if the collision didn't happen in this timestep, we ignore it.

        if (t > 0 || t < -deltaT) {
            return false;
        }
        COLLIDE_CIRCLE_CIRCLE_KNOWN_TIME(a, b, t);
        return true;
    }

    public static void COLLIDE_CIRCLE_CIRCLE_KNOWN_TIME(
            final I_CrappyShape a, final I_CrappyShape b, final double knownTime
    ){

        // move a back to where it was when it collided
        final Vect2D aCollidePos = a.getPos().addScaled(a.getVel(),knownTime);

        // move b back to where it was when it collided
        final Vect2D bCollidePos = b.getPos().addScaled(b.getVel(),knownTime);

        // calculate the AB vector (a to b)
        final Vect2D aToB = Vect2DMath.VECTOR_BETWEEN(aCollidePos, bCollidePos);

        // normalizing aToB to get collision normal
        final Vect2D norm = aToB.norm();

        // finding the relative size of the radius of a compared to the radius of b,
        // as a fraction of aRadius/totalRadii
        final double aRadiusRatio = a.getRadius() / (a.getRadius() + b.getRadius());


        // equal to r vector (displacement from centroid to that corner)
        final M_Vect2D aLocalPos = Vect2DMath.LERP_M(Vect2D.ZERO, aToB, aRadiusRatio);
        final M_Vect2D bLocalPos = Vect2DMath.LERP_M(Vect2D.ZERO, aToB, -1 + aRadiusRatio);

        //Vect2D.ZERO.lerp(norm, -1 + aRadiusRatio);

        CALCULATE_AND_APPLY_IMPULSE(a, aLocalPos, b, bLocalPos, norm);

        aLocalPos.discard();
        bLocalPos.discard();

    }




    /**
     * Finds exact timestep where CrappyCircle A and CrappyCircle B actually collide with each other
     * @param a the first CrappyCircle
     * @param b the other CrappyCircle
     * @return the exact T where a collides with b
     */
    public static double GET_EXACT_COLLISION_TIME_CIRCLE_CIRCLE(final I_CrappyShape a, final I_CrappyShape b){

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


    public static boolean COLLIDE_CIRCLE_EDGE(final CrappyCircle c, final CrappyEdge e, final double deltaT){


        if( // first, we attempt to collide the circle with the end point edge of this body.
                c.getBoundingBox().check_bb_intersect(e.getEndPointCircle().getBody().getAABB())
                && COLLIDE_CIRCLE_CIRCLE(c, e.getEndPointCircle(), deltaT)
        ){
            // if they collided, we stop here.
            return true;
        }

        final Vect2D ap = Vect2DMath.MINUS(c.getBodyTransform().getPos(), e.getWorldStart());

        // we obtain the signed distance between the edge itself and the circle
        double distOnCorrectSideOfBarrierToCentre = ap.dot(e.getWorldNorm());

        if (distOnCorrectSideOfBarrierToCentre > c.getRadius() || // if circle is too far away from the barrier
                (distOnCorrectSideOfBarrierToCentre < -c.getRadius())// if circle is already past the barrier
        ){
            return false; // if the circle's too deep, we move along
        }

        double distAlongBarrier = ap.dot(e.getWorldTang());

        if (distAlongBarrier < 0 || distAlongBarrier > e.getLength()){
            return false; // if the circle isn't in the barrier, we ignore it.
        }

        // we get where (in local coords) on the edge the collision happened
        final Vect2D localCollisionPosOnEdge = e.getLocalTang().mult(distAlongBarrier);

        // we find local position of where the collision happened in the circle
        final Vect2D localCollisionPosInCircle = Vect2DMath.WORLD_TO_LOCAL_M(
                e.getWorldStart().addScaled(e.getWorldProj(), distAlongBarrier),
                c.getBodyTransform()
        ).finished();


        CALCULATE_AND_APPLY_IMPULSE(c, localCollisionPosInCircle, e, localCollisionPosOnEdge, e.getWorldNorm());

        return true;

    }


    public static boolean COLLIDE_CIRCLE_LINE(final CrappyCircle c, final CrappyLine l, final double deltaT) {

        for (final CrappyEdge crappyEdge : l) {
            if (COLLIDE_CIRCLE_EDGE(c, crappyEdge, deltaT)) {
                return true;
            }
        }

        return false;



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





    }
}
