package crappy.collisions;

import crappy.math.I_Vect2D;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.bitmasks.BitmaskPredicate;
import crappy.utils.bitmasks.IHaveBitmask;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;


public final class AABBQuadTreeTools {

    /**
     * An interface for each 'node' of a given AABB QuadTree
     */
    public interface AABBQuadTreeNode extends HasCandidates {

        I_Vect2D getComparisonPoint();

        Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWithToOut(
                final I_Crappy_AABB attemptBoundingBox,
                final Set<CrappyShape_QuadTree_Interface> out
        );

        int getCandidatesLeft();

    }

    public interface AABBQuadTreeRootNode extends AABBQuadTreeNode{

        default Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWith(
                final I_Crappy_AABB attemptBoundingBox
        ){
            return getShapesThatProbablyCollideWithToOut(
                    attemptBoundingBox,
                    new HashSet<>(getCandidatesLeft())
            );
        }
    }

    private interface HasCandidates{
        int getCandidatesLeft();
    }

    /**
     * An implementation of a quadtree for axis-aligned bounding boxes
     */
    abstract static class A_AABBQuadTree implements AABBQuadTreeNode {

        final int candidatesLeft;

        // TODO: this


        A_AABBQuadTree(final int candidates){
            candidatesLeft = candidates;
        }


        @Override
        public int getCandidatesLeft() {
            return candidatesLeft;
        }
    }



    private abstract static class QuadTreeLeafNode extends A_AABBQuadTree {

        final List<CrappyShape_QuadTree_Interface> all_children;

        @SuppressWarnings("StaticCollection")
        static final List<CrappyShape_QuadTree_Interface> empty = Collections.unmodifiableList(new ArrayList<>(0));

        @SuppressWarnings("StaticCollection")
        static final Set<CrappyShape_QuadTree_Interface> emptySet = Collections.unmodifiableSet(
                new HashSet<>(0)
        );


        QuadTreeLeafNode(
                CrappyShape_QuadTree_Interface... children
        ) {
            this(Collections.unmodifiableList(Arrays.asList(children)));
        }



        QuadTreeLeafNode(List<CrappyShape_QuadTree_Interface> immutableChildren){
            super(immutableChildren.size());
            all_children = immutableChildren;
        }

        QuadTreeLeafNode(){
            super(0);
            all_children = empty;
        }

        public I_Vect2D getComparisonPoint() {
            return Vect2D.ZERO;
        }

    }

    private static class EmptyQuadtreeLeafNode extends QuadTreeLeafNode {



        EmptyQuadtreeLeafNode() {
            super();
        }

        public Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWithToOut(
                final I_Crappy_AABB attemptBoundingBox,
                final Set<CrappyShape_QuadTree_Interface> results
        ) {
            return results;
        }


    }

    private static class SingleItemQuadtreeLeafNode extends QuadTreeLeafNode {

        // TODO: this

        private final CrappyShape_QuadTree_Interface theShape;


        SingleItemQuadtreeLeafNode(CrappyShape_QuadTree_Interface s) {
            super(Collections.singletonList(s));
            theShape = s;
        }

        public Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWithToOut(
                final I_Crappy_AABB attemptBoundingBox,
                final Set<CrappyShape_QuadTree_Interface> results
        ) {
            if (attemptBoundingBox.check_bb_intersect(theShape.getBoundingBox())) {
                results.add(theShape);
            }
            return results;
        }

    }

    private static class MultipleItemQuadtreeLeafNode extends QuadTreeLeafNode {

        MultipleItemQuadtreeLeafNode(List<CrappyShape_QuadTree_Interface> mutableShapes){
            super(Collections.unmodifiableList(mutableShapes));
        }

        public Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWithToOut(
                final I_Crappy_AABB attemptBoundingBox,
                final Set<CrappyShape_QuadTree_Interface> results
        ) {

            for (CrappyShape_QuadTree_Interface csqti : all_children) {
                if (csqti.getBoundingBox().check_bb_intersect(attemptBoundingBox)) {
                    results.add(csqti);
                }
            }
            return results;
        }

    }

    public static I_StaticGeometryQuadTreeRootNode STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(
            final Collection<CrappyShape_QuadTree_Interface> geometryShapes
    ){
        return new StaticGeometryAABBTreeNode.StaticGeometryRootNode(
                geometryShapes.stream()
                    .unordered()
                    .distinct()
                    .collect(
                        Collectors.collectingAndThen(
                            Collectors.toCollection(ArrayList::new),
                            l -> {
                                l.trimToSize();
                                return Collections.unmodifiableList(l);
                            }
                        )
                    )
        );
    }


    public interface I_StaticGeometryQuadTreeRootNode extends AABBQuadTreeRootNode, Iterable<A_CrappyShape>{

    }


    private static class StaticGeometryAABBTreeNode extends A_AABBQuadTree{

        private static class StaticGeometryRootNode
                extends StaticGeometryAABBTreeNode
                implements I_StaticGeometryQuadTreeRootNode, Iterable<A_CrappyShape>
        {

            private final List<A_CrappyShape> allShapes;

            private StaticGeometryRootNode(final Collection<CrappyShape_QuadTree_Interface> geometryShapes) {
                super(geometryShapes);

                allShapes = geometryShapes.stream()
                        .unordered()
                        .map(CrappyShape_QuadTree_Interface::getShape)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toCollection(ArrayList::new),
                                l -> {
                                    l.trimToSize();
                                    return Collections.unmodifiableList(l);
                                }
                        ));
            }

            /**
             * Returns an iterator over elements of type {@code T}.
             *
             * @return an Iterator.
             */
            @Override
            public Iterator<A_CrappyShape> iterator() {
                return allShapes.iterator();
            }

        }


        /**
         * x bigger y bigger
         */
        final A_AABBQuadTree GG;

        /**
         * x bigger y smaller
         */
        final A_AABBQuadTree GL;

        /**
         * x smaller y smaller
         */
        final A_AABBQuadTree LL;

        /**
         * x smaller y bigger
         */
        final A_AABBQuadTree LG;

        final Vect2D midpoint;

        StaticGeometryAABBTreeNode(
                final Collection<CrappyShape_QuadTree_Interface> geometryShapes
        ){
            this(
                    geometryShapes,
                    geometryShapes.size() * 2,
                    MEDIAN_OPS.MEDIAN_OF_MIDPOINTS
            );
        }

        StaticGeometryAABBTreeNode(
                final Collection<CrappyShape_QuadTree_Interface> geometryShapes,
                final int parentShapesCount,
                final MEDIAN_OPS thingUsedForMedian
        ){
            this(geometryShapes, parentShapesCount * 2, parentShapesCount, thingUsedForMedian);
        }

        StaticGeometryAABBTreeNode(
                final Collection<CrappyShape_QuadTree_Interface> geometryShapes,
                final int grandparentShapesCount,
                final int parentShapesCount,
                final MEDIAN_OPS thingUsedForMedian
        ){
            super(geometryShapes.size());

            final List<I_Crappy_AABB> boundingBoxes = new ArrayList<>(candidatesLeft);
            geometryShapes.forEach(s -> boundingBoxes.add(s.getBoundingBox()));

            midpoint = MEDIAN_OPS.MEDIAN_OF_AABBS(boundingBoxes, thingUsedForMedian);

            Map<AABB_Quad_Enum, List<CrappyShape_QuadTree_Interface>> map = new HashMap<>(4, 1f);

            for (AABB_Quad_Enum k: AABB_Quad_Enum.values()) {
                map.put(k, new ArrayList<>(candidatesLeft));
            }


            geometryShapes.forEach(
                shape -> {
                    final AABB_Quad_Enum.AABB_Choose_Quadtree_Enum e = AABB_Quad_Enum.AABB_Choose_Quadtree_Enum.get(
                            midpoint, shape
                    );
                    map.forEach(
                        (k, v) ->{
                            if (k.test(e)){
                                v.add(shape);
                            }
                        }
                    );
                }
            );

            GG = NEXT_LAYER_FACTORY_FOR_STATIC_GEOMETRY(
                    map.get(AABB_Quad_Enum.X_GREATER_Y_GREATER),
                    grandparentShapesCount,
                    parentShapesCount,
                    getCandidatesLeft(),
                    map.get(AABB_Quad_Enum.X_SMALLER_Y_SMALLER).size()
            );
            LL = NEXT_LAYER_FACTORY_FOR_STATIC_GEOMETRY(
                    map.get(AABB_Quad_Enum.X_SMALLER_Y_SMALLER),
                    grandparentShapesCount,
                    parentShapesCount,
                    getCandidatesLeft(),
                    map.get(AABB_Quad_Enum.X_GREATER_Y_GREATER).size()
            );
            LG = NEXT_LAYER_FACTORY_FOR_STATIC_GEOMETRY(
                    map.get(AABB_Quad_Enum.X_SMALLER_Y_GREATER),
                    grandparentShapesCount,
                    parentShapesCount,
                    getCandidatesLeft(),
                    map.get(AABB_Quad_Enum.X_GREATER_Y_SMALLER).size()
            );
            GL = NEXT_LAYER_FACTORY_FOR_STATIC_GEOMETRY(
                    map.get(AABB_Quad_Enum.X_GREATER_Y_SMALLER),
                    grandparentShapesCount,
                    parentShapesCount,
                    getCandidatesLeft(),
                    map.get(AABB_Quad_Enum.X_SMALLER_Y_GREATER).size()
            );
        }


        /**
         * Attempts to create the next layer for the static geometry AABB tree, and sets up the variables
         * which that other layer will use when creating its child layers (if any)
         * @param nextLayerShapes all of the shapes to go into this region of the next layer
         * @param grandparentLayerShapeCount how many shapes were on the layer before the layer before this layer?
         * @param parentLayerShapeCount  how many shapes were on the layer before this layer?
         * @param currentLayerShapeCount how many shapes were on this current layer?
         * @param shapesFromThisLayerOnOtherSide how many shapes are in the region on the opposite diagonal region
         *                                       to the region we're creating with this method?
         * @return a new A_AABBQuadTree node, for static geometry, attempting to
         */
        private static A_AABBQuadTree NEXT_LAYER_FACTORY_FOR_STATIC_GEOMETRY(
                final List<CrappyShape_QuadTree_Interface> nextLayerShapes,
                final int grandparentLayerShapeCount,
                final int parentLayerShapeCount,
                final int currentLayerShapeCount,
                final int shapesFromThisLayerOnOtherSide
        ){
            if (nextLayerShapes.isEmpty()){
                return new EmptyQuadtreeLeafNode();
            } else if (nextLayerShapes.size() == 1){
                return new SingleItemQuadtreeLeafNode(nextLayerShapes.get(0));
            }

            // if this child has all the children from the parent
            if (nextLayerShapes.size() == currentLayerShapeCount){
                // if we completely failed to bisect this region
                if (nextLayerShapes.size() == shapesFromThisLayerOnOtherSide) {

                    // if we've had no improvement over 2 generations, we give up.
                    if (nextLayerShapes.size() == grandparentLayerShapeCount){

                        return new MultipleItemQuadtreeLeafNode(nextLayerShapes);
                    }
                    // we try again, with a completely random median for midpoint
                    return new StaticGeometryAABBTreeNode(
                            nextLayerShapes,
                            parentLayerShapeCount,
                            currentLayerShapeCount,
                            (nextLayerShapes.size() == parentLayerShapeCount)
                                    ? StaticGeometryAABBTreeNode.MEDIAN_OPS.WHATEVER
                                    // we panic if no improvement since parent
                                    : StaticGeometryAABBTreeNode.MEDIAN_OPS.MEDIAN_OF_MAX
                            // otherwise we calmly attempt comparing maximums
                    );

                } else if (nextLayerShapes.size() == parentLayerShapeCount){
                    // if there are some shapes that were at least omitted from the other side
                    // we try a different approach to make sure we properly bisect it
                    return new StaticGeometryAABBTreeNode(
                            nextLayerShapes,
                            parentLayerShapeCount,
                            currentLayerShapeCount,
                            (nextLayerShapes.size() == grandparentLayerShapeCount)
                                    ? StaticGeometryAABBTreeNode.MEDIAN_OPS.WHATEVER
                                    // we panic if no improvement since grandparent
                                    : StaticGeometryAABBTreeNode.MEDIAN_OPS.MEDIAN_OF_MAX
                            // otherwise we calmly attempt comparing maximums
                    );
                } else {
                    // if this was an improvement over the parent (somehow), we try another midpoint comparison
                    return new StaticGeometryAABBTreeNode(
                            nextLayerShapes,
                            parentLayerShapeCount,
                            currentLayerShapeCount,
                            StaticGeometryAABBTreeNode.MEDIAN_OPS.MEDIAN_OF_MIDPOINTS
                    );
                }
            }

            return new StaticGeometryAABBTreeNode(
                    nextLayerShapes,
                    parentLayerShapeCount,
                    currentLayerShapeCount,
                    StaticGeometryAABBTreeNode.MEDIAN_OPS.MEDIAN_OF_MIDPOINTS
            );

        }

        @Override
        public I_Vect2D getComparisonPoint() {
            return midpoint;
        }

        @Override
        public Set<CrappyShape_QuadTree_Interface> getShapesThatProbablyCollideWithToOut(
                final I_Crappy_AABB attemptBoundingBox,
                final Set<CrappyShape_QuadTree_Interface> results
        ) {


            final AABB_Quad_Enum.AABB_Choose_Quadtree_Enum c = AABB_Quad_Enum.AABB_Choose_Quadtree_Enum.get(
                    midpoint,
                    attemptBoundingBox
            );

            if (AABB_Quad_Enum.X_GREATER_Y_GREATER.test(c)){
                GG.getShapesThatProbablyCollideWithToOut(
                        attemptBoundingBox,
                        results
                );
            }
            if (AABB_Quad_Enum.X_GREATER_Y_SMALLER.test(c)){
                GL.getShapesThatProbablyCollideWithToOut(
                        attemptBoundingBox,
                        results
                );
            }
            if (AABB_Quad_Enum.X_SMALLER_Y_SMALLER.test(c)){
                LL.getShapesThatProbablyCollideWithToOut(
                        attemptBoundingBox,
                        results
                );
            }
            if (AABB_Quad_Enum.X_SMALLER_Y_GREATER.test(c)){
                LG.getShapesThatProbablyCollideWithToOut(
                        attemptBoundingBox,
                        results
                );
            }
            return results;

        }

        enum MEDIAN_OPS {
            MEDIAN_OF_MIDPOINTS,
            MEDIAN_OF_MAX,
            MEDIAN_OF_MIN,
            WHATEVER;

            static Vect2D get_from_aabb(final I_Crappy_AABB aabb, final MEDIAN_OPS thingToGet){

                switch (thingToGet){
                    case MEDIAN_OF_MAX:
                        return aabb.getMax();
                    case MEDIAN_OF_MIN:
                        return aabb.getMin();
                    case MEDIAN_OF_MIDPOINTS:
                        return aabb.getMidpoint();
                    case WHATEVER:
                    default:
                        return Vect2DMath.RANDOM_VECTOR_IN_BOUNDS(aabb.getMin(), aabb.getMax());
                }

            }

            static Vect2D MEDIAN_OF_AABBS(final Collection<I_Crappy_AABB> boundingBoxes, final MEDIAN_OPS opType) {

                final List<Double> xList = new ArrayList<>(boundingBoxes.size());
                final List<Double> yList = new ArrayList<>(boundingBoxes.size());

                M_Vect2D temp = M_Vect2D._GET_RAW();

                for (I_Crappy_AABB bb : boundingBoxes) {
                    temp.set(get_from_aabb(bb, opType));
                    xList.add(temp.x);
                    yList.add(temp.y);
                }
                Collections.sort(xList);
                Collections.sort(yList);

                if (boundingBoxes.size() % 2 == 0) {
                    // it's even
                    return temp.set(xList.get(boundingBoxes.size() / 2), yList.get(boundingBoxes.size() / 2)).finished();
                } else {
                    // it's odd
                    int lb = Math.floorDiv(boundingBoxes.size(), 2);
                    return temp.set(
                            (xList.get(lb) + xList.get(lb + 1)) / 2.0,
                            (yList.get(lb) + yList.get(lb + 1)) / 2.0
                    ).finished();
                }

            }
        }


    }


    public interface RegionTreeNode extends AABBQuadTreeNode{

        boolean isRoot();

        RegionTreeNode getFrom(AABB_Quad_Enum e);



    }


    static class DynamicKinematicAABBQuadTreeRegionTree{


        // TODO:
        //   Root node:
        //       Get boundary of all bounding boxes in list(s)
        //       Create empty structure, based on original bounding boxes. Predefined depth?
        // TODO:
        //   Insertion + collision checking at same time
        //       Start with kinematics, don't bother checking their collisions with each other.
        //       For the dynamics
        //           Find leave(s) that they can fit in, add them to those leaves
        //           Check BB of new object against the BBs already in those leaves
        //           Return found intersections for that object
        //       Combine these found intersections with dynamic/static intersections
        //      Return all found intersections to main update loop, where they can actually be dealt with properly.
    }


    /**
     * An enumeration used to identify sub-regions of a quadtree region
     */
    public enum AABB_Quad_Enum implements BitmaskPredicate, Comparable<AABB_Quad_Enum> {

        X_SMALLER_Y_SMALLER(0),
        X_GREATER_Y_SMALLER(1),
        X_SMALLER_Y_GREATER(2),
        X_GREATER_Y_GREATER(3);

        /**
         * bitmask value of each thing
         */
        private final int bm_value;

        private AABB_Quad_Enum(final int bitPos) {
            bm_value = 0b0001 << bitPos;
        }

        @Override
        public int getBitmask() {
            return bm_value;
        }


        /**
         * An enumeration used to indicate relationship between a bounding box's bounds and a given 'midpoint' it's
         * being compared to
         */
        public enum AABB_Choose_Quadtree_Enum implements IHaveBitmask {

            // TODO: 9 values, indicating relationship of given AABB to parent midpoint.

            X_SMALLER_Y_SMALLER(AABB_Quad_Enum.X_SMALLER_Y_SMALLER.getAsInt()),
            X_SMALLER_Y_EITHER(AABB_Quad_Enum.X_SMALLER_Y_SMALLER, AABB_Quad_Enum.X_SMALLER_Y_GREATER),
            X_SMALLER_Y_GREATER(AABB_Quad_Enum.X_SMALLER_Y_GREATER.getAsInt()),
            X_EITHER_Y_SMALLER(AABB_Quad_Enum.X_SMALLER_Y_SMALLER, AABB_Quad_Enum.X_GREATER_Y_SMALLER),
            X_EITHER_Y_EITHER(AABB_Quad_Enum.X_SMALLER_Y_SMALLER, AABB_Quad_Enum.X_GREATER_Y_SMALLER, AABB_Quad_Enum.X_SMALLER_Y_GREATER, AABB_Quad_Enum.X_GREATER_Y_GREATER),
            X_EITHER_Y_GREATER(AABB_Quad_Enum.X_SMALLER_Y_GREATER, AABB_Quad_Enum.X_GREATER_Y_GREATER),
            X_GREATER_Y_SMALLER(AABB_Quad_Enum.X_GREATER_Y_SMALLER.getAsInt()),
            X_GREATER_Y_EITHER(AABB_Quad_Enum.X_GREATER_Y_SMALLER, AABB_Quad_Enum.X_GREATER_Y_GREATER),
            X_GREATER_Y_GREATER(AABB_Quad_Enum.X_GREATER_Y_GREATER.getAsInt());


            public final int bm_value;

            private AABB_Choose_Quadtree_Enum(final int bits) {
                bm_value = bits;
            }

            private AABB_Choose_Quadtree_Enum(final AABB_Quad_Enum... quads) {
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
            ) {
                if (min.getX() > comparisonPoint.getX()) {

                    if (min.getY() > comparisonPoint.getY()) {
                        return AABB_Choose_Quadtree_Enum.X_GREATER_Y_GREATER;
                    } else if (max.getY() > comparisonPoint.getY()) {
                        return AABB_Choose_Quadtree_Enum.X_GREATER_Y_EITHER;
                    } else {
                        return AABB_Choose_Quadtree_Enum.X_GREATER_Y_SMALLER;
                    }

                } else if (max.getX() > comparisonPoint.getX()) {

                    if (min.getY() > comparisonPoint.getY()) {
                        return AABB_Choose_Quadtree_Enum.X_EITHER_Y_GREATER;
                    } else if (max.getY() > comparisonPoint.getY()) {
                        return AABB_Choose_Quadtree_Enum.X_EITHER_Y_EITHER;
                    } else {
                        return AABB_Choose_Quadtree_Enum.X_EITHER_Y_SMALLER;
                    }

                } else {
                    if (min.getY() > comparisonPoint.getY()) {
                        return AABB_Choose_Quadtree_Enum.X_SMALLER_Y_GREATER;
                    } else if (max.getY() > comparisonPoint.getY()) {
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

            public static AABB_Choose_Quadtree_Enum get(
                    final I_Vect2D comparisonPoint,
                    final CrappyShape_QuadTree_Interface shape
            ) {
                return get(comparisonPoint, shape.getBoundingBox());
            }
        }

    }

}
