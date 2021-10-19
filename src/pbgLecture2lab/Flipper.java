package pbgLecture2lab;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Flipper extends AnchoredBarrier{

    private final Vect2D pivot;

    private final Vect2D initial_spine;

    private final Vect2D apex;

    private Vect2D spine;

    private final double start_radius;

    private final double end_radius;

    private final boolean rotates_clockwise;

    private static final double MAX_ANGLE = 30.0;
    private static final double ROTATION_SPEED = 60.0;
    private double current_angle = 0.0;

    private final List<AnchoredBarrier> components;

    private final AnchoredBarrier_Curve pivot_curve;

    private final AnchoredBarrier_Curve default_end_curve;

    private AnchoredBarrier collidedWith;

    private FlipperEnum flipperState;

    public Flipper(final Vect2D start, final Vect2D end, final double startRadius, final double endRadius, final boolean clockwise){

        flipperState = FlipperEnum.READY;

        start_radius = startRadius;
        end_radius = endRadius;

        pivot = start;
        System.out.println(pivot);
        apex = end;
        System.out.println(apex);
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

        collidedWith = pivot_curve;

        resetFlipper();

    }

    @Override
    public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e) {
        return collidedWith.calculateVelocityAfterACollision(pos, vel, 1.1);
    }

    @Override
    public boolean isCircleCollidingBarrier(Vect2D circleCentre, double radius) {
        for(AnchoredBarrier b: components){
            if (b.isCircleCollidingBarrier(circleCentre, radius)){
                collidedWith = b;
                return true;
            }
        }
        if (pivot_curve.isCircleCollidingBarrier(circleCentre, radius)){
            collidedWith = pivot_curve;
            return true;
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
        int x2 = BasicPhysicsEngine.convertWorldXtoScreenX(apex.x);
        int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(pivot.y);
        int y2 = BasicPhysicsEngine.convertWorldYtoScreenY(apex.y);

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
                }
                reposition();
                break;
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
                }
                reposition();
                break;
        }

    }

    private void resetFlipper(){
        components.clear();

        components.add(default_end_curve);
        spine = initial_spine;

        addLineBarriers();

    }

    private void reposition(){
        components.clear();

        double turnRads = Math.toRadians(rotates_clockwise ? -current_angle : current_angle);

        spine = initial_spine.rotate(turnRads);

        components.add(new AnchoredBarrier_Curve(
                //end,
                pivot.add(spine),
                end_radius,
                0,
                360,
                false,
                end_radius,
                Color.RED
        ));
        addLineBarriers();
    }

    private void addLineBarriers(){

        Vect2D sp90 = spine.normalise().rotate90degreesAnticlockwise();

        AnchoredBarrier_StraightLine upBarrier = new AnchoredBarrier_StraightLine(
                pivot.addScaled(sp90, start_radius),
                pivot.add(spine).addScaled(sp90, end_radius),
                Color.red,
                end_radius
        );

        AnchoredBarrier_StraightLine downBarrier = new AnchoredBarrier_StraightLine(
                pivot.add(spine).addScaled(sp90, -end_radius),
                pivot.addScaled(sp90, -start_radius),
                Color.red,
                end_radius
        );

        components.add(upBarrier);
        components.add(downBarrier);
    }
}

enum FlipperEnum{
    READY,
    FLIP_OUT,
    FLIP_IN
}
