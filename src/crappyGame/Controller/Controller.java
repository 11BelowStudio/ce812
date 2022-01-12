package crappyGame.Controller;

import crappyGame.UI.IHaveScaledDimensions;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Controller implements IController{

    private final Action act = new Action();

    private final IHaveScaledDimensions v;

    public Controller(final IHaveScaledDimensions h){
        v = h;
    }

    @Override
    public IAction getAction() {
        return act;
    }

    @Override
    public void timestepEndReset() {
        act.reset();
    }

    @Override
    public void resetAll() {
        act.resetAll();
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ESCAPE){
            act.justPressedAny();
        }
        switch (e.getKeyCode()){
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                act.pressed(Action.ACTION_ENUM.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                act.pressed(Action.ACTION_ENUM.RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                act.pressed(Action.ACTION_ENUM.SPACE);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                act.pressed(Action.ACTION_ENUM.UP);
                break;
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                act.released(Action.ACTION_ENUM.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                act.released(Action.ACTION_ENUM.RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                act.released(Action.ACTION_ENUM.SPACE);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                act.released(Action.ACTION_ENUM.UP);
                break;
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(final MouseEvent e) {
        act.clicked(v.getPointScaledFromCurrentToOriginal(e.getPoint()));
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }
}
