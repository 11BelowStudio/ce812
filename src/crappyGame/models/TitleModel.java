package crappyGame.models;

import crappy.CrappyConnector;
import crappy.math.M_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappyGame.A_Model;
import crappyGame.Controller.IController;
import crappyGame.Controller.PressedAnyAct;
import crappyGame.Controller.RandomAction;
import crappyGame.GameObjects.Payload;
import crappyGame.GameObjects.Spaceship;
import crappyGame.GameObjects.StringObject;
import crappyGame.IGameRunner;
import crappyGame.assets.ImageManager;
import crappyGame.assets.SoundManager;
import crappyGame.misc.HighScoreHandler;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class TitleModel extends A_Model implements IRecieveDebris {

    boolean done = false;

    Spaceship ship;

    Payload pl;

    Optional<CrappyConnector> mayOrMayNotBeTheTowrope = Optional.empty();

    final StringObject backupA = new StringObject(
            "A Scientific Interpretation of Daily Life in",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/3)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );
    final StringObject backupB = new StringObject(
            "the Space Towing Industry",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/2)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );
    final StringObject backupC = new StringObject(
            "(Circa 3052 CE)",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT*2/3)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject backupD = new StringObject(
            "PRESS THE 'ANY' BUTTON TO START!",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT*3/4)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject backupE = new StringObject(
            "press 'escape' to quit",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT*7/8)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final StringObject byLine = new StringObject(
            "by 11BelowStudio, 2022 (v1.1.0)",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT*15/16)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    StringObject bestScore;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static final Optional<BufferedImage> bg = ImageManager.TITLE.getOptional();

    private RandomAction randomAct;

    public TitleModel(IController ctrl, IGameRunner runner) {
        super(ctrl, runner);


        HighScoreHandler.ScoreRecord highScore = runner.getHighScore();

        bestScore = new StringObject(
                "High score (least fuel used): " + String.format("%.2f",highScore.getScore()),
                gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2, VISIBLE_WORLD_HEIGHT/16)).finished(),
                Rot2D.IDENTITY,
                StringObject.ALIGNMENT_ENUM.MIDDLE
        );

        done = false;
        SoundManager.toggleSFX(false);
        SoundManager.playBackgroundMusic(SoundManager.MUSIC_THEMES.CONVERSATIONAL_INTERLUDE);

        ship = new Spaceship(new Vect2D((VISIBLE_WORLD_WIDTH/2) + 0.2, VISIBLE_WORLD_HEIGHT/2), world, this, 0.5);
        pl = new Payload(new Vect2D((VISIBLE_WORLD_WIDTH/2)-0.1, (VISIBLE_WORLD_HEIGHT/2)-1), world, this);

        randomAct = new RandomAction(ship);

        ship.update(new PressedAnyAct());

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
        mayOrMayNotBeTheTowrope = Optional.ofNullable(rope);
        world.addConnector(rope);

        ship.setState(Spaceship.SHIP_STATE.TOWING);
        pl.setBeingTowed(true);

    }

    private void resetShipPayload(){
        if (ship.respawn(world)){
            pl.respawn(world);
            mayOrMayNotBeTheTowrope = Optional.empty();
        }
        if (!mayOrMayNotBeTheTowrope.isPresent()){

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
            mayOrMayNotBeTheTowrope = Optional.ofNullable(rope);
            world.addConnector(rope);

            ship.setState(Spaceship.SHIP_STATE.TOWING);
            pl.setBeingTowed(true);
        }


    }

    @Override
    public void update(double deltaTime) {


        if (controller.getAction().pressedAny()){

            SoundManager.togglePlayThrusters(false);

            SoundManager.toggleSFX(true);
            done = true;
            SoundManager.playClap();
            runner.levelWon(0, 3);
            return;
        }

        //randomAct.updateMe();

        ship.update(randomAct.updateMe(deltaTime));

        world.update(deltaTime);

        if (!ship.isStillAlive() || !pl.isAlive()){
            if (mayOrMayNotBeTheTowrope.isPresent()) {
                world.removeConnector(mayOrMayNotBeTheTowrope.get());
                mayOrMayNotBeTheTowrope = Optional.empty();
            }
            resetShipPayload();
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
            return Vect2D.ZERO;
        }
    }


    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void draw(Graphics2D g){

        gt.updateViewport(viewportCorner);


        final Vect2D bgVP = gt.TO_SCREEN_COORDS_V(gt.TO_WORLD_COORDS_M(Vect2D.ZERO).sub(viewportCorner).finished());

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY
        );

        g.setColor(spaceColour);
        if (bg.isPresent()){
            final BufferedImage bimg = bg.get();

            g.setPaint(
                    new TexturePaint(
                            bimg,
                            new Rectangle2D.Double(
                                    bgVP.getX(),//0,
                                    bgVP.getY(),//0,
                                    bimg.getWidth(),
                                    bimg.getHeight()
                            )
                    )
            );

            g.fillRect(0, 0, dims.width, dims.height);


            final AffineTransform at = g.getTransform();
            super.draw(g);
            g.setTransform(at);
        } else {
            g.fillRect(0, 0,  dims.width, dims.height);

            final AffineTransform at2 = g.getTransform();
            super.draw(g);
            g.setTransform(at2);

            backupA.draw(g);
            backupB.draw(g);
            backupC.draw(g);
            backupD.draw(g);
            backupE.draw(g);
        }

        //bestScore.draw(g);
        //byLine.draw(g);
    }

    @Override
    public void addDebris(Vect2D fromPos, Rot2D fromRot, Vect2D fromVel, int debrisToAdd, DebrisSource source) {

    }
}
