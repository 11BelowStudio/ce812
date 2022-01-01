package crappyGame;

public interface IPause {

    /**
     * Call this to notify the thing being viewed that it needs to be paused
     * @param isPaused true if it needs to be paused, false if it needs to be unpaused.
     */
    void notifyAboutPause(boolean isPaused);
}
