package pbgLecture4lab;


import java.awt.Color;
import java.awt.Graphics2D;


public class AnchoredBarrier_Curve extends AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */

	private final Vect2D centreOfCircleBarrierArc;
	private final Color col;
	private final Double barrierDepth;
	private final double deltaAngle;
	private final double startAngle;
	private final double radiusOfBarrier;
	private final int radiusInScreenCoordinates;
	private final boolean normalPointsInwards;


	public AnchoredBarrier_Curve(double centrex, double centrey, double radiusOfBarrier, double startAngle, double deltaAngle, boolean normalPointsInwards, Color col) {
		this(centrex, centrey,radiusOfBarrier, startAngle, deltaAngle, normalPointsInwards,null, col);
	}

	public AnchoredBarrier_Curve(double centrex, double centrey, double radiusOfBarrier, double startAngle, double deltaAngle, boolean normalPointsInwards, Double barrierDepth, Color col) {
		centreOfCircleBarrierArc=new Vect2D(centrex, centrey);
		this.barrierDepth=barrierDepth;
		this.deltaAngle=deltaAngle;
		this.startAngle=startAngle;
		this.radiusOfBarrier=radiusOfBarrier;
		this.radiusInScreenCoordinates=BasicPhysicsEngine.convertWorldLengthToScreenLength(radiusOfBarrier);
		this.normalPointsInwards=normalPointsInwards;
		this.col=col;
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(centreOfCircleBarrierArc.x);
		int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(centreOfCircleBarrierArc.y);
		g.setColor(col);
		// g.drawArc arguments give dimensions of a rectangle (x,y,width,height) that contains the full ellipse that contains the arc
		g.drawArc(x1-radiusInScreenCoordinates, y1-radiusInScreenCoordinates, 
				radiusInScreenCoordinates*2, radiusInScreenCoordinates*2, (int) startAngle, (int) deltaAngle);
	}

	@Override
	public boolean isCircleCollidingBarrier(Vect2D circleCentre, double radius) {
		Vect2D ap=Vect2D.minus(circleCentre, centreOfCircleBarrierArc);
		double ang=ap.angle(); // relies on Math.atan2 function
		ang=ang*180/Math.PI; //convert from radians to degrees
		ang=(ang+360)%360;	// remove any negative angles to avoid confusion
		boolean withinAngleRange=false;
		if (deltaAngle<0 && ((ang>=startAngle+deltaAngle && ang<=startAngle) ||(ang>=startAngle+deltaAngle+360 && ang<=startAngle+360)))
			withinAngleRange=true;
		if (deltaAngle>=0 && ((ang>=startAngle && ang<=startAngle+deltaAngle) ||(ang>=startAngle-360 && ang<=startAngle+deltaAngle-360)))
			withinAngleRange=true;
		double distToCentreOfBarrierArc=ap.mag();
		boolean withinDistanceRange=(normalPointsInwards && distToCentreOfBarrierArc+radius>=this.radiusOfBarrier && distToCentreOfBarrierArc-radius<=this.radiusOfBarrier+(barrierDepth!=null?barrierDepth:0)) 
				|| (!normalPointsInwards && distToCentreOfBarrierArc-radius<=this.radiusOfBarrier && distToCentreOfBarrierArc+radius>=this.radiusOfBarrier-(barrierDepth!=null?barrierDepth:0));
		return withinDistanceRange && withinAngleRange;
	}

	
	@Override
	public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel,double e) {
		Vect2D normal=Vect2D.minus(pos, centreOfCircleBarrierArc).normalise();
		if (normalPointsInwards) normal=normal.mult(-1);
		Vect2D tangent=normal.rotate90degreesAnticlockwise().normalise();
		double vParallel=vel.scalarProduct(tangent);
		double vNormal=vel.scalarProduct(normal);
		if (vNormal<0) // assumes normal points AWAY from barrierPoint... 
			vNormal=-vNormal*e;
		Vect2D result=tangent.mult(vParallel);
		result=result.addScaled(normal, vNormal);
		return result;
	}


}
