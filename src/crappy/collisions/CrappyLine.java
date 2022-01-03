package crappy.collisions;

import crappy.CrappyBody_ShapeSetter_Interface;
import crappy.CrappyBody_Shape_Interface;
import crappy.I_Transform;
import crappy.graphics.DrawableCrappyShape;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class CrappyLine extends A_CrappyShape implements Iterable<I_CrappyEdge>, I_CrappyLine, DrawableCrappyShape.DrawableLine {

    // TODO: refactor so it's functionally just two CrappyEdges that are the reverse of each other

    final CrappyEdge edgeA;

    final CrappyEdge edgeB;


    final Vect2D[] localVertices;

    final Vect2D[] worldVertices;

    private Vect2D drawableStart;

    private Vect2D drawableEnd;

    private Vect2D drawableNormStart;

    private Vect2D drawableNormEnd;


    /**
     * Constructor for a CrappyLine with a specified end point (implicitly with a point at 0,0 local coords)
     * @param body the CrappyBody which this line belongs to
     * @param end the end point for this line (in local coords)
     */
    public CrappyLine(final CrappyBody_ShapeSetter_Interface body, final Vect2D end){
        this(body, end, Vect2D.ZERO);
    }


    /**
     * Constructor for a CrappyLine
     * @param body the CrappyBody
     * @param start where this line starts
     * @param end where this line ends
     */
    public CrappyLine(final CrappyBody_ShapeSetter_Interface body, final Vect2D start, final Vect2D end) {
        super(body, CRAPPY_SHAPE_TYPE.LINE, Vect2DMath.MIDPOINT(start, end));


        edgeA = new CrappyEdge(start, end, body, 0.0);
        edgeB = new CrappyEdge(end, start, body, 0.0);


        this.localVertices = new Vect2D[]{start, end};
        this.worldVertices = new Vect2D[2];


        this.aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(body, localVertices, worldVertices)
        );

        body.__setShape__internalDoNotCallYourselfPlease(
                this, Vect2DMath.LINE_START_CENTROID_MOMENT_OF_INERTIA(start, getCentroid(), body.getMass())
        );


    }

    @Override
    public Crappy_AABB updateShape(final I_Transform rootTransform) {

        // TODO: update AABB without using localVertices/worldVertices,
        aabb.update_aabb(
                Vect2DMath.LOCAL_TO_WORLD_FOR_BODY_TO_OUT_AND_GET_BOUNDS(rootTransform, localVertices, worldVertices)
        );

        edgeA.updateShape(rootTransform);
        edgeB.updateShape(rootTransform);
        return aabb;
    }


    @Override
    public void drawCrappily(I_CrappilyDrawStuff renderer) {
        renderer.acceptLine(this);
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


    @Override
    public String toString() {
        return "CrappyLine{" +
                "shapeType=" + shapeType +
                //", body=" + body +
                ", aabb=" + aabb +
                ", thisFrameAABB=" + thisFrameAABB +
                ", lastFrameAABB=" + lastFrameAABB +
                ", localCentroid=" + localCentroid +
                ", radius=" + radius +
                ", radiusSquared=" + radiusSquared +
                ", syncer=" + syncer +
                ", edgeA=" + edgeA +
                ", edgeB=" + edgeB +
                ", localVertices=" + Arrays.toString(localVertices) +
                ", worldVertices=" + Arrays.toString(worldVertices) +
                ", drawableStart=" + drawableStart +
                ", drawableEnd=" + drawableEnd +
                ", drawableNormStart=" + drawableNormStart +
                ", drawableNormEnd=" + drawableNormEnd +
                '}';
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

    public void updateDrawables(){
        super.updateDrawables();
        synchronized (drawableSyncer){
            drawableStart = getWorldStart();
            drawableEnd = getWorldEnd();
            drawableNormStart = getCentroid().add(edgeA.getWorldNorm());
            drawableNormEnd = getCentroid().add(edgeB.getWorldNorm());
            edgeA.getEndPointCircle().updateDrawables();
            edgeB.getEndPointCircle().updateDrawables();
        }
    }

    @Override
    public Vect2D getDrawableStart() {
        synchronized (drawableSyncer){
            return drawableStart;
        }
    }

    @Override
    public Vect2D getDrawableEnd() {
        synchronized (drawableSyncer){
            return drawableEnd;
        }
    }

    @Override
    public Vect2D getDrawableNorm() {
        synchronized (drawableSyncer){
            return drawableNormStart;
        }
    }

    @Override
    public Vect2D getDrawableNormEnd() {
        synchronized (drawableSyncer){
            return drawableNormEnd;
        }
    }

    @Override
    public DrawableCircle getDrawableEndCircle() {
        synchronized (drawableSyncer){
            return edgeA.getDrawableEndCircle();
        }
    }

    @Override
    public DrawableCircle getDrawableOtherEndCircle() {
        synchronized (drawableSyncer){
            return edgeB.getDrawableEndCircle();
        }
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
