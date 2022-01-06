package crappyGame.models;

import crappy.math.Rot2D;
import crappy.math.Vect2D;

public interface IRecieveDebris {

    public void addDebris(Vect2D fromPos, Rot2D fromRot, Vect2D fromVel, int debrisToAdd);
}
