package crappy.math;

import crappy.utils.IPair;

public interface I_Vect2D extends IPair<Double, Double>, Comparable<I_Vect2D> {

    double getX();

    double getY();

    static double angle(final I_Vect2D v1, final I_Vect2D v2){
        return Math.atan2(v2.getY() - v1.getY(), v2.getX() - v1.getX());
    }

    static double dot(final I_Vect2D v1, final I_Vect2D v2){
        return (v1.getX() * v2.getX()) + (v1.getY() * v2.getY());
    }

    static double mag(final I_Vect2D v){
        return Math.hypot(v.getX(), v.getY());
    }

    static double cross(final I_Vect2D v1, final I_Vect2D v2){
        return v1.getX() * v2.getY() + v1.getY() * v2.getX();
    }

    static Vect2D cross(final I_Vect2D v, final double s){
        return new Vect2D(-s * v.getY(), s * v.getX());
    }


    default Double getFirst(){
        return getX();
    }

    default Double getSecond(){
        return getY();
    }

    default I_Vect2D to_I_Vect2D(){ return this; }

    default Vect2D toVect2D(){ return new Vect2D(this); }

    /**
     * A helper method to compare doubles.
     * Like Double.compare(double d1, double d2) but omits the double to long bits stuff
     * and just returns 0 if neither are found to be bigger than each other, to save time.
     * @param a first double
     * @param b second double
     * @return +1 if a > b, -1 if a < b, otherwise 0.
     */
    static int COMPARE_DOUBLES(final double a, final double b){
        if (a > b){
            return 1;
        } else if (a < b){
            return -1;
        }
        return 0;
    }



    /**
     * This is going to be abused by the axis-aligned bounding boxes AND A WHOLE LOAD OF EXTRA THINGS BESIDES THAT!
     * @param o the other I_Vect2D
     * @return it will either return 5, 3, 2, 1, 0, -1, -2, -3, or -5, depending on where this is in relation to o.
     * <html>
     *     <code><br>
     *      -1 |+2 |+5 <br>
     *      ---|---|---<br>
     *      -3 | 0 |+3 <br>
     *      ---|---|---<br>
     *      -5 |-2 |+1 <br>
     *      </code>
     *
     * </html>
     *
     */
    default I_Vect2D_Comp_Enum relative_pos_compare(final I_Vect2D o){
        // +5 +2 -1
        // +3  0 -3
        // +1 -2 -5
        return I_Vect2D_Comp_Enum.fromCompResults(
                COMPARE_DOUBLES(getX(), o.getX()),
                COMPARE_DOUBLES(getY(), o.getY())
        );
    }

    default I_Vect2D_Quad_Enum relative_pos_quad(final I_Vect2D o){
        if (getX() >= o.getX()){
            if (getY() >= o.getY()){
                return I_Vect2D_Quad_Enum.X_GREATER_Y_GREATER;
            } else{
                return I_Vect2D_Quad_Enum.X_GREATER_Y_SMALLER;
            }
        } else if (getY() >= o.getY()){
            return I_Vect2D_Quad_Enum.X_SMALLER_Y_GREATER;
        } else {
            return I_Vect2D_Quad_Enum.X_SMALLER_Y_SMALLER;
        }

    }

    /**
     * Is this I_Vect2D greater than the other one? (this.x > o.x && this.y > o.y)
     * @param o other I_Vect2D
     * @return true if X and Y of this are not less than X and Y of other
     */
    default boolean isGreaterThanOrEqualTo(final I_Vect2D o){
        return (getX() >= o.getX()) && (getY() >= o.getY());
    }

    /**
     * Comparison operation.
     * Returns result of comparing x values, then attempts to compare y values if x values are identical.
     * @param o the other I_Vect2D to compare it to
     * @return 1 if x > o.x, -1 if x < o.x, else 1 if y > o.y, -1 if y < o.y, else 0.
     */
    @Override
    default int compareTo(final I_Vect2D o) {
        final int x_comp = COMPARE_DOUBLES(getX(), o.getX());
        if (x_comp == 0){
            return COMPARE_DOUBLES(getY(), o.getY());
        }
        return x_comp;
    }

}


