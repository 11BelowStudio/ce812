package crappyGame.models;

import crappy.CrappyBody;
import crappy.CrappyCallbackHandler;
import crappy.CrappyConnector;
import crappy.I_View_CrappyBody;
import crappy.collisions.AABBQuadTreeTools;
import crappy.math.M_Vect2D;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class GameLevel extends A_Model implements IModel, Viewable, CrappyCallbackHandler, IRecieveDebris {

    Spaceship ship;

    Payload pl;

    final static double TOWING_RANGE = 2;

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
                            fromPos.add(Vect2DMath.RANDOM_POLAR_VECTOR(0.1, 0.4)),
                            fromVel.add(Vect2DMath.RANDOM_POLAR_VECTOR(0.2, 0.5)),
                            fromRot,
                            fromRot.angle()
                    );
            debris.add(d);
            pendingBodiesToAdd.add(d.getBody());
        }
    }

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
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(31 * VISIBLE_WORLD_WIDTH/32.0, 30 * VISIBLE_WORLD_HEIGHT/32.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.RIGHT
    );

    double fuelUsed = 0.0;

    final StringObject.AttributeStringObject<Double> fuelUsedHUD = new StringObject.AttributeStringObject<Double>(
            new AttributeString<>(0.0, "FUEL USED: ", "", d -> String.format("%.2f",d)),
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(31 * VISIBLE_WORLD_WIDTH/32.0, 7 * VISIBLE_WORLD_HEIGHT/8.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.RIGHT
    );

    final StringObject pressTheAnyButtonWords = new StringObject(
            "PRESS THE ANY BUTTON TO START!",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.LEFT
    );

    final StringObject pressTheAnyButtonToRespawnWords = new StringObject(
            "PRESS THE ANY BUTTON TO TRY AGAIN!",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject congratsWords = new StringObject(
            "congartulation u did it",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject youFailedWords = new StringObject(
            "THAT'S IT, GAME OVER",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/2.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );


    final Vect2D cameraOffset = new Vect2D(-VISIBLE_WORLD_WIDTH/4.0, -VISIBLE_WORLD_HEIGHT/4.0);

    final Vect2D safeCameraOffset = new Vect2D(-VISIBLE_WORLD_WIDTH/3.0, -VISIBLE_WORLD_HEIGHT/3.0);

    final double lerpSpeed = 0.1;

    final Vect2D screenMid = new Vect2D(dims.getWidth()/2,  dims.getHeight()/2);

    final double dimsRadius = new Vect2D(VISIBLE_WORLD_WIDTH/3.0, VISIBLE_WORLD_HEIGHT/3.0).mag();

    final double squaredDimsRadius = Math.pow(dimsRadius, 2);

    // TODO: if ship within middle third, keep camera as-is. Lerp camera when it gets to outer quarters/half


    // TODO: add debris when stuff gets destroyed
    final List<Debris> debris = new ArrayList<>();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    GameLevel(
            IController ctrl,
            Vect2D shipStartPos,
            Vect2D payloadStartPos,
            Optional<BufferedImage> bg
    ){
        super(ctrl);

        ship = new Spaceship(shipStartPos, world, this);
        pl = new Payload(payloadStartPos, world, this);

        background = bg;

    }

    @Override
    public void update() {

        if (!pendingBodiesToAdd.isEmpty()){
            for (final Iterator<CrappyBody> citer = pendingBodiesToAdd.iterator(); citer.hasNext();){
                world.addBody(citer.next());
                citer.remove();
            }
        }

        final IAction act = controller.getAction();

        switch (gamestate){
            case AWAITING_INPUT:
                if (act.pressedAny()){
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
                respawn();
                gamestate = GAMESTATE.AWAITING_INPUT;
            }
        }

        world.update();

        for (Iterator<Debris> diter = debris.iterator(); diter.hasNext();) {
            Debris d = diter.next();
            d.update(world.totalDelta);
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
                } else if (act.isUpHeld() || act.isRightHeld() || act.isLeftHeld()){
                    fuelUsed += 0.1;
                    fuelUsedHUD.setData(fuelUsed);
                }
                break;


        }

        if (ship.isStillAlive()){


            Vect2D worldMid = gt.TO_WORLD_COORDS_V(new Vect2D(dims.getWidth()/2.0, dims.getHeight()/2.0));

            Vect2D shipScreenPos = gt.TO_SCREEN_COORDS_V(ship.getPos());

            Vect2D midToShip = Vect2DMath.VECTOR_BETWEEN(screenMid, shipScreenPos);

            if (midToShip.magSquared() > squaredDimsRadius){
                System.out.println("midToShip = " + midToShip);
                double currentDist = midToShip.mag();

                double scaleMidToShipBy = dimsRadius/currentDist;
                //Vect2D finalPos = midToShip.mult(scaleMidToShipBy);

                double nextDist = (1-lerpSpeed) * currentDist + (lerpSpeed * dimsRadius);
                //viewportCorner = viewportCorner.add(midToShip.divide(currentDist/nextDist));

                //viewportCorner = M_Vect2D.GET(viewportCorner).mult(1-lerpSpeed).addScaled(finalPos, lerpSpeed).finished();
            }


        }

        controller.timestepEndReset();

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
        } else {
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
                Vect2DMath.DIST(ship.getPos(), pl.getPos()),
                5000,
                100,
                false,
                CrappyConnector.TRUNCATION_RULE_FACTORY(
                        CrappyConnector.TruncationEnum.COSINE_TRUNCATION,
                        100
                ),
                false
        );
        mayOrMayNotBeTheTowrope = Optional.of(rope);
        world.addConnector(rope);

        ship.setState(Spaceship.SHIP_STATE.TOWING);
        pl.setBeingTowed(true);
        SoundManager.playTowNoise();
    }

    private void won(){

        SoundManager.playScored();
        gamestate = GAMESTATE.WON_LEVEL;
        mayOrMayNotBeTheTowrope.ifPresent(t->t.setAllowedToExist(false));

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

        AffineTransform at = g.getTransform();
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

    // TODO:
}
