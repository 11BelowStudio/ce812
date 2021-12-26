package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.I_Vect2D_Quad_Enum;
import crappy.math.Vect2D;
import crappy.utils.bitmasks.BitmaskPredicate;
import crappy.utils.bitmasks.IHaveBitmask;

import java.util.*;
import java.util.function.Predicate;

/**
 * An implementation of a quadtree for axis-aligned bounding boxes
 */
public abstract class A_AABBQuadTree implements AABBQuadTreeNode {

    final int candidatesLeft;

    // TODO: this

    A_AABBQuadTree(CrappyShape_QuadTree_Interface... children){
        candidatesLeft = children.length;
    }

    A_AABBQuadTree(){
        candidatesLeft = 0;
    }

    public static abstract class QuadTreeRootNode extends A_AABBQuadTree{

        final Set<CrappyShape_QuadTree_Interface> all_children;

        QuadTreeRootNode(
                CrappyShape_QuadTree_Interface... children
        ){
            super(children);
            Set<CrappyShape_QuadTree_Interface> tempSet = new LinkedHashSet<>(Arrays.asList(children));
            all_children = Collections.unmodifiableSet(tempSet);

        }

        QuadTreeRootNode(Set<CrappyShape_QuadTree_Interface> allChildren){
            super();
            all_children = allChildren;
        }

        public I_Vect2D getComparisonPoint(){
            return Vect2D.ZERO;
        }

        private static class EmptyQuadtreeRootNode extends QuadTreeRootNode {

            private static final Set<CrappyShape_QuadTree_Interface> empty = Collections.unmodifiableSet(new HashSet<>(0));

            EmptyQuadtreeRootNode(){
                super(empty);
            }

            public Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWith(
                    final I_Crappy_AABB attemptBoundingBox
            ){
                return empty;
            }


        }

        private static class SingleItemQuadtreeRootNode{

            // TODO: this

        }

        private static class MultipleItemQuadtreeRootNode{

            // TODO: this

        }
    }

    public static class StaticGeometryAABBTree extends A_AABBQuadTree{

        // TODO: this
    }

    /**
     * An enumeration used to identify sub-regions of a quadtree region
     */
    public static enum AABB_Quad_Enum implements BitmaskPredicate {

        X_SMALLER_Y_SMALLER(0),
        X_GREATER_Y_SMALLER(1),
        X_SMALLER_Y_GREATER(2),
        X_GREATER_Y_GREATER(3);

        /**
         * bitmask value of each thing
         */
        private final int bm_value;

        private AABB_Quad_Enum(final int bitPos){ bm_value = 0b0001 << bitPos; }

        @Override
        public int getBitmask() {
            return bm_value;
        }


        /**
         * An enumeration used to indicate relationship between a bounding box's bounds
         * and a given 'midpoint' it's being compared to
         */
        public static enum AABB_Choose_Quadtree_Enum implements IHaveBitmask{

            // TODO: 9 values, indicating relationship of given AABB to parent midpoint.

            X_SMALLER_Y_SMALLER(AABB_Quad_Enum.X_SMALLER_Y_SMALLER.getAsInt()),
            X_SMALLER_Y_EITHER (AABB_Quad_Enum.X_SMALLER_Y_SMALLER, AABB_Quad_Enum.X_SMALLER_Y_GREATER),
            X_SMALLER_Y_GREATER(AABB_Quad_Enum.X_SMALLER_Y_GREATER.getAsInt()),
            X_EITHER_Y_SMALLER (AABB_Quad_Enum.X_SMALLER_Y_SMALLER, AABB_Quad_Enum.X_GREATER_Y_SMALLER),
            X_EITHER_Y_EITHER  (AABB_Quad_Enum.X_SMALLER_Y_SMALLER, AABB_Quad_Enum.X_GREATER_Y_SMALLER, AABB_Quad_Enum.X_SMALLER_Y_GREATER, AABB_Quad_Enum.X_GREATER_Y_GREATER),
            X_EITHER_Y_GREATER (AABB_Quad_Enum.X_SMALLER_Y_GREATER, AABB_Quad_Enum.X_GREATER_Y_GREATER),
            X_GREATER_Y_SMALLER(AABB_Quad_Enum.X_GREATER_Y_SMALLER.getAsInt()),
            X_GREATER_Y_EITHER (AABB_Quad_Enum.X_GREATER_Y_SMALLER, AABB_Quad_Enum.X_GREATER_Y_GREATER),
            X_GREATER_Y_GREATER(AABB_Quad_Enum.X_GREATER_Y_GREATER.getAsInt());


            public final int bm_value;

            private AABB_Choose_Quadtree_Enum(final int bits){
                bm_value = bits;
            }

            private AABB_Choose_Quadtree_Enum(final AABB_Quad_Enum... quads){
                this(IHaveBitmask.COMBINE_BITMASKS_OR(quads));
            }

            @Override
            public int getBitmask() {
                return bm_value;
            }

            public static AABB_Choose_Quadtree_Enum get(
                    final I_Vect2D comparisonPoint,
                    final I_Vect2D min,
                    final I_Vect2D max
            ){
                if (min.getX() > comparisonPoint.getX()){

                    if (min.getY() > comparisonPoint.getY()){
                        return AABB_Choose_Quadtree_Enum.X_GREATER_Y_GREATER;
                    } else if (max.getY() > comparisonPoint.getY()){
                        return AABB_Choose_Quadtree_Enum.X_GREATER_Y_EITHER;
                    } else {
                        return AABB_Choose_Quadtree_Enum.X_GREATER_Y_SMALLER;
                    }

                } else if (max.getX() > comparisonPoint.getX()){

                    if (min.getY() > comparisonPoint.getY()){
                        return AABB_Choose_Quadtree_Enum.X_EITHER_Y_GREATER;
                    } else if (max.getY() > comparisonPoint.getY()){
                        return AABB_Choose_Quadtree_Enum.X_EITHER_Y_EITHER;
                    } else {
                        return AABB_Choose_Quadtree_Enum.X_EITHER_Y_SMALLER;
                    }

                } else {
                    if (min.getY() > comparisonPoint.getY()){
                        return AABB_Choose_Quadtree_Enum.X_SMALLER_Y_GREATER;
                    } else if (max.getY() > comparisonPoint.getY()){
                        return AABB_Choose_Quadtree_Enum.X_SMALLER_Y_EITHER;
                    } else {
                        return AABB_Choose_Quadtree_Enum.X_SMALLER_Y_SMALLER;
                    }
                }
            }

            public static AABB_Choose_Quadtree_Enum get(
                    final I_Vect2D comparisonPoint,
                    final I_Crappy_AABB aabbBeingCompared
            ) {
                return AABB_Choose_Quadtree_Enum.get(
                        comparisonPoint, aabbBeingCompared.getMin(), aabbBeingCompared.getMax()
                );
            }
        }

    }



}
