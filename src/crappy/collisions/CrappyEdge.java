package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

public class CrappyEdge extends A_CrappyShape implements I_CrappyEdge{

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
            final Vect2D localStart,
            final Vect2D localProj
    ) {
        super(body, CRAPPY_SHAPE_TYPE.EDGE, localStart.addScaled(localProj, 0.5));

        this.localStart = localStart;
        this.localProj = localProj;
        this.length = localProj.mag();
        this.localTang = localProj.mult(1/length); // probably quicker than calling norm when we already know the length
        this.localNorm = localTang.rotate90degreesAnticlockwise();

        updateShape(body);
    }

    CrappyEdge(
            final Vect2D localStart,
            final Vect2D localEnd,
            final CrappyBody_Shape_Interface body
    ){
        this(body, localStart, Vect2DMath.VECTOR_BETWEEN(localStart, localEnd));
    }

    CrappyEdge(
            final Vect2D localStart,
            final Vect2D localEnd,
            final CrappyBody_Shape_Interface body,
            final Vect2D centroid
    ){
        super(body, CRAPPY_SHAPE_TYPE.EDGE, centroid);
        this.localStart = localStart;
        this.localProj = Vect2DMath.VECTOR_BETWEEN(localStart, localEnd);
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

    public I_CrappyCircle getEndPointCircle(){
        return circle;
    }

    public Vect2D getWorldStart(){
        return worldStart;
    }

    public Vect2D getWorldProj(){
        return worldProj;
    }

    public Vect2D getWorldNorm(){
        return worldNorm;
    }

    public Vect2D getWorldTang(){ return worldTang; }

    public double getLength(){return length;}

    public Vect2D getLocalTang(){ return localTang; }

    public Vect2D getLocalStart(){ return localStart; }



    /**
     * A wrapper class which allows the start point of the edge to be expressed as a CrappyCircle,
     * for ease of collision handling.
     */
    static class EdgePointCircle implements I_CrappyCircle{

        private final I_CrappyEdge edge;

        private final Crappy_AABB point_aabb;


        EdgePointCircle(final CrappyEdge e){
            edge = e;
            point_aabb = new Crappy_AABB(e.worldStart);
        }

        void startUpdateAABB(){
            point_aabb.update_aabb(edge.getWorldStart());
        }

        void midUpdateAABB(){
            point_aabb.add_point(edge.getWorldStart());
        }

        void endUpdateAABB(){
            point_aabb.add_point(edge.getWorldStart());
        }

        @Override
        public CrappyBody_Shape_Interface getBody() {
            return edge.getBody();
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

        /**
         * Obtains the centroid of this body in local coords.
         *
         * @return centroid.
         */
        @Override
        public Vect2D getLocalCentroid() {
            return edge.getLocalStart();
        }

        @Override
        public Vect2D getPos() {
            return edge.getPos();
        }

        @Override
        public Vect2D getVel() {
            return edge.getLocalStart().getWorldVelocityOfLocalCoordinate(getBodyTransform());
        }

        @Override
        public I_Crappy_AABB getBoundingBox() {
            return point_aabb;
        }
    }
}
