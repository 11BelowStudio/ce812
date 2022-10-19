package crappyGame.Controller;

import crappy.math.Vect2D;

public class PressedAnyAct implements IAction{
    @Override
    public boolean isLeftHeld() {
        return false;
    }

    @Override
    public boolean isRightHeld() {
        return false;
    }

    @Override
    public boolean isUpHeld() {
        return false;
    }

    @Override
    public boolean isSpacePressed() {
        return false;
    }

    @Override
    public boolean pressedAny() {
        return true;
    }

    @Override
    public boolean anyDirectionPressed() {
        return true;
    }

    @Override
    public boolean isLeftClick() {
        return false;
    }

    @Override
    public Vect2D getClickLocation() {
        return Vect2D.ZERO;
    }
}
