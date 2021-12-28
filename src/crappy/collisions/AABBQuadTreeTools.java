package crappy.collisions;

import crappy.CrappyBody;
import crappy.math.I_Vect2D;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.bitmasks.BitmaskPredicate;
import crappy.utils.bitmasks.IHaveBitmask;
import crappy.utils.containers.IPair;
import crappy.utils.containers.IQuadruplet;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
     * An iterator over child nodes for a non-leaf node.
     *
     * Works via abusing an iterator over the AABB_Quad_Enum
     * @param <ChildNode> child node class
     * @param <ChildEnum> enum that is used to identify each one
     */
    @FunctionalInterface
    private interface ChildNodeIterable<ChildNode, ChildEnum extends Enum<?>>{

        /**
         * Obtains the appropriate child node based on the given AABB_Quad_Enum value
         * @param q AABB_Quad_Enum value we're getting the child node from
         * @return the appropriate child node
         */
        ChildNode getChildLayer(final ChildEnum q);

        /**
         * Creates the iterator that iterates over the childNodes
         * @param iterSource Iterator over ChildEnum values
         * @return an iterator which iterates over the childnodes that can be accessed via {@link #getChildLayer(Enum)}
         * from the values that the iterSource iterates over.
         */
        default Iterator<ChildNode> childNodeIterator(final Iterable<ChildEnum> iterSource){
            return new ChildNodeIterator<>(this, iterSource);
        }

        /**
         * An iterator over child nodes for a given parent node.
         *
         * Works via abusing iterators over ChildEnum values.
         */
        class ChildNodeIterator<
                ParentNode extends ChildNodeIterable<ChildNode, ChildEnum>,
                ChildNode,
                ChildEnum extends Enum<?>
            > implements Iterator<ChildNode>{

            /**
             * The actual node with the child nodes that we're performing the iterations on
             */
            private final ParentNode node;

            /**
             * The iterator over the ChildEnum values that we'll actually be looking at.
             *
             * All the important iterator stuff is delegated to qIter.
             */
            private final Iterator<ChildEnum> qIter;


            /**
             * Iterator over all the child elements in a ParentNode,
             * but using the iterator of it from a particular source.
             * @param n the parent we're iterating over
             * @param iterSource where we're getting our iterator over the AABB_Quad_Enum
             */
            ChildNodeIterator(final ParentNode n, final Iterable<ChildEnum> iterSource){
                node = n;
                this.qIter = iterSource.iterator();
            }

            /**
             * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link
             * #next} would return an element rather than throwing an exception.)
             *
             * @return {@code true} if the iteration has more elements
             */
            @Override
            public boolean hasNext() {
                return qIter.hasNext();
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             *
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public ChildNode next() {
                return node.getChildLayer(qIter.next());
            }

            /**
             * Applies specified action with the rules which the rest of the stuff in this iterator refers to
             * @param action action to take
             */
            @Override
            public void forEachRemaining(Consumer<? super ChildNode> action) {
                qIter.forEachRemaining(a -> action.accept(node.getChildLayer(a)));
            }

        }

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


    private static class StaticGeometryAABBTreeNode extends A_AABBQuadTree implements ChildNodeIterable<A_AABBQuadTree, AABB_Quad_Enum>{


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

        /**
         * midpoint of the node
         */
        final Vect2D midpoint;


        /**
         * Mapping from AABB_Quad_Enum values to the actual subtrees
         * @param q AABB_Quad_Enum value to turn into subtree
         * @return the appropriate subtree
         */
        public A_AABBQuadTree getChildLayer(final AABB_Quad_Enum q){
            switch (q){
                case X_SMALLER_Y_SMALLER:
                    return LL;
                case X_GREATER_Y_SMALLER:
                    return GL;
                case X_SMALLER_Y_GREATER:
                    return LG;
                case X_GREATER_Y_GREATER:
                default:
                    return GG;
            }
        }




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

            for (final Iterator<A_AABBQuadTree> it = childNodeIterator(AABB_Quad_Enum.AABB_Choose_Quadtree_Enum.get(
                    midpoint,
                    attemptBoundingBox
            )); it.hasNext(); ) {
                it.next().getShapesThatProbablyCollideWithToOut(
                        attemptBoundingBox, results
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

    interface I_DynamicKinematicAABBQuadTreeNode extends
            Consumer<CrappyShape_QuadTree_Interface>,
            Function<CrappyShape_QuadTree_Interface, Set<CrappyShape_QuadTree_Interface>>
    {


        /**
         * Clears all of the bodies in this tree (retains structure), allowing it to be promptly repopulated in the
         * next sub-timestep.
         */
        void clearAll();

        /**
         * Use this to add each individual kinematic body shape to the treenode
         *
         * @param kBody the kinematic body shape to add
         */
        @Override
        void accept(final CrappyShape_QuadTree_Interface kBody);

        /**
         * Use this to add each dynamic body shape to the treenode, and return the set of the other body shapes
         * which its bounding box overlaps
         *
         * @param dBody the dynamic body shape to add
         *
         * @return all of the other body shapes (dynamic and kinematic) which dBody's bounding boxes overlap with
         */
        @Override
        Set<CrappyShape_QuadTree_Interface> apply(final CrappyShape_QuadTree_Interface dBody);

    }

    public interface I_DynamicKinematicAABBQuadTreeRootNode extends I_DynamicKinematicAABBQuadTreeNode{

        /**
         * Shortcut method to add all of the kinematic bodies to the tree at once
         * @param kinematics all the kinematic bodies to add
         */
        default void addAllKinematicBodies(Collection<? extends CrappyBody> kinematics){
            for (final CrappyBody k: kinematics) {
                if (k.getBodyType() != CrappyBody.CRAPPY_BODY_TYPE.KINEMATIC){
                    throw new IllegalArgumentException("Expected a kinematic shape, got " + k.getBodyType() + "!");
                }
                accept(k.getShape());
            }
        }

        /**
         * Wrapper for {@link #apply(CrappyShape_QuadTree_Interface)}, less awkward to look at.
         *
         * Anyway, it returns all of the {@link CrappyShape_QuadTree_Interface} items in this QuadTree that
         * have a bounding box that intersects with this one (and also adds it to the appropriate child nodes of the
         * QuadTree after it's done working out what things in there it intersected with)
         * @param dBody the dynamic body we're trying to add/check.
         * @return set of all the other pre-existing CrappyShape_QuadTree_Interface objects that the new shape collides with
         */
        default Set<CrappyShape_QuadTree_Interface> checkDynamicBodyAABB(final CrappyShape_QuadTree_Interface dBody){
            if (dBody.getShape().body.getBodyType() != CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC){
                throw new IllegalArgumentException("Expected a dynamic body, and that sure isn't dynamic!");
            }
            return apply(dBody);
        }

    }


    /**
     * A node in the Dynamic/Kinematic AABB quad tree
     */
    private static class DynamicKinematicAABBQuadTreeNode implements
            I_DynamicKinematicAABBQuadTreeNode,
            ChildNodeIterable<I_DynamicKinematicAABBQuadTreeNode, AABB_Quad_Enum>
    {


        private final Vect2D midpoint;

        private final I_DynamicKinematicAABBQuadTreeNode x_min_y_min;
        private final I_DynamicKinematicAABBQuadTreeNode x_max_y_max;
        private final I_DynamicKinematicAABBQuadTreeNode x_min_y_max;
        private final I_DynamicKinematicAABBQuadTreeNode x_max_y_min;


        DynamicKinematicAABBQuadTreeNode(
                final IPair<Vect2D, Vect2D> minAndMidpoint, final int thisDepth, final int depthLimit
        ) {
            this(minAndMidpoint.getFirst(), minAndMidpoint.getSecond(), thisDepth, depthLimit);
        }

        DynamicKinematicAABBQuadTreeNode(
                final Vect2D min, final Vect2D mid, final int thisDepth, final int depthLimit
        ){
            midpoint = mid;

            if (thisDepth==depthLimit){
                x_min_y_min = new DynamicKinematicAABBQuadTreeLeafNode();
                x_max_y_max = new DynamicKinematicAABBQuadTreeLeafNode();
                x_min_y_max = new DynamicKinematicAABBQuadTreeLeafNode();
                x_max_y_min = new DynamicKinematicAABBQuadTreeLeafNode();
            } else {
                final IQuadruplet<
                        IPair<Vect2D, Vect2D>,
                        IPair<Vect2D, Vect2D>,
                        IPair<Vect2D, Vect2D>,
                        IPair<Vect2D, Vect2D>
                > lbmids = Vect2DMath.ALL_QUARTER_REGIONS_LOWERBOUND_MIDPOINTS(min, mid);

                x_min_y_min = new DynamicKinematicAABBQuadTreeNode(
                        lbmids.getFirst(), thisDepth+1, depthLimit
                );
                x_max_y_max = new DynamicKinematicAABBQuadTreeNode(
                        lbmids.getSecond(), thisDepth+1, depthLimit
                );
                x_min_y_max = new DynamicKinematicAABBQuadTreeNode(
                        lbmids.getThird(), thisDepth+1, depthLimit
                );
                x_max_y_min = new DynamicKinematicAABBQuadTreeNode(
                        lbmids.getFourth(), thisDepth+1, depthLimit
                );

            }

        }


        /**
         * Clears all of the bodies in this tree (retains structure), allowing it to be promptly repopulated in the next
         * sub-timestep.
         */
        @Override
        public void clearAll() {
            x_min_y_min.clearAll();
            x_max_y_max.clearAll();
            x_min_y_max.clearAll();
            x_max_y_min.clearAll();
        }

        @Override
        public void accept(final CrappyShape_QuadTree_Interface kBody){

            //childNodeIterator(AABB_Quad_Enum.AABB_Choose_Quadtree_Enum.get(midpoint, kBody)).forEachRemaining(a -> a.accept(kBody));

            for (final Iterator<I_DynamicKinematicAABBQuadTreeNode> it =
                    childNodeIterator(AABB_Quad_Enum.AABB_Choose_Quadtree_Enum.get(midpoint, kBody)); it.hasNext(); ) {
                it.next().accept(kBody);
            }

        }


        @Override
        public Set<CrappyShape_QuadTree_Interface> apply(CrappyShape_QuadTree_Interface dBody) {
            final Set<CrappyShape_QuadTree_Interface> results = new HashSet<>();
            for (final Iterator<I_DynamicKinematicAABBQuadTreeNode> it =
                 childNodeIterator(AABB_Quad_Enum.AABB_Choose_Quadtree_Enum.get(midpoint, dBody)); it.hasNext(); ) {
                results.addAll(it.next().apply(dBody));
            }
            return results;
        }

        /**
         * Obtains the relevant child layer via an AABB_Quad_Enum
         * @param e the AABB_Quad_Enum for the relevant child layer
         * @return the appropriate child layer.
         */
        @Override
        public I_DynamicKinematicAABBQuadTreeNode getChildLayer(final AABB_Quad_Enum e){
            switch (e){
                case X_SMALLER_Y_SMALLER:
                    return x_min_y_min;
                case X_GREATER_Y_SMALLER:
                    return x_max_y_min;
                case X_SMALLER_Y_GREATER:
                    return x_min_y_max;
                case X_GREATER_Y_GREATER:
                default:
                    return x_max_y_max;
            }
        }


        /**
         * A leaf node for dynamic/kinematic quad tree leaf nodes
         */
        private static class DynamicKinematicAABBQuadTreeLeafNode implements I_DynamicKinematicAABBQuadTreeNode{

            /**
             * Holds all the shapes in this set
             */
            private final Collection<CrappyShape_QuadTree_Interface> allShapes = new ArrayList<>();

            /**
             * Boring default empty set for future reference.
             */
            private static final Set<CrappyShape_QuadTree_Interface> emptySet =
                    Collections.unmodifiableSet(new HashSet<>(0));

            DynamicKinematicAABBQuadTreeLeafNode(){}



            @Override
            public void clearAll() {
                allShapes.clear();
            }

            /**
             * Use this to add each individual kinematic body shape to the treenode
             *
             * @param kBody the kinematic body shape to add
             */
            @Override
            public void accept(final CrappyShape_QuadTree_Interface kBody){
                allShapes.add(kBody);
            }

            /**
             * Use this to add each dynamic body shape to the treenode, and return the set of the other body shapes
             * which its bounding box overlaps
             *
             * @param dBody the dynamic body shape to add
             *
             * @return all of the other body shapes (dynamic and kinematic) which dBody's bounding boxes overlap with
             */
            @Override
            public Set<CrappyShape_QuadTree_Interface> apply(final CrappyShape_QuadTree_Interface dBody) {
                if (allShapes.isEmpty()){
                    allShapes.add(dBody);
                    return emptySet;
                }
                final Set<CrappyShape_QuadTree_Interface> collidedWith = new LinkedHashSet<>();
                for (final CrappyShape_QuadTree_Interface s: allShapes) {
                    if (s.getBoundingBox().check_bb_intersect(s.getBoundingBox())){
                        collidedWith.add(s);
                    }
                }
                allShapes.add(dBody);
                return collidedWith;
            }
        }



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


        private static class DynamicKinematicQuadTreeRootNode extends DynamicKinematicAABBQuadTreeNode implements I_DynamicKinematicAABBQuadTreeRootNode{

            // TODO: factory method

            DynamicKinematicQuadTreeRootNode(final IPair<Vect2D, Vect2D> minAndMidpoint, final int depthLimit) {
                super(minAndMidpoint, 0, depthLimit);
            }

            public DynamicKinematicQuadTreeRootNode(Vect2D min, Vect2D mid, int depthLimit) {
                super(min, mid, 0, depthLimit);
            }
        }
    }

    /**
     * Simple factory method for dynamicKinematicAABBQuadTree root nodes
     * @param min lower bound (x,y) coord
     * @param mid midpoint coord
     * @param depthLimit how deep will this tree go?
     * @param kBodies kinematic bodies to add to the tree
     * @return an initialized dynamic/kinematic body AABB QuadTree, with kinematic bodies in there already,
     * just waiting for static bodies to be added.
     */
    public static I_DynamicKinematicAABBQuadTreeRootNode DYN_KIN_AABB_FACTORY(
            final Vect2D min, final Vect2D mid, final int depthLimit,
            final Collection<? extends CrappyBody> kBodies
    ){

        final I_DynamicKinematicAABBQuadTreeRootNode quadTree =
                new DynamicKinematicAABBQuadTreeNode.DynamicKinematicQuadTreeRootNode(min,mid, depthLimit);

        quadTree.addAllKinematicBodies(kBodies);

        return quadTree;

    }


    /**
     * An enumeration used to identify sub-regions of a quadtree region
     */
    public enum AABB_Quad_Enum implements BitmaskPredicate, Comparable<AABB_Quad_Enum>{

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
         * Returns an iterator over the values of this enum
         *
         * @return an Iterator.
         */
        public static Iterator<AABB_Quad_Enum> iterator() {
            return new AABB_Quad_Iter();
        }

        /**
         * A rather boring iterator over values of AABB_Quad_Enum
         */
        private static class AABB_Quad_Iter implements Iterator<AABB_Quad_Enum>{

            private static final AABB_Quad_Enum[] vals = AABB_Quad_Enum.values();

            private int c = 0;

            AABB_Quad_Iter(){}

            @Override
            public boolean hasNext() {
                return c < vals.length;
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             *
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public AABB_Quad_Enum next() {
                return vals[c++];
            }
        }


        /**
         * An enumeration used to indicate relationship between a bounding box's bounds and a given 'midpoint' it's
         * being compared to
         */
        public enum AABB_Choose_Quadtree_Enum implements IHaveBitmask, Iterable<AABB_Quad_Enum> {

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

            /**
             * Returns an iterator over the elements of {@link AABB_Quad_Enum}
             * which basically return true when they have {@code other.test(this)} applied
             * to them with this AABB_Choose_Quadtree_Enum value.
             *
             * @return an Iterator.
             */
            @Override
            public Iterator<AABB_Quad_Enum> iterator() {
                return new ChooseEnumQuadEnum_Iter(this);
            }

            private static class ChooseEnumQuadEnum_Iter implements Iterator<AABB_Quad_Enum>{

                private final IHaveBitmask bm;

                private final AABB_Quad_Iter qiter = new AABB_Quad_Iter();

                private AABB_Quad_Enum next = AABB_Quad_Enum.X_SMALLER_Y_SMALLER;

                ChooseEnumQuadEnum_Iter(final IHaveBitmask bm){
                    this.bm = bm;
                }

                /**
                 * Tries to find the next value in qiter which
                 *
                 * @return {@code true} if the iteration has more elements
                 */
                @Override
                public boolean hasNext() {
                    while (qiter.hasNext()){
                        next = qiter.next();
                        if (next.test(bm)){
                            return true;
                        }
                    }
                    return false;
                }

                /**
                 * Returns the next element in the iteration.
                 *
                 * @return the next element in the iteration
                 *
                 * @throws NoSuchElementException if the iteration has no more elements
                 */
                @Override
                public AABB_Quad_Enum next() {
                    return next;
                }
            }
        }

    }

}
