package pbgLecture2lab;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;

/**
 * A class for listening to keyboard events and being a controller
 * Partially based on code originally implemented for CE218
 * (and re-tweaked for several other personal projects after that)
 */
public class Controller implements KeyListener {

    private Action action;

    public Controller(){
        action = new Action();
    }

    public ActionView getAction(){
        return action;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_LEFT:
                action.pressLeft();
                break;
            case KeyEvent.VK_RIGHT:
                action.pressRight();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_LEFT:
                action.releaseLeft();
                break;
            case KeyEvent.VK_RIGHT:
                action.releaseRight();
                break;
        }
    }
}
