package crappyGame.UI;

import crappy.math.Vect2D;
import crappy.utils.containers.IPair;
import crappyGame.IPause;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class View extends JComponent implements IHaveScaledDimensions {



    Viewable viewThis;

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

        AffineTransform at = g.getTransform();

        IPair<Double, Double> dimRatio = currentDimRatio();

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



    private static class DefaultViewable implements Viewable{

        private static final Dimension defaultDim = new Dimension(800,600);

        private static final Color placeholder = new Color(41, 55, 75);

        @Override
        public void draw(Graphics2D g) {

            g.setColor(placeholder);

            g.fillRect(0, 0, defaultDim.width, defaultDim.height);

            g.setColor(Color.BLACK);

            g.drawString("Placeholder!", 101, 101);
            g.drawString("Placeholder!", 101, 99);
            g.drawString("Placeholder!", 99, 101);
            g.drawString("Placeholder!", 99, 99);

            g.setColor(Color.WHITE);
            g.drawString("Placeholder!", 100, 100);
        }

        @Override
        public Dimension getSize() {
            return defaultDim;
        }

        @Override
        public void notifyAboutPause(boolean isPaused) {}
    }
}
