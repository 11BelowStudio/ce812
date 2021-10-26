package pbgLecture2lab;


import java.awt.Color;
import java.awt.Graphics2D;

public class BasicParticle implements CollidaBall {
	private final int SCREEN_RADIUS;

	private Vect2D pos;
	private Vect2D vel;
	private final double radius;
	private final double mass;
	private final Color col;

	private final boolean improvedEuler;

	private final double drag = 1;//1 - 1e-09;

	private final Vect2D[] quarterSteps = new Vect2D[4];

	

	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col, double mass) {
		setPos(new Vect2D(sx,sy));
		setVel(new Vect2D(vx,vy));
		this.radius=radius;
		this.mass=mass;
		this.improvedEuler=improvedEuler;
		this.SCREEN_RADIUS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;

		final Vect2D quarterVel = getVel().mult(0.25);
		Vect2D nextStep = getPos().add(quarterVel);
		for (int i = 0; i < 4; i++) {
			quarterSteps[i] = nextStep;
			nextStep = nextStep.add(quarterVel);
		}

	}

	public void update(double gravity, double deltaT) {
		Vect2D acc=new Vect2D(0,-gravity);
		if (improvedEuler) {
			Vect2D pos2=getPos().addScaled(getVel(), deltaT);// in theory this could be used,e.g. if acc2 depends on pos - but in this constant gravity field it will not be relevant
			Vect2D vel2=getVel().addScaled(acc, deltaT);
			Vect2D velAv=vel2.add(getVel()).mult(0.5);
			Vect2D acc2=new Vect2D(0,-gravity);//same as acc in this simple example of constant acceleration, but that won't generally be true
			Vect2D accAv=acc2.add(acc).mult(0.5);
			setPos(getPos().addScaled(velAv, deltaT));
			setVel(getVel().addScaled(accAv, deltaT));
		} else {
			// basic Euler
			setPos(getPos().addScaled(getVel(), deltaT));
			setVel(getVel().addScaled(acc, deltaT));
		}
		setVel(getVel().mult(drag));

		// updating the quarter steps
		final Vect2D quarterVel = getVel().mult(0.25 * deltaT);
		Vect2D nextStep = getPos().add(quarterVel);
		for (int i = 0; i < 4; i++) {
			quarterSteps[i] = nextStep;
			nextStep = nextStep.add(quarterVel);
		}
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



	/**
	 * Whether or not this collidaball is colliding with the other CollidaBall
	 * @param other the other collidaball
	 * @return true if they're colliding, otherwise returns false.
	 */
	@Override
	public boolean collidesWith(CollidaBall other) {
		return (Vect2D.minus(other.getPos(), getPos()).mag() - (radius + other.getRadius()) < 0) &&
				(getVel().normalise().scalarProduct(other.getVel().normalise()) < 0);

		/*
		final double combinedRadius = radius + other.getRadius();

		// double dist = Vect2D.minus(other.getPos(), getPos()).mag()

		// If the two CollidaBalls are too far away from each other to collide
		if (Vect2D.minus(other.getPos(), getPos()).mag() > combinedRadius){

			// avoiding the pass-through problem, by checking quarter-steps
			final Vect2D[] otherSteps = other.getQuarterSteps();

			for (int i = 0; i < 3; i++) {

				//final double dist = Vect2D.minus(otherSteps[i], quarterSteps[i]).mag();

				// If they're close enough to each other at any of these quarter-steps
				if (Vect2D.minus(otherSteps[i], quarterSteps[i]).mag() <= combinedRadius){
					break; // might be colliding
				}
			}
			// If they were not close enough at any quarter step, they definitely won't collide
			return false;
		}


		// use the scalar product of the normalized collision vectors to see if they're travelling towards each other.
		// if the angle between them is greater than 90 degrees, the result will be negative.
		return getVel().normalise().scalarProduct(other.getVel().normalise()) < 0;

		 */
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

	public double getMass(){
		return mass;
	}

	@Override
	public Vect2D[] getQuarterSteps() {
		return quarterSteps.clone();
	}
}
