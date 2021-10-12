package pbgLecture1lab;

import static pbgLecture1lab.BasicPhysicsEngine.DELTA_T;


import java.awt.Color;
import java.awt.Graphics2D;

public class BasicParticle {
	private final int RADIUS_OF_PARTICLE_IN_PIXELS;

	private double sx,sy,vx,vy;// HINT: This line needs deleting completely and replacing by two Vect2D variables
	
	private final double radius;
	private final Color col;
	private final boolean improvedEuler;
	
	// DO NOT Alter the signature of this constructor, and do not add a new constructor

	/**
	 * The constructor. DO NOT ALTER SIGNATURE, DO NOT ADD NEW CONSTRUCTOR.
	 * @param sx
	 * @param sy
	 * @param vx
	 * @param vy
	 * @param radius
	 * @param improvedEuler
	 * @param col
	 */
	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col) {
		this.sx=sx;
		this.sy=sy;
		this.vx=vx;
		this.vy=vy;
		this.radius=radius;
		this.improvedEuler=improvedEuler;
		this.RADIUS_OF_PARTICLE_IN_PIXELS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
	}

	public void update() {
		if (improvedEuler) {
			// improved Euler
			//TODO
		} else {
			// basic Euler: TODO extend this to include BasicPhysicsEngine.GRAVITY
			sx+=vx*DELTA_T;
			sy+=vy*DELTA_T;
		}
	}


	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngine.convertWorldXtoScreenX(sx);
		int y = BasicPhysicsEngine.convertWorldYtoScreenY(sy);
		g.setColor(col);
		g.fillOval(x - RADIUS_OF_PARTICLE_IN_PIXELS, y - RADIUS_OF_PARTICLE_IN_PIXELS, 2 * RADIUS_OF_PARTICLE_IN_PIXELS, 2 * RADIUS_OF_PARTICLE_IN_PIXELS);
	}

	public double getRadius() {
		return radius;
	}

	public double getX() {
		return sx;
	}
	public double getY() {
		return sy;
	}
}
