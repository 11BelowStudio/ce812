package crappyGame;

import crappy.CrappyWorld;
import crappy.math.Vect2D;
import crappyGame.Controller.IController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public abstract class A_Model implements IModel{

    final Dimension dims = new Dimension(880,660 );

    final double VISIBLE_WORLD_HEIGHT = 10;
    final double VISIBLE_WORLD_WIDTH = (10*4)/3.0;
    Vect2D viewportCorner = Vect2D.ZERO;

    final GraphicsTransform gt = new GraphicsTransform(VISIBLE_WORLD_WIDTH, VISIBLE_WORLD_HEIGHT, dims, viewportCorner);

    final Vect2D GAME_GRAVITY = new Vect2D(0, -1.9);

    final CrappyWorld world = new CrappyWorld(GAME_GRAVITY);

    final MyRenderer renderer = new MyRenderer(gt);

    final IController controller;



    A_Model(final IController ctrl){
        controller = ctrl;
    }





    @Override
    public void draw(Graphics2D g) {


        renderer.prepareToRender(g);
        world.renderCrappily(renderer);
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
    public void notifyAboutPause(boolean isPaused) {}



}
