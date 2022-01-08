package crappy.collisions;


import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.graphics.DrawableCrappyShape;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.internals.CrappyInternalException;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

import java.util.Objects;

import static crappy.math.Vect2DMath.MINUS_M;

/**
 * A shape class
 *
 * @author Rachel Lowe
 */
public abstract class A_CrappyShape implements CrappyShape_QuadTree_Interface, I_CrappyShape, DrawableCrappyShape {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */

    public final CRAPPY_SHAPE_TYPE shapeType;

    public final CrappyBody_Shape_Interface body;

    final Crappy_AABB aabb = new Crappy_AABB();

    final Crappy_AABB thisFrameAABB = new Crappy_AABB();

    final Crappy_AABB lastFrameAABB = new Crappy_AABB();

    final Vect2D localCentroid;

    double radius;

    double radiusSquared;

    final Object syncer = new Object();

    final Object drawableSyncer = new Object();

    Vect2D drawableWorldCentroid;

    Vect2D drawableWorldPos;

    Vect2D drawableVel;

    /**
     * Is this considered renderable by the default draw operation?
     */
    public boolean renderable = true;

    // TODO: collision method

    /**
     * CONSTRUCTOR FOR CIRCLES
     * @param shapeType shapeType (MUST BE CIRCLE!)
     * @param body the body which this CIRCLE is attached to
     * @param centroid centroid of this circle
     * @param rad radius
     * @throws crappy.internals.CrappyInternalException if shapeType isn't CIRCLE
     */
    public A_CrappyShape(
            final CRAPPY_SHAPE_TYPE shapeType,
            final Vect2D centroid,
            final CrappyBody_Shape_Interface body,
            final double rad
    ){
        if (shapeType != CRAPPY_SHAPE_TYPE.CIRCLE){
            throw new CrappyInternalException("This superclass constructor is for circles only!");
        }
        this.shapeType = shapeType;
        this.localCentroid = centroid;
        this.body = body;
        this.radius = rad;
        this.radiusSquared = Math.pow(radius, 2);
    }

    /**
     * CONSTRUCTOR FOR POLYGONS
     * @param shapeType shapeType (MUST BE POLYGON!)
     * @param body the body which this shape is attached to
     * @param centroid centroid of this shape
     * @param vertices how many vertices this shape has
     * @throws CrappyInternalException if this is not used for a polygon shape.
     */
    public A_CrappyShape(
            final CRAPPY_SHAPE_TYPE shapeType,
            final CrappyBody_Shape_Interface body,
            final Vect2D centroid,
            final int vertices
    ){
        if (shapeType != CRAPPY_SHAPE_TYPE.POLYGON){
            throw new CrappyInternalException("This superclass constructor is for polygons only!");
        }
        this.shapeType = shapeType;
        this.body = body;
        this.localCentroid = centroid;
    }


    /**
     * CONSTRUCTOR FOR LINES AND ALSO EDGES
     * @param body the body which this shape is attached to
     * @param shapeType shape type (MUST BE LINE OR EDGE!)
     * @param centroid the midpoint of this line/edge.
     * @throws CrappyInternalException if shapetype isn't Line or Edge
     */
    public A_CrappyShape(
            final CrappyBody_Shape_Interface body,
            final CRAPPY_SHAPE_TYPE shapeType,
            final Vect2D centroid
    ){

        switch (shapeType){
            case LINE:
            case EDGE:
                break;
            default:
                throw new CrappyInternalException(
                        "This superclass constructor is for LINEs or EDGEs, not for " + shapeType + "!"
                );
        }

        this.shapeType = shapeType;
        this.localCentroid = centroid;
        this.body = body;
        this.radius = 0;
        this.radiusSquared = 0;
    }

    public double getRadius() {
        synchronized (syncer) {
            return radius;
        }
    }

    /**
     * What type of shape is this?
     * @return what type of collision shape this is.
     */
    public CRAPPY_SHAPE_TYPE getShapeType() {
        return shapeType;
    }

    @Override
    public Crappy_AABB getBoundingBox(){
        return aabb;
    }


    public void timestepStartUpdate(){
        thisFrameAABB.update_aabb(aabb);
    }

    public void midTimestepUpdate(){
        thisFrameAABB.add_aabb(updateShape(getBodyTransform()));
    }

    public void timestepEndUpdate(){
        updateShape(getBodyTransform());
        aabb.update_aabb_compound(lastFrameAABB, thisFrameAABB);
        lastFrameAABB.update_aabb(thisFrameAABB);
    }

    @Override
    public CrappyBody_Shape_Interface getBody() {
        return body;
    }

    public abstract Crappy_AABB updateShape(final I_Transform rootTransform);

    public I_Transform getBodyTransform(){
        return body.getTempTransform();
    }

    public double getRestitution(){
        return body.getRestitution();
    }

    public double getMass(){
        return body.getMass();
    }

    public double getMInertia(){ return body.getMomentOfInertia(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        A_CrappyShape that = (A_CrappyShape) o;
        return shapeType == that.shapeType && body.equalsID(that.body) && this.localCentroid.equals(that.localCentroid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shapeType, body.getID(), localCentroid);
    }

    @Override
    public A_CrappyShape getShape(){ return this; }

    @Override
    public Vect2D getLocalCentroid() {
        return localCentroid;
    }


    /**
     * Obtains the centroid of this body in world coords.
     * @return centroid.
     */
    public Vect2D getCentroid(){
        synchronized (syncer) {
            return getLocalCentroid().localToWorldCoordinates(getBodyTransform());
        }
    }

    public Vect2D getLastFrameWorldPos(){
        return Vect2DMath.LOCAL_TO_WORLD_M(getCentroid(), body.getLastPos(), body.getLastRot()).finished();
    }

    /**
     * Computes the normals for a polygon shape
     * @param vertices array holding all of the vertices
     * @param out empty array which the normals will be put into
     */
    static void NORMALS_TO_OUT(final Vect2D[] vertices, final Vect2D[] out){
        for (int i = 0; i < vertices.length-2; i++){
            out[i] = MINUS_M(vertices[i], vertices[i+1]).norm().rotate90degreesAnticlockwise().finished();
        }
        out[vertices.length-1] = MINUS_M(vertices[vertices.length-1], vertices[0]).norm().rotate90degreesAnticlockwise().finished();
    }


    public abstract void drawCrappily(final I_CrappilyDrawStuff renderer);


    public void updateDrawables(){
        synchronized (drawableSyncer){
            drawableWorldPos = getPos();
            drawableWorldCentroid = getCentroid();
            drawableVel = getVel();
        }

    }

    public Vect2D getDrawableCentroid(){
        synchronized (drawableSyncer) {
            return drawableWorldCentroid;
        }
    }

    public Vect2D getDrawablePos(){
        synchronized (drawableSyncer) {
            return drawableWorldPos;
        }
    }

    public Vect2D getDrawableVel(){
        synchronized (drawableSyncer) {
            return getVel();
        }
    }

    @Override
    public String toString() {
        return "A_CrappyShape{" +
                "shapeType=" + shapeType +
                ", aabb=" + aabb +
                ", thisFrameAABB=" + thisFrameAABB +
                ", lastFrameAABB=" + lastFrameAABB +
                ", localCentroid=" + localCentroid +
                ", radius=" + radius +
                ", radiusSquared=" + radiusSquared +
                ", syncer=" + syncer +
                ", drawableSyncer=" + drawableSyncer +
                ", drawableWorldCentroid=" + drawableWorldCentroid +
                ", drawableWorldPos=" + drawableWorldPos +
                ", drawableVel=" + drawableVel +
                '}';
    }
}
