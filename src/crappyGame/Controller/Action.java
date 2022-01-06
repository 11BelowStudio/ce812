package crappyGame.Controller;

import crappy.math.Vect2D;

public class Action implements IAction{

    boolean leftHeld = false;

    boolean rightHeld = false;

    boolean upHeld = false;

    boolean spacePressed = false;

    boolean clicked = false;

    Vect2D clickPos = Vect2D.ZERO;

    boolean pressedAny = false;

    Action(){}

    void reset(){
        spacePressed = false;
        clicked = false;
        pressedAny = false;
    }



    @Override
    public boolean isLeftHeld() {
        return leftHeld;
    }

    @Override
    public boolean isRightHeld() {
        return rightHeld;
    }

    @Override
    public boolean isUpHeld() {
        return upHeld;
    }

    @Override
    public boolean isSpacePressed() {
        return spacePressed;
    }

    @Override
    public boolean pressedAny() {
        return pressedAny;
    }

    @Override
    public boolean isLeftClick() {
        return clicked;
    }

    @Override
    public Vect2D getClickLocation() {
        return clickPos;
    }

    void clicked(Vect2D clickPos){
        pressedAny = true;
        clicked = true;
        this.clickPos = clickPos;
    }

    enum ACTION_ENUM{
        LEFT,
        RIGHT,
        UP,
        SPACE
    }


    void pressed(final ACTION_ENUM a){
        pressedAny = true;
        switch (a){
            case UP:
                upHeld = true;
                break;
            case RIGHT:
                rightHeld =true;
                break;
            case LEFT:
                leftHeld = true;
                break;
            case SPACE:
                spacePressed = true;
                break;
        }
    }

    void released(final ACTION_ENUM a){
        switch (a){
            case UP:
                upHeld = false;
                break;
            case RIGHT:
                rightHeld =false;
                break;
            case LEFT:
                leftHeld = false;
                break;
            case SPACE:
                spacePressed = false;
                break;
        }
    }
}
