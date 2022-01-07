package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.containers.IPair;
import crappy.utils.containers.Quadruplet;

import java.util.Arrays;


/**
 * An axis-aligned bounding box
 */
public class Crappy_AABB implements I_Crappy_AABB, Cloneable {

    /**
     * Min corner of the box (lowest x, y)
     */
    private Vect2D min;

    /**
     * Upper corner of the box (upper x, y)
     */
    private Vect2D max;

    /**
     * Constructor that just sets this to have values of 0 for min and max.
     */
    public Crappy_AABB(){
        min = max = Vect2D.ZERO;
    }

    /**
     * Creates a new AABB with given bounds
     * @param min the lower bound for it (minimum x, y)
     * @param max the upper bound for it (maximum x, y)
     */
    public Crappy_AABB(final Vect2D min, final Vect2D max){
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a Crappy_AABB describing a point.
     * @param point the point this Crappy_AABB is describing
     */
    public Crappy_AABB(final Vect2D point){
        this.min = this.max = point;
    }

    /**
     * Creates a new AABB that copies the given AABB
     * @param other the AABB to copy
     */
    public Crappy_AABB(final Crappy_AABB other){
        this(other.min, other.max);
    }

    /**
     * Creates a new AABB that copies the given AABB
     * @param other the AABB to copy
     */
    public Crappy_AABB(final I_Crappy_AABB other){
        this(other.getMin(), other.getMax());
    }

    /**
     * Creates a new AABB with given bounds described by a pair of objects that extend I_Vect2D
     * @param bounds pair of (min xy, max xy)
     */
    public Crappy_AABB(final IPair<? extends I_Vect2D, ? extends I_Vect2D> bounds){
        this.update_aabb(bounds);
    }

    /**
     * Creates a new AABB with bounds containing all of the given boxes in the varargs
     * @param boxes a list of I_Crappy_AABB objects that this CrappyAABB will contain
     */
    public Crappy_AABB(final I_Crappy_AABB... boxes){
        this.update_aabb_compound(boxes);
    }

    /**
     * Creates a new AABB with bounds containing all of the given boxes in the varargs
     * @param pairBoxes a list of IPairs of (min xy, max xy) vectors that this CrappyAABB will contain
     */
    @SafeVarargs
    public Crappy_AABB(final IPair<Vect2D, Vect2D>... pairBoxes){
        this.update_aabb_compound(pairBoxes);
    }

    /**
     * Creates a new AABB describing this circle.
     * @param mid midpoint of the circle
     * @param radius radius of the circle
     */
    public Crappy_AABB(final I_Vect2D mid, final double radius){
        this.update_aabb_circle(mid.toVect2D(), radius);
    }

    /**
     * @return a copy of this AABB.
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Crappy_AABB clone(){
        return new Crappy_AABB(this);
    }


    /**
     * Overwrites this AABB with a new AABB containing all the AABBs in childAABBs.
     * @param childAABBs the list of all the child AABBs to add together into a new AABB.
     */
    public void update_aabb_compound(final I_Crappy_AABB... childAABBs){
        min = childAABBs[0].getMin();
        max = childAABBs[0].getMax();
        for (int i = childAABBs.length-1; i > 1; i--) {
            add_aabb(childAABBs[i]);
        }
    }

    /**
     * Overwrites this AABB with a new AABB containing all the AABBs described by childAABBs.
     * @param childAABBPairs the list of all the pairs that describe child AABBs to add together into a new AABB.
     */
    @SafeVarargs
    public final void update_aabb_compound(final IPair<Vect2D, Vect2D>... childAABBPairs){
        min = childAABBPairs[0].getFirst();
        max = childAABBPairs[0].getSecond();
        for (int i = childAABBPairs.length-1; i > 0; i--) {
            add_aabb(childAABBPairs[i]);
        }
    }

    /**
     * adds the area described by the other AABB to this AABB
     * @param other the other AABB area to add to this AABB
     */
    public void add_aabb(final I_Crappy_AABB other){
        min = Vect2DMath.LOWER_BOUND(min, other.getMin());
        max = Vect2DMath.UPPER_BOUND(max, other.getMax());
    }

    /**
     * adds the area described by the pair of vectors to this AABB
     * @param other the pair describing the other AABB area to add to this AABB
     */
    public void add_aabb(final IPair<Vect2D, Vect2D> other){
        min = Vect2DMath.LOWER_BOUND(min, other.getFirst());
        max = Vect2DMath.UPPER_BOUND(max, other.getSecond());
    }

    /**
     * Adds the point to the area described by the AABB
     * @param point point to add to this AABB
     */
    public void add_point(final I_Vect2D point){
        min = Vect2DMath.LOWER_BOUND(min, point);
        max = Vect2DMath.UPPER_BOUND(max, point);
    }

    /**
     * Adds the area of this circle to this aabb
     * @param mid midpoint of circle
     * @param radius circle radius
     */
    public void add_circle(final I_Vect2D mid, final double radius){
        min = Vect2DMath.LOWER_BOUND(min, Vect2DMath.ADD(mid, -radius));
        max = Vect2DMath.UPPER_BOUND(max, Vect2DMath.ADD(mid, radius));
    }

    /**
     * Updates this AABB to be a copy of the other one
     * @param other the other AABB to copy
     */
    public void update_aabb(final I_Crappy_AABB other){
        min = other.getMin();
        max = other.getMax();
    }


    /**
     * call this with a pair of < minimum, maximum > I_Vect2D objects describing the bounds of this bounding box
     * @param bounds pair of < min xy, max xy>
     */
    public void update_aabb(final IPair<? extends I_Vect2D, ? extends I_Vect2D> bounds){
        min = bounds.getFirst().toVect2D();
        max = bounds.getSecond().toVect2D();
    }


    /**
     * Call this with a known minimum vect and a maximum vect to set the bounds to that
     * @param min lower x,y bounds vector
     * @param max upper x,y bounds vector
     */
    public void update_aabb(final Vect2D min, final Vect2D max){
        this.min = min;
        this.max = max;
    }

    /**
     * Updates this AABB to describe a point
     * @param point the point described by this AABB
     */
    public void update_aabb(final Vect2D point){
        min = max = point;
    }

    /**
     * Use this to update the bounding box for a circle
     * @param mid the new midpoint of the bounding box
     * @param radius the radius of the circle which this bounding box describes
     */
    public void update_aabb_circle(final Vect2D mid, final double radius){

        this.min = mid.add(-radius);
        this.max = mid.add(radius);

    }

    /**
     * Call this to update the AABB to enclose the line described by the arguments,
     * starting at worldStart in world coords and ending at worldProj in world coords
     * @param worldStart start position of this AABB
     * @param worldProj polar vector describing the overall projection of that line in the world
     */
    public void update_aabb_edge(final Vect2D worldStart, final Vect2D worldProj, final Vect2D worldNorm, double depth){
        if (!Double.isFinite(depth)){
            depth = 10;
        }
        M_Vect2D endPoint = M_Vect2D.GET(worldStart).add(worldProj);
        this.update_aabb(
                Vect2DMath.GET_BOUNDS_VARARGS(
                        worldStart, endPoint.toVect2D(),
                        worldStart.addScaled(worldNorm, -depth),
                        endPoint.addScaled(worldNorm, -depth)
                )
        );
        endPoint.discard();
    }

    public void enlarge(final double scale){

        Vect2D mid = getMidpoint();

        Vect2D minProj = Vect2DMath.VECTOR_BETWEEN_M(mid, min).add(-0.1, -0.1).finished();
        min = mid.addScaled(minProj, scale);

        Vect2D maxProj = Vect2DMath.VECTOR_BETWEEN_M(mid, max).add(0.1, 0.1).finished();
        max = mid.addScaled(maxProj, scale);

    }

    @Override
    public Vect2D getMax() {
        return max;
    }

    @Override
    public Vect2D getMin(){
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

    /**
     * Checks if AABB intersects with this, then adds it to this AABB anyway, before returning
     * whether or not they intersected at that first check
     * @param aabb the other AABB to check + add to this one
     * @return true if argument intersected before it was added, false otherwise
     */
    public boolean add_aabb_but_also_check_if_in_bounds(final I_Crappy_AABB aabb){
        final boolean res = I_Crappy_AABB.DO_THESE_BOUNDING_BOXES_OVERLAP(this, aabb);
        add_aabb(aabb);
        return res;
    }


    @Override
    public Vect2D getFirst() { return getMax(); }

    @Override
    public Vect2D getSecond() { return getMin(); }

    /**
     * Obtains the centroid of this AABB
     * @return midpoint of this AABB
     */
    @Override
    public Vect2D getMidpoint(){
        return Vect2DMath.MIDPOINT_MIN_MAX(min, max);
    }


    @Override
    public String toString() {
        return "Crappy_AABB{" +
                "min=" + min +
                ", max=" + max +
                ", range=" + getWidthHeight() +
                '}';
    }
}
