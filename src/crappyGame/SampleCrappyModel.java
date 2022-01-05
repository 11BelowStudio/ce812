package crappyGame;

import crappy.*;
import crappy.collisions.*;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.UI.Viewable;
import crappyGame.assets.ImageManager;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.awt.*;
import java.util.List;

public class SampleCrappyModel implements Viewable {

    final Dimension dims = new Dimension(800,650 );

    final static Vect2D THRUST_GRAV = new Vect2D(0, -0.95);

    final CrappyWorld world = new CrappyWorld(CrappyWorld.GRAVITY);

    final GraphicsTransform gt = new GraphicsTransform(10, 10, 800, 650);

    final MyRenderer r = new MyRenderer(gt);

    SampleCrappyModel(){

        List<CrappyBody> statics = new ArrayList<>();

        CrappyBody c = new CrappyBody(
                new Vect2D(0, 0),
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                1,
                0.000000001,
                0,
                CrappyBody.CRAPPY_BODY_TYPE.STATIC,
                1,
                -1,
                new CrappyCallbackHandler() {},
                new Object(),
                "line",
                true,
                false,
                false
        );
        new CrappyLine(c, new Vect2D(0, 1), new Vect2D(10, 2));

        //new CrappyEdge(c, new Vect2D(0, 1), new Vect2D(10, 1), 0.0);

        statics.add(c);

        world.setStaticGeometry(
                AABBQuadTreeTools.STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(statics)
        );


        CrappyBody c2 = new CrappyBody(
                new Vect2D(2.5, 5),
                Vect2D.ZERO,
                //Vect2D.POLAR(Rot2D.FROM_DEGREES(-45), 10),
                Rot2D.IDENTITY,
                0,
                1,
                1,
                0.0001,
                0.0001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                3,
                -1,
                new CrappyCallbackHandler() {},
                new Object(),
                "cungaradeo",
                true,
                true,
                true
        );
        CrappyPolygon.POLYGON_FACTORY_REGULAR(c2, 5, 0.375);
        //new CrappyCircle(c2, 0.25);

        world.addBody(c2);


        CrappyBody c3 = new CrappyBody(
                new Vect2D(7.5, 5),
                Vect2D.ZERO,
                //Vect2D.POLAR(Rot2D.FROM_DEGREES(45), 10),
                Rot2D.IDENTITY,
                0,
                1,
                1,
                0.0000001,
                0.0001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                3,
                -1,
                new CrappyCallbackHandler() {},
                new Object(),
                "cungaradeo2",
                true,
                true,
                true
        );

        //new CrappyCircle(c3, 0.25);
        CrappyPolygon.POLYGON_FACTORY_REGULAR(c3, 5, 0.375);

        world.addBody(c3);

        CrappyBody c4 = new CrappyBody(
                new Vect2D(5, 10),
                Vect2D.ZERO,
                //Vect2D.POLAR(Rot2D.FROM_DEGREES(45), 5),
                Rot2D.IDENTITY,
                0,
                2,
                1,
                0.000001,
                0.001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                3,
                -1,
                new CrappyCallbackHandler() {},
                new Object(),
                "cungaradeo3",
                true,
                true,
                true
        );

        //CrappyPolygon.POLYGON_FACTORY_REGULAR(c4, 4, 0.25);

        Vect2D[] shipShape = new Vect2D[]{
                new Vect2D(0,0.25), new Vect2D(-0.25, -0.25), new Vect2D(0, -0.1), new Vect2D(0.25, -0.25)
        };
        //Vect2D shipCentroid = Vect2DMath.AREA_AND_CENTROID_OF_VECT2D_POLYGON(shipShape).getSecond();
        //System.out.println(Arrays.toString(shipShape));
        //Vect2DMath.OFFSET_VECTORS_IN_PLACE(shipCentroid.invert(), shipShape);
        //System.out.println(Arrays.toString(shipShape));
        new CrappyPolygon(
                c4, shipShape
        );



        System.out.println(c.getMomentOfInertia());
        System.out.println(c2.getMomentOfInertia());
        System.out.println(c3.getMomentOfInertia());
        System.out.println(c4.getMomentOfInertia());


        //CrappyPolygon.POLYGON_FACTORY_REGULAR(c4, 3, Math.sqrt(2)/4);
        //new CrappyCircle(c4, 0.25);
        //CrappyPolygon.POLYGON_FACTORY_REGULAR(c3, 5, 0.375);

        world.addBody(c4);


        world.addConnector(
                new CrappyConnector(
                        c2,
                        Vect2D.ZERO,
                        c3,
                        new Vect2D(0.1, 0.1),
                        0.0/0.0,
                        10,
                        10,
                        true,
                        CrappyConnector.TRUNCATION_RULE_FACTORY(CrappyConnector.TruncationEnum.STANDARD_TRUNCATION, 10),
                        false
                )
        );


    }


    public void update(){


        world.update();
    }

    @Override
    public void draw(Graphics2D g) {

        //gt.updateViewport(Vect2DMath.ADD_SCALED(gt.viewportCorner, Vect2D.ONES, 0.002));

        g.setColor(new Color(40, 43, 47));
        //g.fillRect(0, 0, 800, 650);
        /*
        ImageManager.getImages().c("bg1", (s, i) -> {
            g.setPaint(
                    new TexturePaint(
                            i,
                            new Rectangle2D.Double(gt.viewportCorner.getX(), gt.viewportCorner.getY(), dims.getWidth(), dims.getHeight())
                    )
            );
        })
        // TODO: double check how to optional
         */

        g.fillRect(0, 0, 800, 650);

        r.prepareToRender(g);

        world.renderCrappily(r);
    }

    @Override
    public Dimension getSize() {
        return dims;
    }

    /**
     * Call this to notify the thing being viewed that it needs to be paused
     *
     * @param isPaused true if it needs to be paused, false if it needs to be unpaused.
     */
    @Override
    public void notifyAboutPause(boolean isPaused) {

    }
}
