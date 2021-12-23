package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.math.Vect2D;
import static crappy.math.Vect2DMath.GET_BOUNDS_VARARGS;

public class CrappyLine extends A_CrappyShape{

    final int vertexCount;

    final Vect2D[] localVertices;


    final Vect2D[] worldVertices;


    /**
     * Constructor for a CrappyLine
     * @param body the CrappyBody
     * @param vertices line vertices. If only one vertex is defined, a vertex at (0,0) will be added.
     * @throws IllegalArgumentException if line has 0 vertices.
     */
    CrappyLine(final CrappyBody_Shape_Interface body, final Vect2D... vertices) {
        super(CRAPPY_SHAPE_TYPE.LINE, body, Math.max(2, vertices.length-1));

        int vCount = vertices.length;
        if (vCount < 1){
            throw new IllegalArgumentException("Cannot have a line with 0 vertices!");
        } else if (vCount == 1){
            vertexCount = 2;
            this.localVertices = new Vect2D[]{Vect2D.ZERO, vertices[0]};
        } else if (vCount == 2) {
            vertexCount = 2;
            this.localVertices = new Vect2D[vertexCount];
            System.arraycopy(vertices, 0, localVertices, 0, vertexCount);
        } else {
            throw new IllegalArgumentException(
                    "Don't define a line with more than 2 vertices! Please define them separately."
            );
        }
        this.worldVertices = new Vect2D[vertexCount];

        // TODO: calculate AABB


    }

    @Override
    public Crappy_AABB updateShape() {
        return aabb;
    }
}
