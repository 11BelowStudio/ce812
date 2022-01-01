package crappyGame.UI;

import java.awt.Dimension;

public interface Viewable extends Drawable{

    Dimension getSize();

    /**
     * Call this to notify the thing being viewed that it needs to be paused
     * @param isPaused true if it needs to be paused, false if it needs to be unpaused.
     */
    void notifyAboutPause(boolean isPaused);
}
