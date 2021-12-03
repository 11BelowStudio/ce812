package pbgLecture8lab_wrapperForJBox2D;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.*;

import java.awt.*;

public class Bike implements Drawable{

    private final BasicPolygon bikeBody;

    private final BasicParticle leftWheel;

    private final BasicParticle rightWheel;

    private final RevoluteJoint motorJoint;

    private final float motor_speed = -50f;

    private final float motor_strength = 150f;

    private final BikeController controller;

    public static Bike DEFAULT_BIKE_FACTORY(BikeController bc){
        return new Bike(0f, 0.625f, 3f, 0.5f, 0.4f, 0.35f, -1.25f,
                -0.2f, 1.25f, -0.25f, 0.75f, 100f, 100f, 10f,
                8f, 6f, bc);
    }

    public Bike(final float sx, final float sy, final float width, final float height, final float left_radius,
                final float right_radius, final float left_rel_x, final float left_rel_y, final float right_rel_x,
                final float right_rel_y, final float linearFriction, final float leftFriction, final float rightFriction, final float bodyMass,
                final float leftMass, final float rightMass, BikeController bc){

        final World world = BasicPhysicsEngineUsingBox2D.world;

        controller = bc;


        bikeBody = BasicPolygon.BRAVE_RECTANGLE_FACTORY(
                sx, sy, 0, 0, 1, new Color(74,65,42), bodyMass, linearFriction, width, height, BodyType.DYNAMIC
        );

        final RevoluteJointDef wheelDef = new RevoluteJointDef();
        wheelDef.collideConnected = false;
        wheelDef.bodyA = bikeBody.getBody();

        leftWheel = new BasicParticle(sx+left_rel_x, sy+left_rel_y, 0, 0, left_radius, Color.ORANGE, leftMass, linearFriction, leftFriction);

        wheelDef.bodyB = leftWheel.getBody();
        wheelDef.localAnchorA = new Vec2(left_rel_x, left_rel_y);
        wheelDef.localAnchorB = new Vec2(0, 0);
        wheelDef.motorSpeed = motor_speed;
        wheelDef.maxMotorTorque = motor_strength;

        motorJoint = (RevoluteJoint) world.createJoint(wheelDef);

        //WheelJointDef wj = new WheelJointDef();

        rightWheel = new BasicParticle(sx + right_rel_x, sy+right_rel_y, 0, 0, right_radius, Color.YELLOW, rightMass, linearFriction, rightFriction);

        //wj.bodyA = bikeBody.getBody();
        //wj.bodyB = rightWheel.getBody();
        //wj.collideConnected = false;
        //wj.initialize(bikeBody.getBody(), rightWheel.getBody(), bikeBody.getBody().getLocalVector(new Vec2(0, 1f)), new Vec2());
        //wj.localAnchorA.set(right_rel_x, right_rel_y);
        //wj.localAnchorB.set(0, 0);
        //bikeBody.getBody().getLocalVectorToOut(new Vec2(0, 1f), wj.localAxisA);
        //wj.dampingRatio = 1f;
        //wj.frequencyHz = 0.5f;

        //RopeJointDef rope = new RopeJointDef();
        //rope.bodyA = bikeBody.getBody();
        //rope.bodyB = rightWheel.getBody();
        //rope.collideConnected = false;
        //rope.localAnchorA.set(0, 0);
        //rope.localAnchorB.set(0,0);
        //rope.maxLength = new Vec2(right_rel_x, right_rel_y).length() * 0.1f;

        wheelDef.enableMotor = false;
        wheelDef.bodyB = rightWheel.getBody();
        wheelDef.localAnchorA = new Vec2(right_rel_x, right_rel_y);

        world.createJoint(wheelDef);
        //world.createJoint(wj);
        //world.createJoint(rope);




    }


    public void update(){
        bikeBody.notificationOfNewTimestep();
        leftWheel.notificationOfNewTimestep();
        rightWheel.notificationOfNewTimestep();


        motorJoint.enableMotor(controller.isRightPressed());
        /*
        if (controller.isLeftPressed()){
            if (!motorJoint.isMotorEnabled()){
                motorJoint.enableMotor(true);
            }
        } else if (motorJoint.isMotorEnabled()){
            motorJoint.enableMotor(false);
        }
        */

    }

    public float get_x(){
        return bikeBody.getBody().getPosition().x;
    }

    @Override
    public void draw(Graphics2D g) {

        bikeBody.draw(g);
        leftWheel.draw(g);
        rightWheel.draw(g);


    }
}
