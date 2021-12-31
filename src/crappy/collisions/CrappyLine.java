package crappy.collisions;

import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.math.M_Vect2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class CrappyLine extends A_CrappyShape implements Iterable<I_CrappyEdge>, I_CrappyLine{

    // TODO: refactor so it's functionally just two CrappyEdges that are the reverse of each other

    final CrappyEdge edgeA;

    final CrappyEdge edgeB;


    final Vect2D[] localVertices;

    final Vect2D[] worldVertices;


    /**
     * Constructor for a CrappyLine with a specified end point (implicitly with a point at 0,0 local coords)
     * @param body the CrappyBody which this line belongs to
     * @param end the end point for this line (in local coords)
     */
    public CrappyLine(final CrappyBody_Shape_Interface body, final Vect2D end){
        this(body, end, Vect2D.ZERO);
    }


    /**
     * Constructor for a CrappyLine
     * @param body the CrappyBody
     * @param start where this line starts
     * @param end where this line ends
     */
    public CrappyLine(final CrappyBody_Shape_Interface body, final Vect2D start, final Vect2D end) {
        super(CRAPPY_SHAPE_TYPE.LINE, body, Vect2DMath.MIDPOINT(start, end),  2);


        edgeA = new CrappyEdge(start, end, body);
        edgeB = new CrappyEdge(end, start, body);


        this.localVertices = new Vect2D[]{start, end};
        this.worldVertices = new Vect2D[2];


        this.aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(body, localVertices, worldVertices)
        );

        body.setMomentOfInertia(Vect2DMath.LINE_MOMENT_OF_INERTIA(start, end, body.getMass()));

    }

    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {
        aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(rootTransform, localVertices, worldVertices)
        );

        edgeA.updateShape(rootTransform);
        edgeB.updateShape(rootTransform);
        return aabb;
    }

    @Override
    public void updateFinalWorldVertices() {
        synchronized (syncer) {
            Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT(body, localVertices, finalWorldVertices);
        }
        edgeA.updateFinalWorldVertices();
        edgeB.updateFinalWorldVertices();
    }

    public Vect2D getWorldStart(){
        return worldVertices[0];
    }


    public Vect2D getWorldEnd(){
        return worldVertices[1];
    }

    public I_CrappyEdge getEdge(){ return edgeA; }

    public I_CrappyEdge getEdgeA(){return edgeA;}

    public I_CrappyEdge getEdgeB(){return edgeB;}


    public void timestepStartUpdate(){
        super.timestepStartUpdate();
        edgeA.timestepStartUpdate();
        edgeB.timestepStartUpdate();
    }

    @Override
    public void midTimestepUpdate() {
        super.midTimestepUpdate();
        edgeA.midTimestepUpdate();
        edgeB.midTimestepUpdate();
    }

    @Override
    public void timestepEndUpdate(){
        super.timestepEndUpdate();
        edgeA.timestepEndUpdate();
        edgeB.timestepEndUpdate();
    }

    /**
     * Returns an iterator over the edges which this line technically consists of
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<I_CrappyEdge> iterator() {
        return new LineEdgeIterator(this);
    }

    private static class LineEdgeIterator implements Iterator<I_CrappyEdge>{

        private final CrappyLine l;

        private int count = 0;

        private LineEdgeIterator(final CrappyLine c){
            l = c;
        }

        /**
         * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link
         * #next} would return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return count++ < 2;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         *
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public I_CrappyEdge next() throws NoSuchElementException {
            switch (count){
                case 1:
                    return l.getEdgeA();
                case 2:
                    return l.getEdgeB();
            }
            throw new NoSuchElementException("out of edges!");
        }
    }
}
