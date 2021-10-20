package pbgLecture2lab;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Flipper extends AnchoredBarrier{

    /**
     * Where the pivot of this flipper is
     */
    private final Vect2D pivot;

    /**
     * The 'spine' of this flipper in the default rotation (when not being flipped)
     */
    private final Vect2D initial_spine;

    /**
     * Where the end point of this flipper is (by default)
     */
    private final Vect2D end_point;

    /**
     * The 'spine' of this flipper in the current rotation (from pivot point to the middle of the flipper end)
     */
    private Vect2D spine;

    /**
     * radius of the pivot circle
     */
    private final double start_radius;

    /**
     * radius of the end circle
     */
    private final double end_radius;

    /**
     * whether this barrier rotates clockwise or not
     */
    private final boolean rotates_clockwise;

    /**
     * turns a maximum of 30 degrees
     */
    private static final double MAX_ANGLE = 30.0;

    /**
     * rotates at 60 degrees per second
     */
    private static final double ROTATION_SPEED = 60.0;

    /**
     * The current rotation of the flipper (compared to the default spine)
     */
    private double current_angle = 0.0;

    /**
     * All of the barriers that are part of this flipper.
     */
    private final List<AnchoredBarrier> components;

    /**
     * The stationary circle that's being used for the
     */
    private final AnchoredBarrier_Curve pivot_curve;

    /**
     * Default end circle for when the barrier's in the default position
     */
    private final AnchoredBarrier_Curve default_end_curve;

    /**
     * The upper barrier that is used when the barrier is in its default position
     */
    private final AnchoredBarrier_StraightLine default_upper_barrier;

    /**
     * The upper barrier that's being used for this particular frame.
     */
    private AnchoredBarrier_StraightLine upperBarrier;

    /**
     * The lower barrier that is used when the barrier is in its default position
     */
    private final AnchoredBarrier_StraightLine default_lower_barrier;

    /**
     * The lower barrier that's being used for this particular frame
     */
    private AnchoredBarrier_StraightLine lowerBarrier;

    /**
     * The current end curve barrier.
     */
    private AnchoredBarrier_Curve end_curve;

    /**
     * Which barrier the ball collided with
     */
    private AnchoredBarrier collidedWith;

    /**
     * Current state of the flipper.
     */
    private FlipperEnum flipperState;

    public Flipper(final Vect2D start, final Vect2D end, final double startRadius, final double endRadius, final boolean clockwise){

        flipperState = FlipperEnum.READY;

        start_radius = startRadius;
        end_radius = endRadius;

        pivot = start;
        System.out.println(pivot);
        end_point = end;
        System.out.println(end_point);
        spine = start.mult(-1).add(end);
        initial_spine = spine;

        System.out.println(spine);

        components = new ArrayList<>();

        rotates_clockwise = clockwise;

        pivot_curve = new AnchoredBarrier_Curve(
                pivot,
                start_radius,
                0,
                360,
                false,
                start_radius,
                Color.RED
        );

        default_end_curve = new AnchoredBarrier_Curve(
                //end,
                pivot.add(spine),
                end_radius,
                0,
                360,
                false,
                end_radius,
                Color.RED
        );

        end_curve = default_end_curve;

        final Vect2D sp90 = spine.normalise().rotate90degreesAnticlockwise();

        // This barrier is at the top if we're going clockwise.
        AnchoredBarrier_StraightLine clockwise_upper_barrier = new AnchoredBarrier_StraightLine(
                pivot.addScaled(sp90, start_radius),
                pivot.add(spine).addScaled(sp90, end_radius),
                Color.GREEN,
                end_radius
        );

        // This barrier is at the top if we're going anticlockwise
        AnchoredBarrier_StraightLine clockwise_lower_barrier = new AnchoredBarrier_StraightLine(
                pivot.add(spine).addScaled(sp90, -end_radius),
                pivot.addScaled(sp90, -start_radius),
                Color.BLUE,
                end_radius
        );

        if (clockwise){
            default_upper_barrier = clockwise_upper_barrier;
            default_lower_barrier = clockwise_lower_barrier;
        } else{
            default_upper_barrier = clockwise_lower_barrier;
            default_lower_barrier = clockwise_upper_barrier;
        }


        upperBarrier = default_upper_barrier;

        lowerBarrier = default_lower_barrier;

        collidedWith = pivot_curve;

        resetFlipper();

    }

    @Override
    public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e) {



        switch (flipperState){
            case READY:
            case FLIPPED_FULLY_OUT:
                // in these cases, the flipper has stopped moving, so there's no relative velocity to factor in
                return pivot_curve.calculateVelocityAfterACollision(pos, vel, 1);
            case FLIP_IN:
            case FLIP_OUT:
                // in these cases, the flipper is moving. This is the tricky one.


                if (collidedWith == pivot_curve){
                    // if we hit the pivot curve (stationary), there's no relative velocity to factor in
                    return pivot_curve.calculateVelocityAfterACollision(pos, vel, 1);
                }

                // radians per second * distance to contact point

                //
                //        b              b
                //        | \            | \_____
                //        |  \           |        \_____
                // end ---x-- pivot      x  -- end ---- pivot
                //
                // find vector + angle between pivot and ball
                // +-90 degree angle between ball and projected spine
                // find base of that triangle
                // add rotational velocity to it

                // a to b = -a + b
                Vect2D fromPivotToBall = Vect2D.minus(pos, pivot);
                System.out.println(fromPivotToBall);

                // angle  end-pivot-b
                double spineToBallAngle = fromPivotToBall.angle(spine);
                System.out.println(spineToBallAngle);

                // line pivot-b is known

                // TODO: height of triangle
                // TODO: distance of the ball along the spine base
                // TODO: distance * angle to get velocity of that point of the flipper
                // TODO: use that to work out relative velocity of ball
                // TODO: use relative velocity of the ball to affect the ball's velocity.
        }

        /**
         * https://research.ncl.ac.uk/game/mastersdegree/gametechnologies/previousinformation/physics6collisionresponse/2017%20Tutorial%206%20-%20Collision%20Response.pdf
         */

        return collidedWith.calculateVelocityAfterACollision(pos, vel, 1.1);
    }

    /**
     * Computes and returns the normal of the current spine
     * @return the normal of the current spine
     */
    private Vect2D getSpineNormal(){
        return spine.normalise().rotate90degreesAnticlockwise();
    }

    /**
     * We check if the given circle is colliding with this object.
     * It does this by checking if it collides with (in order)
     * the upper barrier, the lower barrier, the end curve, and the pivot curve.
     * If it collides with any of these, 'collidedWith' is set to that barrier, and it returns true.
     * Otherwise, it returns false.
     * @param circleCentre midpoint of that circle
     * @param radius radius of that circle
     * @return whether or not it collides with any part of the flipper.
     */
    @Override
    public boolean isCircleCollidingBarrier(Vect2D circleCentre, double radius) {
        for(AnchoredBarrier b: new AnchoredBarrier[]{upperBarrier, lowerBarrier, end_curve, pivot_curve}){
            if (b.isCircleCollidingBarrier(circleCentre, radius)){
                collidedWith = b;
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        //System.out.println("flip it!");


        for (AnchoredBarrier b: components) {
            b.draw(g);
        }
        g.setColor(Color.RED);
        int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(pivot.x);
        int x2 = BasicPhysicsEngine.convertWorldXtoScreenX(end_point.x);
        int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(pivot.y);
        int y2 = BasicPhysicsEngine.convertWorldYtoScreenY(end_point.y);

        g.drawLine(
                x1,
                y1,
                x2,
                y2
        );
        //g.fillRect(
        //        (int)x1,
        //        (int)y1,
                //x2,
                //y2-y1
        //        BasicPhysicsEngine.convertWorldXtoScreenX(spine.x),
        //        BasicPhysicsEngine.convertWorldYtoScreenY(spine.y)
        //);

        pivot_curve.draw(g);
    }

    public void update(double delta, ActionView currentAction){

        switch (flipperState){
            case READY:
                if(rotates_clockwise){
                    if (currentAction.isRightPressed()){
                        flipperState = FlipperEnum.FLIP_OUT;
                    }
                } else if (currentAction.isLeftPressed()){
                    flipperState = FlipperEnum.FLIP_OUT;
                }
                break;
            case FLIP_OUT:

                if(rotates_clockwise){
                    if (!currentAction.isRightPressed()){
                        flipperState = FlipperEnum.FLIP_IN;
                        break;
                    }
                } else if (!currentAction.isLeftPressed()){
                    flipperState = FlipperEnum.FLIP_IN;
                    break;
                }
                current_angle += (ROTATION_SPEED * delta * 2);
                if (current_angle > MAX_ANGLE){
                    current_angle = MAX_ANGLE;
                    flipperState = FlipperEnum.FLIPPED_FULLY_OUT;
                }
                reposition();
                break;
            case FLIPPED_FULLY_OUT:
                if(rotates_clockwise){
                    if (!currentAction.isRightPressed()){
                        flipperState = FlipperEnum.FLIP_IN;
                        break;
                    }
                } else if (!currentAction.isLeftPressed()){
                    flipperState = FlipperEnum.FLIP_IN;
                    break;
                }
            case FLIP_IN:
                if(rotates_clockwise){
                    if (currentAction.isRightPressed()){
                        flipperState = FlipperEnum.FLIP_OUT;
                        break;
                    }
                } else if (currentAction.isLeftPressed()){
                    flipperState = FlipperEnum.FLIP_OUT;
                    break;
                }
                current_angle -= (ROTATION_SPEED * delta);
                if (current_angle < 0){
                    current_angle = 0;
                    flipperState = FlipperEnum.READY;
                    resetFlipper();
                } else {
                    reposition();
                }

                break;
        }

    }

    private void resetFlipper(){
        components.clear();

        components.add(default_end_curve);
        spine = initial_spine;

        upperBarrier = default_upper_barrier;
        lowerBarrier = default_lower_barrier;

        components.add(upperBarrier);
        components.add(lowerBarrier);

        //addLineBarriers();

    }

    private void reposition(){
        components.clear();

        double turnRads = Math.toRadians(rotates_clockwise ? -current_angle : current_angle);

        spine = initial_spine.rotate(turnRads);

        end_curve = new AnchoredBarrier_Curve(
                //end,
                pivot.add(spine),
                end_radius,
                0,
                360,
                false,
                end_radius,
                Color.RED
        );

        components.add(end_curve);


        Vect2D sp90 = spine.normalise().rotate90degreesAnticlockwise();

        AnchoredBarrier_StraightLine clockwise_up = new AnchoredBarrier_StraightLine(
                pivot.addScaled(sp90, start_radius),
                pivot.add(spine).addScaled(sp90, end_radius),
                Color.red,
                end_radius
        );

        AnchoredBarrier_StraightLine clockwise_down = new AnchoredBarrier_StraightLine(
                pivot.add(spine).addScaled(sp90, -end_radius),
                pivot.addScaled(sp90, -start_radius),
                Color.red,
                end_radius
        );

        if (rotates_clockwise){
            upperBarrier = clockwise_up;
            lowerBarrier = clockwise_down;
        } else{
            upperBarrier = clockwise_down;
            lowerBarrier = clockwise_up;
        }

        components.add(upperBarrier);
        components.add(lowerBarrier);
    }
}

enum FlipperEnum{
    READY,
    FLIP_OUT,
    FLIPPED_FULLY_OUT,
    FLIP_IN
}
