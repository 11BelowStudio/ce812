package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.dynamics.joints.DistanceJointDef;

public class ElasticConnector {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	private final BasicParticle particle1;
	private final BasicParticle particle2; 
	private final double naturalLength;
	private final double springConstant;
	private final double vibrationalDampingConstant;
	private final boolean canGoSlack;
	private final Color col;
	private final Double hookesLawTruncation;
	public ElasticConnector(BasicParticle p1, BasicParticle p2, double naturalLength, double springConstant, double vibrationalDampingConstant, 
			boolean canGoSlack, Color col, Double hookesLawTruncation) {
		this.particle1 = p1;
		this.particle2 = p2;
		this.naturalLength = naturalLength;
		this.springConstant = springConstant;
		this.vibrationalDampingConstant=vibrationalDampingConstant;
		this.canGoSlack = canGoSlack;
		this.hookesLawTruncation=hookesLawTruncation;
		this.col=col;
		// This is a hack to use the closest box2D joint type, which is a distance joint.
		// However this is inextensible. So if we want elasticity, then we have to apply Hookes law 
		// (and damping) ourselves through the application of manually calculated forces to each connected particle
		// - lecture notes show how to do this.  
		// Due to this current limitation, many of the constructor's arguments are not used, and this connector
		// would have best been called "InelasticConnector", but it was left with the old title to retain 
		// backwards compatibility and hopefully make Box2d easier to understand.
		DistanceJointDef jd = new DistanceJointDef(); // This is an inextensible string
		jd.initialize(p1.body, p2.body, p1.body.getPosition(), p2.body.getPosition());
		BasicPhysicsEngineUsingBox2D.world.createJoint(jd);// add the distanceJoint to the world

	}
	

	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle1.body.getPosition().x);
		int y1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle1.body.getPosition().y);
		int x2 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particle2.body.getPosition().x);
		int y2 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particle2.body.getPosition().y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
	}
}
