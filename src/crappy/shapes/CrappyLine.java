package crappy.shapes;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;


public class CrappyLine extends A_CrappyShape{

    // TODO: refactor so it's functionally just two CrappyEdges that are the reverse of each other

    final int vertexCount;

    final Vect2D[] localVertices;


    final Vect2D[] worldVertices;

    final Vect2D localProj;

    final Vect2D localNorm;

    final double length;

    /**
     * This is the world vector between the 'start' and the 'end' vector
     */
    Vect2D worldProj;

    /**
     * This is the world normal vector for this line.
     */
    Vect2D worldNorm;

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
        this.localProj = Vect2DMath.VECTOR_BETWEEN(start, end);
        this.localNorm = M_Vect2D.GET(localProj).rotate90degreesAnticlockwise().norm().finished();
        this.worldVertices = new Vect2D[vertexCount];

        length = localProj.mag();

        this.aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(body, localVertices, worldVertices)
        );

        worldProj = localProj.rotate(body.getRot());
        worldNorm = localNorm.rotate(body.getRot());

        body.setMomentOfInertia(Vect2DMath.LINE_MOMENT_OF_INERTIA(start, end, body.getMass()));

    }

    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {
        aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(rootTransform, localVertices, worldVertices)
        );
        worldProj = localProj.rotate(rootTransform.getRot());
        worldNorm = localNorm.rotate(rootTransform.getRot());
        return aabb;
    }

    @Override
    public void updateFinalWorldVertices() {
        Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT(body, localVertices, finalWorldVertices);
        worldProj = localProj.rotate(body.getRot());
        worldNorm = localNorm.rotate(body.getRot());
    }

    public Vect2D getWorldStart(){
        return worldVertices[0];
    }

    public Vect2D getWorldProj(){
        return worldProj;
    }

    public Vect2D getWorldNorm(){
        return worldNorm;
    }

    public Vect2D getWorldEnd(){
        return worldVertices[1];
    }

    public double getLength(){
        return length;
    }

}
