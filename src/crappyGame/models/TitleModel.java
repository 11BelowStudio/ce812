package crappyGame.models;

import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappyGame.A_Model;
import crappyGame.Controller.IController;
import crappyGame.GameObjects.StringObject;
import crappyGame.IGameRunner;
import crappyGame.IModel;
import crappyGame.assets.ImageManager;
import crappyGame.assets.SoundManager;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TitleModel extends A_Model {

    boolean done = false;

    final StringObject backupA = new StringObject(
            "A Scientific Interpretation of daily life in",
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static final Optional<BufferedImage> bg = ImageManager.TITLE.getOptional();

    public TitleModel(IController ctrl, IGameRunner runner) {
        super(ctrl, runner);
        done = false;
    }

    @Override
    public void update() {
        if (controller.getAction().pressedAny()){
            done = true;
            SoundManager.playClap();
            runner.levelWon(0, 3);
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void draw(Graphics2D g){

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
                                    0,
                                    0,
                                    bimg.getWidth(),
                                    bimg.getHeight()
                            )
                    )
            );
            g.fillRect(0, 0, dims.width, dims.height);
        } else {
            g.fillRect(0, 0,  dims.width, dims.height);

            backupA.draw(g);
            backupB.draw(g);
            backupC.draw(g);
            backupD.draw(g);
            backupE.draw(g);
        }
    }
}
