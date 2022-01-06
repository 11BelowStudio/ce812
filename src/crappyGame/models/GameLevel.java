package crappyGame.models;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyConnector;
import crappy.I_View_CrappyBody;
import crappy.collisions.AABBQuadTreeTools;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappyGame.A_Model;
import crappyGame.Controller.Controller;
import crappyGame.Controller.IAction;
import crappyGame.Controller.IController;
import crappyGame.GameObjects.Debris;
import crappyGame.GameObjects.Payload;
import crappyGame.GameObjects.Spaceship;
import crappyGame.GameObjects.StringObject;
import crappyGame.IModel;
import crappyGame.UI.Viewable;
import crappyGame.assets.SoundManager;
import crappyGame.misc.AttributeString;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class GameLevel extends A_Model implements IModel, Viewable, CrappyCallbackHandler {

    Spaceship ship;

    Payload pl;

    final static double TOWING_RANGE_SQUARED = Math.pow(1.5, 2);

    Optional<CrappyConnector> mayOrMayNotBeTheTowrope = Optional.empty();

    enum GAMESTATE{
        AWAITING_INPUT,
        GAMERING,
        DEAD,
        WON_LEVEL,
        GAME_OVER_YEAHHHHHHHH
    }

    GAMESTATE gamestate = GAMESTATE.AWAITING_INPUT;


    final int max_lives = 3;

    int lives = max_lives;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<BufferedImage> background = Optional.empty();

    final StringObject.AttributeStringObject<Integer> lifeCounterHUD = new StringObject.AttributeStringObject<>(
            new AttributeString<>(max_lives, "LIVES: ", ""),
            new Vect2D(VISIBLE_WORLD_HEIGHT/32, VISIBLE_WORLD_HEIGHT/32),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.RIGHT
    );

    double fuelUsed = 0.0;

    final StringObject.AttributeStringObject<Double> fuelUsedHUD = new StringObject.AttributeStringObject<Double>(
            new AttributeString<>(0.0, "FUEL USED: ", "", d -> String.format("%.2f",d)),
            new Vect2D(VISIBLE_WORLD_HEIGHT/32, VISIBLE_WORLD_HEIGHT*7/8),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.RIGHT
    );

    final StringObject pressTheAnyButtonWords = new StringObject(
            "PRESS THE ANY BUTTON TO START!",
            new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/2),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject pressTheAnyButtonToRespawnWords = new StringObject(
            "PRESS THE ANY BUTTON TO TRY AGAIN!",
            new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/2),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject congratsWords = new StringObject(
            "congartulation u did it",
            new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/2),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject youFailedWords = new StringObject(
            "THAT'S IT, GAME OVER",
            new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/2),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );


    final Vect2D cameraOffset = new Vect2D(-VISIBLE_WORLD_WIDTH/4.0, -VISIBLE_WORLD_HEIGHT/4.0);

    final Vect2D safeCameraOffset = new Vect2D(-VISIBLE_WORLD_WIDTH/3.0, -VISIBLE_WORLD_HEIGHT/3.0);

    final double lerpSpeed = 0.1;

    // TODO: if ship within middle third, keep camera as-is. Lerp camera when it gets to outer quarters/half


    // TODO: add debris when stuff gets destroyed
    final List<Debris> debris = new ArrayList<>();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    GameLevel(
            IController ctrl,
            Vect2D shipStartPos,
            Vect2D payloadStartPos,
            AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode staticGeom,
            Optional<BufferedImage> bg
    ){
        super(ctrl);

        ship = new Spaceship(shipStartPos, world);
        pl = new Payload(payloadStartPos, world);

        world.setStaticGeometry(staticGeom);
        background = bg;

    }

    @Override
    public void update() {

        if (!pendingBodiesToAdd.isEmpty()){
            for (Iterator<CrappyBody> citer = pendingBodiesToAdd.iterator(); citer.hasNext();){
                world.addBody(citer.next());
                citer.remove();
            }
        }

        final IAction act = controller.getAction();

        switch (gamestate){
            case AWAITING_INPUT:
                if (act.pressedAny()){
                    gamestate = GAMESTATE.GAMERING;
                }
                break;
            case GAMERING:
                if (ship.getState() == Spaceship.SHIP_STATE.GOING_IN &&
                        act.isSpacePressed() &&
                        !mayOrMayNotBeTheTowrope.isPresent() &&
                        Vect2DMath.DIST_SQUARED(ship.getPos(), pl.getPos()) <= TOWING_RANGE_SQUARED
                ){
                    createTowRope();
                }
                break;
            case DEAD:
                if (act.pressedAny()){
                    respawn();
                    gamestate = GAMESTATE.AWAITING_INPUT;
                }
                break;
        }

        ship.update(controller.getAction());

        world.update();

        for (Debris d: debris) {
            d.update(world.totalDelta);
        }

        switch (gamestate){

            case GAMERING:
                if (ship.getState() == Spaceship.SHIP_STATE.DEAD){
                    pl.setBeingTowed(false);
                    lostLife();
                } else if (pl.getState() == Payload.BALL_STATE.DED){
                    ship.setState(Spaceship.SHIP_STATE.FREEFALL_OF_SHAME);
                    lostLife();
                } else if (pl.getState() == Payload.BALL_STATE.SUCCESS){
                    // WIN!
                    won();
                } else if (act.isUpHeld() || act.isRightHeld() || act.isLeftHeld()){
                    fuelUsed += 0.1;
                    fuelUsedHUD.setData(fuelUsed);
                }
                break;


        }



    }

    private void lostLife(){

        lives -= 1;
        if (lives < 0){
            gamestate = GAMESTATE.GAME_OVER_YEAHHHHHHHH;
        } else {
            if (mayOrMayNotBeTheTowrope.isPresent()){
                world.removeConnector(mayOrMayNotBeTheTowrope.get());
                mayOrMayNotBeTheTowrope = Optional.empty();
            }
            lifeCounterHUD.setData(lives);
            gamestate = GAMESTATE.DEAD;
        }
    }

    private void respawn(){
        ship.respawn(world);
        pl.respawn(world);
        SoundManager.playPlac();
    }

    private void createTowRope(){
        final CrappyConnector rope = new CrappyConnector(
                ship.getBody(),
                Vect2D.ZERO,
                pl.getBody(),
                Vect2D.ZERO,
                1000000,
                1000,
                false,
                CrappyConnector.TRUNCATION_RULE_FACTORY(
                        CrappyConnector.TruncationEnum.COSINE_TRUNCATION,
                        1000000000
                ),
                false
        );
        mayOrMayNotBeTheTowrope = Optional.of(rope);
        world.addConnector(rope);

        ship.setState(Spaceship.SHIP_STATE.TOWING);
        pl.setBeingTowed(true);
    }

    private void won(){

        SoundManager.playScored();
        gamestate = GAMESTATE.WON_LEVEL;


    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void draw(Graphics2D g) {

        g.setColor(new Color(40, 43, 47));
        background.ifPresent(img -> g.setPaint(
                new TexturePaint(
                        img,
                        new Rectangle2D.Double(
                                viewportCorner.getX(),
                                viewportCorner.getY(),
                                img.getWidth(),
                                img.getHeight()
                        )
                )
        ));
        g.fillRect(0, 0, dims.width, dims.height);

        gt.updateViewport(viewportCorner);

        super.draw(g);

        switch (gamestate){
            case AWAITING_INPUT:
                pressTheAnyButtonWords.draw(g);
                break;
            case DEAD:
                pressTheAnyButtonToRespawnWords.draw(g);
                break;
            case WON_LEVEL:
                congratsWords.draw(g);
                break;
            case GAME_OVER_YEAHHHHHHHH:
                youFailedWords.draw(g);
                break;
        }

        lifeCounterHUD.draw(g);
        fuelUsedHUD.draw(g);

    }

    // TODO:
}
