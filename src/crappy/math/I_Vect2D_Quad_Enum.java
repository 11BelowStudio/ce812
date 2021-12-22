package crappy.math;


import java.rmi.UnexpectedException;

/**
 * Comparison enum for I_Vect2D but for a quadtree instead
 */
public enum I_Vect2D_Quad_Enum {

    X_GREATER_Y_GREATER(3),
    X_GREATER_Y_SMALLER(2),
    X_SMALLER_Y_GREATER(1),
    X_SMALLER_Y_SMALLER(0);

    public final int value;

    I_Vect2D_Quad_Enum(final int comparison_value){
        value = comparison_value;
    }

    public static I_Vect2D_Quad_Enum from_int(final int i){
        switch (i){
            case 0:
                return X_SMALLER_Y_SMALLER;
            case 1:
                return X_SMALLER_Y_GREATER;
            case 2:
                return X_GREATER_Y_SMALLER;
            case 3:
                return X_GREATER_Y_GREATER;
            default:
                throw new IllegalArgumentException(i + " is not a valid Quad_Enum value!");
        }
    }

    public static I_Vect2D_Quad_Enum from_comp_enum(final I_Vect2D_Comp_Enum c){
        switch (c){
            case X_BIGGER_Y_BIGGER:
            case X_BIGGER_Y_SAME:
            case X_SAME_Y_BIGGER:
            case X_SAME_Y_SAME:
                return X_GREATER_Y_GREATER;
            case X_BIGGER_Y_SMALLER:
            case X_SAME_Y_SMALLER:
                return X_GREATER_Y_SMALLER;
            case X_SMALLER_Y_BIGGER:
            case X_SMALLER_Y_SAME:
                return X_SMALLER_Y_GREATER;
            case X_SMALLER_Y_SMALLER:
                return X_SMALLER_Y_SMALLER;
            default:
                throw new IllegalArgumentException(c + " caused an unexpected exception!");
        }
    }

}
