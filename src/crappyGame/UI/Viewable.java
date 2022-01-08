package crappyGame.UI;

import crappyGame.Drawable;

import java.awt.Dimension;

/**
 * Interface for things that the view can view.
 */
public interface Viewable extends Drawable {

    Dimension getSize();

    /**
     * Call this to notify the thing being viewed that it needs to be paused
     * @param isPaused true if it needs to be paused, false if it needs to be unpaused.
     */
    void notifyAboutPause(boolean isPaused);
}
