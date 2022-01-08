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

    boolean pressedAnyDirection = false;

    Action(){}

    void reset(){
        spacePressed = false;
        clicked = false;
        pressedAny = false;
        pressedAnyDirection = false;
    }

    void resetAll(){
        reset();
        upHeld = false;
        leftHeld = false;
        rightHeld = false;
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
        //return true;
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
    public boolean anyDirectionPressed(){ return pressedAnyDirection; }

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
                pressedAnyDirection = true;
                break;
            case RIGHT:
                rightHeld = true;
                pressedAnyDirection = true;
                break;
            case LEFT:
                leftHeld = true;
                pressedAnyDirection = true;
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

    @Override
    public String toString() {
        return "Action{" +
                "leftHeld=" + leftHeld +
                ", rightHeld=" + rightHeld +
                ", upHeld=" + upHeld +
                ", spacePressed=" + spacePressed +
                ", clicked=" + clicked +
                ", clickPos=" + clickPos +
                ", pressedAny=" + pressedAny +
                ", pressedAnyDirection=" + pressedAnyDirection +
                '}';
    }
}
