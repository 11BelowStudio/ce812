package crappyGame.models;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyConnector;
import crappy.collisions.AABBQuadTreeTools;
import crappy.math.*;
import crappy.utils.containers.IQuadruplet;
import crappyGame.A_Model;
import crappyGame.Controller.IAction;
import crappyGame.Controller.IController;
import crappyGame.GameObjects.Debris;
import crappyGame.GameObjects.Payload;
import crappyGame.GameObjects.Spaceship;
import crappyGame.GameObjects.StringObject;
import crappyGame.IGameRunner;
import crappyGame.IModel;
import crappyGame.UI.Viewable;
import crappyGame.assets.SoundManager;
import crappyGame.misc.AttributeString;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A class which works as a level in the game.
 */
public class GameLevel extends A_Model implements IModel, Viewable, CrappyCallbackHandler, IRecieveDebris {

    Spaceship ship;

    Payload pl;

    final static double TOWING_RANGE = 1.5;



    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<CrappyConnector> mayOrMayNotBeTheTowrope = Optional.empty();

    public enum GAMESTATE{
        AWAITING_INPUT,
        GAMERING,
        DEAD,
        WON_LEVEL,
        GAME_OVER_YEAHHHHHHHH
    }

    GAMESTATE gamestate = GAMESTATE.AWAITING_INPUT;


    final int max_lives = 5;

    int lives = max_lives;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<BufferedImage> background = Optional.empty();

    final StringObject.AttributeStringObject<Integer> lifeCounterHUD = new StringObject.AttributeStringObject<>(
            new AttributeString<>(max_lives, "LIVES: ", ""),
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(15 * VISIBLE_WORLD_WIDTH/16.0, 30 * VISIBLE_WORLD_HEIGHT/32.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.RIGHT
    );

    double fuelUsed = 0.0;

    final StringObject.AttributeStringObject<Double> fuelUsedHUD = new StringObject.AttributeStringObject<>(
            new AttributeString<>(0.0, "FUEL USED: ", "", d -> String.format("%.2f",d)),
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(15 * VISIBLE_WORLD_WIDTH/16, 7 * VISIBLE_WORLD_HEIGHT/8.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.RIGHT
    );

    final StringObject pressTheAnyButtonWords = new StringObject(
            "PRESS A DIRECTION TO START!",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject pressTheAnyButtonToRespawnWords = new StringObject(
            "PRESS THE ANY BUTTON TO TRY AGAIN!",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject congratsWords = new StringObject(
            "congartulation u did it (press space to continue)",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject youFailedWords = new StringObject(
            "THAT'S IT, GAME OVER (press space to go back to the title screen IN SHAME)",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );





    final List<Debris> debris = new ArrayList<>();



    /**
     * Creates this level.
     * @param ctrl the controller
     * @param levelGeomMaker the bifunction responsible for giving us the ship/payload positions, static world geometry, and background image.
     * @param myLives lives that the player still has
     * @param myUsedFuel how much fuel the player has used so far.
     */
    public GameLevel(
            IController ctrl,
            BiFunction<Double, Double, IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>>> levelGeomMaker,
            final int myLives,
            final double myUsedFuel,
            final IGameRunner runner
    ){
        this(
                ctrl,
                levelGeomMaker.apply(VISIBLE_WORLD_WIDTH, VISIBLE_WORLD_HEIGHT),
                myLives,
                myUsedFuel,
                runner
        );
    }

    GameLevel(
            final IController ctrl,
            final IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>> levelGeom,
            final int myLives,
            final double myUsedFuel,
            final IGameRunner runner
    ){
        super(ctrl, runner);

        ship = new Spaceship(levelGeom.getFirst(), world, this, 1);
        pl = new Payload(levelGeom.getSecond(), world, this);

        world.setStaticGeometry(levelGeom.getThird());

        background = levelGeom.getFourth();
        lives = myLives;
        fuelUsed = myUsedFuel;
        lifeCounterHUD.setData(lives);
        fuelUsedHUD.setData(fuelUsed);
    }

    Vect2D getCurrentGravity(){
        return super.GAME_GRAVITY;
    }


    @Override
    public void update(double deltaTime) {


        if (!pendingBodiesToAdd.isEmpty()){
            for (final Iterator<CrappyBody> citer = pendingBodiesToAdd.iterator(); citer.hasNext();){
                world.addBody(citer.next());
                citer.remove();
            }
        }

        final IAction act = controller.getAction();

        switch (gamestate){
            case AWAITING_INPUT:
                if (act.anyDirectionPressed()){
                    SoundManager.playClap();
                    gamestate = GAMESTATE.GAMERING;
                }
                break;
            case GAMERING:
                if (ship.getState() == Spaceship.SHIP_STATE.GOING_IN &&
                        act.isSpacePressed() &&
                        !mayOrMayNotBeTheTowrope.isPresent() &&
                        Vect2DMath.DIST(ship.getPos(), pl.getPos()) < TOWING_RANGE
                ){
                    createTowRope();
                }
                break;
        }

        ship.update(controller.getAction());

        if (gamestate==GAMESTATE.DEAD){
            if (act.pressedAny()){
                if (respawn()){
                    gamestate = GAMESTATE.AWAITING_INPUT;
                } else {
                    ship.destroy();
                }
            }
        }

        //world.update();
        world.update(deltaTime, getCurrentGravity());

        for (final Iterator<Debris> diter = debris.iterator(); diter.hasNext();) {
            final Debris d = diter.next();
            d.update(deltaTime);
            if (!d.isStillExists()){
                diter.remove();
            }
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
                } else if (act.isUpHeld()){
                    fuelUsed += deltaTime;
                    fuelUsedHUD.setData(fuelUsed);
                }
                break;
            case WON_LEVEL:
                if (act.isSpacePressed()){
                    runner.levelWon(fuelUsed, lives);
                }
                break;
            case GAME_OVER_YEAHHHHHHHH:
                if (act.isSpacePressed()){
                    runner.levelLost();
                }


        }


        Vect2D midToLerpPos = M_Vect2D.GET(viewportCorner)
                .add(halfVisibleWorld)
                .mult(-1)
                .add(lerpTarget()) // .add(ship.getPos())
                .finished();



        if (midToLerpPos.isFinite() && midToLerpPos.magSquared() > safeCamDistSquared){
            viewportCorner =
                    Vect2DMath.VECTOR_BETWEEN_M(
                            midToLerpPos,
                            midToLerpPos.divide(
                                    Vect2DMath.RETURN_X_IF_NOT_FINITE_OR_IF_ZERO(safeCamDist/midToLerpPos.mag(), 1)
                            )
                    ).mult(lerpSpeed)
                    .add(viewportCorner)
                    .finished();
        }

        if (!viewportCorner.isFinite()){
            viewportCorner = ship.getStartPos().addScaled(halfVisibleWorld, -1);
        }

        controller.timestepEndReset();

    }

    private Vect2D lerpTarget(){

        if (ship.isStillAlive()){
            return ship.getPos_safe();
        } else if (pl.canBeLerpedTo()){
            return pl.getLerpPos();
        } else{
            return ship.getDedPos();
        }
    }

    private void lostLife(){

        lives -= 1;
        if (mayOrMayNotBeTheTowrope.isPresent()){
            world.removeConnector(mayOrMayNotBeTheTowrope.get());
            mayOrMayNotBeTheTowrope = Optional.empty();
            SoundManager.playTowBroke();
        }
        if (lives < 0){
            gamestate = GAMESTATE.GAME_OVER_YEAHHHHHHHH;
            SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.GAME_OVER);
        } else {
            lifeCounterHUD.setData(lives);
            gamestate = GAMESTATE.DEAD;
        }
    }

    boolean respawn(){
        if (ship.respawn(world)) {
            pl.respawn(world);
            SoundManager.playPlac();
            return true;
        }
        return false;
    }

    void createTowRope(){
        final CrappyConnector rope = new CrappyConnector(
                ship.getBody(),
                Spaceship.connectorPos,
                pl.getBody(),
                Vect2D.ZERO,
                50000,
                100,
                false,
                CrappyConnector.TRUNCATION_RULE_FACTORY(
                        CrappyConnector.TruncationEnum.PARTIAL_TANH,
                        150,
                        0.75
                ),
                false
        );
        mayOrMayNotBeTheTowrope = Optional.of(rope);
        world.addConnector(rope);

        ship.setState(Spaceship.SHIP_STATE.TOWING);
        pl.setBeingTowed(true);
        SoundManager.playTowNoise();
    }

    void won(){

        SoundManager.playScored();
        gamestate = GAMESTATE.WON_LEVEL;
        mayOrMayNotBeTheTowrope.ifPresent(t->t.setAllowedToExist(false));
        ship.setState(Spaceship.SHIP_STATE.FREEFALL_OF_SHAME);
        controller.resetAll();
    }


    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void draw(Graphics2D g) {

        g.setColor(spaceColour);


        gt.updateViewport(viewportCorner);


        final Vect2D bgVP = gt.TO_SCREEN_COORDS_V(gt.TO_WORLD_COORDS_M(Vect2D.ZERO).sub(viewportCorner).finished());


        background.ifPresent(img -> g.setPaint(
                new TexturePaint(
                        img,
                        new Rectangle2D.Double(
                                bgVP.getX(),
                                bgVP.getY(),
                                img.getWidth(),
                                img.getHeight()
                        )
                )
        ));
        g.fillRect(0, 0, dims.width, dims.height);


        final AffineTransform at = g.getTransform();
        super.draw(g);
        g.setTransform(at);

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

    @Override
    public void addDebris(final Vect2D fromPos, final Rot2D fromRot, final Vect2D fromVel, int debrisToAdd, final DebrisSource source) {
        //double angleOffset = 360.0/(double)debrisToAdd;
        //double fromDeg = Math.toRadians(fromRot.angle());
        switch (source){
            case SHIP:
                if (gamestate==GAMESTATE.WON_LEVEL){
                    SoundManager.playSolidHit();
                } else {
                    SoundManager.playBoom();
                }
                break;
            case PAYLOAD:
                SoundManager.playSolidHit();
                break;
            default:
                SoundManager.playClap();
                break;
        }
        for (int i = 0; i < debrisToAdd; i++) {
            Debris d = new Debris(
                    fromPos.add(Vect2DMath.RANDOM_POLAR_VECTOR(-0.1, 0.1)),
                    fromVel.add(Vect2DMath.RANDOM_POLAR_VECTOR(0.2, 0.5)),
                    fromRot.rotateBy(I_Rot2D.RANDOM_RADIANS_ANGLE()),
                    I_Rot2D.RANDOM_RADIANS_ANGLE() * 2
            );
            debris.add(d);
            pendingBodiesToAdd.add(d.getBody());
        }
    }

}
