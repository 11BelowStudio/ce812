package crappy.collisions;


import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.internals.CrappyInternalException;
import crappy.math.Vect2D;

import static crappy.math.Vect2DMath.MINUS_M;

/**
 * A shape class
 */
public abstract class A_CrappyShape {

    public final CRAPPY_SHAPE_TYPE shapeType;

    public final CrappyBody_Shape_Interface body;

    final Crappy_AABB aabb = new Crappy_AABB();

    final Crappy_AABB thisFrameAABB = new Crappy_AABB();

    final Crappy_AABB lastFrameAABB = new Crappy_AABB();

    final Vect2D[] finalWorldVertices;

    final Vect2D localCentroid;

    double radius;

    // TODO: collision method

    /**
     * CONSTRUCTOR FOR CIRCLES
     * @param shapeType shapeType (MUST BE CIRCLE!)
     * @param body the body which this CIRCLE is attached to
     * @param centroid centroid of this circle
     * @param rad radius
     * @throws crappy.internals.CrappyInternalException if shapeType isn't CIRCLE
     */
    A_CrappyShape(
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
        this.finalWorldVertices = new Vect2D[1];
        this.body = body;
        this.radius = rad;
    }

    /**
     * CONSTRUCTOR FOR POLYGONS
     * @param shapeType shapeType (MUST BE POLYGON!)
     * @param body the body which this shape is attached to
     * @param centroid centroid of this shape
     * @param vertices how many vertices this shape has
     * @throws CrappyInternalException if this is not used for a polygon shape.
     */
    A_CrappyShape(
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
        finalWorldVertices = new Vect2D[vertices];
    }


    /**
     * CONSTRUCTOR FOR LINES AND ALSO EDGES
     * @param body the body which this shape is attached to
     * @param shapeType shape type (MUST BE LINE OR EDGE!)
     * @param centroid the midpoint of this line/edge.
     * @throws CrappyInternalException if shapetype isn't Line or Edge
     */
    A_CrappyShape(
            final CrappyBody_Shape_Interface body,
            final CRAPPY_SHAPE_TYPE shapeType,
            final Vect2D centroid
    ){

        switch (shapeType){
            case LINE:
                this.finalWorldVertices = new Vect2D[2];
                break;
            case EDGE:
                this.finalWorldVertices = new Vect2D[1];
                break;
            default:
                throw new CrappyInternalException(
                        "This superclass constructor is for LINEs or EDGEs, not for " + shapeType + "!"
                );
        }

        this.shapeType = shapeType;
        this.localCentroid = centroid;
        this.body = body;
    }


    /**
     * Something to define what each of these collision shapes are
     */
    public static enum CRAPPY_SHAPE_TYPE{
        CIRCLE,
        POLYGON,
        //COMPOUND_POLYGON,
        LINE,
        EDGE
    }

    public double getRadius() {
        return radius;
    }

    /**
     * What type of shape is this?
     * @return what type of collision shape this is.
     */
    public CRAPPY_SHAPE_TYPE getShapeType() {
        return shapeType;
    }

    public Crappy_AABB getBoundingBox(){
        return aabb;
    }


    public void timestepStartUpdate(){
        aabb.update_aabb(thisFrameAABB);
    }

    public void midTimestepUpdate(){
        aabb.add_aabb(updateShape(body.getTempTransform()));
    }

    public void timestepEndUpdate(){

        updateShape(body);

        aabb.update_aabb_compound(lastFrameAABB, thisFrameAABB);

        lastFrameAABB.update_aabb(thisFrameAABB);

        updateFinalWorldVertices();
    }

    public abstract Crappy_AABB updateShape(final I_Transform rootTransform);

    public abstract void updateFinalWorldVertices();

    public I_Transform getBodyTransform(){
        return body;
    }

    public double getRestitution(){
        return body.getRestitution();
    }

    public double getMass(){
        return body.getMass();
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



}
