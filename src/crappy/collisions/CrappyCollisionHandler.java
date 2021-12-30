package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;

import java.util.Collection;

/**
 * Just contains the static method(s) for collision handling
 */
public final class CrappyCollisionHandler {

    /**
     * no constructing
     */
    private CrappyCollisionHandler(){}

    /**
     * Attempts to perform collision handling
     * @param body the body that has had collisions
     * @param bbIntersects everything that it had a bounding box intersect with
     * @param deltaT timestep
     */
    public static <T extends CrappyShape_QuadTree_Interface> void HANDLE_COLLISIONS(
            final T body,
            final Collection<? extends CrappyShape_QuadTree_Interface> bbIntersects,
            final double deltaT
    ){
        final I_CrappyShape s = body.getShape();

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

        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (c.getBody().anyMatchInBitmasks(s.getBody())){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (! CrappyCollisionMath.COLLIDE_CIRCLE_CIRCLE(c, s, deltaT)){
                            continue;
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (!CrappyCollisionMath.COLLIDE_CIRCLE_EDGE(c, (I_CrappyEdge) s, deltaT)){
                            continue;
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (!CrappyCollisionMath.COLLIDE_CIRCLE_LINE(c, (I_CrappyLine) s, deltaT)){
                            continue;
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (!CrappyCollisionMath.COLLIDE_CIRCLE_POLYGON(c, (I_CrappyPolygon) s, deltaT)){
                            continue;
                        }
                        break;
                }
                c.getBody().notifyAboutCollision(s.getBody());
                s.getBody().notifyAboutCollision(c.getBody());
            }
        }

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

        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (l.getBody().anyMatchInBitmasks(s.getBody())){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (! CrappyCollisionMath.COLLIDE_CIRCLE_LINE(s, l, deltaT)){
                            continue;
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (!CrappyCollisionMath.COLLIDE_LINE_EDGE(l, (I_CrappyEdge) s, deltaT)){
                            continue;
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (!CrappyCollisionMath.COLLIDE_LINE_LINE(l, (I_CrappyLine) s, deltaT)){
                            continue;
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (!CrappyCollisionMath.POLYGON_LINE_COLLISIONS((I_CrappyPolygon) s, l, deltaT)){
                            continue;
                        }
                        break;
                }
                l.getBody().notifyAboutCollision(s.getBody());
                s.getBody().notifyAboutCollision(l.getBody());
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

        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (e.getBody().anyMatchInBitmasks(s.getBody())){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (! CrappyCollisionMath.COLLIDE_CIRCLE_EDGE(s, e, deltaT)){
                            continue;
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (!CrappyCollisionMath.COLLIDE_EDGE_EDGE(e, (I_CrappyEdge) s, deltaT)){
                            continue;
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (!CrappyCollisionMath.COLLIDE_LINE_EDGE((I_CrappyLine) s, e, deltaT)){
                            continue;
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (!CrappyCollisionMath.POLYGON_EDGE_COLLISIONS((I_CrappyPolygon) s, e, deltaT)){
                            continue;
                        }
                        break;
                }
                e.getBody().notifyAboutCollision(s.getBody());
                s.getBody().notifyAboutCollision(e.getBody());
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

        for (final CrappyShape_QuadTree_Interface si: candidates) {
            final I_CrappyShape s = si.getShape();

            // assuming that the two objects are actually allowed to collide with each other,
            // we attempt to let them collide.
            if (p.getBody().anyMatchInBitmasks(s.getBody())){
                switch (s.getShapeType()){
                    case CIRCLE:
                        assert (s instanceof I_CrappyCircle);
                        if (! CrappyCollisionMath.COLLIDE_CIRCLE_POLYGON((I_CrappyCircle) s, p, deltaT)){
                            continue;
                        }
                        break;
                    case EDGE:
                        assert (s instanceof I_CrappyEdge);
                        if (!CrappyCollisionMath.POLYGON_EDGE_COLLISIONS(p, (I_CrappyEdge) s, deltaT)){
                            continue;
                        }
                        break;
                    case LINE:
                        assert (s instanceof I_CrappyLine);
                        if (!CrappyCollisionMath.POLYGON_LINE_COLLISIONS(p, (I_CrappyLine) s, deltaT)){
                            continue;
                        }
                        break;
                    case POLYGON:
                        assert (s instanceof I_CrappyPolygon);
                        if (!CrappyCollisionMath.POLYGON_POLYGON_COLLISIONS(p, (I_CrappyPolygon) s, deltaT)){
                            continue;
                        }
                        break;
                }
                p.getBody().notifyAboutCollision(s.getBody());
                s.getBody().notifyAboutCollision(p.getBody());
            }
        }

    }
}
