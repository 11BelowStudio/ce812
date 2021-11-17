package pbgLecture6lab_wrapperForJBox2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javax.imageio.ImageIO;

public class BasicParticle implements Drawable, IHaveABody {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	public final int SCREEN_RADIUS;

	private final float linearDragForce,mass;
	public final Color col;
	protected final Body body;


	public BasicParticle(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce, float friction){
		World w= BasicPhysicsEngineUsingBox2D.world; // a Box2D object
		BodyDef bodyDef = new BodyDef();  // a Box2D object
		bodyDef.type = BodyType.DYNAMIC; // this says the physics engine is to move it automatically
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		this.body = w.createBody(bodyDef);
		CircleShape circleShape = new CircleShape();// This class is from Box2D
		circleShape.m_radius = radius;
		FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
		fixtureDef.shape = circleShape;
		fixtureDef.density = (float) (mass/(Math.PI*radius*radius));
		fixtureDef.friction = friction;// this is surface friction;
		fixtureDef.restitution = 1.0f;
		body.createFixture(fixtureDef);
		this.linearDragForce=linearDragForce;
		this.mass=mass;
		this.SCREEN_RADIUS=(int)Math.max(BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
	}

	public BasicParticle(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce) {
		this(sx, sy, vx, vy, radius, col, mass, linearDragForce, 0.0f);
	}

	public void draw(Graphics2D g) {
		if (body.isActive()) {
			int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
			int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
			g.setColor(col);
			g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);

			Vec2 outwardsVec = BasicPhysicsEngineUsingBox2D.rotateVec(new Vec2(1,0),body.getAngle());
			outwardsVec = outwardsVec.add(body.getPosition());

			g.setColor(Color.WHITE);
			g.drawLine(
					x , y,
					BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(outwardsVec.x),
					BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(outwardsVec.y)
			);
		}
	}


	public void notificationOfNewTimestep() {
		if (linearDragForce>0) {
			Vec2 dragForce1=new Vec2(body.getLinearVelocity());
			dragForce1=dragForce1.mul(-linearDragForce*mass);
			body.applyForceToCenter(dragForce1);
		}
	}

	public boolean isActive(){
		return body.isActive();
	}

	public Body getBody(){
		return body;
	}
	
}
