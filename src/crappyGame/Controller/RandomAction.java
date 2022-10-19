package crappyGame.Controller;

import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.GameObjects.Spaceship;

import java.util.Random;

public class RandomAction implements IAction {

    boolean leftHeld = false;

    boolean rightHeld = false;

    boolean upHeld = false;

    boolean spacePressed = false;

    boolean clicked = false;

    Vect2D clickPos = Vect2D.ZERO;

    boolean pressedAny = false;

    boolean pressedAnyDirection = false;

    private Random myRNG;

    private double leftRightValue;

    private Spaceship myShip;

    public static final double HALF_PI = Math.PI/2.0;

    public static final double THIRD_PI = Math.PI/3.0;

    public static final double QUARTER_PI = Math.PI/4.0;

    public RandomAction(Spaceship ship){
        myRNG = new Random();
        myShip = ship;
        updateMe(1);
        final Rot2D myRot = myShip.getRot();

        final double myAngle = myRot.angle();

        System.out.println(myAngle);
    }

    public RandomAction updateMe(double deltaT)
    {

        final Rot2D myRot = myShip.getRot();

        final double myAngle = myRot.angle();

        if (myAngle <= -THIRD_PI){

            //System.out.println("beyond half");
            leftHeld = true;
            rightHeld = false;
            leftRightValue = -2;

            upHeld = (myAngle >= -HALF_PI);

        } else if (myAngle >= THIRD_PI){
            leftHeld = false;
            rightHeld = true;
            leftRightValue = 2;

            upHeld = (myAngle <= HALF_PI);

        } else {
            final double thisFrameLeftRight = ((myRNG.nextDouble() *4)-2)*deltaT;
            double newLeftRight = leftRightValue + thisFrameLeftRight;
            if (newLeftRight > 3){
                newLeftRight -= 6;
            } else if (newLeftRight < -3){
                newLeftRight += 6;
            }
            leftRightValue = newLeftRight;

            if (leftRightValue <= -1){
                leftHeld = true;
                rightHeld = false;
            } else if (leftRightValue >= 1){
                leftHeld = false;
                rightHeld = true;
            } else {
                leftHeld = false;
                rightHeld =false;
            }

            upHeld = myRNG.nextInt(3) != 0;
        }

        pressedAnyDirection = (leftHeld | rightHeld | upHeld);
        pressedAny = pressedAnyDirection;
        return this;
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
        return false;
    }

    @Override
    public boolean pressedAny() {
        return pressedAny;
    }

    @Override
    public boolean anyDirectionPressed() {
        return pressedAnyDirection;
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
