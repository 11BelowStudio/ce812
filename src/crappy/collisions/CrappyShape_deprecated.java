package crappy.collisions;

import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

@Deprecated
public class CrappyShape_deprecated {


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

    private final Vect2D[] bodyNormals;

    private final int vertices;

    private Crappy_AABB aabb;

    private Vect2D[] worldCoordinates;

    private Vect2D[] worldNormals;

    private CrappyShape_deprecated[] childShapes;

    private final int children;


    /**
     * Constructor for circles.
     * @param radius the radius of the circle
     * @param mass the mass of the circle
     */
    private CrappyShape_deprecated(final double radius, final double mass){

        relativePosition = Vect2D.ZERO;

        vertices = 1;
        children = 0;

        shapeType = COLLISION_SHAPE_TYPE.CIRCLE;
        this.radius = radius;
        this.mass = mass;
        bodyCoordinates = new Vect2D[]{Vect2D.ZERO};
        this.worldCoordinates = new Vect2D[]{Vect2D.ZERO};
        bodyNormals = new Vect2D[]{Vect2D.ZERO};
        worldNormals = new Vect2D[]{Vect2D.ZERO};

        childShapes = new CrappyShape_deprecated[0];

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
                        Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(
                                bodyPos, bodyRot, bodyCoordinates, bodyNormals, worldCoordinates, worldNormals
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
