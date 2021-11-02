package pbgLecture4lab;


import java.awt.Color;
import java.awt.Graphics2D;

public class AnchoredBarrier_StraightLine extends AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */

	private final Vect2D startPos,endPos,unitNormal,unitTangent;
	private final Color col;
	private final double barrierLength;
	private final Double barrierDepth;


	public AnchoredBarrier_StraightLine(double startx, double starty, double endx, double endy, Color col) {
		this(startx, starty, endx, endy, col, null);
	}

	public AnchoredBarrier_StraightLine(double startx, double starty, double endx, double endy, Color col, Double barrierWidth) {
		startPos=new Vect2D(startx,starty);
		endPos=new Vect2D(endx,endy);
		
		Vect2D temp=Vect2D.minus(endPos,startPos);
		this.barrierLength=temp.mag();
		temp=temp.normalise();
		this.unitTangent = temp;
		this.unitNormal = unitTangent.rotate90degreesAnticlockwise();
		this.col=col;
		this.barrierDepth=barrierWidth;
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(startPos.x);
		int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(startPos.y);
		int x2 = BasicPhysicsEngine.convertWorldXtoScreenX(endPos.x);
		int y2 = BasicPhysicsEngine.convertWorldYtoScreenY(endPos.y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
	}

	@Override	
	public boolean isCircleCollidingBarrier(Vect2D circleCentre, double circleRadius) {
		Vect2D ap=Vect2D.minus(circleCentre, startPos);
		double distOnCorrectSideOfBarrierToCentre=ap.scalarProduct(unitNormal);
		double distAlongBarrier=ap.scalarProduct(unitTangent);
		// Note barrierDepth is type Double declared in constructor.  
		// barrierDepth null indicates infinite barrierDepth
		// barrierLength is ||AB||, declared in constructor.
		return distOnCorrectSideOfBarrierToCentre<=circleRadius && (barrierDepth==null || distOnCorrectSideOfBarrierToCentre>=-(barrierDepth+circleRadius))
				&& distAlongBarrier>=0 && distAlongBarrier<=barrierLength;
	}

	@Override
	public Vect2D calculateVelocityAfterACollision(Vect2D pos, Vect2D vel, double e) {
		double vParallel=vel.scalarProduct(unitTangent);
		double vNormal=vel.scalarProduct(unitNormal);
		if (vNormal<0) // assumes normal points AWAY from wall... 
			vNormal=-vNormal*e;
		Vect2D result=unitTangent.mult(vParallel);
		result=result.addScaled(unitNormal, vNormal);
		return result;
	}
}
