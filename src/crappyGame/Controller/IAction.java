package crappyGame.Controller;

import crappy.math.Vect2D;

public interface IAction {


    boolean isLeftHeld();

    boolean isRightHeld();

    boolean isUpHeld();

    boolean isSpacePressed();

    boolean pressedAny();

    boolean anyDirectionPressed();

    boolean isLeftClick();

    Vect2D getClickLocation();

}
