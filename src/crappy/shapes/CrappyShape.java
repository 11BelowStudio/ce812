package crappy.shapes;

import crappy.math.Rot2D;
import crappy.math.Vect2D;

public class CrappyShape {


    /**
     * Something to define what each of these collision shapes are
     */
    public static enum COLLISION_SHAPE_TYPE{
        CIRCLE,
        POLYGON,
        COMPOUND_POLYGON
    }

    public final COLLISION_SHAPE_TYPE shapeType;

    final Vect2D relativePosition;

    public final double radius;

    public final double mass;

    private final Vect2D[] bodyCoordinates;

    private final int vertices;

    private Crappy_AABB aabb;

    private Vect2D[] worldCoordinates;

    private CrappyShape[] childShapes;

    private final int children;


    /**
     * Constructor for circles.
     * @param radius the radius of the circle
     * @param mass the mass of the circle
     */
    private CrappyShape(final double radius, final double mass){

        relativePosition = Vect2D.ZERO;

        vertices = 1;
        children = 0;

        shapeType = COLLISION_SHAPE_TYPE.CIRCLE;
        this.radius = radius;
        this.mass = mass;
        bodyCoordinates = new Vect2D[]{Vect2D.ZERO};
        this.worldCoordinates = new Vect2D[]{Vect2D.ZERO};

        childShapes = new CrappyShape[0];

        aabb = new Crappy_AABB(new Vect2D(radius, radius), new Vect2D(-radius, -radius));

    }

    void updateWorldCoordinates(final Vect2D bodyPos, final Rot2D bodyRot){

        switch (shapeType){
            case COMPOUND_POLYGON:
                for (int i = 0; i < children; i++) {
                    childShapes[i].updateWorldCoordinates(
                            bodyPos.add(childShapes[i].relativePosition), bodyRot
                    );
                }
                break;
            case POLYGON:
                /*
                Vect2D.localToWorldCoordinatesForBodyToOut(
                        bodyPos, bodyRot, bodyCoordinates, worldCoordinates
                );
                aabb.update_aabb(I_Vect2D.min_and_max_varargs(worldCoordinates));

                 */

                aabb.update_aabb(
                        Vect2D.localToWorldCoordinatesForBodyToOutAndGetBounds(
                                bodyPos, bodyRot, bodyCoordinates, worldCoordinates
                        )
                );

                break;

            case CIRCLE:
                worldCoordinates[0] = bodyPos;
                aabb.update_aabb_circle(bodyPos, radius);
                break;
        }


    }




}
