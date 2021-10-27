package pbgLecture3lab.stuffThatsBeenPutToOneSide;

import pbgLecture3lab.*;

import java.awt.*;
import java.util.List;

/**
 * A class responsible for the whole snooker game stuff
 * Adapted from the BasicPhysicsEngine class.
 *
 *
 */
public class SnookerEngine {

    public static final int SCREEN_HEIGHT = 800;
    public static final int SCREEN_WIDTH = 450;
    public static final Dimension FRAME_SIZE = new Dimension(
            SCREEN_WIDTH, SCREEN_HEIGHT);
    public static final double WORLD_WIDTH=10;//metres
    public static final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
    public static final double GRAVITY = 0;


    // sleep time between two drawn frames in milliseconds
    public static final int DELAY = 20;
    public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
    // estimate for time between two frames in seconds
    public static final double DELTA_T = DELAY / 1000.0 / NUM_EULER_UPDATES_PER_SCREEN_REFRESH ;

    public static int convertWorldXtoScreenX(double worldX) {
        return (int) (worldX/WORLD_WIDTH*SCREEN_WIDTH);
    }
    public static int convertWorldYtoScreenY(double worldY) {
        // minus sign in here is because screen coordinates are upside down.
        return (int) (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT));
    }
    public static int convertWorldLengthToScreenLength(double worldLength) {
        return (int) (worldLength/WORLD_WIDTH*SCREEN_WIDTH);
    }
    public static double convertScreenXtoWorldX(int screenX) {

        return ((double)screenX)/SCREEN_WIDTH * WORLD_WIDTH;
        // to get this to work you need to program the inverse function to convertWorldXtoScreenX
        // this means rearranging the equation z=(worldX/WORLD_WIDTH*SCREEN_WIDTH) to make worldX the subject,
        // and then returning worldX
    }
    public static double convertScreenYtoWorldY(int screenY) {

        return (((double)SCREEN_HEIGHT - screenY)/(double)SCREEN_HEIGHT) * WORLD_HEIGHT;
        // to get this to work you need to program the inverse function to convertWorldYtoScreenY
        // this means rearranging the equation z= (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT)) to make
        // worldY the subject, and then returning worldY
    }


    private CueBall theCueBall;

    private List<SnookerBall> theBalls;

    private List<SnookerBall> drawaBalls; // drawable snooker balls

    private List<SnookerHole> theHoles;

    private List<AnchoredBarrier> theBarriers;



    private void createCushion(List<AnchoredBarrier> barriers, double centrex, double centrey, double orientation, double cushionLength, double cushionDepth) {
        // on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
        Color col=Color.WHITE;
        Vect2D p1=new Vect2D(cushionDepth/2, -cushionLength/2-cushionDepth/2);
        Vect2D p2=new Vect2D(-cushionDepth/2, -cushionLength/2);
        Vect2D p3=new Vect2D(-cushionDepth/2, +cushionLength/2);
        Vect2D p4=new Vect2D(cushionDepth/2, cushionLength/2+cushionDepth/2);
        p1=p1.rotate(orientation);
        p2=p2.rotate(orientation);
        p3=p3.rotate(orientation);
        p4=p4.rotate(orientation);
        // we are being careful here to list edges in an anticlockwise manner, so that normals point inwards!
        barriers.add(new AnchoredBarrier_StraightLine(centrex+p1.x, centrey+p1.y, centrex+p2.x, centrey+p2.y, col));
        barriers.add(new AnchoredBarrier_StraightLine(centrex+p2.x, centrey+p2.y, centrex+p3.x, centrey+p3.y, col));
        barriers.add(new AnchoredBarrier_StraightLine(centrex+p3.x, centrey+p3.y, centrex+p4.x, centrey+p4.y, col));

        barriers.add(new AnchoredBarrier_Point(centrex+p1.x, centrey+p1.y));
        barriers.add(new AnchoredBarrier_Point(centrex+p2.x, centrey+p2.y));
        barriers.add(new AnchoredBarrier_Point(centrex+p3.x, centrey+p3.y));
        barriers.add(new AnchoredBarrier_Point(centrex+p4.x, centrey+p4.y));
        // oops this will have concave corners so will need to fix that some time!

    }


    public void update() {

        for (SnookerBall s : theBalls) {
            s.update(DELTA_T); // tell each particle to move
        }


        for (SnookerBall ball : theBalls) {
            for (AnchoredBarrier b : theBarriers) {
                if (b.isCircleCollidingBarrier(ball.getPos(), ball.getRadius())) {
                    Vect2D bouncedVel=b.calculateVelocityAfterACollision(ball.getPos(), ball.getVel(),0.9);
                    ball.setVel(bouncedVel);
                }
            }
            for (SnookerHole s: theHoles){
                if (s.wasBallPotted(ball)){
                    ball.setPotted(true);
                }
            }
        }
        double e=0.9; // coefficient of restitution for all particle pairs
        for (int n=0;n<theBalls.size();n++) {
            SnookerBall p1 = theBalls.get(n);

            for (int m=0;m<n;m++) {// avoids double check by requiring m<n
                SnookerBall p2 = theBalls.get(m);
                if (p1.collidesWith(p2)) {
                    CollidaBall.implementElasticCollision(p1, p2, e);
                }
            }
        }
    }
}
