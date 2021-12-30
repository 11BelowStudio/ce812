package crappy.collisions;

import crappy.math.Vect2D;

public interface I_CrappyEdge extends I_CrappyShape {


    Vect2D getWorldStart();

    Vect2D getWorldProj();

    Vect2D getWorldNorm();

    Vect2D getWorldTang();

    double getLength();

    Vect2D getLocalTang();

    default Vect2D getWorldEnd(){
        return getWorldStart().add(getWorldProj());
    }

    I_CrappyCircle getEndPointCircle();
}
