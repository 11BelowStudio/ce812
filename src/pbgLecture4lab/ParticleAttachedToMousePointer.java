package pbgLecture4lab;


import java.awt.Color;

public class ParticleAttachedToMousePointer extends BasicParticle {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */

	public ParticleAttachedToMousePointer(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler,
			double mass) {
		super(sx, sy, vx, vy, radius, improvedEuler, Color.CYAN, mass, 0);
	}
	
	
	@Override
	public void update(double gravity, double deltaT) {
		setPos(new Vect2D(BasicMouseListener.getWorldCoordinatesOfMousePointer()));
	}



}
