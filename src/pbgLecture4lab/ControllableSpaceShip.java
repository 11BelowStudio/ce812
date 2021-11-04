package pbgLecture4lab;


import static pbgLecture4lab.BasicPhysicsEngine.DELTA_T;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class ControllableSpaceShip extends BasicParticle implements CanBeBannedFromRespawning {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	private double angle=0; // direction ship is facing.
	public static final double STEER_RATE = 2 * Math.PI;
	public static final double MAGNITUDE_OF_ENGINE_THRUST_FORCE = (243.2/(double)BasicPhysicsEngine.SCREEN_HEIGHT) * BasicPhysicsEngine.WORLD_HEIGHT;

	/*
	Gravity = ~60.8 in world coords.
	Takes ~4 seconds for the engine to decelerate from max speed

	v = u + at
	0 = u + (-60.8*4)
	243.2 = u max speed.
	 */

	final static double TOWING_RANGE = 1.5;

	private boolean waitingToReleaseSpace;

	private boolean bannedFromRespawning = false;

	public ControllableSpaceShip(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler,
			double mass) {
		this(new Vect2D(sx, sy), new Vect2D(vx, vy), radius, improvedEuler, mass);
	}

	public ControllableSpaceShip(Vect2D pos, Vect2D vel, double radius, boolean improvedEuler,
								 double mass) {
		super(pos, vel, radius, improvedEuler, Color.CYAN, mass, 0);
		waitingToReleaseSpace = false;
	}
	
	@Override
	public void draw(Graphics2D g) {

		if (isInactive()){
			return;
		}

		int x = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
		int y = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);
		g.setColor(col);
		final int[] XP = { -2, 0, 2, 0 };
		final int[] YP = { 2, -2, 2, 0 };
		final int[] XPTHRUST = { -2, 0, 2, 0 };
		final int[] YPTHRUST = { 2, 3, 2, 0 };
		final double SCALE = SCREEN_RADIUS;

		AffineTransform at = g.getTransform();
		g.translate(x,y);
		double rot = -angle;
		g.rotate(rot);
		g.scale(SCALE, SCALE);
		g.setColor(col);
		g.fillPolygon(XP, YP, XP.length);
		if (BasicKeyListener.isThrustKeyPressed()) {
			g.setColor(Color.red);
			g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
		}
		g.setTransform(at);
	}
	
	@Override
	public void update(double gravity, double deltaT) {

		if (inactive){ // if waiting to respawn
			if (bannedFromRespawning){ // no respawning whilst banned from doing that
				return;
			}
			if (waitingToReleaseSpace){
				// wait for player to release the thrust key if they were holding it when they died.
				if(!BasicKeyListener.isSpacebarPressed()) {
					waitingToReleaseSpace = false;
				}
			} else if (BasicKeyListener.isSpacebarPressed()){
				// if player released thrust key, then pressed it again, we reset the ship (reset angle, not inactive)
				angle = 0;
				inactive = false;
				setPos(startPos);
				setVel(startVel);
			}
		} else {

			if (BasicKeyListener.isRotateLeftKeyPressed())
				angle += STEER_RATE * DELTA_T;
			if (BasicKeyListener.isRotateRightKeyPressed())
				angle -= STEER_RATE * DELTA_T;
			if (BasicKeyListener.isThrustKeyPressed()) {
				//Vect2D force = new Vect2D(0,MAGNITUDE_OF_ENGINE_THRUST_FORCE);
				//force=force.rotate(angle);
				//applyForceToParticle(force);
				applyForceToParticle(Vect2D.POLAR(angle, MAGNITUDE_OF_ENGINE_THRUST_FORCE));
			}
			super.update(gravity, deltaT); // do usual move due to gravity.
		}
	}

	public void setBannedFromRespawning(boolean ban){
		bannedFromRespawning = ban;
	}

	public void gotHit(){
		inactive = true;
		System.out.println("yes this ship is inactive");
		if (BasicKeyListener.isSpacebarPressed()){
			waitingToReleaseSpace = true;
		}
	}

	public boolean canThisItemBeTowed(Towable theItem){
		return  !(isInactive() || theItem.isTowed() || theItem.isInactive()) &&
				getVectorTo(theItem).mag() <= TOWING_RANGE;
	}


}
