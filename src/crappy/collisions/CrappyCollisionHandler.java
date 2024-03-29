/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_View_CrappyBody;

import java.util.Collection;

/**
 * Just contains the static method(s) for organizing the collision handling
 * @author Rachel Lowe
 */
public final class CrappyCollisionHandler {

    /**
     * no constructing
     */
    private CrappyCollisionHandler(){}

    /**
     * Attempts to perform collision handling
     * @param shapeInterface the CrappyShape_QuadTree_Interface for the shape of the body that has had collisions
     * @param bbIntersects everything that it had a bounding box intersect with
     * @param deltaT timestep
     */
    public static <T extends CrappyShape_QuadTree_Interface> void HANDLE_COLLISIONS(
            final T shapeInterface,
            final Collection<? extends CrappyShape_QuadTree_Interface> bbIntersects,
            final double deltaT
    ){

        if (bbIntersects.isEmpty()){
            return;
        }

        final I_CrappyShape s = shapeInterface.getShape();

        switch (s.getShapeType()){
            case CIRCLE:
                assert (s instanceof I_CrappyCircle);
                HANDLE_COLLISIONS_CIRCLE((I_CrappyCircle) s, bbIntersects, deltaT);
                break;
            case LINE:
                assert (s instanceof I_CrappyLine);
                HANDLE_COLLISIONS_LINE((I_CrappyLine) s, bbIntersects, deltaT);
                break;
            case EDGE:
                assert (s instanceof I_CrappyEdge);
                HANDLE_COLLISIONS_EDGE((I_CrappyEdge) s, bbIntersects, deltaT);
                break;
            case POLYGON:
                assert (s instanceof I_CrappyPolygon);
                HANDLE_COLLISIONS_POLYGON((I_CrappyPolygon) s, bbIntersects, deltaT);
                break;

        }
    }

    /**
     * Handles collisions for circles.
     * @param c the main shape
     * @param candidates everything it had a bounding box intersection with
     * @param deltaT timestep
     */
    static void HANDLE_COLLISIONS_CIRCLE(
            final I_CrappyCircle c,
            final Iterable<? extends CrappyShape_QuadTree_Interface> candidates,
            final double deltaT
    ){
        final CrappyBody_Shape_Interface b = c.getBody();
        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (CAN_COLLIDE(b, s)){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_CIRCLE(c, s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_EDGE(c, (I_CrappyEdge) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_LINE(c, (I_CrappyLine) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_POLYGON(c, (I_CrappyPolygon) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                }
                //c.getBody().notifyAboutCollision(s.getBody());
                //s.getBody().notifyAboutCollision(c.getBody());
            }
        }

    }

    private static void NOTIFY_ABOUT_COLLISION(CrappyBody_Shape_Interface a, CrappyBody_Shape_Interface b){
        a.notifyAboutCollision(b);
        b.notifyAboutCollision(a);
    }

    /**
     * Handles collisions for lines.
     * @param l the main shape
     * @param candidates everything it had a bounding box intersection with
     * @param deltaT timestep
     */
    static void HANDLE_COLLISIONS_LINE(
            final I_CrappyLine l,
            final Iterable<? extends CrappyShape_QuadTree_Interface> candidates,
            final double deltaT
    ){
        final CrappyBody_Shape_Interface b = l.getBody();
        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (CAN_COLLIDE(b, s)){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_LINE(s, l, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (CrappyCollisionMath.COLLIDE_LINE_EDGE(l, (I_CrappyEdge) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (CrappyCollisionMath.COLLIDE_LINE_LINE(l, (I_CrappyLine) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (CrappyCollisionMath.POLYGON_LINE_COLLISIONS((I_CrappyPolygon) s, l, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                }
            }
        }

    }

    /**
     * Handles collisions for edges.
     * @param e the main shape
     * @param candidates everything it had a bounding box intersection with
     * @param deltaT timestep
     */
    static void HANDLE_COLLISIONS_EDGE(
            final I_CrappyEdge e,
            final Iterable<? extends CrappyShape_QuadTree_Interface> candidates,
            final double deltaT
    ){
        final CrappyBody_Shape_Interface b = e.getBody();
        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (CAN_COLLIDE(b, s)){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_EDGE(s, e, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (CrappyCollisionMath.COLLIDE_EDGE_EDGE(e, (I_CrappyEdge) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (CrappyCollisionMath.COLLIDE_LINE_EDGE((I_CrappyLine) s, e, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (CrappyCollisionMath.POLYGON_EDGE_COLLISIONS((I_CrappyPolygon) s, e, deltaT)){
                            NOTIFY_ABOUT_COLLISION(s.getBody(), b);
                        }
                        break;
                }
            }
        }

    }

    /**
     * Handles collisions for polygons.
     * @param p the main shape
     * @param candidates everything it had a bounding box intersection with
     * @param deltaT timestep
     */
    static void HANDLE_COLLISIONS_POLYGON(
            final I_CrappyPolygon p,
            final Iterable<? extends CrappyShape_QuadTree_Interface> candidates,
            final double deltaT
    ){
        final CrappyBody_Shape_Interface b = p.getBody();
        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (CAN_COLLIDE(b, s)){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (CrappyCollisionMath.COLLIDE_CIRCLE_POLYGON((I_CrappyCircle) s, p, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (CrappyCollisionMath.POLYGON_EDGE_COLLISIONS(p, (I_CrappyEdge) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (CrappyCollisionMath.POLYGON_LINE_COLLISIONS(p, (I_CrappyLine) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (CrappyCollisionMath.POLYGON_POLYGON_COLLISIONS(p, (I_CrappyPolygon) s, deltaT)){
                            NOTIFY_ABOUT_COLLISION(b, s.getBody());
                        }
                        break;
                }

            }
        }

    }


    /**
     * Makes sure body A is allowed to collide with shape B's body, and vice versa
     * @param a first body
     * @param b the other shape
     * @return true if they're both allowed to collide with each other
     */
    private static boolean CAN_COLLIDE(final I_View_CrappyBody a, final IHaveBody b){
        return CAN_COLLIDE(a, b.getBody());
    }

    /**
     * Makes sure body A is allowed to collide with body B, and vice versa
     * @param a first body
     * @param b other body
     * @return true if they're both allowed to collide with each other
     */
    private static boolean CAN_COLLIDE(final I_View_CrappyBody a, final I_View_CrappyBody b){
        return a.allowedToCollideWith(b) && b.allowedToCollideWith(a);
    }
}
