package crappy.shapes;


public class A_CrappyShape {

    public CRAPPY_SHAPE_TYPE shapeType;
    double radius;

    /**
     * Something to define what each of these collision shapes are
     */
    public static enum CRAPPY_SHAPE_TYPE{
        CIRCLE,
        POLYGON,
        COMPOUND_POLYGON
    }

    public double getRadius() {
        return radius;
    }

    public CRAPPY_SHAPE_TYPE getShapeType() {
        return shapeType;
    }
}
