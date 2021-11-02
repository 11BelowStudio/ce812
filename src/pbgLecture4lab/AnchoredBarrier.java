package pbgLecture4lab;

import java.awt.Graphics2D;

public abstract class AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	public abstract Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel,double e);
	public abstract boolean isCircleCollidingBarrier(Vect2D circleCentre, double radius);
	public abstract void draw(Graphics2D g);

}