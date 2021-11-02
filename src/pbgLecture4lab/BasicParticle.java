package pbgLecture4lab;


import java.awt.Color;
import java.awt.Graphics2D;

public class BasicParticle {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	public final int SCREEN_RADIUS;

	private Vect2D pos;
	private Vect2D vel;
	private Vect2D totalForceThisTimeStep;
	private final double radius;
	private final double mass;
	private final double dragForce;
	public final Color col;

	private final boolean improvedEuler;

	

	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col, double mass, double dragForce) {
		setPos(new Vect2D(sx,sy));
		setVel(new Vect2D(vx,vy));
		this.radius=radius;
		this.dragForce=dragForce;
		this.mass=mass;
		this.improvedEuler=improvedEuler;
		this.SCREEN_RADIUS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
		this.totalForceThisTimeStep=new Vect2D();
	}

	public void update(double gravity, double deltaT) {
		// Apply forces that always exist on particle:
		applyParticleWeight(gravity);
		if (dragForce!=0)
			// this particle has been told to slow down gradually due to drag 
			applyDragForce(dragForce);
		//calculate Acceleration using Newton's second law.
		Vect2D acc=totalForceThisTimeStep.mult(1/mass);// using a=F/m from Newton's Second Law
		
		if (improvedEuler) {
			Vect2D pos2=getPos().addScaled(getVel(), deltaT);// in theory this could be used,e.g. if acc2 depends on pos - but in this constant gravity field it will not be relevant
			Vect2D vel2=getVel().addScaled(acc, deltaT);
			Vect2D velAv=vel2.add(getVel()).mult(0.5);
			Vect2D acc2=new Vect2D(acc);//assuming acceleration is constant
			// Note acceleration is NOT CONSTANT for distance dependent forces such as 
			// Hooke's law or newton's law of gravity, so this is BUG  
			// in this Improved Euler implementation.  
			// The whole program structure needs changing to fix this problem properly!			
			Vect2D accAv=acc2.add(acc).mult(0.5);
			setPos(getPos().addScaled(velAv, deltaT));
			setVel(getVel().addScaled(accAv, deltaT));
		} else {
			// basic Euler
			setPos(getPos().addScaled(getVel(), deltaT));
			setVel(getVel().addScaled(acc, deltaT));
		}
	}

	private void applyParticleWeight(double gravity) {
		Vect2D weightVector=new Vect2D(0,-gravity*mass);// using formula weight = mass * 9.8, downwards
		applyForceToParticle(weightVector);
	}

	private void applyDragForce(double amountOfDragForce) {
		Vect2D dragForce=getVel().mult(-amountOfDragForce*mass);
		applyForceToParticle(dragForce);
	}

	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngine.convertWorldXtoScreenX(getPos().x);
		int y = BasicPhysicsEngine.convertWorldYtoScreenY(getPos().y);
		g.setColor(col);
		g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
	}

	public double getRadius() {
		return radius;
	}

	public Vect2D getPos() {
		return pos;
	}

	public void setPos(Vect2D pos) {
		this.pos = pos;
	}

	public Vect2D getVel() {
		return vel;
	}

	public void setVel(Vect2D vel) {
		this.vel = vel;
	}

	public boolean collidesWith(BasicParticle p2) {
		Vect2D vecFrom1to2 = Vect2D.minus(p2.getPos(), getPos());
		boolean movingTowardsEachOther = Vect2D.minus(p2.getVel(), getVel()).scalarProduct(vecFrom1to2)<0;
		return vecFrom1to2.mag()<getRadius()+p2.getRadius() && movingTowardsEachOther;
	}

	public static void implementElasticCollision(BasicParticle p1, BasicParticle p2, double e) {
		if (!p1.collidesWith(p2)) throw new IllegalArgumentException();
		Vect2D vec1to2 = Vect2D.minus(p2.getPos(), p1.getPos());
		vec1to2=vec1to2.normalise();
		Vect2D tangentDirection=new Vect2D(vec1to2.y, -vec1to2.x);
		double v1n=p1.getVel().scalarProduct(vec1to2);
		double v2n=p2.getVel().scalarProduct(vec1to2);
		double v1t=p1.getVel().scalarProduct(tangentDirection);
		double v2t=p2.getVel().scalarProduct(tangentDirection);
		double approachSpeed=v2n-v1n;
		double j=p1.mass*p2.mass*(1+e)*-approachSpeed/(p1.mass+p2.mass);
		Vect2D v1=p1.getVel().addScaled(vec1to2, -j/p1.mass);
		p1.setVel(v1);
		Vect2D v2=p2.getVel().addScaled(vec1to2, j/p2.mass);
		p2.setVel(v2);
	}
	
	public void applyForceToParticle(Vect2D force) {
		// To calculate F_net, as used in Newton's Second Law,
		// we need to accumulate all of the forces and add them up
		totalForceThisTimeStep=totalForceThisTimeStep.add(force);
	}
	
	public void resetTotalForce() {
		totalForceThisTimeStep=new Vect2D(0,0);
	}
	
}
