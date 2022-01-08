package crappyGame.models;

import crappy.CrappyBody;
import crappy.CrappyWorld;
import crappy.collisions.AABBQuadTreeTools;
import crappy.collisions.CrappyEdge;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.bitmasks.IHaveBitmask;
import crappy.utils.containers.IQuadruplet;
import crappy.utils.containers.ITriplet;
import crappy.utils.containers.Quadruplet;
import crappyGame.GameObjects.BodyTagEnum;
import crappyGame.assets.ImageManager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

public final class LevelGeometry {

    private LevelGeometry(){}

    @FunctionalInterface
    public interface IGeomMaker extends
            BiFunction<
                    Double, Double,
                    IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>>>{}

    private static CrappyBody edgeMaker(double sx, double sy, double ex, double ey){
        return edgeMaker(new Vect2D(sx,sy), new Vect2D(ex, ey), Double.NaN);
    }

    private static CrappyBody edgeMaker(double sx, double sy, double ex, double ey, double w){
        return edgeMaker(new Vect2D(sx,sy), new Vect2D(ex, ey), w);
    }

    private static CrappyBody edgeMaker(Vect2D start, Vect2D end, double w){
        return CrappyBody.EDGE_BODY_MAKER(start,end, w, 1, BodyTagEnum.WORLD.bitmask, -1,
                CrappyBody.CrappyBodyCreator.defaultCallbackHandler, new Object(), "wall"
        );
    }

    public static IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>> makeLevel1(
            final double WIDTH, final double HEIGHT
    ){


        final Vect2D SHIP_POS = new Vect2D(WIDTH/6, HEIGHT*7/8);

        final Vect2D PAYLOAD_POS = new Vect2D(WIDTH*5/16, HEIGHT/4);

        final Optional<BufferedImage> BACKGROUND_IMAGE = ImageManager.BG1.getOptional();

        final Collection<CrappyBody> bodies = new ArrayList<>();

        bodies.add(edgeMaker(WIDTH/2, HEIGHT, 0, HEIGHT));
        bodies.add(edgeMaker(0,HEIGHT,0,HEIGHT*3/4));
        bodies.add(edgeMaker(0,HEIGHT*3/4, WIDTH/3, HEIGHT*3/4, 2.5));
        bodies.add(edgeMaker(WIDTH/3, HEIGHT*3/4, WIDTH*3/8, HEIGHT/2, 0.15));
        bodies.add(edgeMaker(WIDTH*3/8, HEIGHT/2, WIDTH*3/16, HEIGHT*7/16, 0.15));
        bodies.add(edgeMaker(WIDTH*3/16, HEIGHT*7/16, WIDTH/17, HEIGHT/14, 0.15));
        bodies.add(edgeMaker(WIDTH/17, HEIGHT/14, WIDTH/2, HEIGHT/16));
        bodies.add(edgeMaker(WIDTH/2, HEIGHT/16, WIDTH*13/16, HEIGHT/8));
        bodies.add(edgeMaker(WIDTH*13/16, HEIGHT/8, WIDTH*10/12, HEIGHT/2));
        bodies.add(edgeMaker(WIDTH*10/12, HEIGHT/2, WIDTH*3/4, HEIGHT*3/4));
        bodies.add(edgeMaker(WIDTH*3/4, HEIGHT*3/4, WIDTH/2, HEIGHT));
        //bodies.add(edgeMaker(0, 0, WIDTH, 0));

        System.out.println(bodies.size());

        CrappyBody finishLineBody = new CrappyBody(
                new Vect2D(WIDTH/2, HEIGHT),
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                0,
                0,
                0,
                CrappyBody.CRAPPY_BODY_TYPE.STATIC,
                BodyTagEnum.FINISH_LINE.getBitmask(),
                IHaveBitmask.COMBINE_BITMASKS_OR(BodyTagEnum.PAYLOAD, BodyTagEnum.SHIP),
                CrappyBody.CrappyBodyCreator.defaultCallbackHandler,
                new Object(),
                "Finish line",
                false,
                false,
                false
        );
        bodies.add(finishLineBody);

        CrappyEdge finishLineEdge = new CrappyEdge(
                finishLineBody, Vect2D.ZERO, Vect2DMath.VECTOR_BETWEEN(new Vect2D(WIDTH/2, HEIGHT),
                new Vect2D(WIDTH/3, HEIGHT*3/4)), Double.NaN
        );
        finishLineEdge.renderable = false;



        System.out.println(bodies.size());

        return IQuadruplet.of(
                SHIP_POS,
                PAYLOAD_POS,
                AABBQuadTreeTools.STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(bodies),
                BACKGROUND_IMAGE
        );

    }


    public static IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>> makeLevel2(
            final double WIDTH, final double HEIGHT
    ){

        final Optional<BufferedImage> BACKGROUND_IMAGE = ImageManager.BG3.getOptional();

        final double W = WIDTH*2;
        final double H = HEIGHT * 2;

        final Vect2D SHIP_POS =new Vect2D(W/8, H*7/8);

        final Vect2D PAYLOAD_POS = new Vect2D(W*3/4, H/2);

        final Collection<CrappyBody> bodies = new ArrayList<>();


        bodies.add(edgeMaker(0,H,W/16, H*7/8));
        bodies.add(edgeMaker(W/16, H*7/8, W/32, H*3/4));
        bodies.add(edgeMaker(W/32, H*3/4, W/12, H/2));
        bodies.add(edgeMaker(W/12, H/2, W/8, H/8));
        bodies.add(edgeMaker(W/8, H/8, W/2, H/18));
        bodies.add(edgeMaker(W/2, H/18, W*3/4, H/15));
        bodies.add(edgeMaker(W*3/4, H/15, W*13/14, H/2));
        bodies.add(edgeMaker(W*13/14, H/2, W*15/18, H*9/12));
        bodies.add(edgeMaker(W*15/18, H*9/12, W*2/3, H*2/3, 0.2));
        bodies.add(edgeMaker(W*2/3, H*2/3, W/2, H/3, 0.4));
        bodies.add(edgeMaker(W/2, H/3, W/3, H*4/7, 0.4));
        bodies.add(edgeMaker(W/3, H*4/7, W*5/16, H*3/4, 0.6));
        bodies.add(edgeMaker(W*5/16, H*3/4, W/2, H, 0.5));
        bodies.add(edgeMaker(W/2, H, 0, H));

        CrappyBody finishLineBody = new CrappyBody(
                new Vect2D(W*5/16, H*3/4),
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                0,
                0,
                0,
                CrappyBody.CRAPPY_BODY_TYPE.STATIC,
                BodyTagEnum.FINISH_LINE.getBitmask(),
                IHaveBitmask.COMBINE_BITMASKS_OR(BodyTagEnum.PAYLOAD, BodyTagEnum.SHIP),
                CrappyBody.CrappyBodyCreator.defaultCallbackHandler,
                new Object(),
                "Finish line",
                false,
                false,
                false
        );
        bodies.add(finishLineBody);

        CrappyEdge finishLineEdge = new CrappyEdge(
                finishLineBody, Vect2D.ZERO, Vect2DMath.VECTOR_BETWEEN(new Vect2D(W*5/16, H*3/4),
                new Vect2D(W/32, H*3/4)), Double.NaN
        );
        finishLineEdge.renderable = false;

        return IQuadruplet.of(
                SHIP_POS,
                PAYLOAD_POS,
                AABBQuadTreeTools.STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(bodies),
                BACKGROUND_IMAGE
        );
    }

    public static IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>> makeLevel3(
            final double WIDTH, final double HEIGHT
    ) {

        final double H = HEIGHT * 3;
        final double W = WIDTH;// * 1.1;

        final Vect2D SHIP_POS = new Vect2D(W/2, H - (HEIGHT/2));

        final Vect2D PAYLOAD_POS = new Vect2D(W/2, HEIGHT/2);

        final Optional<BufferedImage> BACKGROUND_IMAGE = ImageManager.BG6.getOptional();

        final Collection<CrappyBody> bodies = new ArrayList<>();

        bodies.add(edgeMaker(0, H, W/30, H-(HEIGHT/2)));
        bodies.add(edgeMaker(W/30, H-(HEIGHT/2), W*6/16, HEIGHT/8));
        bodies.add(edgeMaker(W*6/16, HEIGHT/8,W*15/32, HEIGHT/16));
        bodies.add(edgeMaker(W*15/32, HEIGHT/16, W*17/32, HEIGHT/16));
        bodies.add(edgeMaker(W*17/32, HEIGHT/16, W*10/16, HEIGHT/8));
        bodies.add(edgeMaker(W*10/16, HEIGHT/8, W*29/30, H-(HEIGHT/2)));
        bodies.add(edgeMaker(W*29/30, H-(HEIGHT/2), W, H));
        bodies.add(edgeMaker(W,H,0,H));

        CrappyBody finishLineBody = new CrappyBody(
                new Vect2D(W, H-(HEIGHT/2)),
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                0,
                0,
                0,
                CrappyBody.CRAPPY_BODY_TYPE.STATIC,
                BodyTagEnum.FINISH_LINE.getBitmask(),
                IHaveBitmask.COMBINE_BITMASKS_OR(BodyTagEnum.PAYLOAD, BodyTagEnum.SHIP),
                CrappyBody.CrappyBodyCreator.defaultCallbackHandler,
                new Object(),
                "Finish line",
                false,
                false,
                false
        );
        bodies.add(finishLineBody);

        CrappyEdge finishLineEdge = new CrappyEdge(
                finishLineBody,
                Vect2D.ZERO,
                new Vect2D(-W, 0),
                Double.NaN
        );
        finishLineEdge.renderable = false;

        return IQuadruplet.of(
                SHIP_POS,
                PAYLOAD_POS,
                AABBQuadTreeTools.STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(bodies),
                BACKGROUND_IMAGE
        );
    }
}
