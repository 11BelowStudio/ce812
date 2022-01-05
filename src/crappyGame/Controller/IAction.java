package crappyGame.Controller;

import crappy.math.Vect2D;

public interface IAction {


    boolean isLeftPressed();

    boolean isRightPressed();

    boolean isUpPressed();

    boolean isSpacePressed();

    Vect2D isLeftClick();

    Vect2D getClickLocation();


}
