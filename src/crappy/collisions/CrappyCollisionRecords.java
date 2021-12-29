package crappy.collisions;

import crappy.CrappyBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CrappyCollisionRecords {

    /**
     * Static unmodifiable empty list for when there is nothing to collide with, for future reference.
     */
    @SuppressWarnings("StaticCollection")
    private static final Collection<A_CollisionRecord> nothing = Collections.emptyList();

    public static Collection<A_CollisionRecord> GENERATE_COLLISION_RECORDS(
            final CrappyBody collidedBody,
            final Collection<CrappyShape_QuadTree_Interface> potentialCollisions,
            final double deltaT
    ){
        if (potentialCollisions.isEmpty()){
            return nothing;
        }

        final List<A_CollisionRecord> records = new ArrayList<A_CollisionRecord>();

        switch (collidedBody.getBodyType()){
            case STATIC:
            case KINEMATIC:
                return nothing;
        }

        A_CrappyShape s = collidedBody.getShape();
        switch (s.getShapeType()){
            case CIRCLE:
                assert (s instanceof CrappyCircle);
                CIRCLE_COLLISION_RECORDS((CrappyCircle) s, potentialCollisions, deltaT, records);
                break;
            case LINE:
                assert (s instanceof CrappyLine);

                break;
            case EDGE:
                assert (s instanceof CrappyEdge);
                break;
            case POLYGON:
                assert (s instanceof CrappyPolygon);

                break;
            default:
                return nothing;
        }

        return records;
    }

    private static void CIRCLE_COLLISION_RECORDS(
            final CrappyCircle c, final Collection<CrappyShape_QuadTree_Interface> potentials, final double deltaT,
            final List<A_CollisionRecord> outRecords
    ){


    }

    public abstract class A_CollisionRecord{

        /**
         * First shape involved in the collision
         */
        final A_CrappyShape a;

        /**
         * Second shape involved
         */
        final A_CrappyShape b;

        /**
         * Time at which the collision happened
         */
        final double t;


        private A_CollisionRecord(final A_CrappyShape aa, final A_CrappyShape bb, final double dt){
            a = aa;
            b = bb;
            t = dt;
        }

        abstract void performCollision();

    }

    private class CircleCircle_Collision extends A_CollisionRecord {

        CircleCircle_Collision(final CrappyCircle a, final CrappyCircle b, final double t){
            super(a, b, t);
        }

        @Override
        void performCollision() {
            CrappyCollisionMath.COLLIDE_CIRCLE_CIRCLE_KNOWN_TIME(a, b, t);
        }
    }
}
