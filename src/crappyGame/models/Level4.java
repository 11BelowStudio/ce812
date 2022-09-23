package crappyGame.models;

import crappyGame.Controller.IController;
import crappyGame.IGameRunner;

/**
 * Level 4 subclasses FlipGravityLevel,
 * because it has some special 'game complete'-related logic inside it
 */
public class Level4 extends FlipGravityLevel{

    public Level4(
            IController ctrl,
            int lives,
            double fuel,
            IGameRunner runner
    ) {
        super(
                ctrl,
                LevelGeometry.makeLevel4(VISIBLE_WORLD_WIDTH, VISIBLE_WORLD_HEIGHT),
                lives,
                fuel,
                runner
        );
        congratsWords.updateWords("Congratulations! You have survived the Space Towing Industry!");
        flippedGravity = false;
    }

    @Override
    void won() {
        super.won();
        System.out.println("CONGARTULATION!");
        System.out.println(fuelUsedHUD.getWords());
    }
}
