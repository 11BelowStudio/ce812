package pbgLecture2lab;

/**
 * A view-only interface for the Action class.
 */
public interface ActionView {

    /**
     * Check if left is currently being pressed
     * @return true if left is currently being pressed
     */
    boolean isLeftPressed();

    /**
     * Check if right is currently being pressed
     * @return true if right is currently being pressed
     */
    boolean isRightPressed();
}
