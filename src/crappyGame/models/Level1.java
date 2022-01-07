package crappyGame.models;

import crappy.collisions.AABBQuadTreeTools;
import crappy.math.Vect2D;
import crappyGame.Controller.IController;
import crappyGame.GameObjects.LevelGeometry;
import crappyGame.assets.ImageManager;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class Level1 extends GameLevel{


    public Level1(IController ctrl) {
        super(
                ctrl,
                new Vect2D(VISIBLE_WORLD_WIDTH/6,
                        VISIBLE_WORLD_HEIGHT*7/8),
                new Vect2D(VISIBLE_WORLD_WIDTH/4, VISIBLE_WORLD_HEIGHT*3/16),
                ImageManager.BG1.getOptional()
        );
        LevelGeometry.makeLevel1(super.world, VISIBLE_WORLD_WIDTH, VISIBLE_WORLD_HEIGHT);
    }
}
