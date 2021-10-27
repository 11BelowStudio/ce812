package pbgLecture3lab.stuffThatsBeenPutToOneSide;


import pbgLecture3lab.Vect2D;

/**
 * A class that represents a hole for a snooker game.
 * Nothing fancy about it, just a hole.
 */
public class SnookerHole {

    private Vect2D pos;

    private float radius;


    /**
     * Constructs a new SnookerHole at specified position with given radius and belonging to specified game
     * @param position position
     * @param radius where it is
     */
    public SnookerHole(
            final Vect2D position,
            final float radius
    ){
        pos = position;
        this.radius = radius;
    }

    /**
     * Called to see if the ball is/was potted (in contact with the hole)
     * @param theBall the ball that got potted
     * @return true if it was potted, false otherwise
     */
    public boolean wasBallPotted(SnookerBall theBall){
        return  (Vect2D.minus(theBall.getPos(), pos).mag() <= radius + theBall.getRadius());
    }


}
