package crappy.math;

import crappy.utils.IPair;

/**
 * An interface for the mutable/immutable Rot2D objects.
 */
public interface I_Rot2D extends IPair<Double, Double> {

    double get_sin();

    double get_cos();

    static double GET_ANGLE(final I_Rot2D rot){
        return Math.atan2(rot.get_sin(), rot.get_cos());
    }

    @Override
    default Double getFirst() {
        return get_sin();
    }

    @Override
    default Double getSecond(){
        return get_cos();
    }
}
