package pbgLecture1lab;

import static pbgLecture1lab.BasicPhysicsEngine.DELTA_T;


import java.awt.Color;
import java.awt.Graphics2D;

public class BasicParticle implements IHaveAPosition {
	private final int RADIUS_OF_PARTICLE_IN_PIXELS;

	private Vect2D pos;

	private Vect2D vel;
	
	private final double radius;
	private final Color col;
	private final boolean improvedEuler;
	
	// DO NOT Alter the signature of this constructor, and do not add a new constructor

	/**
	 * The constructor. DO NOT ALTER SIGNATURE, DO NOT ADD NEW CONSTRUCTOR.
	 * @param sx x pos
	 * @param sy y pos
	 * @param vx x velocity
	 * @param vy y velocity
	 * @param radius radius of it
	 * @param improvedEuler whether or not we're using improvedEuler
	 * @param col colour of the particle
	 */
	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col) {
		pos = new Vect2D(sx, sy);
		vel = new Vect2D(vx, vy);
		this.radius=radius;
		this.improvedEuler=improvedEuler;
		this.RADIUS_OF_PARTICLE_IN_PIXELS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
	}

	public void update() {
		update(BasicPhysicsEngine.GRAVITY, DELTA_T);

		/*
		final Vect2D grav = new Vect2D(0, -BasicPhysicsEngine.GRAVITY);

		if (improvedEuler) {
			// improved euler stuff
			Vect2D trialVel = vel.addScaled(grav, DELTA_T);

			Vect2D dist = vel.add(trialVel);

			pos = pos.addScaled(dist, DELTA_T * 0.5);

			vel = vel.addScaled(grav, DELTA_T);

		} else {
			// basic Euler stuff
			pos = pos.addScaled(vel, DELTA_T);
			vel = vel.addScaled(grav, DELTA_T);
		}

		 */


	}


	/**
	 * A version of update with a given gravity and deltaT time step
	 * @param gravity the amount of gravity that we're using
	 * @param deltaT timestep stuff
	 */
	public void update(double gravity, double deltaT) {
		// Paste in the contents of your update function into this gap so that it can be verified by the Moodle CodeRunnerVect2D acc=new Vect2D(0,-gravity);
		// Note that your function should be exactly the same as the function which you implemented in lab1, and it should update the value of this.pos
		final Vect2D grav = new Vect2D(0, -gravity);

		if (improvedEuler) {
			Vect2D trialVel = vel.addScaled(grav, deltaT);

			Vect2D dist = vel.add(trialVel);

			pos = pos.addScaled(dist, deltaT * 0.5);

			vel = vel.addScaled(grav, deltaT);

		} else {

			pos = pos.addScaled(vel, deltaT);
			vel = vel.addScaled(grav, deltaT);
		}

	}



	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngine.convertWorldXtoScreenX(pos.x);
		int y = BasicPhysicsEngine.convertWorldYtoScreenY(pos.y);
		g.setColor(col);
		g.fillOval(x - RADIUS_OF_PARTICLE_IN_PIXELS, y - RADIUS_OF_PARTICLE_IN_PIXELS, 2 * RADIUS_OF_PARTICLE_IN_PIXELS, 2 * RADIUS_OF_PARTICLE_IN_PIXELS);
	}

	public double getRadius() {
		return radius;
	}

	public double getX() {
		return pos.x;
	}
	public double getY() {
		return pos.y;
	}

	public Vect2D getPosition(){
		return pos;
	}
}
