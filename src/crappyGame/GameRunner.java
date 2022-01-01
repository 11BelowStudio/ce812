package crappyGame;

import crappy.CrappyWorld;
import crappyGame.UI.DisplayFrame;
import crappyGame.UI.View;

import javax.swing.*;
import java.awt.*;

public class GameRunner implements IQuit, IChangeScenes, IPause, IGameRunner{

    public static void main(String[] args) {
        new GameRunner();
    }

    private boolean quitting = false;

    private final DisplayFrame display;

    private final View theView;

    private final Timer repaintTimer;

    private boolean isPaused = false;


    public GameRunner(){


        theView = new View();

        display = new DisplayFrame(theView, "placeholder", this);

        display.repackAndRevalidateAndSetVisible();

        repaintTimer = new Timer(CrappyWorld.DELAY, e -> theView.repaint());

    }







    public void quitPrompt(){

        notifyAboutPause(true);

        switch (JOptionPane.showConfirmDialog(
                display.getTheFrame(),
                "Do you want to quit?\nAll progress will be lost!\n(select yes to quit)",
                "you sure about that?",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
        )){
            case JOptionPane.YES_OPTION:
                JOptionPane.showMessageDialog(
                        display.getTheFrame(), "ok then", "bye",
                        JOptionPane.INFORMATION_MESSAGE
                );
                display.getTheFrame().dispose();
                System.exit(0);
                break;
            case JOptionPane.NO_OPTION:
                JOptionPane.showMessageDialog(
                        display.getTheFrame(), "resuming game", "get back to it",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
            default:
                break;
        }
        notifyAboutPause(false);

    }

    /**
     * Call this to notify the thing being viewed that it needs to be paused
     *
     * @param newPause true if it needs to be paused, false if it needs to be unpaused.
     */
    @SuppressWarnings("BooleanParameter")
    @Override
    public void notifyAboutPause(final boolean newPause) {

        // if no change is being made to this being paused, we don't pause it.
        if (newPause == isPaused){
            return;
        }
        isPaused = newPause; // and now we overwrite the 'isPaused' value

        theView.notifyAboutPause(isPaused);
        if(isPaused){
            repaintTimer.stop();
        } else {
            repaintTimer.restart();
        }


    }
}
