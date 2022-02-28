package crappyGame;

import crappy.CrappyBody;
import crappy.CrappyWorld;
import crappy.math.Vect2D;
import crappyGame.Controller.IController;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic 'model' for the overall MVC-based architecture used by this game.
 */
public abstract class A_Model implements IModel{

    protected final Dimension dims = new Dimension(880,660 );

    protected static final double VISIBLE_WORLD_HEIGHT = 10;
    protected static final double VISIBLE_WORLD_WIDTH = (10*4)/3.0;
    protected Vect2D viewportCorner = Vect2D.ZERO;

    protected final GraphicsTransform gt = new GraphicsTransform(VISIBLE_WORLD_WIDTH, VISIBLE_WORLD_HEIGHT, dims, viewportCorner);

    protected final Vect2D GAME_GRAVITY = new Vect2D(0, -1.9);
    protected final Vect2D INVERTED_GRAVITY = GAME_GRAVITY.invert();

    protected final CrappyWorld world = new CrappyWorld(GAME_GRAVITY);

    protected final MyRenderer renderer = new MyRenderer(gt);

    protected final IController controller;

    protected final List<CrappyBody> pendingBodiesToAdd = new ArrayList<>();

    protected final IGameRunner runner;

    protected static final Color spaceColour = new Color(11, 25, 45);

    protected A_Model(final IController ctrl, IGameRunner runner){

        controller = ctrl;
        this.runner = runner;
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


    public void addNewBody(final CrappyBody c){
        pendingBodiesToAdd.add(c);
    }



}
