package crappy;

import crappy.utils.IPair;


/**
 * An axis-aligned bounding box
 */
public class Crappy_AABB implements I_Crappy_AABB {


    private I_Vect2D min;

    private I_Vect2D max;

    Crappy_AABB(final Vect2D min, final Vect2D max){
        this.min = min;
        this.max = max;
    }

    Crappy_AABB(final IPair<? extends I_Vect2D, ? extends I_Vect2D> bounds){
        this.update_aabb(bounds);
    }

    /**
     * call this with a pair of < minimum, maximum > I_Vect2D objects describing the bounds of this bounding box
     * @param bounds pair of < min xy, max xy>
     */
    void update_aabb(final IPair<? extends I_Vect2D, ? extends I_Vect2D> bounds){
        min = bounds.getFirst().to_I_Vect2D();
        max = bounds.getSecond().to_I_Vect2D();
    }

    /**
     * Call this with a known minimum vect and a maximum vect to set the bounds to that
     * @param min lower x,y bounds vector
     * @param max upper x,y bounds vector
     */
    void update_aabb(final Vect2D min, final Vect2D max){
        this.min = min;
        this.max = max;
    }

    /**
     * Use this to update the bounding box for a circle
     * @param mid the new midpoint of the bounding box
     * @param radius the radius of the circle which this bounding box describes
     */
    void update_aabb_circle(final Vect2D mid, final double radius){
        this.min = mid.add(-radius);
        this.max = mid.add(radius);

    }

    @Override
    public I_Vect2D getMax() {
        return max;
    }

    @Override
    public I_Vect2D getMin(){
        return min;
    }

    /**
     * Abuses the compareTo method of I_Vect2D to see if v is within this AABB's boundaries.
     * @param v the I_Vect2D we're trying to check
     * @return true if the I_Vect2D is in bounds, false otherwise.
     */
    @Override
    public boolean check_if_in_bounds(final I_Vect2D v){
        return v.isGreaterThanOrEqualTo(min) && max.isGreaterThanOrEqualTo(v);
    }

    public boolean check_bb_intersect(final Crappy_AABB other){
        return check_if_in_bounds(other.max) || check_if_in_bounds(other.min);
    }

    @Override
    public I_Vect2D getFirst() { return getMax(); }

    @Override
    public I_Vect2D getSecond() { return getMin(); }
}
