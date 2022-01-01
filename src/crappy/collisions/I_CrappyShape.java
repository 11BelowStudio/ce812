package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

/**
 * A view-only interface for collision shapes
 */
public interface I_CrappyShape extends I_HaveRestitution, I_HaveMass {

    /**
     * Obtain body this is attached to
     * @return the body
     */
    CrappyBody_Shape_Interface getBody();

    /**
     * Obtains transform of attached body
     * @return body transform
     */
    I_Transform getBodyTransform();

    default double getRestitution(){
        return getBody().getRestitution();
    }

    default double getMass(){
        return getBody().getMass();
    }

    /**
     * obtain moment of inertia
     * @return moment of inertia
     */
    default double getMInertia(){
        return getBody().getMomentOfInertia();
    }

    /**
     * obtain radius of this shape
     * @return radius
     */
    double getRadius();

    /**
     * Obtains the square of the radius
     * (easier to do distances when I don't need to perform any inverse square roots)
     * @return radius squared
     * @implNote default method squares result of radius on-the-fly,
     * could be made a bit faster by storing squared radius in advance and just returning that.
     */
    default double getRadiusSquared(){
        return Math.pow(getRadius(), 2);
    }

    /**
     * Obtains the centroid of this body in world coords.
     * @return centroid.
     */
    default Vect2D getCentroid(){
        return getLocalCentroid().localToWorldCoordinates(getBodyTransform());
    }

    /**
     * Obtains the centroid of this body in local coords.
     * @return centroid.
     */
    Vect2D getLocalCentroid();

    default Vect2D getPos(){
        return getBodyTransform().getPos();
    }

    default Vect2D getVel(){
        return getBodyTransform().getVel();
    }

    default double getAngVel(){
        return getBodyTransform().getAngVel();
    }

    I_Crappy_AABB getBoundingBox();

    default Vect2D getVelOfLocalPoint(final I_Vect2D localPos){
        return Vect2DMath.WORLD_VEL_OF_LOCAL_COORD_M(localPos, getBodyTransform()).finished();
    }

    default Vect2D getVelOfWorldPoint(final I_Vect2D worldPos){
        return getVelOfLocalPoint(Vect2DMath.WORLD_TO_LOCAL_M(worldPos, getBodyTransform()).finished());
    }


    /**
     * Works out whether a given point is in this shape or not
     * @param worldPoint the point we're checking
     * @return true if it's within this shape, false otherwise.
     */
    boolean isPointInShape(final I_Vect2D worldPoint);

    /**
     * What type of shape is this shape?
     * @return this shape's shape type.
     */
    CRAPPY_SHAPE_TYPE getShapeType();

    /**
     * Use this to update the 'drawable' values in the shape
     */
    void updateDrawables();

    /**
     * Something to define what each of these collision shapes are
     */
    static enum CRAPPY_SHAPE_TYPE{
        CIRCLE,
        POLYGON,
        //COMPOUND_POLYGON,
        LINE,
        EDGE
    }

}

interface I_HaveRestitution{

    double getRestitution();
}

interface I_HaveMass{

    double getMass();
}