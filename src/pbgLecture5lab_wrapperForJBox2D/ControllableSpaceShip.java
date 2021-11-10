package pbgLecture5lab_wrapperForJBox2D;


import static pbgLecture5lab_wrapperForJBox2D.BasicPhysicsEngineUsingBox2D.DELTA_T;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.jbox2d.common.Vec2;

import pbgLecture5lab_wrapperForJBox2D.BasicKeyListener;

public class ControllableSpaceShip extends BasicParticle  {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	private double angle=0; // direction ship is facing.
	public static final double STEER_RATE = 2 * Math.PI;
	public static final float MAGNITUDE_OF_ENGINE_THRUST_FORCE = 500;

	public ControllableSpaceShip(float sx, float sy, float vx, float vy, float radius, boolean improvedEuler,
			float mass) {
		super(sx, sy, vx, vy, radius, Color.CYAN, mass, 0f);
	}
	
	@Override
	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
		int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
		final int[] XP = { -2, 0, 2, 0 };
		final int[] YP = { -2, 2, -2, 0 };
		final int[] XPTHRUST = { -2, 0, 2, 0 };
		final int[] YPTHRUST = { -2, -3, -2, 0 };
		final double SCALE = SCREEN_RADIUS;

		AffineTransform at = g.getTransform();
		g.translate(x,y);
		g.scale(SCALE, -SCALE);// need a minus on the y-coord here because screenCoords are upside-down compared to worldCoords
		g.rotate(angle);
		g.setColor(col);

		g.fillPolygon(XP, YP, XP.length);
		if (BasicKeyListener.isThrustKeyPressed()) {
			g.setColor(Color.red);
			g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
		}
		g.setTransform(at);
	}
	
	@Override
	public void notificationOfNewTimestep() {
		if (BasicKeyListener.isRotateLeftKeyPressed()) 
			angle+=STEER_RATE * DELTA_T;
		if (BasicKeyListener.isRotateRightKeyPressed()) 
			angle-=STEER_RATE * DELTA_T;
		if (BasicKeyListener.isThrustKeyPressed()) {
			Vec2 force = new Vec2((float)(-Math.sin(angle)*MAGNITUDE_OF_ENGINE_THRUST_FORCE), (float)(Math.cos(angle)*MAGNITUDE_OF_ENGINE_THRUST_FORCE));
			body.applyForceToCenter(force);
		}
	}
}
