package crappy.collisions;

/**
 * Interface for the CrappyLine, with methods to get both of the overlapping CrappyEdges.
 */
public interface I_CrappyLine extends I_CrappyShape, Iterable<I_CrappyEdge>{

    /**
     * Returns the first crappyedge
     * @return the first crappyedge
     */
    public I_CrappyEdge getEdgeA();

    /**
     * Returns the second crappyedge
     * @return the second crappyedge
     */
    public I_CrappyEdge getEdgeB();
}
