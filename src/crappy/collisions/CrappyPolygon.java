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


public class CrappyPolygon extends A_CrappyShape implements Iterable<I_CrappyEdge>, I_CrappyPolygon{

    // TODO refactor so it's just an array of CrappyEdges with outward-facing normals connected to each other

    final int vertexCount;

    /**
     * Positions of the vertices relative to origin (local coords, unrotated)
     */
    final Vect2D[] localVertices;

    /**
     * Positions of those vertices relative to centroid (local coords, unrotated)
     */
    final Vect2D[] localWhiskers;

    /**
     * pre-normalized local whiskers.
     */
    final Vect2D[] normalWhiskers;

    /**
     * Positions of vertices in world (relative to world (0,0), rotated)
     */
    final Vect2D[] worldVertices;

    /**
     * Projections from centroid to vertices (rotated)
     */
    final Vect2D[] worldWhiskers;

    /**
     * Pre-normalized world orientation whiskers.
     */
    final Vect2D[] worldNormalWhiskers;

    /**
     * The outer edges of this CrappyPolygon
     */
    final CrappyEdge[] edges;

    final double area;

    /**
     * The incircle (largest possible circle from centroid fully enclosed within the shape).
     * Anything within this distance from the centroid can be treated like a circle.
     */
    final CrappyPolygonIncircle circle;

    /**
     * The radius of aforementioned incircle.
     */
    final double innerCircleRadius;


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
        worldVertices = new Vect2D[vertexCount];
        edges = new CrappyEdge[vertexCount];

        localWhiskers = new Vect2D[vertexCount];
        worldWhiskers = new Vect2D[vertexCount];
        normalWhiskers = new Vect2D[vertexCount];
        worldNormalWhiskers = new Vect2D[vertexCount];

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
            final Vect2D next = localVertices[(i < vertexCount-1)? i : 0];
            edges[i-1] = new CrappyEdge(current, next, body, localCentroid);
            worldVertices[i-1] = edges[i-1].getWorldStart();
            current = next;
        }

        //final IPair<Double, Double> min_max_radius = Vect2DMath.INCIRCLE_AND_MAX_MAGNITUDE_OFFSET(getCentroid(), localVertices);

        final IPair<Double, Double> min_max_radius =
                Vect2DMath.INCIRCLE_AND_MAX_MAGNITUDE_OFFSET_ALSO_CENTROID_TO_CORNERS_TO_OUT(
                        getCentroid(), localVertices, localWhiskers
                );

        for (int i = 0; i < vertexCount; i++) {
            normalWhiskers[i] = localWhiskers[i].norm();
        }

        innerCircleRadius = min_max_radius.getFirst();
        radius = min_max_radius.getSecond();

        updateShape(body);

        circle = new CrappyPolygonIncircle(this, getCentroid(), innerCircleRadius);

        System.arraycopy(worldVertices, 0, finalWorldVertices, 0, vertexCount);

    }

    public void timestepStartUpdate(){
        super.timestepStartUpdate();
        for (int i = vertexCount; i >= 0; i--) {
            edges[i].timestepStartUpdate();
        }
        circle.startUpdateAABB();
    }

    @Override
    public void midTimestepUpdate() {
        super.midTimestepUpdate();
        for (int i = vertexCount; i >= 0; i--) {
            edges[i].midTimestepUpdate();
        }
        circle.midUpdateAABB();
    }

    @Override
    public void timestepEndUpdate(){
        super.timestepEndUpdate();
        for (int i = vertexCount; i >= 0; i--) {
            edges[i].timestepEndUpdate();
        }
        circle.endUpdateAABB();
    }

    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {
        thisFrameAABB.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                        rootTransform,
                        localVertices,
                        localWhiskers,
                        normalWhiskers,
                        worldVertices,
                        worldWhiskers,
                        worldNormalWhiskers
                )
        );
        for (int i = vertexCount; i >= 0; i--) {
            edges[i].updateShape(rootTransform);
        }
        return thisFrameAABB;
    }

    @Override
    public void updateFinalWorldVertices() {
        Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT(body, localVertices, finalWorldVertices);
        for (int i = vertexCount; i >= 0; i--) {
            edges[i].updateFinalWorldVertices();
        }
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
    public Iterator<I_CrappyEdge> iterator() {
        return new ArrayIterator<>(edges);
    }

    /**
     * Obtains the 'whiskers' from the centroid in world rotation
     *
     * @return the rotated 'whiskers' between centroid and vertices
     */
    @Override
    public Vect2D[] getWorldWhiskers() {
        return worldWhiskers;
    }

    /**
     * Obtains the normalized 'whiskers' in world rotation
     *
     * @return the normalized versions of the whiskers
     */
    @Override
    public Vect2D[] getWorldNormalWhiskers() {
        return worldNormalWhiskers;
    }

    /**
     * Get world whisker at given index
     *
     * @param i index of world whisker to get
     *
     * @return whisker at index i of getWorldWhiskers
     */
    @Override
    public Vect2D getWorldWhisker(final int i) {
        return worldWhiskers[i];
    }

    /**
     * Get normalized world whisker at given index
     *
     * @param i index of normalized world whisker to get
     *
     * @return whisker at index i of getWorldNormalWhiskers
     */
    @Override
    public Vect2D getWorldNormalWhisker(final int i) {
        return worldNormalWhiskers[i];
    }

    /**
     * Returns vertex count
     *
     * @return number of vertices
     */
    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Obtains the centroid of this body in world coords.
     * @return centroid.
     * @implNote Overridden, centroid is different.
     */
    public Vect2D getCentroid(){
        return Vect2DMath.LOCAL_TO_WORLD_M(localCentroid, getBodyTransform()).finished();
    }


    private static class CrappyPolygonIncircle implements I_CrappyCircle, I_Transform {

        private final CrappyPolygon p;

        private final Vect2D localCentroid;

        private final double radius;

        private final Crappy_AABB aabb;


        CrappyPolygonIncircle(final CrappyPolygon p, final Vect2D centroid, final double radius){
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
         * Obtains the centroid of this body in local coords.
         *
         * @return centroid.
         */
        @Override
        public Vect2D getLocalCentroid() {
            return localCentroid;
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
