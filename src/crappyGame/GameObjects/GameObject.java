package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.math.Rot2D;
import crappy.math.Vect2D;

public interface GameObject {

    Vect2D getPos();

    Rot2D getRot();

    CrappyBody getBody();

    Vect2D getVel();
}
