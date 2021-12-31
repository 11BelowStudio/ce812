package crappy.graphics;

import crappy.math.I_Vect2D;
import crappy.utils.containers.IPair;

import java.awt.*;

public class GraphicsTransform {

    final Dimension viewportSize;

    final IPair<Double, Double> sizeOfViewableWorld;

    final IPair<Double, Double> screenScaling;

    final IPair<Double, Double> screenFixingTranslation;

    final IPair<Double, Double> screenFixingScaling;

    final I_Vect2D viewportOrigin;

    public GraphicsTransform(
            final Dimension screenSize,
            final IPair<Double, Double> sizeOfViewableWorld,
            final IPair<Double, Double> screenFixingTranslation,
            final IPair<Double, Double> screenFixingScaling,
            final I_Vect2D viewportOrigin
    ){

        viewportSize = screenSize;
        this.sizeOfViewableWorld = sizeOfViewableWorld;
        screenScaling = IPair.of(
                sizeOfViewableWorld.getFirst()/viewportSize.getWidth(),
                sizeOfViewableWorld.getSecond()/viewportSize.getHeight()
        );

        this.screenFixingTranslation = screenFixingTranslation;
        this.screenFixingScaling = screenFixingScaling;
        this.viewportOrigin = viewportOrigin;
    }

    public Dimension getViewportSize() {
        return viewportSize;
    }

    public I_Vect2D getViewportOrigin() {
        return viewportOrigin;
    }

    public IPair<Double, Double> getScreenFixingScaling() {
        return screenFixingScaling;
    }

    public IPair<Double, Double> getScreenFixingTranslation() {
        return screenFixingTranslation;
    }

    public IPair<Double, Double> getScreenScaling() {
        return screenScaling;
    }

    public IPair<Double, Double> getSizeOfViewableWorld() {
        return sizeOfViewableWorld;
    }
}
