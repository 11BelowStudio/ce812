package pbgLecture5lab_wrapperForJBox2D;


import java.awt.Color;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

public class ParticleAttachedToMousePointer extends BasicParticle  {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */

	public ParticleAttachedToMousePointer(float sx, float sy, float vx, float vy, float radius,
			float mass) {
		super(sx, sy, vx, vy, radius, Color.CYAN, mass, 0.0f);
		body.setType(BodyType.KINEMATIC);// this means we will take care of this particle's motion ourselves.
			
	}
	
	@Override
	public void notificationOfNewTimestep() {
		body.setTransform(new Vec2(BasicMouseListener.getWorldCoordinatesOfMousePointer()),0.0f);
	}



}
