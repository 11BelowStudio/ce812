package crappyGame.models;

import crappy.math.Vect2D;
import crappyGame.Controller.IController;
import crappyGame.IGameRunner;
import crappyGame.assets.SoundManager;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.*;

/**
 * Level 3 is a subclass of the main FlipGravityLevel, because it has the Funny Ergodic Literature(tm)
 * which can only be delivered by subclassing FlipGravityLevel basically
 */
public class Level3 extends FlipGravityLevel{

    private boolean notHadTheTalk = true;

    public Level3(
            IController ctrl,
            int lives,
            double fuel,
            IGameRunner runner
    ) {
        super(
                ctrl,
                LevelGeometry.makeLevel3(VISIBLE_WORLD_WIDTH, VISIBLE_WORLD_HEIGHT),
                lives,
                fuel,
                runner
        );
        notHadTheTalk = true;
    }



    @Override
    void createTowRope() {
        huh();
        super.createTowRope();
    }

    private void huh(){

        if (notHadTheTalk) {
            notHadTheTalk = false;
            letsTalkAboutThis talkinBout = letsTalkAboutThis.INTRO;
            controller.resetAll();
            final Component p = runner.getViewComponent();
            SoundManager.togglePlayThrusters(false);
            SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.CONVERSATIONAL_INTERLUDE);
            runner.notifyAboutPause(true);
            do {
                talkinBout = talkinBout.talk(p);
            } while (talkinBout != letsTalkAboutThis.__END__);
            runner.notifyAboutPause(false);
            SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.MAIN_THEME);
        }

    }

    @Override
    void won() {
        super.won();
        //System.out.println("CONGARTULATION!");
        //System.out.println(fuelUsedHUD.getWords());
    }
}

/**
 * Time for some ergodic literature.
 */
enum letsTalkAboutThis{

    __END__,
    INTRO,
    WAIT_WHAT,
    UNFORESEEN,
    WHY_YES_I_AM_MOVING_IT,
    REGRETS;


    /**
     * Yes, this is literally just a hypertext game, delivered via the medium of JOptionPanes
     * @param parent parent component for the JOptionPane
     * @return the appropriate next state given the choice made by the player at the current state.
     */
    letsTalkAboutThis talk(final Component parent){

        switch (this){

            case INTRO:
                Object[] options = new Object[]{"wait what","Why yes, I do know that!"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html>" +
                                "<h1>FUN SPACE FACT!</h1>" +
                                "<p>" +
                                "Did <i>you</i> know that this circle right here<br/>" +
                                "is responsible for making the gravity here point downwards?</p>" +
                                "</html>",
                        "Fun Facts!",
                        YES_NO_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    case YES_OPTION:
                        return WAIT_WHAT;
                    case NO_OPTION:
                        return UNFORESEEN;
                }
                break;
            case WAIT_WHAT:
                Object[] options1 = new Object[]{"oh no","oh yes"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html>" +
                                "<h1>FUN SPACE FACT!</h1>" +
                                "<p>" +
                                "Did <i>you</i> know that moving this will have<br/>" +
                                "<b>unforeseen consequences?</b>" +
                                "</p>" +
                                "</html>",
                        "Fun Facts!",
                        YES_NO_OPTION,
                        QUESTION_MESSAGE,
                        null,
                        options1,
                        options1[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    default:
                        return UNFORESEEN;
                }

            case UNFORESEEN:
                Object[] options2 = new Object[]{"can I stop moving it?","oh yes"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html>" +
                                "<h1>FUN SPACE FACT!</h1>" +
                                "<p>" +
                                "Did <i>you</i> know that you have started<br/>" +
                                "<b>to move this?</b>" +
                                "</p>" +
                                "</html>",
                        "Fun Facts!",
                        JOptionPane.YES_NO_OPTION,
                        WARNING_MESSAGE,
                        null,
                        options2,
                        options2[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    case YES_OPTION:
                        return REGRETS;
                    case NO_OPTION:
                        return WHY_YES_I_AM_MOVING_IT;
                }
                break;
            case REGRETS:
                JOptionPane.showMessageDialog(
                        parent,
                        "<html>" +
                                "<h1>FUN SPACE FACT!</h1>" +
                                "<p>" +
                                "This transaction is <i><b>NON-REFUNDABLE!</b></i>" +
                                "</p>" +
                                "</html>",
                        "Fun Facts!",
                        ERROR_MESSAGE,
                        null
                );
                return __END__;
            case WHY_YES_I_AM_MOVING_IT:
                JOptionPane.showMessageDialog(
                        parent,
                        "<html>" +
                                "<h1>FUN SPACE FACT!</h1>" +
                                "<p>have fun :)</p>" +
                                "</html>",
                        "Fun Facts!",
                        INFORMATION_MESSAGE,
                        null
                );
                return __END__;

        }
        return this;
    }


}
