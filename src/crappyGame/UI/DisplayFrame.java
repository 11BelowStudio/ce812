package crappyGame.UI;

import crappyGame.IGameRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Dimension2D;

public class DisplayFrame implements I_DisplayFrame {

    private final JFrame theFrame;

    private final View view;

    private final IGameRunner runner;

    public JFrame getTheFrame() {
        return theFrame;
    }

    @Override
    public View getTheView() {
        return view;
    }

    public IGameRunner getRunner(){
        return runner;
    }

    public DisplayFrame(final View v, final String frameName, final IGameRunner runner){

        theFrame = new JFrame(frameName);

        view = v;

        this.runner = runner;

        final LayoutManager boxLayout = new BoxLayout(theFrame.getContentPane(), BoxLayout.Y_AXIS);

        theFrame.getContentPane().setLayout(boxLayout);
        theFrame.getContentPane().add(v);

        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        theFrame.addWindowListener(new DisplayFrameWindowListener(runner));



    }

    public void addKeyListener(final KeyListener k){
        theFrame.addKeyListener(k);
    }

    public void addMouseListener(final MouseListener m){
        theFrame.getContentPane().addMouseListener(m);
    }

    public void addMouseMotionListener(final MouseMotionListener m){
        theFrame.getContentPane().addMouseMotionListener(m);
    }

    @Override
    public void repackAndRevalidateAndSetVisible() {
        theFrame.pack();
        theFrame.revalidate();
        theFrame.repaint();
        theFrame.setVisible(true);
    }

    public View getView(){
        return view;
    }






    /**
     * A window listener used to notify the game runner if the frame is minimized/user attempts quitting
     */
    private static class DisplayFrameWindowListener extends WindowAdapter {

        private final IGameRunner runner;

        DisplayFrameWindowListener(final IGameRunner r){
            this.runner = r;
        }

        /**
         * Invoked when the user attempts to close the window from the window's system menu.
         *
         * calls the quit prompt method of the runner
         *
         * @param e the event to be processed
         */
        @Override
        public void windowClosing(WindowEvent e) {
            runner.quitPrompt();
        }


        /**
         * Invoked when a window is changed from a normal to a minimized state. For many platforms, a minimized window
         * is displayed as the icon specified in the window's iconImage property.
         *
         * pauses the game when it is minimized
         *
         * @param e the event to be processed
         *
         * @see Frame#setIconImage
         */
        @Override
        public void windowIconified(WindowEvent e) {
            runner.notifyAboutPause(true);
        }

    }
}
