package crappy.shapes;


import crappy.CrappyBody_Shape_Interface;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;

import java.awt.*;

import static crappy.math.Vect2DMath.MINUS_M;

/**
 * A shape class
 */
public abstract class A_CrappyShape {

    public final CRAPPY_SHAPE_TYPE shapeType;

    public final CrappyBody_Shape_Interface body;

    final Crappy_AABB aabb;

    final Vect2D[] finalWorldVertices;

    final Vect2D localCentroid;

    double radius;

    // TODO: collision method

    A_CrappyShape(final CRAPPY_SHAPE_TYPE shapeType, final CrappyBody_Shape_Interface body, final Vect2D centroid) {
        this(shapeType, body,  centroid, 1);
    }

    A_CrappyShape(final CRAPPY_SHAPE_TYPE shapeType, final CrappyBody_Shape_Interface body, final Vect2D centroid, final int vertices){
        this.shapeType = shapeType;
        this.body = body;
        this.localCentroid = centroid;
        aabb = new Crappy_AABB();
        finalWorldVertices = new Vect2D[vertices];
    }


    /**
     * Something to define what each of these collision shapes are
     */
    public static enum CRAPPY_SHAPE_TYPE{
        CIRCLE,
        POLYGON,
        //COMPOUND_POLYGON,
        LINE
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

    public abstract Crappy_AABB updateShape();



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
