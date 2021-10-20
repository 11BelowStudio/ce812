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

        final double spineDegrees = Math.toDegrees(spine.angle());

        //System.out.println(Math.abs(spineDegrees));

        default_end_curve = new AnchoredBarrier_Curve(
                end,
                end_radius,
                270 + spineDegrees,
                180,
                false,
                end_radius,
                Color.RED
        );

        end_curve = default_end_curve;

        final Vect2D sp90 = spine.normalise().rotate90degreesAnticlockwise();

        // This barrier is at the top if we're going anticlockwise.
        AnchoredBarrier_StraightLine anticlockwise_upper_barrier = new AnchoredBarrier_StraightLine(
                pivot.addScaled(sp90, start_radius),
                pivot.add(spine).addScaled(sp90, end_radius),
                Color.RED,//Color.GREEN,
                end_radius
        );

        // This barrier is at the top if we're going anticlockwise
        AnchoredBarrier_StraightLine anticlockwise_lower_barrier = new AnchoredBarrier_StraightLine(
                pivot.add(spine).addScaled(sp90, -end_radius),
                pivot.addScaled(sp90, -start_radius),
                Color.RED, //Color.BLUE,
                end_radius
        );

        if (clockwise){
            default_upper_barrier = anticlockwise_upper_barrier;
            default_lower_barrier = anticlockwise_lower_barrier;
        } else{
            default_upper_barrier = anticlockwise_lower_barrier;
            default_lower_barrier = anticlockwise_upper_barrier;
        }


        upperBarrier = default_upper_barrier;

        lowerBarrier = default_lower_barrier;

        collidedWith = pivot_curve;

        resetFlipper();

    }

    /**
     * Attempts to calculate the position of the given object (with known position and velocity) after colliding
     * with this flipper.
     *
     * If this flipper in the 'ready' or 'flipped_fully_out' state, or if the other object hit the pivot collider,
     * the velocity of the other object is manipulated as if it just collided with the collider it hit, as normal.
     *
     * But, if this flipper is in the process of flipping in/flipping out, and the pivot collider isn't what was
     * hit, it performs stuff on the relative velocity of that other object to the moving parts of this flipper.
     * @param pos object position
     * @param vel object velocity
     * @param e barrier elasticity
     * @return velocity of the other object after hitting this flipper.
     */
    @Override
    public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e) {


        switch (flipperState){
            case READY:
            case FLIPPED_FULLY_OUT:
                // in these cases, the flipper has stopped moving, so there's no relative velocity to factor in
                return collidedWith.calculateVelocityAfterACollision(pos, vel, e);
            case FLIP_IN:
            case FLIP_OUT:
                // in these cases, the flipper is moving. This is the tricky one.


                if (collidedWith == pivot_curve){
                    // if we hit the pivot curve (stationary), there's no relative velocity to factor in
                    return pivot_curve.calculateVelocityAfterACollision(pos, vel, e);
                }

                // radians per second * distance to contact point

                // distance to contact point is pb.spine

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
                final Vect2D fromPivotToBall = Vect2D.minus(pos, pivot);
                //System.out.println(fromPivotToBall);

                final double collisionDist = fromPivotToBall.scalarProduct(spine);

                //final Vect2D collisionLocation = pivot.addScaled(spine.normalise(), collisionDist);

                //System.out.println("pivot, collision dist, collision location, cross info, contact velocity");
                //System.out.println(pivot);
                //System.out.println(collisionDist);
                //System.out.println(collisionLocation);

                // check if ball is above flipper

                Vect2D spineTangent = getSpineTangent();

                final double crossInfo = Vect2D.CROSS_PRODUCT(spineTangent, fromPivotToBall);
                // if greater than 0, ball hit the 'above' area of the flipper
                // if less than 0, ball hit the 'below' area of the flipper
                //System.out.println(crossInfo);

                int movementScale = 0; // how much to multiply the flipper's relative movement to the ball by

                if (crossInfo > 0){ // if ball above flipper
                    if (flipperState == FlipperEnum.FLIP_OUT){
                        // if it's going out, the flipper's moving to the ball
                        movementScale = 1;
                    } else {
                        // otherwise, it's going away from the ball
                        movementScale = -1;
                    }
                } else if (crossInfo < 0){ // if ball below flipper
                    if (flipperState == FlipperEnum.FLIP_IN){
                        // if it's going in, the flipper's moving to the ball below it
                        movementScale = 1;
                    } else {
                        // going away from the ball above it
                        movementScale = -1;
                    }
                }

                final double contactSpeed = Math.toRadians(ROTATION_SPEED) * collisionDist * movementScale;

                //System.out.println(contactSpeed);

                // line pivot-b is known

                // relative velocity of b in respect to A
                //      v(b/a) = vb - va
                //      vb = va + v(b/a)

                final Vect2D barrierVel = getSpineNormal().mult(contactSpeed);

                final Vect2D ballVel = Vect2D.minus(barrierVel, vel);

                return collidedWith.calculateVelocityAfterACollision(pos, ballVel, e);

        }

        return collidedWith.calculateVelocityAfterACollision(pos, vel, e);
    }

    /**
     * Computes and returns the normal of the current spine.
     * Forced to point 'up' (towards the 'upper' barrier)
     * @return the normal of the current spine
     */
    private Vect2D getSpineNormal(){
        if (rotates_clockwise){
            return spine.normalise().mult(-1).rotate90degreesAnticlockwise();
        }
        return spine.normalise().rotate90degreesAnticlockwise();
    }

    private Vect2D getSpineTangent(){
        if (rotates_clockwise){
            return spine.normalise().mult(-1);
        }
        return spine.normalise();
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
        for(AnchoredBarrier b: new AnchoredBarrier[]{lowerBarrier, upperBarrier, end_curve, pivot_curve}){
            if (b.isCircleCollidingBarrier(circleCentre, radius)){
                collidedWith = b;
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g) {

        for (AnchoredBarrier b: components) {
            b.draw(g);
        }
        pivot_curve.draw(g);

        /*
        g.setColor(Color.RED);
        final int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(pivot.x);
        final int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(pivot.y);

        // rendering line that shows the default spine of the flipper
        g.drawLine(x1, y1, BasicPhysicsEngine.convertWorldXtoScreenX(end_point.x), BasicPhysicsEngine.convertWorldYtoScreenY(end_point.y));


        // rendering the normal for the current spine
        Vect2D spineNormal = getSpineNormal().mult(10);
        g.drawLine(x1, y1, x1 + (int)spineNormal.x, y1 - (int)spineNormal.y);

        g.setColor(Color.BLUE);
        // rendering the tangent of the current spine
        Vect2D spineTangent = getSpineTangent().mult(10);
        g.drawLine(x1, y1, x1 + (int)spineTangent.x, y1 - (int)spineTangent.y);
         */
    }

    /**
     * Updates the flipper
     * @param delta length of the current timestep
     * @param currentAction read-only interface for the current controller action
     */
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

        final double spineDegrees = Math.toDegrees(spine.angle());

        end_curve = new AnchoredBarrier_Curve(
                pivot.add(spine),
                end_radius,
                270 + spineDegrees,//0,
                180, //360,
                false,
                end_radius,
                Color.ORANGE //Color.RED
        );

        components.add(end_curve);


        Vect2D sp90 = spine.normalise().rotate90degreesAnticlockwise();

        AnchoredBarrier_StraightLine anticlockwise_up = new AnchoredBarrier_StraightLine(
                pivot.addScaled(sp90, start_radius),
                pivot.add(spine).addScaled(sp90, end_radius),
                Color.ORANGE, //Color.red,
                end_radius
        );

        AnchoredBarrier_StraightLine anticlockwise_down = new AnchoredBarrier_StraightLine(
                pivot.add(spine).addScaled(sp90, -end_radius),
                pivot.addScaled(sp90, -start_radius),
                Color.ORANGE, //Color.RED,
                end_radius
        );

        if (rotates_clockwise){
            upperBarrier = anticlockwise_down;
            lowerBarrier = anticlockwise_up;
        } else{
            upperBarrier = anticlockwise_up;
            lowerBarrier = anticlockwise_down;

        }

        components.add(upperBarrier);
        components.add(lowerBarrier);
    }
}

/**
 * The possible states for the flipper.
 */
enum FlipperEnum{
    /**
     * Ready to flip (no rotation, button not held)
     */
    READY,
    /**
     * Flipping out (button held), hasn't fully rotated yet.
     */
    FLIP_OUT,
    /**
     * It has flipped out.
     * Reached full rotation, player still holding the flip button.
     */
    FLIPPED_FULLY_OUT,
    /**
     * Flipping back in
     * Player has released the flip button, rotating back to default
     */
    FLIP_IN
}
