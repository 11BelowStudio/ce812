package crappy.graphics;

import crappy.math.Vect2D;

public interface DrawableConnector {

    Vect2D getDrawableAPos();

    Vect2D getDrawableBPos();

    double getNaturalLength();

    void updateDrawables();
}
