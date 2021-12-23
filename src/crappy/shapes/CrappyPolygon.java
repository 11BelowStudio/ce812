package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;


public class CrappyPolygon extends A_CrappyShape{

    final int vertexCount;

    final Vect2D[] localVertices;

    final Vect2D[] localNormals;

    final Vect2D[] worldVertices;

    final Vect2D[] worldNormals;


    public CrappyPolygon(final CrappyBody_Shape_Interface body, final Vect2D[] vertices){
        super(CRAPPY_SHAPE_TYPE.POLYGON, body, vertices.length);

        vertexCount = vertices.length;
        localVertices = new Vect2D[vertexCount];
        localNormals = new Vect2D[vertexCount];
        worldVertices = new Vect2D[vertexCount];
        worldNormals = new Vect2D[vertexCount];

        System.arraycopy(vertices, 0, localVertices, 0, vertexCount);
        A_CrappyShape.NORMALS_TO_OUT(vertices, localNormals);
        radius = Vect2DMath.MAX_MAGNITUDE(localVertices);
        updateShape();

        System.arraycopy(worldVertices, 0, finalWorldVertices, 0, vertexCount);

    }


    @Override
    public Crappy_AABB updateShape() {
        aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                        body,
                        localVertices,
                        localNormals,
                        worldVertices,
                        worldNormals
                )
        );
        return aabb;
    }


}
