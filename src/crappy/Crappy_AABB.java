package crappy;

import crappy.utils.IPair;


/**
 * An axis-aligned bounding box
 */
public class Crappy_AABB implements IPair<I_Vect2D, I_Vect2D> {


    private I_Vect2D min;

    private I_Vect2D max;

    void update_aabb(final IPair<I_Vect2D, I_Vect2D> bounds){
        min = bounds.getFirst();
        max = bounds.getSecond();
    }

    /**
     * Abuses the compareTo method of I_Vect2D to see if v is within this AABB's boundaries.
     * @param v the I_Vect2D we're trying to check
     * @return true if the I_Vect2D is in bounds, false otherwise.
     */
    boolean check_if_in_bounds(I_Vect2D v){
        return v.isGreaterThanOrEqualTo(min) && max.isGreaterThanOrEqualTo(v);
    }


    @Override
    public I_Vect2D getFirst() {
        return max;
    }

    @Override
    public I_Vect2D getSecond() {
        return min;
    }
}
