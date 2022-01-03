package crappyGame;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyWorld;
import crappy.I_View_CrappyBody;
import crappy.collisions.AABBQuadTreeTools;
import crappy.collisions.CrappyCircle;
import crappy.collisions.CrappyLine;
import crappy.collisions.CrappyPolygon;
import crappy.graphics.I_GraphicsTransform;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.UI.Viewable;

import java.util.*;
import java.awt.*;
import java.util.List;

public class CrappyModel implements Viewable {

    final Dimension dims = new Dimension(800,600 );

    final CrappyWorld world = new CrappyWorld(CrappyWorld.GRAVITY);

    final I_GraphicsTransform gt = new GraphicsTransform(10, 10, 800, 600);

    final MyRenderer r = new MyRenderer(gt);

    CrappyModel(){

        List<CrappyBody> statics = new ArrayList<>();

        CrappyBody c = new CrappyBody(
                new Vect2D(0.5, 0.5),
                Vect2D.ZERO,
                Rot2D.IDENTITY,
                0,
                0,
                1.5,
                0,
                0,
                CrappyBody.CRAPPY_BODY_TYPE.STATIC,
                1,
                -1,
                new CrappyCallbackHandler() {},
                new Object(),
                "crap"
        );
        new CrappyLine(c, new Vect2D(0, 1), new Vect2D(10, 1.25));

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
                1.5,
                0.0001,
                0.0001,
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
        new CrappyCircle(c2, 0.25);

        world.addBody(c2);


        CrappyBody c3 = new CrappyBody(
                new Vect2D(7.5, 5),
                //Vect2D.ZERO,
                Vect2D.POLAR(Rot2D.FROM_DEGREES(45), 5),
                Rot2D.IDENTITY,
                0,
                1.2,
                1.5,
                0.0001,
                0.0001,
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

        new CrappyCircle(c3, 0.25);
        //CrappyPolygon.POLYGON_FACTORY_REGULAR(c3, 5, 0.375);

        world.addBody(c3);
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
