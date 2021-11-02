package pbgLecture4lab;

import java.awt.Color;
import java.awt.Graphics2D;

public class ElasticConnector {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	private final BasicParticle particle1;
	private final BasicParticle particle2; 
	private final double naturalLength;
	private final double springConstant;
	private final double motionDampingConstant;
	private final boolean canGoSlack;
	private final Color col;
	private final Double hookesLawTruncation;
	public ElasticConnector(BasicParticle p1, BasicParticle p2, double naturalLength, double springConstant, double motionDampingConstant, 
			boolean canGoSlack, Color col, Double hookesLawTruncation) {
		this.particle1 = p1;
		this.particle2 = p2;
		this.naturalLength = naturalLength;
		this.springConstant = springConstant;
		this.motionDampingConstant=motionDampingConstant;
		this.canGoSlack = canGoSlack;
		this.hookesLawTruncation=hookesLawTruncation;
		this.col=col;
	}
	
	public double calculateTension() {
		// implementation of truncated hooke's law
		double dist=Vect2D.minus(particle1.getPos(), particle2.getPos()).mag();
		if (dist<naturalLength && canGoSlack) return 0;
		
		double extensionRatio = (dist-naturalLength)/naturalLength;
		Double truncationLimit=this.hookesLawTruncation;// this stops Hooke's law giving too high a force which might cause instability in the numerical integrator
		if (truncationLimit!=null && extensionRatio>truncationLimit) 
			extensionRatio=truncationLimit;
		if (truncationLimit!=null && extensionRatio<-truncationLimit) 
			extensionRatio=-truncationLimit;
		double tensionDueToHookesLaw = extensionRatio*springConstant;
		double tensionDueToMotionDamping=motionDampingConstant*rateOfChangeOfExtension();
		return tensionDueToHookesLaw+tensionDueToMotionDamping;
	}
	
	public double rateOfChangeOfExtension() {
		Vect2D v12=Vect2D.minus(particle2.getPos(), particle1.getPos()); // goes from p1 to p2
		v12=v12.normalise(); // make it a unit vector.
		Vect2D relativeVeloicty=Vect2D.minus(particle2.getVel(), particle1.getVel()); // goes from p1 to p2
		return relativeVeloicty.scalarProduct(v12);// if this is positive then it means the 
		// connector is getting longer
	}
	
	public void applyTensionForceToBothParticles() {
		double tension=calculateTension();
		Vect2D p12=Vect2D.minus(particle2.getPos(), particle1.getPos()); // goes from p1 to p2
		p12=p12.normalise(); // make it a unit vector.
		Vect2D forceOnP1=p12.mult(tension);
		particle1.applyForceToParticle(forceOnP1);

		Vect2D forceOnP2=p12.mult(-tension);// tension on second particle acts in opposite direction (an example of Newton's 3rd Law)
		particle2.applyForceToParticle(forceOnP2);
	}
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(particle1.getPos().x);
		int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(particle1.getPos().y);
		int x2 = BasicPhysicsEngine.convertWorldXtoScreenX(particle2.getPos().x);
		int y2 = BasicPhysicsEngine.convertWorldYtoScreenY(particle2.getPos().y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
	}
}
