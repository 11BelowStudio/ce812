package crappyGame.models;

import crappyGame.Controller.IController;
import crappyGame.IGameRunner;
import crappyGame.assets.SoundManager;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.*;

/**
 * Level 3 is a subclass of the main GameLevel, because it has the Funny Ergodic Literature(tm)
 * which can only be delivered by subclassing GameLevel basically
 */
public class Level3 extends GameLevel{


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
        congratsWords.updateWords("Congratulations! You have survived the Space Towing Industry!");
    }


    @Override
    void createTowRope() {
        huh();
        super.createTowRope();
    }

    private void huh(){

        letsTalkAboutThis talkinBout = letsTalkAboutThis.INTRO;
        controller.resetAll();
        final Component p = runner.getViewComponent();
        SoundManager.togglePlayThrusters(false);
        SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.CONVERSATIONAL_INTERLUDE);
        runner.notifyAboutPause(true);
        do{
            talkinBout = talkinBout.talk(p);
        } while (talkinBout != letsTalkAboutThis.__END__);
        runner.notifyAboutPause(false);
        SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.MAIN_THEME);

    }
}


enum letsTalkAboutThis{

    __END__,
    INTRO,
    WAIT_WHAT,
    IM_A_TRUCKER,
    THE_NAME_OF_THE_GAME,
    HAHA_TOWROPE_GO_BRR,
    ok_and,
    WHY_DONT_YOU_FLY,
    IMMA_TOW_YOU,
    AD_HOMININ;


    letsTalkAboutThis talk(Component parent){

        switch (this){

            case INTRO:
                Object[] options = new Object[]{"wait what","I'm in the space towing industry!"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "I beg your pardon! What do you think you're doing here, you ruffain?",
                        "Excuse me???",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    case JOptionPane.YES_OPTION:
                        return WAIT_WHAT;
                    case JOptionPane.NO_OPTION:
                        return IM_A_TRUCKER;
                }
                break;
            case WAIT_WHAT:
                Object[] options1 = new Object[]{"That's because I'm in the space towing industry!","I'm space towing!"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "You know perfectly well, what I'm talking about, trying to abduct me like that!",
                        "I am disappointed",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options1,
                        options1[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    default:
                        return IM_A_TRUCKER;
                }
            case IM_A_TRUCKER:
                Object[] options2 = new Object[]{"But this game is about being a space tower!","DON'T CARE HAHA TOWROPE GO BRRRR"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html><p>"+
                                "Wrong. You are not a space tower. You are merely a person at a computer,<br>"+
                                "interacting with a program intended to provide a very inaccurate simulation of<br>"+
                                "how the laws of physics probably don't work, under the disguise of being an<br>"+
                                "incredibly inaccurate guesstimate of humanity's future idiocy, assuming that<br>"+
                                "it survives long enough to be able to participate in this idiocy.<br>"+
                                "You, the person who I am talking to, are not a space tower."+
                                "</p></html>",
                        "I see through your lies",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options2,
                        options2[0]
                )) {
                    case CLOSED_OPTION:
                        return this;
                    case YES_OPTION:
                        return THE_NAME_OF_THE_GAME;
                    case NO_OPTION:
                        return HAHA_TOWROPE_GO_BRR;
                }
                break;
            case HAHA_TOWROPE_GO_BRR:
                Object[] options3 = new Object[]{"I said \"HAHA TOWROPE GO BRRR\"", "But space towing is what the game is about!"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "Did you seriously not bother reading a word I said?",
                        "That's rude.",
                        YES_NO_OPTION,
                        QUESTION_MESSAGE,
                        null,
                        options3,
                        options3[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    case YES_OPTION:
                        return __END__;
                    case NO_OPTION:
                        return THE_NAME_OF_THE_GAME;
                }
                break;
            case THE_NAME_OF_THE_GAME:
                Object[] options4 = new Object[]{"Well, why don't you have a go at it?","ok and"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html><p>" +
                                "And, if you were a space tower, you would have known that there are plenty<br>" +
                                "of regulations which a member of the industry needs to follow, almost all<br>" +
                                "of which you have broken several times over. Heck, I've seen your piloting<br>" +
                                "in that lopsided spaceship of yours, you're nowhere near qualified to be<br>" +
                                "flying that thing in the first place!</p></html>",
                        ">:(",
                        YES_NO_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        options4,
                        options4[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    case YES_OPTION:
                        return WHY_DONT_YOU_FLY;
                    case NO_OPTION:
                        return ok_and;
                }
                break;
            case ok_and:
                Object[] options5 = new Object[]{"better shut up","better shut up"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "what",
                        "what",
                        YES_NO_OPTION,
                        QUESTION_MESSAGE,
                        null,
                        options5,
                        options5[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    default:
                        return __END__;
                }
            case WHY_DONT_YOU_FLY:
                Object[] options6 = new Object[]{"So why are you lecturing me about space towing?","You can't move? Sounds like you need to be towed! In space!"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html><p>" +
                                "I'm literally just a poorly-implemented rigidbody with a circle shape on it<br>" +
                                "as part of a poorly-implemented physics engine with an utterly terrible name<br>" +
                                "(which was made to allow the developer of it to give it that terrible name)<br>" +
                                "who is presently unable to move by itself (due to temporarily being made<br>" +
                                "immovable and intangible, until I recieve a prompt to become able to move)<br>" +
                                "in an equally terrible game that shamelessly copies <i>Thrust</i> (1986)<br>" +
                                "and is ultimately much worse at being <i>Thrust</i> than it." +
                                "</p></html>",
                        "bruh",
                        YES_NO_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        options6,
                        options6[0]
                )){
                    case YES_OPTION:
                        return AD_HOMININ;
                    case NO_OPTION:
                        return IMMA_TOW_YOU;
                    case CLOSED_OPTION:
                        return this;
                }
                break;
            case IMMA_TOW_YOU:
                Object[] options7 = new Object[]{"Yes","Well, strictly speaking, you're incapable of perambulation, but figuratively yes"};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html><p>" +
                                "I suppose I walked right into that one, didn't I?</p></html>",
                        "I give up",
                        YES_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        options7,
                        options7[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    default:
                        return __END__;
                }
            case AD_HOMININ:
                Object[] options8 = new Object[]{"In that case, I guess I'll just start towing you now","I'll take this as my cue to leave."};
                switch (JOptionPane.showOptionDialog(
                        parent,
                        "<html><p>" +
                                "Because you're the person who is reading these words on your screen that are<br>" +
                                "lecturing you on the specifics of towing and the space towing industry and the<br>" +
                                "nature of this software and the nature of these words you are reading,<br>" +
                                "instead of just skimming through them and pressing one of the buttons beneath<br>" +
                                "these words, ultimately choosing to be an active participant in this experience!<br>" +
                                "And as you're being an active participant, needing to put some effort into<br>" +
                                "reading these words, congratulations, you can now say that you have partaken<br>" +
                                "in experiencing some totes legit ergodic literature whenever you're at some sort<br>" +
                                "of high class dinner event or something along those lines and you need to appear<br>" +
                                "mildly cultured to not get shunned because we are <b>living</b> in a <b>society</b>.</p>" +
                                "<p>BOTTOM TEXT.</p></html>",
                        "bruhhhhh",
                        YES_NO_OPTION,
                        INFORMATION_MESSAGE,
                        null,
                        options8,
                        options8[0]
                )){
                    case CLOSED_OPTION:
                        return this;
                    default:
                        return __END__;
                }
        }
        return this;
    }


}
