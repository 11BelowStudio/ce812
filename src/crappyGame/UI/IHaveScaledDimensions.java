package crappyGame.UI;

import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.containers.IPair;

import java.awt.*;
import java.awt.geom.Point2D;

public interface IHaveScaledDimensions {

    Dimension getOriginalDimensions();

    Dimension getCurrentDimensions();

    /**
     * Returns pair of (current dims/original dims)
     * @return how big the current dimensions are relative to the original dimensions
     */
    default IPair<Double, Double> currentDimRatio(){
        return IPair.of(
                getCurrentDimensions().getWidth()/getOriginalDimensions().getWidth(),
                getCurrentDimensions().getHeight()/getOriginalDimensions().getHeight()
        );
    }

    /**
     * Obtains a new Vect2D, representing this point, but scaled according to the current dimension ratio
     * @param p
     * @return
     */
    default Vect2D getPointScaledFromCurrentToOriginal(final Point2D p){
        return Vect2DMath.DIVIDE_M(new Vect2D(p), currentDimRatio()).finished();
    }

    /**
     * aspect ratio of original dimensions
     * @return
     */
    default double aspectRatio(){ return getOriginalDimensions().getWidth()/getOriginalDimensions().getHeight(); }
}
