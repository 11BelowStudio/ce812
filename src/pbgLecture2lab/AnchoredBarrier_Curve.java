package pbgLecture2lab;


import java.awt.Color;
import java.awt.Graphics2D;

public class AnchoredBarrier_Curve extends AnchoredBarrier {

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

	public AnchoredBarrier_Curve(
			Vect2D middle,
			double radiusOfBarrier,
			double startAngle,
			double deltaAngle,
			boolean normalPointsInwards,
			Double barrierDepth,
			Color col
	) {
		centreOfCircleBarrierArc = middle;
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
	public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e) {
		// calculate vector PC
		// flip it if normal faces outwards
		// normalize it to get normal
		// rotate by 90 degrees to get tangent

		Vect2D norm =  Vect2D.minus(centreOfCircleBarrierArc, pos).normalise();
		if (normalPointsInwards){
			norm = norm.mult(-1);
		}
		final Vect2D tan = norm.rotate90degreesAnticlockwise();

		// TIME FOR SOME ILLEGAL MATHS!
		// dot product of two identical unit vectors = 1
		// dot product of two unit vectors at 90 degrees from each other: 0
		// dot product of opposite unit vectors = -1
		final double similarity = norm.scalarProduct(vel.normalise());

		// so, if the dot of normalized velocity and normalized normal is negative
		// that means the angle between them is more than 90 degrees
		// this means that the object is going away from the normal (and away from the wall)
		// so it won't collide with the wall, and velocity is untouched.
		if (similarity < 0){
			return vel;
		}

		final double vParallel=vel.scalarProduct(tan);
		final double vNormal= -vel.scalarProduct(norm) * e;

		Vect2D result=tan.mult(vParallel);
		result=result.addScaled(norm, vNormal);
		return result;

	}


}
