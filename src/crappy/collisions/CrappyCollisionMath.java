/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.CrappyBody;
import crappy.math.*;


import static crappy.math.Vect2DMath.*;


/**
 * utilty class with all the maths for collision handling
 * @author Rachel Lowe
 */
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

        final double massBit = (s.getMass() <= 0)? 0 : (1.0/s.getMass());

        /*
        double massBit = 0;

        if (s.getMass() > 0){
            massBit = 1/ s.getMass();
        }

         */


        final double angleBit = (s.getMInertia() <= 0) ? 0 : M_Vect2D.GET(rotatedLocalPos)
                .cross(
                        M_Vect2D.GET(rotatedLocalPos).cross_discard(cNorm), false
                ).divide(s.getMInertia())
                .dot_discard(cNorm); ;
        /*
        if (s.getMInertia() > 0){
            angleBit = M_Vect2D.GET(rotatedLocalPos)
                    .cross(
                            M_Vect2D.GET(rotatedLocalPos).cross_discard(cNorm), false
                    ).divide(s.getMInertia())
                    .dot_discard(cNorm);


        }

        */
        return massBit + angleBit;
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

        /*
        if (a.getBody().getBodyType() == CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC) {
            switch (b.getBody().getBodyType()) {
                case STATIC:
                case KINEMATIC:
                    CALCULATE_IMPULSE_IF_ONLY_ONE_BODY_IS_DYNAMIC(a, aLocalPos, b, bLocalPos, norm);
                    return;
                default:
                    break;
            }
        } else if (b.getBody().getBodyType() == CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC) {
            CALCULATE_IMPULSE_IF_ONLY_ONE_BODY_IS_DYNAMIC(b, bLocalPos, a, aLocalPos, Vect2DMath.INVERT(norm));
            return;
        }

         */


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

        if (!Double.isFinite(jb)){
            bLocalWorldVel.discard();
            aLocalWorldVel.discard();
            return;
        }


        // vb = ub + norm*(jb/mb)

        b.getBody().overwriteVelocityAfterCollision(
                Vect2DMath.ADD_SCALED(b.getVel(), norm, jb/b.getMass()),
                bLocalPos.cross(norm) * jb / b.getMInertia(),
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        a.getBody().overwriteVelocityAfterCollision(
                Vect2DMath.ADD_SCALED(a.getVel(), norm, -jb/a.getMass()),
                aLocalPos.cross(norm) * -jb/a.getMInertia(),
                CrappyBody.FORCE_SOURCE.ENGINE
        );


        bLocalWorldVel.discard();
        aLocalWorldVel.discard();
    }

    public static void CALCULATE_IMPULSE_IF_ONLY_ONE_BODY_IS_DYNAMIC(
            final I_CrappyShape dyn, final I_Vect2D dLocalPos,
            final I_CrappyShape o, final I_Vect2D oLocalPos,
            final I_Vect2D norm
    ){



        final M_Vect2D dLocalWorldVel =
                Vect2DMath.WORLD_VEL_OF_LOCAL_COORD_M(dLocalPos, dyn.getBodyTransform());

        final double j = (
                -(dyn.getRestitution() + 1) + dLocalWorldVel.dot(norm)
                )/ CALCULATE_IMPULSE_DENOMINATOR_FOR_SHAPE(dyn, dLocalPos, norm);

        /*
        dyn.getBody().applyForce(
                Vect2DMath.MULTIPLY(norm, j),
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        dyn.getBody().applyTorque(
                Vect2DMath.CROSS(dLocalPos, norm) * j,
                CrappyBody.FORCE_SOURCE.ENGINE
        );

         */

        //dyn.getBody().applyHitSomethingStatic(dLocalPos, norm, j);

        dyn.getBody().overwriteVelocityAfterCollision(
                Vect2DMath.ADD_SCALED(dyn.getBody().getVel(), norm, j/dyn.getMass()),
                dLocalPos.cross(norm) * j/dyn.getMInertia(),
                CrappyBody.FORCE_SOURCE.ENGINE
        );

        dLocalWorldVel.finished();
    }


    public static boolean COLLIDE_CIRCLE_CIRCLE(final I_CrappyShape a, final I_CrappyShape b, final double deltaT) {


        // double-check bounding boxes, return false if they don't intersect.
        //if (!a.getBoundingBox().check_bb_intersect(b.getBoundingBox())){
        if (!I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(a.getBoundingBox(), b.getBoundingBox())){
            return false;
        }



        // we find out when the collision actually happened
        final double t = GET_EXACT_COLLISION_TIME_CIRCLE_CIRCLE(a, b);

        // if the collision didn't happen in this timestep, we ignore it.
        if (
                t > 0 || t < -deltaT || !Double.isFinite(t) ||
                    // also we ignore it if the two objects are going in completely different directions to each other
                Vect2DMath.MINUS_M(b.getVel(), a.getVel()).dot_discardBoth(
                        Vect2DMath.MINUS_M(b.getPos(), a.getPos())
                ) > 0.0001
        ) {
            return false;
        }

        if (a.getBody().isTangible() && b.getBody().isTangible()) {
            COLLIDE_CIRCLE_CIRCLE_KNOWN_TIME(a, b, t);
        }
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


        final Vect2D c = Vect2DMath.MINUS(b.getPos(), a.getPos());
        final Vect2D v = Vect2DMath.MINUS(b.getVel(), a.getVel());
        final double d = a.getRadius() + b.getRadius();

        final double vv = v.dot(v);
        final double cv = c.dot(v);
        final double ccd = c.dot(c) - Math.pow(d,2);

        // sqrt(b^2 - 4ac)
        final double theThingThatHasThePlusMinus = Math.sqrt(Math.pow(cv,2) - (vv * ccd));

        if (Double.isNaN(theThingThatHasThePlusMinus)){
            return theThingThatHasThePlusMinus;
        }


        return Math.min(
                (-cv - theThingThatHasThePlusMinus)/vv,
                (-cv + theThingThatHasThePlusMinus)/vv
        );

    }




    public static boolean COLLIDE_CIRCLE_EDGE(final I_CrappyShape c, final I_CrappyEdge e, final double deltaT){


        if( // first, we attempt to collide the circle with the end point edge of this body.
                COLLIDE_CIRCLE_CIRCLE(c, e.getEndPointCircle(), deltaT)
        ){
            // if they collided, we stop here.
            return true;
        }

        final Vect2D ap = Vect2DMath.MINUS(c.getBodyTransform().getPos(), e.getWorldStart());

        // we obtain the signed distance between the edge itself and the circle
        final double distOnCorrectSideOfBarrierToCentre = ap.dot(e.getWorldNorm());

        if (distOnCorrectSideOfBarrierToCentre > c.getRadius() || // if circle is too far away from the barrier
                (Double.isFinite(e.getDepth()) && distOnCorrectSideOfBarrierToCentre < -(e.getDepth())) // if circle is already past the barrier
        ){
            return false; // if the circle's too deep, we move along
        }


        Vect2D endToNowPos = Vect2DMath.VECTOR_BETWEEN(e.getWorldStart(), c.getPos());
        //System.out.println("\tendToNowPos = " + endToNowPos);
        final Vect2D tang = e.getWorldTang();
        final double thisFrameDistAlongBarrier = tang.dot(endToNowPos);
        //System.out.println("\tthisFrameDistAlongBarrier = " + thisFrameDistAlongBarrier);

        if (thisFrameDistAlongBarrier < 0 || thisFrameDistAlongBarrier > e.getLength()){
            //System.out.println("Too far!");
            return false;
        }

        //double velDotNorm = c.getVel().dot(e.getWorldNorm());

        if (c.getVel().dot(e.getWorldNorm()) >= 0){
            return false;
        }

        if (!c.getBody().isTangible() || !e.getBody().isTangible()){
            return true;
        }

        final Vect2D collisionPos = e.getWorldStart().addScaled(tang, thisFrameDistAlongBarrier);

        if (c.getBody().getBodyType() == CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC && e.getBody().getBodyType() != CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC){

            ShapeAgainstImmovableEdge(c, e, collisionPos, e.getWorldNorm());
        } else {
            CALCULATE_AND_APPLY_IMPULSE(
                    c, Vect2DMath.WORLD_TO_LOCAL_M(collisionPos, c.getBodyTransform()),
                    e, Vect2DMath.WORLD_TO_LOCAL_M(collisionPos, e.getBodyTransform()),
                    e.getWorldNorm()
            );
        }
        return true;




        /*
        final double distAlongBarrier = ap.dot(e.getWorldTang());

        if (distAlongBarrier < 0 || distAlongBarrier > e.getLength()){
            return false; // if the circle isn't in the barrier, we ignore it.
        }

        if (c.getBody().isTangible() && e.getBody().isTangible()) {
            // if the two objects are actually tangible, we actually calculate the collision forces.

            // we get where (in local coords) on the edge the collision happened
            final Vect2D localCollisionPosOnEdge = e.getLocalTang().mult(distAlongBarrier);

            // we find local position of where the collision happened in the circle
            final Vect2D localCollisionPosInCircle = Vect2DMath.WORLD_TO_LOCAL_M(
                    e.getWorldStart().addScaled(e.getWorldProj(), distAlongBarrier),
                    c.getBodyTransform()
            ).finished();


            CALCULATE_AND_APPLY_IMPULSE(c, localCollisionPosInCircle, e, localCollisionPosOnEdge, e.getWorldNorm());
        }

        return true;

         */

    }


    public static boolean COLLIDE_CIRCLE_LINE(final I_CrappyShape c, final I_CrappyLine l, final double deltaT) {

        //return (COLLIDE_CIRCLE_EDGE(c, l.getEdgeB(), deltaT));


        //System.out.println("CrappyCollisionMath.COLLIDE_CIRCLE_LINE");
        //System.out.println("c = " + c + ", l = " + l + ", deltaT = " + deltaT);

        //System.out.println("\tc.getBody().getName() = " + c.getBody().getName());



        // first attempt to collide the circle with the endpoints of this line
        if (COLLIDE_CIRCLE_CIRCLE(c, l.getEdgeA().getEndPointCircle(), deltaT) || COLLIDE_CIRCLE_CIRCLE(c, l.getEdgeB().getEndPointCircle(), deltaT)){
            //System.out.println("done!");
            return true;
        } else {

            return COLLIDE_CIRCLE_EDGE(c, l.getEdgeA(), deltaT) || COLLIDE_CIRCLE_EDGE(c, l.getEdgeB(), deltaT);
        }
        /*

        //System.out.println("\tStill colliding circle-line!");


        Vect2D endToNowPos = Vect2DMath.VECTOR_BETWEEN(l.getWorldStart(), c.getPos());
        //System.out.println("\tendToNowPos = " + endToNowPos);
        Vect2D tang = l.getEdgeA().getWorldTang();
        double thisFrameDistAlongBarrier = l.getEdgeA().getWorldTang().dot(endToNowPos);
        //System.out.println("\tthisFrameDistAlongBarrier = " + thisFrameDistAlongBarrier);

        if (thisFrameDistAlongBarrier < -c.getRadius() || thisFrameDistAlongBarrier > l.getEdgeA().getLength() + c.getRadius()){
            //System.out.println("Too far!");
            return false;
        }


        Vect2D endToLastPos = Vect2DMath.VECTOR_BETWEEN(l.getWorldStart(), c.getLastFrameWorldPos());
        //System.out.println("\tendToLastPos = " + endToLastPos);
        Vect2D aNorm = l.getEdgeA().getWorldNorm();
        //System.out.println("\taNorm = " + aNorm);
        double lastFrameDist = aNorm.dot(endToLastPos);
        //System.out.println("\tlastFrameDist = " + lastFrameDist);
        double thisFrameDist = aNorm.dot(endToNowPos);
        //System.out.println("\tthisFrameDist = " + thisFrameDist);

        Vect2D collisionPos = l.getWorldStart().addScaled(tang, thisFrameDistAlongBarrier);
        //System.out.println("\tcollisionPos = " + collisionPos);

        //System.out.println("\tc.getVel() = " + c.getVel());
        double velDotNorm = c.getVel().dot(aNorm);
        //System.out.println("\tvelDotNorm = " + velDotNorm);
        //double velDotTang = c.getVel().dot(tang);
        //System.out.println("\tvelDotTang = " + velDotTang);

        final double bufferedRadius = c.getRadius() * BUFFERING;

        if (velDotNorm < 0){


            // this means that the thing is going in the opposite direction of the normal (normal facing out towards shape)
            if (thisFrameDist < bufferedRadius && thisFrameDist > -(bufferedRadius/2) && Math.abs(lastFrameDist) > Math.abs(thisFrameDist)){
                // if it's travelling towards the barrier, and was further away from the barrier last frame

                if (!c.getBody().isTangible() || !l.getBody().isTangible()){
                    return true;
                }

                if (c.getBody().getBodyType() == CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC && l.getBody().getBodyType() != CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC){

                    ShapeAgainstImmovableEdge(c, l, collisionPos, aNorm.invert());
                } else {
                    CALCULATE_AND_APPLY_IMPULSE(
                            c, Vect2DMath.WORLD_TO_LOCAL_M(collisionPos, c.getBodyTransform()),
                            l, Vect2DMath.WORLD_TO_LOCAL_M(collisionPos, l.getBodyTransform()),
                            aNorm.invert()
                    );
                }
                return true;
            }
        } else {


            if (thisFrameDist > -bufferedRadius && thisFrameDist < (bufferedRadius/2) && lastFrameDist < thisFrameDist){
                // normal facing in same direction as shape velocity

                if (!c.getBody().isTangible() || !l.getBody().isTangible()){
                    return true;
                }

                if (c.getBody().getBodyType() == CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC && l.getBody().getBodyType() != CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC){

                    ShapeAgainstImmovableEdge(c, l, collisionPos, aNorm);
                } else {
                    CALCULATE_AND_APPLY_IMPULSE(
                            c, Vect2DMath.WORLD_TO_LOCAL_M(collisionPos, c.getBodyTransform()),
                            l, Vect2DMath.WORLD_TO_LOCAL_M(collisionPos, l.getBodyTransform()),
                            aNorm//.invert()
                    );
                }
                return true;
            }
        }

        return false;
        /*
        if (lastFrameDist < -c.getRadius() && velDotNorm < 0){


            System.out.println("\t(thisFrameDist > -c.getRadius()) = " + (thisFrameDist > -c.getRadius()));

            if (thisFrameDist > -c.getRadius()){



                CALCULATE_AND_APPLY_IMPULSE(
                        c, Vect2DMath.WORLD_TO_LOCAL_M(collisionAt, c.getBodyTransform()),
                        l, Vect2DMath.WORLD_TO_LOCAL_M(collisionAt, l.getBodyTransform()),
                        aNorm
                );

                return true;

            }

        } else if (lastFrameDist > c.getRadius() && velDotNorm > 0){

            System.out.println("\t(thisFrameDist < c.getRadius()) = " + (thisFrameDist < c.getRadius()));

            if (thisFrameDist < c.getRadius()){


                CALCULATE_AND_APPLY_IMPULSE(
                        c, Vect2DMath.WORLD_TO_LOCAL_M(collisionAt, c.getBodyTransform()),
                        l, Vect2DMath.WORLD_TO_LOCAL_M(collisionAt, l.getBodyTransform()),
                        aNorm.invert()
                );
                return true;
            }

        }
        return false;


         */



        //return COLLIDE_CIRCLE_EDGE(c, l.getEdgeA(), deltaT) || COLLIDE_CIRCLE_EDGE(c, l.getEdgeB(), deltaT);

    }

    /**
     * Attempts to collide a dynamic shape against a non-dynamic edge
     * @param c the shape
     * @param r the edge
     * @param worldCollisionPos where the shape collides with the edge (in world coordinates)
     * @param norm normal vector for the edge.
     */
    private static void ShapeAgainstImmovableEdge(
            final I_CrappyShape c, final I_CrappyShape r, final I_Vect2D worldCollisionPos, final Vect2D norm
    ){

        assert (r.getShapeType() == I_CrappyShape.CRAPPY_SHAPE_TYPE.LINE || r.getShapeType() == I_CrappyShape.CRAPPY_SHAPE_TYPE.EDGE);
        assert (r.getBody().getBodyType() != CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC);
        assert (c.getBody().getBodyType() == CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC);

        Vect2D localCollisionPos = Vect2DMath.WORLD_TO_LOCAL_M(worldCollisionPos, c.getPos(), c.getRot()).finished();


        Vect2D velCentroid = c.getVel();

        Vect2D velColPos = velCentroid.add(localCollisionPos.cross(c.getAngVel(), false));
        
        double jDenominator = (1/c.getMass()) + (
                localCollisionPos.cross(localCollisionPos.cross(norm), false).divide(c.getMInertia()).dot(norm)
        );
        

        double j = (-(AVG_RESTITUTION(c, r) + 1) * velColPos.dot(norm)) / jDenominator;



        c.getBody().overwriteVelocityAfterCollision(
                c.getVel().addScaled(
                        norm, j/c.getMass()
                ),
                c.getAngVel() + ((localCollisionPos.cross(norm) * j)/c.getMInertia()),
                CrappyBody.FORCE_SOURCE.ENGINE
        );

    }

    private static boolean attemptIndividualEdge(
            final I_CrappyShape c, final I_CrappyEdge e, final double deltaT,
            final Vect2D endToPrevPos, final Vect2D endToNowPos
    ){

        double lastFrameDist = e.getWorldNorm().dot(endToPrevPos);

        double thisFrameDist = e.getWorldNorm().dot(endToNowPos);

        return false;
    }


    public static boolean COLLIDE_CIRCLE_POLYGON(I_CrappyShape circle, I_CrappyPolygon polygon, final double deltaT){

        // we first see if the circle collides with the polygon's inner circle.
        if (
                //circle.getBoundingBox().check_bb_intersect(polygon.getIncircle().getBoundingBox()) &&
                I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(
                        circle.getBoundingBox(), polygon.getIncircle().getBoundingBox()
                ) && COLLIDE_CIRCLE_CIRCLE(circle, polygon.getIncircle(), deltaT)
        ){
            return true;
        }

        final Vect2D polyToCircleNorm = Vect2DMath.VECTOR_BETWEEN_M(
                polygon.getCentroid(), circle.getCentroid()
        ).norm().finished();



        // if the inner circles didn't collide, we see if each edge of the polygon collided with the circle.
        for (final I_CrappyEdge crappyEdge : polygon) {
            // first we make sure it's pointing the right way
            if (crappyEdge.getWorldNorm().dot(polyToCircleNorm) > 0 &&
                    COLLIDE_CIRCLE_CIRCLE(circle, crappyEdge.getEndPointCircle(), deltaT)
            ) {
                return true;
            }
        }
        return COLLIDE_CIRCLE_CIRCLE(
                polygon.getCircleForLocalCollisionPos(
                        Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                                polyToCircleNorm.mult(polygon.getRadiusSquared()),
                                polygon.getRotatedLocals(polygon.getRot())
                        )
                ),
                circle,
                deltaT
        ) || COLLIDE_CIRCLE_CIRCLE(
                polygon.getCircleForLocalCollisionPos(
                        Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                                polyToCircleNorm.mult(polygon.getRadiusSquared()),
                                polygon.getRotatedLocals(polygon.getBody().getLastRot())
                        )
                ),
                circle,
                deltaT
        ) ;

        //return false;


    }

    /**
     * Attempts to perform collisions between a line and an edge.
     * @param l line
     * @param e edge
     * @param deltaT timestep
     * @return true if they collided, false otherwise.
     */
    public static boolean COLLIDE_LINE_EDGE(final I_CrappyLine l, final I_CrappyEdge e, final double deltaT){

        //if (!l.getBoundingBox().check_bb_intersect(e.getBoundingBox())){
        if (!I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(l.getBoundingBox(), e.getBoundingBox())){
            return false;
        }
        for (final I_CrappyEdge lineEdge: l) {

            if (
                    //e.getEndPointCircle().getBoundingBox().check_bb_intersect(lineEdge.getEndPointCircle().getBoundingBox()) &&
                    I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(
                            l.getBoundingBox(), e.getEndPointCircle().getBoundingBox()
                    ) && COLLIDE_CIRCLE_CIRCLE(lineEdge.getEndPointCircle(), e.getEndPointCircle(), deltaT)
            ){
                return true;
            }
        }

        return COLLIDE_EDGE_EDGE(l.getEdgeA(), e, deltaT);
    }

    public static boolean COLLIDE_LINE_LINE(final I_CrappyLine l1, final I_CrappyLine l2, final double deltaT){

        //if (!l1.getBoundingBox().check_bb_intersect(l2.getBoundingBox())){
        if (!I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(l1.getBoundingBox(), l2.getBoundingBox())){
            return false;
        }
        for (final I_CrappyEdge e1: l1) {

            for (final I_CrappyEdge e2: l2) {
                if (
                        //e1.getEndPointCircle().getBoundingBox().check_bb_intersect(e2.getEndPointCircle().getBoundingBox()) &&
                        I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(
                                e1.getEndPointCircle().getBoundingBox(), e2.getEndPointCircle().getBoundingBox()
                        ) && COLLIDE_CIRCLE_CIRCLE(e1.getEndPointCircle(), e2.getEndPointCircle(), deltaT)
                ){
                    return true;
                }
            }

        }

        return COLLIDE_EDGE_EDGE(l1.getEdgeA(), l2.getEdgeA(), deltaT);

    }

    /**
     * Attempts to collide two edge shapes together.
     * Somewhat based on https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/.
     * @param e1 first edge
     * @param e2 second edge
     * @param deltaT timestep
     * @return true if the edges are deemed to have collided.
     * @see <a href="https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/">https://martin-thoma.com/how-to-check-if-two-line-segments-intersect/</a>
     */
    public static boolean COLLIDE_EDGE_EDGE(I_CrappyEdge e1, I_CrappyEdge e2, final double deltaT){

        //if (!e1.getBoundingBox().check_bb_intersect(e2.getBoundingBox())){
        if (!I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(e1.getBoundingBox(), e2.getBoundingBox())){
            return false;
        }

        if (COLLIDE_CIRCLE_EDGE(e1.getEndPointCircle(), e2, deltaT) || COLLIDE_CIRCLE_EDGE(e2.getEndPointCircle(), e1, deltaT)){
            return true;
        }

        if (
                (!DO_EDGES_INTERSECT(e1, e2)) // if the lines don't intersect
                        // or if they're going in completely different directions to each other
                || Vect2DMath.MINUS_M(e2.getVel(), e1.getVel())
                        .dot_discardBoth(Vect2DMath.MINUS_M(e2.getPos(), e1.getPos())) > 0
        ){
            return false;
        }


        if (e1.getBody().isTangible() && e2.getBody().isTangible()) {
            // again, we only calculate the collision forces if both bodies are tangible.

            final Vect2D intersectionPointWorld = Vect2DMath.GET_INTERSECTION_POINT(
                    e1.getWorldStart(), e1.getWorldProj(), e2.getWorldStart(), e2.getWorldProj()
            );


            CALCULATE_AND_APPLY_IMPULSE(
                    e1, Vect2DMath.MINUS(intersectionPointWorld, e1.getPos()),
                    e2, Vect2DMath.MINUS(intersectionPointWorld, e2.getPos()),
                    Vect2DMath.VECTOR_BETWEEN_M(e1.getPos(), e2.getPos()).norm().finished()
            );
        }

        return true;

    }
    private static boolean COLLIDE_EDGE_VECTOR(
            final I_CrappyEdge e, final I_Vect2D start,
            final I_Vect2D proj, final I_Vect2D projNorm,
            final I_CrappyShape vectorShape
    ){

        // if the projection of the vector and the normal of the edge are pointing in the same direction as each other,
        // we skip this.
        if (e.getWorldNorm().dot(projNorm) > 0){
            return false;
        }

        if ((!DOES_EDGE_INTERSECT_WITH_PROJ_POSITION_VECTOR(e, start, proj))
            || Vect2DMath.MINUS_M(vectorShape.getVelOfWorldPoint(start), e.getVel())
                .dot_discardBoth(Vect2DMath.MINUS_M(start, e.getPos())) > 0){
            return false;
        }


        if (e.getBody().isTangible() && vectorShape.getBody().isTangible()) {
            // if both bodies are tangible, we properly collide them.

            final Vect2D intersectionPointWorld = Vect2DMath.GET_INTERSECTION_POINT(
                    e.getWorldStart(), e.getWorldProj(), start, proj
            );


            CALCULATE_AND_APPLY_IMPULSE(
                    e, Vect2DMath.MINUS(intersectionPointWorld, e.getPos()),
                    vectorShape, Vect2DMath.MINUS(intersectionPointWorld, vectorShape.getPos()),
                    Vect2DMath.VECTOR_BETWEEN_M(e.getPos(), vectorShape.getPos()).norm().finished()
            );

        }

        return true;
    }


    private static boolean DO_EDGES_INTERSECT(final I_CrappyEdge e1, final I_CrappyEdge e2){

        return Vect2DMath.DO_LINES_INTERSECT_CHECK_BOTH(
                e1.getWorldStart(), e1.getWorldProj(), e2.getWorldStart(), e2.getWorldProj()
        );
    }

    private static boolean DOES_EDGE_INTERSECT_WITH_PROJ_POSITION_VECTOR(
            final I_CrappyEdge e, final I_Vect2D worldPos, final I_Vect2D worldProj
    ){
        return Vect2DMath.DO_LINES_INTERSECT_CHECK_BOTH(e.getWorldStart(), e.getWorldProj(), worldPos, worldProj);
    }




    /**
     * Call this to collide a polygon with an edge when we don't know what the normalized 'polygon centroid to edge
     * centroid' vector is
     * @param p the polygon
     * @param e the edge
     * @param deltaT timestep
     * @return true if they collide, false otherwise
     */
    public static boolean POLYGON_EDGE_COLLISIONS(final I_CrappyPolygon p, final I_CrappyEdge e, final double deltaT) {

        return POLYGON_EDGE_COLLISIONS(p, e, deltaT, Vect2DMath.VECTOR_BETWEEN_M(p.getCentroid(), e.getCentroid()).norm().finished());
    }

    /**
     * Call this to collide a polygon with an edge when we know what the normalized 'polygon centroid to edge centroid'
     * vector is.
     *
     * KNOWN ISSUES: if the incircle of the polygon collides with the edge, the polygon will 'rotate' around the edge.
     *
     * @param p the polygon
     * @param e the edge
     * @param deltaT timestep
     * @param polygonToEdgeCentroid normalize polygon from polygon centroid to edge centroid
     * @return true if they collided, false otherwise.
     */
    static boolean POLYGON_EDGE_COLLISIONS(
            final I_CrappyPolygon p, final I_CrappyEdge e, final  double deltaT, final Vect2D polygonToEdgeCentroid
    ){

        // if the edge's normal is pointing away from the polygon, return false.
        if (polygonToEdgeCentroid.dot(e.getWorldNorm()) >= 0 || p.getVel().dot(e.getWorldNorm()) > 0){
            return false;
        }

        // we attempt to collide this polygon's incircle with the edge (returning true if it works)
        if (COLLIDE_CIRCLE_EDGE(p.getIncircle(), e, deltaT)){
            return true;
        }

        final Vect2D endToNowPos = Vect2DMath.VECTOR_BETWEEN(e.getWorldStart(), p.getPos());
        //System.out.println("\tendToNowPos = " + endToNowPos);




        //Vect2D tang = e.getWorldTang();
        //final double thisFrameDistAlongBarrier = tang.dot(endToNowPos);
        //Vect2D collisionPoint = e.getWorldStart().addScaled(tang, thisFrameDistAlongBarrier);
        //Vect2D collisionPoint = e.getWorldStart().addScaled(e.getWorldTang(), e.getWorldTang().dot(endToNowPos));

        Vect2D collisionPoint = Vect2DMath.MULT_DOT_OTHER(e.getWorldTang(), endToNowPos).add(e.getWorldStart()).finished();

        // we see if the point on the line itself is within the bounds of the polygon
        if (!p.isWorldPointInPolyBounds(collisionPoint)) {

            //final double distBetweenPolygonAndBarrier = endToNowPos.dot(e.getWorldNorm());


            // if depth is NaN (therefore infinite), we treat depth as being as deep as we need it to be.
            // WE USE -e.getDepth() AND POSITIVE distBetweenPolygonAndBarrier TO REMOVE ANY FALSE POSITIVES
            // FROM CASES WHERE THE POLYGON IS NOT WITHIN THE BARRIER'S DEPTH!
            final double depthLimit = RETURN_X_IF_NOT_FINITE(
                    -e.getDepth(),
                    Math.min(
                            0,
                            endToNowPos.dot(e.getWorldNorm())
                            //distBetweenPolygonAndBarrier
                    )
            );

            // then we find the vector between the collision point on the line and the position of the polygon
            //final Vect2D depthBetweenPos = VECTOR_BETWEEN(collisionPoint, p.getPos());

            // find the dot of the norm vector and the depthBetweenPos vector.
            // THIS WILL BE NEGATIVE IF THE POLYGON IS INSIDE THE BARRIER, POSITIVE IF THE POLYGON IS OUTSIDE THE BARRIER!
            final double actualDepth = e.getWorldNorm().dot(
                    VECTOR_BETWEEN(collisionPoint, p.getPos())
                    //depthBetweenPos
            );

            if (actualDepth >= 0 || actualDepth < depthLimit){
                // if the polygon isn't deep enough, or if the polygon is too deep, we give up and move on.
                return false;
            } else{
                // we find the actual collision point at the given depth
                collisionPoint = collisionPoint.addScaled(
                        e.getWorldNorm(),
                        actualDepth
                );
                // and we make sure that it's actually valid ofc
                if (!p.isWorldPointInPolyBounds(collisionPoint)){
                    return false;
                }
            }

        }


        if (Vect2DMath.WORLD_VEL_OF_ROTATED_LOCAL_COORD_M(
                collisionPoint, p.getBodyTransform()
        ).dot_discard(e.getWorldNorm()) < 0){
            // if the circle was actually moving towards the edge, we begin
            return COLLIDE_CIRCLE_EDGE(
                    p.getCircleForWorldCollisionPos(collisionPoint),
                    e,
                    deltaT
            );
        }
        return false;
        
        

        //return false;

        /*
        // if the edge's normal is pointing away from the polygon, return false.
        if (polygonToEdgeCentroid.dot(e.getWorldNorm()) > 0){
            return false;
        }

        for (int i = p.getVertexCount()-1; i >= 0; i--) {
            // if the ith world whisker's normal is pointing towards the centroid of the edge
            if (p.getWorldNormalWhisker(i).dot(polygonToEdgeCentroid) > 0 &&
                    // we attempt to collide the edge with that whisker
                    COLLIDE_EDGE_VECTOR(e, p.getCentroid(), p.getWorldWhisker(i), p.getWorldNormalWhisker(i), p)
            ){
                // if that worked, we return true.
                return true;
            }
        }

        return false;

         */
    }

    public static boolean POLYGON_LINE_COLLISIONS(final I_CrappyPolygon p, final I_CrappyLine l, final double deltaT){

        if (COLLIDE_CIRCLE_LINE(p.getIncircle(), l, deltaT)){
            return true;
        }

        Vect2D endToNowPos = Vect2DMath.VECTOR_BETWEEN(l.getWorldStart(), p.getPos());
        //System.out.println("\tendToNowPos = " + endToNowPos);
        Vect2D tang = l.getEdgeA().getWorldTang();
        double thisFrameDistAlongBarrier = tang.dot(endToNowPos);
        //System.out.println("\tthisFrameDistAlongBarrier = " + thisFrameDistAlongBarrier);

        Vect2D collisionPoint = l.getWorldStart().addScaled(tang, thisFrameDistAlongBarrier);

        if (p.isWorldPointInPolyBounds(collisionPoint)){
            return COLLIDE_CIRCLE_LINE(
                    p.getCircleForWorldCollisionPos(collisionPoint),
                    l,
                    deltaT
            );
        }
        return false;

        /*
        final Vect2D normPolyCentroidToLineCentroid =
                Vect2DMath.VECTOR_BETWEEN_M(p.getCentroid(), l.getCentroid()).norm().finished();

        return POLYGON_EDGE_COLLISIONS(p, l.getEdgeA(), deltaT, normPolyCentroidToLineCentroid) ||
                POLYGON_EDGE_COLLISIONS(p, l.getEdgeB(), deltaT, normPolyCentroidToLineCentroid);

         */

    }

    /**
     * Attempts to collide two polygons with each other
     * @param p1 first polygon
     * @param p2 other polygon
     * @param deltaT timestep
     * @return whether or not they collided.
     */
    public static boolean POLYGON_POLYGON_COLLISIONS(final I_CrappyPolygon p1, final I_CrappyPolygon p2, final double deltaT){

        if (COLLIDE_CIRCLE_CIRCLE(p1.getIncircle(), p2.getIncircle(), deltaT)){
            return true;
        }

        final Vect2D norm_p1_to_p2 = Vect2DMath.VECTOR_BETWEEN_M(p1.getCentroid(), p2.getCentroid()).norm().finished();

        I_CrappyCircle c1 = p1.getCircleForLocalCollisionPos(
                Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                        norm_p1_to_p2.mult(p1.getRadiusSquared()),
                        p1.getRotatedLocals(p1.getRot())
                )
        );

        I_CrappyCircle c2 = p2.getCircleForLocalCollisionPos(
                Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                        norm_p1_to_p2.mult(-p2.getRadiusSquared()),
                        p2.getRotatedLocals(p2.getRot())
                )
        );

        return COLLIDE_CIRCLE_POLYGON(c1, p2, deltaT) || COLLIDE_CIRCLE_POLYGON(c2, p1, deltaT) || COLLIDE_CIRCLE_CIRCLE(c1, c2, deltaT);

        /*
        return COLLIDE_CIRCLE_CIRCLE(
                p1.getCircleForLocalCollisionPos(
                        Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                                norm_p1_to_p2.mult(p1.getRadiusSquared()),
                                p1.getRotatedLocals(p1.getRot())
                        )
                ),
                p2.getCircleForLocalCollisionPos(
                        Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                                norm_p1_to_p2.mult(-p2.getRadiusSquared()),
                                p2.getRotatedLocals(p2.getRot())
                        )
                ),
                deltaT
        ) || COLLIDE_CIRCLE_CIRCLE(
                p1.getCircleForLocalCollisionPos(
                        Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                                norm_p1_to_p2.mult(p1.getRadiusSquared()),
                                p1.getRotatedLocals(p1.getBody().getLastRot())
                        )
                ),
                p2.getCircleForLocalCollisionPos(
                        Vect2DMath.GET_POINT_ON_POLYGON_EDGE_WHERE_LINE_FROM_ORIGIN_INTERSECTS_WITH_EDGE(
                                norm_p1_to_p2.mult(-p2.getRadiusSquared()),
                                p2.getRotatedLocals(p2.getBody().getLastRot())
                        )
                ),
                deltaT
        );

         */
        /*
        return TEST_POLYGON_A_ON_POLYGON_B(p1, p2, deltaT, norm_p1_to_p2) ||
                TEST_POLYGON_A_ON_POLYGON_B(p2, p1, deltaT, norm_p1_to_p2.invert());

         */
    }


}
