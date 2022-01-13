package crappyGame;

import java.awt.Graphics2D;

/**
 * An interface for things that can be drawn.
 */
@FunctionalInterface
public interface Drawable {

    /**
     * Draw this object onto the given Graphics2D context
     * @param g graphics2D object
     */
    void draw(Graphics2D g);
}
