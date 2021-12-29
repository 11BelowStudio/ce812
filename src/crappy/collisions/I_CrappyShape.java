package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.Vect2D;

public interface I_CrappyShape extends I_HaveRestitution, I_HaveMass {

    CrappyBody_Shape_Interface getBody();

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

    double getRadius();

    /**
     * Obtains the centroid of this body in world coords.
     * @return centroid.
     * @implNote default implementation returns same as getPos(). Override if centroid may differ from getPos.
     */
    default Vect2D getCentroid(){
        return getBodyTransform().getPos();
    }

    default Vect2D getPos(){
        return getBodyTransform().getPos();
    }

    default Vect2D getVel(){
        return getBodyTransform().getVel();
    }

    default double getAngVel(){
        return getBodyTransform().getAngVel();
    }

}

interface I_HaveRestitution{

    double getRestitution();
}

interface I_HaveMass{

    double getMass();
}