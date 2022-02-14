/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.*;
import crappy.graphics.DrawableCrappyShape;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.*;

/**
 * An edge shape (one-way wall)
 * @author Rachel Lowe
 */
public class CrappyEdge extends A_CrappyShape implements I_CrappyEdge, DrawableCrappyShape.DrawableEdge {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    final Vect2D localStart;

    final Vect2D localProj;

    final Vect2D localTang;

    final Vect2D localNorm;

    final double length;

    final EdgePointCircle circle;

    Vect2D worldStart;

    Vect2D worldProj;

    Vect2D worldTang;

    Vect2D worldNorm;

    private Vect2D drawableStart;
    private Vect2D drawableEnd;
    private Vect2D drawableNorm;

    private final double depth;



    /**
     * Public constructor
     * @param body body which this shape will be attached to
     * @param localStart local start point of this edge
     * @param localProj local projection
     * @param depth thickness of edge
     */
    public CrappyEdge(
            final CrappyBody_ShapeSetter_Interface body, final Vect2D localStart, final Vect2D localProj, final double depth
    ){
        this(localStart, body, localProj, depth);

        body.__setShape__internalDoNotCallYourselfPlease(
                this, Vect2DMath.LINE_START_CENTROID_MOMENT_OF_INERTIA(localStart, getLocalCentroid(), body.getMass())
        );

        this.aabb.update_aabb(updateShape(body));

        this.aabb.enlarge(1.1);

        //this.aabb.enlarge(1.1);
    }

    CrappyEdge(
            final Vect2D localStart,
            final CrappyBody_Shape_Interface body,
            final Vect2D localProj,
            final double depth
    ) {
        super(body, CRAPPY_SHAPE_TYPE.EDGE, localStart.addScaled(localProj, 0.5));
        this.localStart = localStart;
        this.localProj = localProj;
        this.length = localProj.mag();
        this.localTang = localProj.mult(1/length); // probably quicker than calling norm when we already know the length
        this.localNorm = localTang.rotate90degreesAnticlockwise();
        this.depth = depth;
        this.aabb.update_aabb(updateShape(body));
        circle = new EdgePointCircle(this);
    }

    CrappyEdge(
            final Vect2D localStart,
            final I_Vect2D localEnd,
            final CrappyBody_Shape_Interface body,
            final double depth
    ){
        this(localStart, body, Vect2DMath.VECTOR_BETWEEN(localStart, localEnd), depth);
    }

    CrappyEdge(
            final Vect2D localStart,
            final Vect2D localEnd,
            final CrappyBody_Shape_Interface body,
            final Vect2D centroid,
            final double depth
    ){
        super(body, CRAPPY_SHAPE_TYPE.EDGE, centroid);
        this.localStart = localStart;
        this.localProj = Vect2DMath.VECTOR_BETWEEN(localStart, localEnd);
        this.length = localProj.mag();
        this.localTang = localProj.mult(1/length); // probably quicker than calling norm when we already know the length
        this.localNorm = localTang.rotate90degreesAnticlockwise();
        this.depth = depth;
        updateShape(body);
        circle = new EdgePointCircle(this);
    }

    @Override
    public Crappy_AABB updateShape(I_Transform rootTransform) {
        worldStart = localStart.localToWorldCoordinates(rootTransform);

        worldProj = localProj.rotate(rootTransform.getRot());
        worldTang = localTang.rotate(rootTransform.getRot());
        worldNorm = localNorm.rotate(rootTransform.getRot());

        thisFrameAABB.update_aabb_edge(worldStart, worldProj, worldNorm, depth);
        /*
        double d = depth;
        System.out.println(d);
        if (!Double.isFinite(depth)){
            d = 10;
        }
        this.aabb.add_point(worldStart.addScaled(worldNorm, -d));
        this.aabb.add_point(M_Vect2D.GET(worldStart).addScaled(worldNorm,-d).add(worldProj).finished());
        this.aabb.enlarge(1.25);

         */
        return thisFrameAABB;
    }



    @Override
    public void drawCrappily(I_CrappilyDrawStuff renderer) {
        if (renderable) {
            renderer.acceptEdge(this);
        }
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

    public void updateDrawables(){
        super.updateDrawables();
        synchronized (drawableSyncer){
            drawableStart = getWorldStart();
            drawableEnd = getWorldStart().add(getWorldProj());
            drawableNorm = getCentroid().add(getWorldNorm());
            circle.updateDrawables();
        }
    }

    @Override
    public Vect2D getDrawableStart() {
        synchronized (drawableSyncer) {
            return drawableStart;
        }
    }

    @Override
    public Vect2D getDrawableEnd() {
        synchronized (drawableSyncer) {
            return drawableEnd;
        }
    }

    @Override
    public Vect2D getDrawableNorm() {
        synchronized (drawableSyncer) {
            return drawableNorm;
        }
    }

    @Override
    public DrawableCircle getDrawableEndCircle() {
        synchronized (drawableSyncer) {
            return circle;
        }
    }

    public double getDepth(){
        return depth;
    }


    /**
     * A wrapper class which allows the start point of the edge to be expressed as a CrappyCircle,
     * for ease of collision handling.
     */
    private static class EdgePointCircle implements I_CrappyCircle, DrawableCircle{

        private final CrappyEdge edge;

        private final Crappy_AABB point_aabb;

        private Vect2D drawablePos;

        private Vect2D drawableVel;

        private final Object drawableSyncer = new Object();

        private final static double pointRadius = 0.001;


        EdgePointCircle(final CrappyEdge e){
            edge = e;
            point_aabb = new Crappy_AABB(e.getWorldStart(), pointRadius);
        }

        void startUpdateAABB(){
            point_aabb.add_circle(edge.getWorldStart(), pointRadius);
        }

        void midUpdateAABB(){
            point_aabb.add_circle(edge.getWorldStart(), pointRadius);
        }

        void endUpdateAABB(){
            point_aabb.add_circle(edge.getWorldStart(), pointRadius);
        }

        @Override
        public Vect2D getDrawablePos() {
            synchronized (drawableSyncer){
                return drawablePos;
            }
        }

        @Override
        public Vect2D getDrawableCentroid() {
            synchronized (drawableSyncer){
                return drawablePos;
            }
        }

        @Override
        public Vect2D getDrawableVel() {
            synchronized (drawableSyncer){
                return drawableVel;
            }
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
            return pointRadius;
        }

        @Override
        public Vect2D getDrawableRot() {
            return Vect2D.ZERO;
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

        /**
         * Where was the centroid of this object (in world coords) last frame?
         *
         * @return world coords of this object's centroid last frame.
         */
        @Override
        public Vect2D getLastFrameWorldPos() {
            return edge.getLastFrameWorldPos().add(edge.getLocalStart());
        }

        /**
         * What type of shape is this shape?
         *
         * @return this shape's shape type.
         */
        @Override
        public CRAPPY_SHAPE_TYPE getShapeType() {
            return CRAPPY_SHAPE_TYPE.CIRCLE;
        }

        /**
         * Use this to update the 'drawable' values in the shape
         */
        @Override
        public void updateDrawables() {
            synchronized (drawableSyncer){
                this.drawablePos = getPos();
                this.drawableVel = getVel();
            }
        }
    }
}