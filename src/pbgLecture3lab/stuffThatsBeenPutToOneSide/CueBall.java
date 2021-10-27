package pbgLecture3lab.stuffThatsBeenPutToOneSide;

import java.awt.*;

/**
 * The cue ball for the snooker game.
 */
public class CueBall extends SnookerBall{


    public CueBall(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col, double mass) {
        super(sx, sy, vx, vy, radius, col, mass, -1);
    }
}
