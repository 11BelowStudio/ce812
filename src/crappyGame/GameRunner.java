package crappyGame;

import crappyGame.Controller.Controller;
import crappyGame.UI.DisplayFrame;
import crappyGame.UI.View;
import crappyGame.assets.SoundManager;
import crappyGame.models.LEVELS;
import crappyGame.models.TitleModel;

import javax.swing.*;

import java.awt.Component;

import static crappy.CrappyWorld.DEFAULT_UPDATE_DELAY;
import static crappy.CrappyWorld.DEFAULT_UPDATE_DELTA;

public class GameRunner implements IQuit, IChangeScenes, IPause, IGameRunner{

    public static void main(final String[] args) {

        System.out.println("*** A Scientific Recreation of Daily Life in the Space Towing Industry circa 3052 CE ***");

        new GameRunner();
    }

    private boolean quitting = false;

    private final DisplayFrame display;

    private final View theView;

    private final Timer repaintTimer;

    private boolean isPaused = false;

    private final Controller ctrl;

    private LEVELS currentLevel = LEVELS.__END_OF_GAME;

    private static final int DEFAULT_LIVES = 2;

    private A_Model currentModel;

    private boolean modelChanged = false;


    public GameRunner(){


        theView = new View();

        ctrl = new Controller(theView);

        display = new DisplayFrame(theView, "A Scientific Interpretation Of Daily Life In The Space Towing Industry Circa 3052 CE", this);

        display.addKeyListener(ctrl);
        display.addMouseListener(ctrl);

        display.repackAndRevalidateAndSetVisible();

        repaintTimer = new Timer(DEFAULT_UPDATE_DELAY, e -> theView.repaint());

        runIt();
    }

    public void runIt(){

        try{
            gameLoop();
        } catch (InterruptedException e){
            System.out.println("oh no.");
        }
    }


    private void gameLoop() throws InterruptedException{

        //SampleCrappyModel m = new SampleCrappyModel();


        //A_Model m = LEVELS.LEVEL1.generateThisLevel(ctrl, 3, 0, this);

        currentModel = new TitleModel(ctrl, this);
                //LEVELS.LEVEL3.generateThisLevel(ctrl, 3, 0, this);

        theView.setViewable(currentModel);

        display.getTheFrame().pack();

        long startTime; //when it started the current update() call
        long endTime; //when it finished the current update() call
        long timeout; //time it has to wait until it can next perform an update() call

        repaintTimer.start();

        do{

            startTime = System.currentTimeMillis();
            if (!isPaused) {
                //WILL ONLY UPDATE THE MODEL IF NOT PAUSED!
                currentModel.update(DEFAULT_UPDATE_DELTA);
            }
            if (modelChanged){
                theView.setViewable(currentModel);
                currentModel.update(DEFAULT_UPDATE_DELTA);
                modelChanged = false;
            }
            endTime = System.currentTimeMillis();
            timeout = DEFAULT_UPDATE_DELAY - (endTime - startTime);
            if (timeout > 0){
                Thread.sleep(timeout);
            }

        } while (!quitting);

        repaintTimer.stop();

    }






    public void quitPrompt(){

        notifyAboutPause(true);

        switch (JOptionPane.showConfirmDialog(
                display.getTheFrame().getContentPane(),
                "Do you want to quit?\nAll progress will be lost!\n(select yes to quit)",
                "you sure about that?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
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
            ctrl.resetAll();
            repaintTimer.stop();
        } else {
            repaintTimer.restart();
        }


    }

    @Override
    public void levelWon(double fuelUsed, int livesLeft) {
        modelChanged = true;
        ctrl.resetAll();
        if (currentLevel == LEVELS.__END_OF_GAME){
            JOptionPane.showMessageDialog(
                    display.getTheFrame().getContentPane(),
                    "<html><h1>INSTRUCTIONS</h1>" +
                            "<p>Fly into the cave, pick up the mythical Ball Of Being Towed In Space, and tow it back!<br>" +
                            "Also don't fly into the walls of the cave and don't allow the ball to hit the walls either</p><br>" +
                            "<br>" +
                            "<h2>CONTROLS</h2>" +
                            "<p>* Up arrow key or W to thrust your ship in the direction it's facing!<br>" +
                            "* Left/A to rotate anticlockwise, Right/D to rotate clockwise!<br>" +
                            "* Space (when close enough) to start towing the mythical Ball of Being Towed In Space!</p><br>" +
                            "<br>" +
                            "<p>Also see how much fuel it takes you to complete the game, maybe try to minimize it I guess</p>" +
                            "</html>",
                    "instructions I guess",
                    JOptionPane.INFORMATION_MESSAGE
            );
            SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.MAIN_THEME);
            currentLevel = LEVELS.LEVEL_1;
            livesLeft = DEFAULT_LIVES;
        } else {
            livesLeft++;
            currentLevel = currentLevel.nextLevel;
        }
        try{
            currentModel = currentLevel.generateThisLevel(ctrl, livesLeft, fuelUsed, this);
        } catch (LEVELS.EndOfGameException e){
            currentModel = new TitleModel(ctrl, this);
        }
        ctrl.resetAll();


    }

    @Override
    public void levelLost() {
        ctrl.resetAll();
        modelChanged = true;
        currentLevel = LEVELS.__END_OF_GAME;
        currentModel = new TitleModel(ctrl, this);
        ctrl.resetAll();
    }

    @Override
    public Component getViewComponent() {
        return display.getTheFrame().getContentPane();
    }
}
