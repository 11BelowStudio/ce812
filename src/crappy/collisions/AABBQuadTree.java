package crappy.collisions;

/**
 * An implementation of a quadtree for axis-aligned bounding boxes
 */
public class AABBQuadTree {


    public static enum AABB_Quad_Enum{

        X_SMALLER_Y_SMALLER(0),
        X_GREATER_Y_SMALLER(1),
        X_SMALLER_Y_GREATER(2),
        X_GREATER_Y_GREATER(3);

        public final int bm_value;

        private AABB_Quad_Enum(final int bitPos){
            bm_value = 0b0001 << bitPos;
        }

    }

    public static enum AABB_Choose_Quadtree_Enum{

        // TODO: 9 values, indicating relationship of given AABB to parent midpoint.

    }


}
