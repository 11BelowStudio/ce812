package crappyGame.assets;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Loosely based on the SoundManager code provided by Dr. Dimitri Ognibene
 * for CE218 Computer Game Programming (back in 2019),
 * and has since been modified heavily several times over by me.
 */
public final class SoundManager {

    private SoundManager(){}

    private static boolean isThrusting = false;

    private static final String path = "/crappyGame/assets/audio/";

    private static final Clip boom = getClip("explosion");

    private static final Clip thruster = getClip("thruster-y noise");

    private static final Clip clap = getClip("clap");

    private static final Clip plac = getClip("plac");

    private static final Clip solidHit = getClip("solidHit");

    private static final Clip scoredPoint = getClip("scored point!");

    private static final Clip towNoise = getClip("tow noise");

    private static final Clip towBroke = getClip("tow broke");

    private static final Clip mainTheme = getClip("Space Towin'");

    private static final Clip gameOverTheme = getClip("An ending");

    private static final Clip interludeTheme = getClip("conversational");

    private static final Clip ominousDrum = getClip("ominous drum");

    /**
     * An enumeration of values for the background soundtracks that may (or may not) currently be playing
     */
    public static enum MUSIC_THEMES{
        MAIN_THEME,
        GAME_OVER,
        CONVERSATIONAL_INTERLUDE,
        NO_MUSIC;
    }

    private static MUSIC_THEMES currentTheme = MUSIC_THEMES.NO_MUSIC;

    /**
     * Helper method to play the given clip from the start
     * @param clip the clip to play
     */
    private static void play(final Clip clip) {
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Helper method to play the given clip when it needs to be infinitely looping
     * @param clip the clip to play looped.
     */
    private static void playLooped(final Clip clip){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        play(clip);
    }


    /**
     * Loads the named clip
     * @param filename name of the clip
     * @return the clip.
     */
    private static Clip getClip(final String filename) {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            //AudioInputStream sample = AudioSystem.getAudioInputStream(new File(path + filename + ".wav"));
            AudioInputStream sample = AudioSystem.getAudioInputStream(SoundManager.class.getResource(path + filename + ".wav"));
            clip.open(sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clip;
    }

    public static void playClap(){
        play(clap);
    }

    public static void playPlac(){
        play(plac);
    }

    public static void playBoom(){
        play(boom);
    }

    public static void playSolidHit(){
        play(solidHit);
    }

    public static void playScored(){play(scoredPoint);}

    public static void playTowNoise(){play(towNoise);}

    public static void playTowBroke(){play(towBroke);}

    public static void playOminousDrum(){play(ominousDrum);}

    /**
     * Toggles whether or not the thrusters should be playing
     * @param thrusting true if they are thrusting, false otherwise.
     */
    @SuppressWarnings("BooleanParameter")
    public static void togglePlayThrusters(final boolean thrusting){

        if (thrusting ^ isThrusting){
            if (thrusting){
                playLooped(thruster);
            } else {
                thruster.stop();
            }
            isThrusting = thrusting;
        }
    }

    /**
     * Attempts to start playing the appropriate bit of background music.
     * If some different background music is currently playing, it will stop that music, before playing the newly
     * given music. If the music to play is already playing, it allows it to keep playing
     * @param playThis the MUSIC_THEMES value for the piece of background music which is needed.
     */
    public static void playBackgroundMusic(final MUSIC_THEMES playThis){

        MUSIC_THEMES current = currentTheme;
        if (playThis == current){
            return;
        }
        switch (current){
            case GAME_OVER:
                gameOverTheme.stop();
                break;
            case MAIN_THEME:
                mainTheme.stop();
                break;
            case CONVERSATIONAL_INTERLUDE:
                interludeTheme.stop();
                break;
        }
        switch (playThis){
            case CONVERSATIONAL_INTERLUDE:
                playLooped(interludeTheme);
                break;
            case MAIN_THEME:
                playLooped(mainTheme);
                break;
            case GAME_OVER:
                playLooped(gameOverTheme);
                break;
        }
        currentTheme = playThis;
    }


}
