package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.M_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.ArrayIterator;
import crappy.utils.containers.IPair;

import java.util.Iterator;


public class CrappyPolygon extends A_CrappyShape implements Iterable<CrappyEdge>{

    // TODO refactor so it's just an array of CrappyEdges with outward-facing normals connected to each other

    final int vertexCount;

    final Vect2D[] localVertices;

    final Vect2D[] localNormals;

    final Vect2D[] worldVertices;

    final Vect2D[] worldNormals;

    final CrappyEdge[] edges;

    final double area;


    /**
     * Basically the distance between the centroid and the closest point to centroid (anything within this distance can be considered a circle).
     */
    final double innerCircleRadius;

    final CrappyPolygonIncircle circle;

    public CrappyPolygon(final CrappyBody_Shape_Interface body, final Vect2D[] vertices){
        this(body, vertices, Vect2DMath.AREA_AND_CENTROID_OF_VECT2D_POLYGON(vertices));
    }


    public CrappyPolygon(
            final CrappyBody_Shape_Interface body,
            final Vect2D[] vertices,
            final IPair<Double, Vect2D> areaCentroid
    ){
        super(CRAPPY_SHAPE_TYPE.POLYGON, body, areaCentroid.getSecond(), vertices.length);

        area = areaCentroid.getFirst();
        vertexCount = vertices.length;
        localVertices = new Vect2D[vertexCount];
        localNormals = new Vect2D[vertexCount];
        worldVertices = new Vect2D[vertexCount];
        worldNormals = new Vect2D[vertexCount];
        edges = new CrappyEdge[vertexCount];

        body.setMomentOfInertia(Vect2DMath.POLYGON_MOMENT_OF_INERTIA_ABOUT_ZERO(body.getMass(), vertices));

        if (area < 0) {
            // if area is negative, we can copy them in directly as-is,
            // as that indicates that the vertices are ordered clockwise,
            // meaning the normals of these as edges will be pointing outwards.
            System.arraycopy(vertices, 0, localVertices, 0, vertexCount);
        } else {
            // if area is positive, however, that means the vertices are ordered anticlockwise,
            // meaning that the normals of these as edges will be pointing inwards,
            // which is not what we want, so we reverse the order of them when copying them
            // into the localVertices.
            for (int i = 0; i < vertexCount; i++) {
                localVertices[i] = vertices[vertexCount-1-i];
            }
        }
        Vect2D current = localVertices[0];
        for (int i = 1; i < vertexCount; i++) {
            Vect2D next = localVertices[(i < vertexCount-1)? i : 0];
            edges[i-1] = new CrappyEdge(current, next, body);
            worldVertices[i-1] = edges[i-1].getWorldStart();
            current = next;
        }

        IPair<Double, Double> min_max_radius = Vect2DMath.INCIRCLE_AND_MAX_MAGNITUDE_OFFSET(getCentroid(), localVertices);

        innerCircleRadius = min_max_radius.getFirst();
        radius = min_max_radius.getSecond();

        updateShape(body);

        circle = new CrappyPolygonIncircle(this, getCentroid(), innerCircleRadius);

        System.arraycopy(worldVertices, 0, finalWorldVertices, 0, vertexCount);

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

    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {
        thisFrameAABB.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                        rootTransform,
                        localVertices,
                        localNormals,
                        worldVertices,
                        worldNormals
                )
        );
        return thisFrameAABB;
    }

    @Override
    public void updateFinalWorldVertices() {
        Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT(body, localVertices, finalWorldVertices);
    }

    public I_CrappyCircle getIncircle(){
        return circle;
    }

    /**
     * Returns an iterator over the edges of this polygon
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<CrappyEdge> iterator() {
        return new ArrayIterator<>(edges);
    }



    private static class CrappyPolygonIncircle implements I_CrappyCircle, I_Transform {

        private final CrappyPolygon p;

        private final Vect2D localCentroid;

        private final double radius;

        private final Crappy_AABB aabb;

        CrappyPolygonIncircle(CrappyPolygon p, Vect2D centroid, double radius){
            this.p = p;
            this.localCentroid = centroid;
            this.radius = radius;
            aabb = new Crappy_AABB();
            aabb.update_aabb_circle(getPos(), radius);
        }

        @Override
        public CrappyBody_Shape_Interface getBody() {
            return p.getBody();
        }

        @Override
        public I_Transform getBodyTransform() {
            return this;
        }

        @Override
        public double getRadius() {
            return radius;
        }

        /**
         * Get world position of centre of mass
         *
         * @return position
         */
        @Override
        public Vect2D getPos() {
            return M_Vect2D.GET(localCentroid).rotate(getRot()).add(p.getPos()).finished();
        }

        /**
         * Get world rotation of body
         *
         * @return rotation
         */
        public Rot2D getRot() {
            return p.getBodyTransform().getRot();
        }

        /**
         * Get linear velocity of body
         *
         * @return linear velocity
         */
        @Override
        public Vect2D getVel() {
            return Vect2DMath.WORLD_VEL_OF_LOCAL_COORD_M(localCentroid, p.getBodyTransform()).finished();
        }

        /**
         * Get angular velocity of body (expressed as Z axis of this as a 3D vector, aka parallel to axis of rotation,
         * aka the actually important bit)
         *
         * @return angular velocity
         */
        @Override
        public double getAngVel() {
            return p.getAngVel();
        }

        @Override
        public I_Crappy_AABB getBoundingBox() {
            return aabb;
        }

        void startUpdateAABB(){

            aabb.update_aabb_circle(getPos(), radius);
        }

        void midUpdateAABB(){

            aabb.add_circle(getPos(), radius);
        }

        void endUpdateAABB(){
            aabb.add_circle(getPos(), radius);
        }

    }


}
