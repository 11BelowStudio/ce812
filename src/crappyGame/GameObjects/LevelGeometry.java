package crappyGame.GameObjects;

import crappy.CrappyBody;
import crappy.CrappyWorld;
import crappy.collisions.AABBQuadTreeTools;
import crappy.math.Vect2D;

import java.util.ArrayList;
import java.util.Collection;

public final class LevelGeometry {

    private LevelGeometry(){}

    private static CrappyBody edgeMaker(double sx, double sy, double ex, double ey){
        return edgeMaker(new Vect2D(sx,sy), new Vect2D(ex, ey), Double.NaN);
    }

    private static CrappyBody edgeMaker(double sx, double sy, double ex, double ey, double w){
        return edgeMaker(new Vect2D(sx,sy), new Vect2D(ex, ey), w);
    }

    private static CrappyBody edgeMaker(Vect2D start, Vect2D end, double w){
        return CrappyBody.EDGE_BODY_MAKER(start,end, w, 1.1, BodyTagEnum.WORLD.bitmask, -1,
                CrappyBody.CrappyBodyCreator.defaultCallbackHandler, new Object(), "wall"
        );
    }

    public static void makeLevel1(CrappyWorld w, final double WIDTH, final double HEIGHT){


        Collection<CrappyBody> bodies = new ArrayList<>();

        bodies.add(edgeMaker(WIDTH, HEIGHT, 0, HEIGHT));
        bodies.add(edgeMaker(0,HEIGHT,0,0));
        bodies.add(edgeMaker(0,HEIGHT*3/4, WIDTH/3, HEIGHT*3/4, 0.1));
        bodies.add(edgeMaker(WIDTH/3, HEIGHT*3/4, WIDTH/2, WIDTH/2, 0.1));
        bodies.add(edgeMaker(WIDTH/2, HEIGHT/2, WIDTH*3/16, HEIGHT*7/16, 0.1));
        bodies.add(edgeMaker(WIDTH*3/16, HEIGHT*7/16, WIDTH/8, HEIGHT/8, 0.1));
        bodies.add(edgeMaker(WIDTH/8, HEIGHT/8, WIDTH/2, HEIGHT/16, 0.1));
        bodies.add(edgeMaker(WIDTH/2, HEIGHT/16, WIDTH*13/16, HEIGHT/8, 0.1));
        bodies.add(edgeMaker(WIDTH*13/16, HEIGHT/8, WIDTH*10/12, HEIGHT/2, 0.1));
        bodies.add(edgeMaker(WIDTH*10/12, HEIGHT/2, WIDTH*3/4, HEIGHT*3/4, 0.1));
        bodies.add(edgeMaker(WIDTH*3/4, HEIGHT*3/4, WIDTH/2, HEIGHT, 0.1));
        bodies.add(edgeMaker(0, 0, WIDTH, 0));

        // TODO: finish line

        w.setStaticGeometry(
                AABBQuadTreeTools.STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(bodies)
        );


    }
}
