package crappy.collisions;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface CrappyShape_QuadTree_Interface {

    I_Crappy_AABB getBoundingBox();

    A_CrappyShape getShape();

}
