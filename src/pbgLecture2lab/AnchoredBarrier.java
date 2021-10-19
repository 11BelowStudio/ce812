package pbgLecture2lab;

import java.awt.Graphics2D;

public abstract class AnchoredBarrier {

	/**
	 * Calculate velocity after an object collides with this barrier
	 * @param pos object position
	 * @param vel object velocity
	 * @param e barrier elasticity
	 * @return new velocity after that collision at that position and with given elasticity.
	 */
	public abstract Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e);
	public abstract boolean isCircleCollidingBarrier(Vect2D circleCentre, double radius);
	public abstract void draw(Graphics2D g);

}