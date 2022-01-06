package crappyGame.Controller;

import crappy.math.Vect2D;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public interface IController extends KeyListener, MouseListener {

    IAction getAction();

    void timestepEndReset();

}
