package crappyGame.UI;

import crappyGame.IGameRunner;
import crappyGame.IQuit;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface I_DisplayFrame {

    JFrame getTheFrame();

    View getTheView();

    IGameRunner getRunner();

    void addKeyListener(final KeyListener k);

    void addMouseListener(final MouseListener m);

    void addMouseMotionListener(final MouseMotionListener m);

    void repackAndRevalidateAndSetVisible();

}
