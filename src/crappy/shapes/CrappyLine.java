package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;


public class CrappyLine extends A_CrappyShape{

    final int vertexCount;

    final Vect2D[] localVertices;


    final Vect2D[] worldVertices;

    /**
     * Constructor for a CrappyLine with a specified end point (implicitly with a point at 0,0 local coords)
     * @param body the CrappyBody which this line belongs to
     * @param end the end point for this line (in local coords)
     */
    public CrappyLine(final CrappyBody_Shape_Interface body, final Vect2D end){
        this(body, end, Vect2D.ZERO);
    }


    /**
     * Constructor for a CrappyLine
     * @param body the CrappyBody
     * @param start where this line starts
     * @param end where this line ends
     */
    public CrappyLine(final CrappyBody_Shape_Interface body, final Vect2D start, final Vect2D end) {
        super(CRAPPY_SHAPE_TYPE.LINE, body, Vect2DMath.MIDPOINT(start, end),  2);

        vertexCount = 2;
        this.localVertices = new Vect2D[]{start, end};
        this.worldVertices = new Vect2D[vertexCount];

        this.aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(body, localVertices, worldVertices)
        );


    }

    @Override
    public Crappy_AABB updateShape() {
        aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(body, localVertices, worldVertices)
        );
        return aabb;
    }
}
