package crappyGame.models;

import crappy.collisions.AABBQuadTreeTools;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.utils.containers.IQuadruplet;
import crappyGame.Controller.IController;
import crappyGame.GameObjects.StringObject;
import crappyGame.IGameRunner;
import crappyGame.assets.SoundManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class FlipGravityLevel extends GameLevel {

    boolean flippedGravity = false;

    final StringObject gravityFlippedWords = new StringObject(
            "GRAVITY HAS BEEN FLIPPED!",
            gt.TO_RAW_SCREEN_SCALE_M(new Vect2D(VISIBLE_WORLD_WIDTH/2.0, VISIBLE_WORLD_HEIGHT/3.0)).finished(),
            Rot2D.IDENTITY,
            StringObject.ALIGNMENT_ENUM.MIDDLE
    );

    final static double gravityFlippedWordsVisibleSeconds = 1.5;

    double gravityFlippedWordsTimer = 0;

    public FlipGravityLevel(
            IController ctrl,
            BiFunction<Double, Double, IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>>> levelGeomMaker,
            final int myLives,
            final double myUsedFuel,
            final IGameRunner runner
    ){
        super(
                ctrl,
                levelGeomMaker,
                myLives,
                myUsedFuel,
                runner
        );
        flippedGravity = false;
    }

    FlipGravityLevel(
            final IController ctrl,
            final IQuadruplet<Vect2D, Vect2D, AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode, Optional<BufferedImage>> levelGeom,
            final int myLives,
            final double myUsedFuel,
            final IGameRunner runner
    ){
        super(
                ctrl,
                levelGeom,
                myLives,
                myUsedFuel,
                runner
        );
        flippedGravity = false;
        gravityFlippedWordsTimer = 0;
    }

    @Override
    Vect2D getCurrentGravity(){
        if (flippedGravity){
            return super.INVERTED_GRAVITY;
        }
        return super.getCurrentGravity();
    }

    @Override
    void createTowRope() {
        super.createTowRope();
        flippedGravity = true;
        gravityFlippedWordsTimer = gravityFlippedWordsVisibleSeconds;
        SoundManager.playOminousDrum();
    }

    @Override
    boolean respawn(){
        boolean res = super.respawn();
        if (res){
            flippedGravity = false;
        }
        return res;
    }



    @Override
    public void draw(Graphics2D g){
        super.draw(g);
        if (gravityFlippedWordsTimer > 0){
            gravityFlippedWords.draw(g);
        }
    }

    @Override
    public void update(double delta){
        super.update(delta);
        if (gravityFlippedWordsTimer > 0){
            gravityFlippedWordsTimer -= delta;
        }
    }


}
