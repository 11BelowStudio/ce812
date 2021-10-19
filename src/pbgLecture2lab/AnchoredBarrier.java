package pbgLecture2lab;

import java.awt.Graphics2D;

public abstract class AnchoredBarrier {
	public abstract Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e);
	public abstract boolean isCircleCollidingBarrier(Vect2D circleCentre, double radius);
	public abstract void draw(Graphics2D g);

}