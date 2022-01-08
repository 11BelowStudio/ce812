package crappyGame.UI;

import crappy.utils.containers.IPair;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * A class that provides a view of a model.
 */
public class View extends JComponent implements IHaveScaledDimensions {

    /**
     * Thing we're currently viewing.
     */
    private Viewable viewThis;

    public View(){
        this(new DefaultViewable());
    }

    public View(final Viewable v){
        setViewable(v);
    }

    public void setViewable(final Viewable v){
        assert (v != null);
        synchronized (this) {
            viewThis = v;
            super.setPreferredSize(v.getSize());
            super.setMinimumSize(v.getSize());
        }
    }


    @Override
    public void paintComponent(Graphics g0){

        final Graphics2D g = (Graphics2D) g0;

        final AffineTransform at = g.getTransform();

        final IPair<Double, Double> dimRatio = currentDimRatio();

        g.scale(dimRatio.getFirst(),dimRatio.getSecond());

        synchronized (this){
            viewThis.draw(g);
        }

        g.setTransform(at);

    }


    public void notifyAboutPause(boolean isPaused){
        synchronized (this) {
            viewThis.notifyAboutPause(isPaused);
        }
    }


    @Override
    public Dimension getOriginalDimensions() {
        return viewThis.getSize();
    }

    @Override
    public Dimension getCurrentDimensions() {
        return getSize();
    }


    /**
     * This is shown when there isn't anything else to show.
     */
    private static class DefaultViewable implements Viewable{

        private static final Dimension defaultDim = new Dimension(880,660);

        private static final Color defaultBG = new Color(41, 55, 75);

        private static final String placeholderText = "game is loading, pls to wait.";

        @Override
        public void draw(Graphics2D g) {

            g.setColor(defaultBG);

            g.fillRect(0, 0, defaultDim.width, defaultDim.height);

            g.setColor(Color.BLACK);

            g.drawString("Placeholder!", 441, 331);
            g.drawString("Placeholder!", 441, 329);
            g.drawString("Placeholder!", 439, 331);
            g.drawString("Placeholder!", 439, 329);

            g.setColor(Color.WHITE);
            g.drawString("Placeholder!", 440, 330);
        }

        @Override
        public Dimension getSize() {
            return defaultDim;
        }

        @Override
        public void notifyAboutPause(boolean isPaused) {}
    }
}
