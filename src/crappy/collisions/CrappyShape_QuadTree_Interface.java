package crappy.collisions;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An interface for the crappyshape within the axis-aligned bounding box quadtree
 */
public interface CrappyShape_QuadTree_Interface {

    I_Crappy_AABB getBoundingBox();

    I_CrappyShape getShape();

}
