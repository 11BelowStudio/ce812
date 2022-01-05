package crappyGame;

import crappyGame.UI.DisplayFrame;
import crappyGame.UI.View;

import javax.swing.*;

import static crappy.CrappyWorld.DELAY;

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

        repaintTimer = new Timer(DELAY, e -> theView.repaint());

        runIt();
    }

    public void runIt(){

        try{
            placeholderLoop();
        } catch (InterruptedException e){
            System.out.println("oh no.");
        }
    }


    private void placeholderLoop() throws InterruptedException{

        SampleCrappyModel m = new SampleCrappyModel();

        theView.setViewable(m);

        display.getTheFrame().pack();

        long startTime; //when it started the current update() call
        long endTime; //when it finished the current update() call
        long timeout; //time it has to wait until it can next perform an update() call

        repaintTimer.start();

        do{

            startTime = System.currentTimeMillis();
            if (!isPaused) {
                //WILL ONLY UPDATE THE MODEL IF NOT PAUSED!
                m.update();
            }
            endTime = System.currentTimeMillis();
            timeout = DELAY - (endTime - startTime);
            if (timeout > 0){
                Thread.sleep(timeout);
            }

        } while (!quitting);

        repaintTimer.stop();

    }


    /*
    private void mainLoop() throws InterruptedException {
        Model currentModel; //the model which currently is active
        boolean gameActive = true;//whether or not the game is the active model.
        // true by default so it swaps to the title screen on startup

        long startTime; //when it started the current update() call
        long endTime; //when it finished the current update() call
        long timeout; //time it has to wait until it can next perform an update() call

        while (true) { //the loop for swapping and replacing the game stuff

            currentModel = modelSwapper(gameActive); //obtains the model which is to be displayed by view
            gameActive = !gameActive; //if the game was active, it now isn't (and vice versa)
            view.showModel(currentModel); //gets the view to display the appropriate model
            frame.pack(); //repacks the frame
            repaintTimer.start(); //starts the repaintTimer

            //AND NOW THE MODEL UPDATE LOOP
            while (currentModel.keepGoing()){ //keeps updating the model until the endGame variable of it is true
                //basically updates the model once every 'DELAY' milliseconds (
                startTime = System.currentTimeMillis();
                if (!paused) {
                    //WILL ONLY UPDATE THE MODEL IF NOT PAUSED!
                    currentModel.update();
                }
                endTime = System.currentTimeMillis();
                timeout = DELAY - (endTime - startTime);
                if (timeout > 0){
                    Thread.sleep(timeout);
                }
            }

            repaintTimer.stop();
        }
    }

     */




    public void quitPrompt(){

        notifyAboutPause(true);

        switch (JOptionPane.showConfirmDialog(
                display.getTheFrame(),
                "Do you want to quit?\nAll progress will be lost!\n(select yes to quit)",
                "you sure about that?",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        )){
            case JOptionPane.YES_OPTION:
                /*
                JOptionPane.showMessageDialog(
                        display.getTheFrame(), "ok then", "bye",
                        JOptionPane.INFORMATION_MESSAGE
                );

                 */
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
