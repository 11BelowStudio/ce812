package crappyGame;

/**
 * An interface for things that can be paused.
 */
public interface IPause {

    /**
     * Call this to notify the thing being viewed that it needs to be paused
     * @param isPaused true if it needs to be paused, false if it needs to be unpaused.
     */
    @SuppressWarnings("BooleanParameter")
    void notifyAboutPause(boolean isPaused);
}
