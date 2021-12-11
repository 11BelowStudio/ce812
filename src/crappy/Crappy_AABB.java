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
        if (v.compareTo(min) >= 0){
            final int max_comp = v.compareTo(max);
            //noinspection ComparatorResultComparison
            return max_comp < -3 || max_comp == 0;
            // a max_comp value of -1 or -3 means that v is bigger than max in the x or y direction,
            // whilst 0, -4, -5, or -7 means that it's not bigger than max in either direction (so it's in bounds).
        }
        return false;
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
