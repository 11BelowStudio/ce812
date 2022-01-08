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

    /**
     * plays the given clip.
     * @param clip the clip
     */
    private static void play(final Clip clip) {
        clip.setFramePosition(0);
        clip.start();
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
            AudioInputStream sample = AudioSystem.getAudioInputStream(SoundManager.class.getResourceAsStream(path + filename + ".wav"));
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

    /**
     * Toggles whether or not the thrusters should be playing
     * @param thrusting true if they are thrusting, false otherwise.
     */
    @SuppressWarnings("BooleanParameter")
    public static void togglePlayThrusters(final boolean thrusting){

        if (thrusting ^ isThrusting){
            if (thrusting){
                thruster.loop(Clip.LOOP_CONTINUOUSLY);
                play(thruster);
            } else {
                thruster.stop();
            }
            isThrusting = thrusting;
        }
    }
}
