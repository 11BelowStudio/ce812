package crappy.collisions;

import crappy.CrappyBody;
import crappy.CrappyBody_Shape_Interface;
import crappy.math.*;

import java.util.Iterator;

import static crappy.math.Vect2DMath.RETURN_1_IF_0;

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

        // double-check bounding boxes, return false if they don't intersect.
        if (!a.getBoundingBox().check_bb_intersect(b.getBoundingBox())){
            return false;
        }


        // we find out when the collision actually happened
        final double t = GET_EXACT_COLLISION_TIME_CIRCLE_CIRCLE(a, b);

        // if the collision didn't happen in this timestep, we ignore it.
        if (
                t > 0 || t < -deltaT ||
                    // also we ignore it if the two objects are going in completely different directions to each other
                Vect2DMath.MINUS_M(b.getVel(), a.getVel()).dot_discardBoth(
                        Vect2DMath.MINUS_M(b.getPos(), a.getPos())
                ) > 0
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




    public static boolean COLLIDE_CIRCLE_EDGE(final I_CrappyShape c, final I_CrappyEdge e, final double deltaT){


        if( // first, we attempt to collide the circle with the end point edge of this body.
                c.getBoundingBox().check_bb_intersect(e.getEndPointCircle().getBoundingBox())
                && COLLIDE_CIRCLE_CIRCLE(c, e.getEndPointCircle(), deltaT)
        ){
            // if they collided, we stop here.
            return true;
        }

        final Vect2D ap = Vect2DMath.MINUS(c.getBodyTransform().getPos(), e.getWorldStart());

        // we obtain the signed distance between the edge itself and the circle
        final double distOnCorrectSideOfBarrierToCentre = ap.dot(e.getWorldNorm());

        if (distOnCorrectSideOfBarrierToCentre > c.getRadius() || // if circle is too far away from the barrier
                (distOnCorrectSideOfBarrierToCentre < -c.getRadius())// if circle is already past the barrier
        ){
            return false; // if the circle's too deep, we move along
        }

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

    }


    public static boolean COLLIDE_CIRCLE_LINE(final I_CrappyShape c, final I_CrappyLine l, final double deltaT) {

        return COLLIDE_CIRCLE_EDGE(c, l.getEdgeA(), deltaT) || COLLIDE_CIRCLE_EDGE(c, l.getEdgeB(), deltaT);

    }


    public static boolean COLLIDE_CIRCLE_POLYGON(I_CrappyShape circle, I_CrappyPolygon polygon, final double deltaT){

        // we first see if the circle collides with the polygon's inner circle.
        if (
                circle.getBoundingBox().check_bb_intersect(polygon.getIncircle().getBoundingBox()) &&
                        COLLIDE_CIRCLE_CIRCLE(circle, polygon.getIncircle(), deltaT)
        ){
            return true;
        }

        final Vect2D polyToCircleNorm = Vect2DMath.VECTOR_BETWEEN_M(polygon.getCentroid(), circle.getCentroid()).norm().finished();

        // if the inner circles didn't collide, we see if each edge of the polygon collided with the circle.
        for (final I_CrappyEdge crappyEdge : polygon) {
            // first we make sure it's pointing the right way
            if (crappyEdge.getWorldNorm().dot(polyToCircleNorm) > 0 &&
                    COLLIDE_CIRCLE_EDGE(circle, crappyEdge, deltaT)
            ) {
                return true;
            }
        }

        return false;

        // TODO: circle-polygon collision

    }

    /**
     * Attempts to perform collisions between a line and an edge.
     * @param l line
     * @param e edge
     * @param deltaT timestep
     * @return true if they collided, false otherwise.
     */
    public static boolean COLLIDE_LINE_EDGE(final I_CrappyLine l, final I_CrappyEdge e, final double deltaT){

        if (!l.getBoundingBox().check_bb_intersect(e.getBoundingBox())){
            return false;
        }
        for (final I_CrappyEdge lineEdge: l) {

            if (
                    e.getEndPointCircle().getBoundingBox().check_bb_intersect(lineEdge.getEndPointCircle().getBoundingBox()) &&
                            COLLIDE_CIRCLE_CIRCLE(lineEdge.getEndPointCircle(), e.getEndPointCircle(), deltaT)
            ){
                return true;
            }
        }

        return COLLIDE_EDGE_EDGE(l.getEdgeA(), e, deltaT);
    }

    public static boolean COLLIDE_LINE_LINE(final I_CrappyLine l1, final I_CrappyLine l2, final double deltaT){

        if (!l1.getBoundingBox().check_bb_intersect(l2.getBoundingBox())){
            return false;
        }
        for (final I_CrappyEdge e1: l1) {

            for (final I_CrappyEdge e2: l2) {
                if (
                        e1.getEndPointCircle().getBoundingBox().check_bb_intersect(e2.getEndPointCircle().getBoundingBox()) &&
                                COLLIDE_CIRCLE_CIRCLE(e1.getEndPointCircle(), e2.getEndPointCircle(), deltaT)
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

        if (!e1.getBoundingBox().check_bb_intersect(e2.getBoundingBox())){
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
     * @param p the polygon
     * @param e the edge
     * @param deltaT timestep
     * @param polygonToEdgeCentroid normalize polygon from polygon centroid to edge centroid
     * @return true if they collided, false otherwise.
     */
    static boolean POLYGON_EDGE_COLLISIONS(
            final I_CrappyPolygon p, final I_CrappyEdge e, final  double deltaT, final Vect2D polygonToEdgeCentroid
    ){

        // we attempt to collide this polygon's incircle with the edge (returning true if it works)
        if (COLLIDE_CIRCLE_EDGE(p.getIncircle(), e, deltaT)){
            return true;
        }

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
    }

    public static boolean POLYGON_LINE_COLLISIONS(final I_CrappyPolygon p, final I_CrappyLine l, final double deltaT){

        if (COLLIDE_CIRCLE_LINE(p.getIncircle(), l, deltaT)){
            return true;
        }

        final Vect2D normPolyCentroidToLineCentroid =
                Vect2DMath.VECTOR_BETWEEN_M(p.getCentroid(), l.getCentroid()).norm().finished();

        return POLYGON_EDGE_COLLISIONS(p, l.getEdgeA(), deltaT, normPolyCentroidToLineCentroid) ||
                POLYGON_EDGE_COLLISIONS(p, l.getEdgeB(), deltaT, normPolyCentroidToLineCentroid);

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

        return TEST_POLYGON_A_ON_POLYGON_B(p1, p2, deltaT, norm_p1_to_p2) ||
                TEST_POLYGON_A_ON_POLYGON_B(p2, p1, deltaT, norm_p1_to_p2.invert());
    }

    /**
     * Attempts to collide polygon A onto polygon B.
     * This method treats polygon A as a complete polygon, and attempts to collide it against the components of polygon B.
     * Easier than having to write out these tests in full in both directions in POLYGON_POLYGON_COLLISIONS.
     * @param a the polygon we are testing
     * @param b the polygon we are testing against.
     * @param deltaT timestep
     * @param knownAToBNorm known normalized vector of A to B
     * @return true if we found a collision of A against B, false otherwise
     */
    private static boolean TEST_POLYGON_A_ON_POLYGON_B(
            final I_CrappyPolygon a, final I_CrappyPolygon b, final double deltaT, final Vect2D knownAToBNorm
    ){


        // keeps track of which 'whiskers' from A could potentially collide with an edge in B.
        final boolean[] whichWhiskersMightWork = new boolean[a.getVertexCount()];

        for (int i = a.getVertexCount()-1; i>=0; i--) {
            whichWhiskersMightWork[i] = a.getWorldNormalWhisker(i).dot(knownAToBNorm) > 0;
            // only bothering with whiskers that point roughly from A to B.
        }

        for (final I_CrappyEdge e: b) {
            // skip
            if (e.getWorldNorm().dot(knownAToBNorm) > 0){
                continue; // if the edge of polygon b is pointing away from polygon a, we skip it
            }
            if (COLLIDE_CIRCLE_EDGE(a.getIncircle(), e, deltaT)){
                return true;
            }
            // we then try looking at A's whiskers, specifically,
            // the ones that can potentially intersect with an edge of the polygon
            for (int i = a.getVertexCount()-1; i >= 0; i--) {
                // if the current whisker is noted as being potential intersect
                if (whichWhiskersMightWork[i] &&
                        // and if it actually does intersect
                        COLLIDE_EDGE_VECTOR(
                                e, a.getCentroid(), a.getWorldWhisker(i), a.getWorldNormalWhisker(i), a
                        )
                ){
                    // well, it's collided, so we return true.
                    return true;
                }
            }
        }
        // if nothing collided, we return false.
        return false;
    }
}
