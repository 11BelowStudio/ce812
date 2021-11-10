package pbgLecture5lab_wrapperForJBox2D;


import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.common.Vec2;

public class AnchoredBarrier_Curve extends AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:  
	 */

	private final Vec2 centreOfCircleBarrierArc;
	private final Color col;
	private final double deltaAngle;
	private final double startAngle;
	private final int radiusInScreenCoordinates;



	public AnchoredBarrier_Curve(float centrex, float centrey, float radiusOfBarrier, float startAngle, float deltaAngle, Color col) {
		centreOfCircleBarrierArc=new Vec2(centrex, centrey);
		this.deltaAngle=deltaAngle;
		this.startAngle=startAngle;
		this.radiusInScreenCoordinates=(int)BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(radiusOfBarrier);
		this.col=col;
		if (Math.abs(deltaAngle)==360f) {
			// we have a complete circle - this can be implemented by a circleShape box2d Object
			throw new RuntimeException("Not implemented");
		} else {
			// This is not a complete circle.
			// Hence we need an arc.  Box2D does not support arcs.
			// So it needs approximating by a polygon with enough sides so as to make it look smooth.
			// Most efficient to use a Box2D chain.
			// Note that we don't need too many points on this polygon because 
			// the curve is still rendered as a nice smooth curve, even if in physics world 
			// it is slightly jagged.  It's doubtful the user would notice tiny
			// angle discrepancies in the physics bouncing.
			throw new RuntimeException("Not implemented");
		}
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(centreOfCircleBarrierArc.x);
		int y1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(centreOfCircleBarrierArc.y);
		g.setColor(col);
		// g.drawArc arguments give dimensions of a rectangle (x,y,width,height) that contains the full ellipse that contains the arc
		g.drawArc(x1-radiusInScreenCoordinates, y1-radiusInScreenCoordinates, 
				radiusInScreenCoordinates*2, radiusInScreenCoordinates*2, (int) startAngle, (int) deltaAngle);
	}



}
