package pbgLecture2lab;

/**
 * A class that basically records the current controller action
 * Initially based on code provided for CE218,
 * expanded upon and tweaked for several other personal projects
 */
public class Action implements ActionView {

    /**
     * Whether or not the left button has been pressed
     */
    private boolean leftPressed;

    /**
     * Whether or not the right button has been pressed
     */
    private boolean rightPressed;

    /**
     * no-arg constructor, sets left and right to false
     */
    public Action(){
        leftPressed = false;
        rightPressed = false;
    }

    /**
     * Controller calls this when left is pressed.
     */
    void pressLeft(){
        leftPressed = true;
    }

    /**
     * Controller calls this when left is released.
     */
    void releaseLeft(){
        leftPressed = false;
    }

    /**
     * Controller calls this when right is pressed.
     */
    void pressRight(){
        rightPressed = true;
    }

    /**
     * Controller calls this when right is released.
     */
    void releaseRight(){
        rightPressed = false;
    }

    /**
     * Check if left is currently being pressed
     * @return true if left is currently being pressed
     */
    public boolean isLeftPressed(){
        return leftPressed;
    }

    /**
     * Check if right is currently being pressed
     * @return true if right is currently being pressed
     */
    public boolean isRightPressed(){
        return rightPressed;
    }


}
