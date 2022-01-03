package crappyGame;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyWorld;
import crappy.I_View_CrappyBody;
import crappy.collisions.*;
import crappy.graphics.I_GraphicsTransform;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.UI.Viewable;

import java.util.*;
import java.awt.*;
import java.util.List;

public class CrappyModel implements Viewable {

    final Dimension dims = new Dimension(800,600 );

    final static Vect2D THRUST_GRAV = new Vect2D(0, -0.95);

    final CrappyWorld world = new CrappyWorld(CrappyWorld.GRAVITY);

    final I_GraphicsTransform gt = new GraphicsTransform(10, 10, 800, 600);

    final MyRenderer r = new MyRenderer(gt);

    CrappyModel(){

        List<CrappyBody> statics = new ArrayList<>();

        CrappyBody c = new CrappyBody(
                new Vect2D(0, 0),
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                1,
                0,
                0,
                CrappyBody.CRAPPY_BODY_TYPE.STATIC,
                1,
                -1,
                new CrappyCallbackHandler() {},
                new Object(),
                "line"
        );
        //new CrappyLine(c, new Vect2D(0, 1), new Vect2D(10, 1));

        new CrappyEdge(c, new Vect2D(0, 1), new Vect2D(10, 1));

        statics.add(c);

        world.setStaticGeometry(
                AABBQuadTreeTools.STATIC_GEOMETRY_AABB_QUADTREE_FACTORY(statics)
        );


        CrappyBody c2 = new CrappyBody(
                new Vect2D(2.5, 5),
                //Vect2D.ZERO,
                Vect2D.POLAR(Rot2D.FROM_DEGREES(-45), 5),
                Rot2D.IDENTITY,
                0,
                1,
                0.9,
                0.01,
                0.001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                3,
                -1,
                new CrappyCallbackHandler() {

                    @Override
                    public void collidedWith(I_View_CrappyBody otherBody) {
                        System.out.println(otherBody.getName());
                    }
                },
                new Object(),
                "cungaradeo"
        );
        CrappyPolygon.POLYGON_FACTORY_REGULAR(c2, 5, 0.375);
        //new CrappyCircle(c2, 0.25);

        world.addBody(c2);


        CrappyBody c3 = new CrappyBody(
                new Vect2D(7.5, 5),
                //Vect2D.ZERO,
                Vect2D.POLAR(Rot2D.FROM_DEGREES(45), 5),
                Rot2D.IDENTITY,
                1,
                1.2,
                0.9,
                0.01,
                0.001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                3,
                -1,
                new CrappyCallbackHandler() {

                    @Override
                    public void collidedWith(I_View_CrappyBody otherBody) {
                        System.out.println(otherBody.getName());
                    }
                },
                new Object(),
                "cungaradeo2"
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
                0.9,
                0.01,
                0.001,
                CrappyBody.CRAPPY_BODY_TYPE.DYNAMIC,
                3,
                -1,
                new CrappyCallbackHandler() {

                    @Override
                    public void collidedWith(I_View_CrappyBody otherBody) {
                        System.out.println(otherBody.getName());
                    }
                },
                new Object(),
                "cungaradeo3"
        );

        /*
        new CrappyPolygon(
                c4, new Vect2D[]{
                        new Vect2D(0,0.25), new Vect2D(-0.25, -0.25), new Vect2D(0, -0.1), new Vect2D(0.25, -0.25)
                }
        );

         */
        //CrappyPolygon.POLYGON_FACTORY_REGULAR(c4, 3, Math.sqrt(2)/4);
        new CrappyCircle(c4, 0.25);
        //CrappyPolygon.POLYGON_FACTORY_REGULAR(c3, 5, 0.375);

        world.addBody(c4);
    }


    public void update(){


        world.update();
    }

    @Override
    public void draw(Graphics2D g) {

        g.setColor(new Color(40, 43, 47));
        g.fillRect(0, 0, 800, 600);

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
