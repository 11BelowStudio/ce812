package crappy.math;

/**
 * Fuck it, I've just realized that I'm probably going to be able to abuse this
 * to simplify the whole 'getting an N log N collision handling data structure' stuff.
 * So I'm enshrining these funny numbers in an enum so I don't forget to abuse this further.
 *
 * update: ok so basically I didn't get around to abusing this one further.
 * BUT I did abuse this concept in {@link crappy.collisions.AABBQuadTreeTools.AABB_Quad_Enum.AABB_Choose_Quadtree_Enum}!
 *
 * @see crappy.collisions.AABBQuadTreeTools.AABB_Quad_Enum.AABB_Choose_Quadtree_Enum
 */
public enum I_Vect2D_Comp_Enum {


    // +5 +2 -1
    // +3  0 -3
    // +1 -2 -5

    X_BIGGER_Y_BIGGER(5),
    X_BIGGER_Y_SAME(3),
    X_BIGGER_Y_SMALLER(1),
    X_SAME_Y_BIGGER(2),
    X_SAME_Y_SAME(0),
    X_SAME_Y_SMALLER(-2),
    X_SMALLER_Y_BIGGER(-1),
    X_SMALLER_Y_SAME(-3),
    X_SMALLER_Y_SMALLER(-5);

    private static final int y_diff_int = 2;
    private static final int x_diff_int = 3;

    public final int out_val;

    private I_Vect2D_Comp_Enum(final int int_value){
        out_val = int_value;
    }

    /**
     * Obtains the appropriate I_Vect2D_Comp_Enum value from the results of comparing the x and y values of
     * two I_Vect2Ds a and b
     * @param x_comp_res result of comparing the x values of vector a and vector b
     * @param y_comp_res result of comparing the y values of vector a and vector b
     * @return the appropriate I_Vect2D_Comp_Enum describing the relative positions of vectors a and b
     */
    public static I_Vect2D_Comp_Enum fromCompResults(final int x_comp_res, final int y_comp_res){
        return fromInt((x_comp_res * x_diff_int) + (y_diff_int * y_comp_res));
    }

    /**
     * Obtains an I_Vect2D_Comp_enum describing how A compares to B
     * @param a first vector
     * @param b other vector
     * @return I_Vect2D_Comp_Enum describing A relative to B.
     */
    public static I_Vect2D_Comp_Enum compareTwoVectors(I_Vect2D a, I_Vect2D b){
        return fromCompResults(
                Vect2DMath.COMPARE_DOUBLES_CRAPPILY(a.getX(), b.getX()),
                Vect2DMath.COMPARE_DOUBLES_CRAPPILY(a.getY(), b.getY())
        );
    }



    public static I_Vect2D_Comp_Enum fromInt(final int input){
        switch (input){
            case 5:
                return X_BIGGER_Y_BIGGER;
            case 3:
                return X_BIGGER_Y_SAME;
            case 1:
                return X_BIGGER_Y_SMALLER;
            case 2:
                return X_SAME_Y_BIGGER;
            case 0:
                return X_SAME_Y_SAME;
            case -2:
                return X_SAME_Y_SMALLER;
            case -1:
                return X_SMALLER_Y_BIGGER;
            case -3:
                return X_SMALLER_Y_SAME;
            case -5:
                return X_SMALLER_Y_SMALLER;
            default:
                throw new IllegalArgumentException("Invalid input!");
        }
    }

}