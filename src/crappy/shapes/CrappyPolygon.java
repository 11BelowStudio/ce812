package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.IPair;


public class CrappyPolygon extends A_CrappyShape{

    final int vertexCount;

    final Vect2D[] localVertices;

    final Vect2D[] localNormals;

    final Vect2D[] worldVertices;

    final Vect2D[] worldNormals;

    final double area;

    public CrappyPolygon(final CrappyBody_Shape_Interface body, final Vect2D[] vertices){
        this(body, vertices, Vect2DMath.AREA_AND_CENTROID_OF_VECT2D_POLYGON(vertices));
    }


    public CrappyPolygon(final CrappyBody_Shape_Interface body, final Vect2D[] vertices, final IPair<Double, Vect2D> areaCentroid){
        super(CRAPPY_SHAPE_TYPE.POLYGON, body, areaCentroid.getSecond(), vertices.length);

        area = areaCentroid.getFirst();
        vertexCount = vertices.length;
        localVertices = new Vect2D[vertexCount];
        localNormals = new Vect2D[vertexCount];
        worldVertices = new Vect2D[vertexCount];
        worldNormals = new Vect2D[vertexCount];

        System.arraycopy(vertices, 0, localVertices, 0, vertexCount);
        A_CrappyShape.NORMALS_TO_OUT(vertices, localNormals);
        radius = Vect2DMath.MAX_MAGNITUDE(localVertices);
        updateShape(body);

        System.arraycopy(worldVertices, 0, finalWorldVertices, 0, vertexCount);

    }


    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {
        aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                        rootTransform,
                        localVertices,
                        localNormals,
                        worldVertices,
                        worldNormals
                )
        );
        return aabb;
    }

    @Override
    public void updateFinalWorldVertices() {
        Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT(body, localVertices, finalWorldVertices);
    }


}
