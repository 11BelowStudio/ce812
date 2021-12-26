package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.Vect2D;

public class CrappyEdge extends A_CrappyShape{

    final Vect2D localStart;

    final Vect2D localProj;

    final Vect2D localTang;

    final Vect2D localNorm;

    final double length;

    final EdgePointCircle circle = new EdgePointCircle(this);

    Vect2D worldStart;

    Vect2D worldProj;

    Vect2D worldTang;

    Vect2D worldNorm;

    CrappyEdge(
            final CrappyBody_Shape_Interface body,
            final CRAPPY_SHAPE_TYPE shapeType,
            final Vect2D localStart,
            final Vect2D localProj
    ) {
        super(body, shapeType, localStart.addScaled(localProj, 0.5));

        this.localStart = localStart;
        this.localProj = localProj;
        this.length = localProj.mag();
        this.localTang = localProj.mult(1/length); // probably quicker than calling norm when we already know the length
        this.localNorm = localTang.rotate90degreesAnticlockwise();

        updateShape(body);
    }

    @Override
    public Crappy_AABB updateShape(I_Transform rootTransform) {
        worldStart = localStart.localToWorldCoordinates(rootTransform);

        worldProj = localProj.rotate(rootTransform.getRot());
        worldTang = localTang.rotate(rootTransform.getRot());
        worldNorm = localNorm.rotate(rootTransform.getRot());

        thisFrameAABB.update_aabb_edge(worldStart, worldProj);
        return thisFrameAABB;
    }

    @Override
    public void updateFinalWorldVertices() {
        updateShape(body);
    }

    public void timestepStartUpdate(){
        super.timestepStartUpdate();
        circle.startUpdateAABB();
    }

    @Override
    public void midTimestepUpdate() {
        super.midTimestepUpdate();
        circle.midUpdateAABB();
    }

    @Override
    public void timestepEndUpdate(){
        super.timestepEndUpdate();
        circle.endUpdateAABB();
    }

    /**
     * A wrapper class which allows the start point of the edge to be expressed as a CrappyCircle,
     * for ease of collision handling.
     */
    static class EdgePointCircle implements I_CrappyCircle{

        private final CrappyEdge edge;

        private final Crappy_AABB point_aabb;

        private Vect2D lastFramePos;

        EdgePointCircle(final CrappyEdge e){
            edge = e;
            point_aabb = new Crappy_AABB(e.worldStart);
            lastFramePos = e.worldStart;
        }

        void startUpdateAABB(){
            point_aabb.update_aabb(edge.worldStart);
        }

        void midUpdateAABB(){
            point_aabb.add_point(edge.worldStart);
        }

        void endUpdateAABB(){
            point_aabb.add_point(edge.worldStart);
            lastFramePos = edge.worldStart;
        }

        @Override
        public CrappyBody_Shape_Interface getBody() {
            return edge.body;
        }

        @Override
        public I_Transform getBodyTransform() {
            return edge.getBodyTransform();
        }

        @Override
        public double getRestitution() {
            return edge.getRestitution();
        }

        @Override
        public double getMass() {
            return edge.getMass();
        }

        @Override
        public double getRadius() {
            return 0;
        }

        @Override
        public Vect2D getPos() {
            return edge.worldStart;
        }

        @Override
        public Vect2D getVel() {
            return edge.localStart.getWorldVelocityOfLocalCoordinate(getBodyTransform());
        }
    }
}