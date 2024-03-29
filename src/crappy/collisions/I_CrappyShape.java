/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.IHaveIdentifier;
import crappy.I_Transform;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

import java.util.UUID;

/**
 * A view-only interface for collision shapes
 * @author Rachel Lowe
 */
public interface I_CrappyShape extends I_HaveRestitution, I_HaveMass, IHaveBody {

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

    /**
     * Obtains world pos of attached body
     * @return world pos of attached body
     */
    default Vect2D getPos(){
        return getBodyTransform().getPos();
    }

    /**
     * Obtains world rotation of attached body
     * @return world rotation of attached body
     */
    default Rot2D getRot(){ return getBodyTransform().getRot(); }

    /**
     * Obtains world velocity of attached body
     * @return world velocity of attached body
     */
    default Vect2D getVel(){
        return getBodyTransform().getVel();
    }

    /**
     * Obtains angular velocity of attached body
     * @return angular velocity of attached body
     */
    default double getAngVel(){
        return getBodyTransform().getAngVel();
    }

    /**
     * Obtains axis-aligned bounding box of shape
     * @return this shape's AABB (within the world)
     */
    I_Crappy_AABB getBoundingBox();

    /**
     * Where was the centroid of this object (in world coords) last frame?
     * @return world coords of this object's centroid last frame.
     */
    Vect2D getLastFrameWorldPos();

    /**
     * Obtains the world velocity of this local position on the body
     * @param localPos local pos on the body we want to get the world velocity of
     * @return world velocity of that point on the body
     */
    default Vect2D getVelOfLocalPoint(final I_Vect2D localPos){
        return Vect2DMath.WORLD_VEL_OF_LOCAL_COORD_M(localPos, getBodyTransform()).finished();
    }

    /**
     * Obtains the world velocity of the given world position on the body
     * @param worldPos position (relative to the world) of the body that we want to get the world velocity of
     * @return result of calling {@link #getVelOfLocalPoint(I_Vect2D)} using the world pos transformed to local pos
     */
    default Vect2D getVelOfWorldPoint(final I_Vect2D worldPos){
        return getVelOfLocalPoint(Vect2DMath.WORLD_TO_LOCAL_M(worldPos, getBodyTransform()).finished());
    }


    /**
     * Works out whether a given point in the world is in this shape or not
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

/**
 * Shortcut for obtaining restitution of shape
 */
@FunctionalInterface
interface I_HaveRestitution{

    /**
     * obtains coefficient of restitution
     * @return coefficient of restitution
     */
    double getRestitution();
}

/**
 * Shortcut for obtaining mass of shape
 */
@FunctionalInterface
interface I_HaveMass{

    /**
     * obtains mass of shape's body
     * @return mass
     */
    double getMass();
}

/**
 * Shortcut for obtaining body of shape
 */
@FunctionalInterface
interface IHaveBody extends IHaveIdentifier {
    /**
     * Obtain body this is attached to
     * @return the body
     */
    CrappyBody_Shape_Interface getBody();


    /**
     * Obtains unique identifier for this shape's body
     * @return the ID of the body.
     */
    @Override
    default UUID getID() {
        return getBody().getID();
    }
}