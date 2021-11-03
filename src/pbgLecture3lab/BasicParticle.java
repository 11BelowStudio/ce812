package pbgLecture3lab;

import java.awt.Color;
import java.awt.Graphics2D;

public class BasicParticle implements CollidaBall, Pottable {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 * 		it implements CollidaBall (which I made myself on the 22nd of October, for some extra stuff on top of the lab 2 stuff)
	 *
	 */
	public final int SCREEN_RADIUS;

	private Vect2D pos;
	private Vect2D vel;
	private final double radius;
	private final double mass;
	public final Color col;

	private final boolean improvedEuler;

	private final boolean improvedCollisionDetection;

	private final static double drag = 0.999; //1 - 1e-03;

	boolean inactive;

	private final Vect2D startPos;
	private final Vect2D startVel;

	

	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col, double mass) {
		this(new Vect2D(sx,sy), new Vect2D(vx,vy), radius, improvedEuler, col, mass, false);
	}

	public BasicParticle(double sx, double sy, double vx, double vy, double radius, boolean improvedEuler, Color col, double mass, boolean improvedCollisions){
		this(new Vect2D(sx,sy), new Vect2D(vx,vy), radius, improvedEuler, col, mass, improvedCollisions);
	}

	public BasicParticle(Vect2D pos, Vect2D vel, double radius, boolean improvedEuler, Color col, double mass, boolean improvedCollisions){
		setPos(pos);
		setVel(vel);
		startPos = pos;
		startVel = vel;
		this.radius=radius;
		this.mass=mass;
		this.improvedEuler=improvedEuler;
		this.SCREEN_RADIUS=Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
		improvedCollisionDetection = improvedCollisions;
		inactive = false;
	}

	public void reset(){
		pos = startPos;
		vel = startVel;
		inactive = false;
	}

	public void update(double gravity, double deltaT) {

		if (inactive){
			return;
		}

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
		// just stop it if the velocity is super low
		if (vel.mag() <= 0.5){
			setVel(new Vect2D());
		}
	}

	public boolean isMoving(){
		return !vel.equals(new Vect2D());
	}

	public int getValue(){
		return -1;
	}

	public boolean isStriped(){
		return false;
	}


	public void draw(Graphics2D g) {
		if (inactive){
			return;
		}
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

	public boolean isActive(){
		return !inactive;
	}

	public void setActive(boolean newActive){
		inactive = !newActive;
		if (inactive){
			setVel(new Vect2D(0,0));
		}
	}

	@Override
	public double getMass() {
		return mass;
	}

	public boolean collidesWith(CollidaBall p2) {
		if (inactive){
			return false;
		}
		if (Vect2D.minus(p2.getPos(), this.getPos()).mag() < this.getRadius() + p2.getRadius()){
			System.out.println("oh no");
		}
		Vect2D vecFrom1to2 = Vect2D.minus(p2.getPos(), getPos());
		boolean movingTowardsEachOther = Vect2D.minus(p2.getVel(), getVel()).scalarProduct(vecFrom1to2)<0;
		return vecFrom1to2.mag()<getRadius()+p2.getRadius() && movingTowardsEachOther;
	}

	/**
	 * We literally just call the CollidaBall.implementElasticCollision method
	 *
	 * edit: cancel that, the unit tests expect the less cool precise collision time detection :(
	 *
	 * @see CollidaBall#implementElasticCollision(CollidaBall, CollidaBall, double)
	 *
	 * @param p1 the first BasicParticle
	 * @param p2 the other BasicParticle
	 * @param e coefficient of restitution.
	 * @throws IllegalArgumentException if p1 and p2 don't actually collide with each other.
	 */
	public static void implementElasticCollision(BasicParticle p1, BasicParticle p2, double e) {
		if (!p1.collidesWith(p2)) throw new IllegalArgumentException();

		if (p1.inactive || p2.inactive){
			return;
		}

		if (p1.improvedCollisionDetection || p2.improvedCollisionDetection){
			CollidaBall.implementElasticCollision(p1, p2, e);
			return;
		}

		final Vect2D p1Pos = p1.getPos();

		final Vect2D p2Pos = p2.getPos();

		// calculate the AB vector (a to b) normalize it to get collision normal
		final Vect2D norm = Vect2D.minus(p2Pos, p1Pos).normalise();

		// jb = (e+1) * (Ua.norm - Ub.norm) / (1/Ma + 1/Mb)

		final double jb = ((e+1) * (p1.getVel().scalarProduct(norm) - p2.getVel().scalarProduct(norm)))/
				((1/p1.getMass()) + (1/p2.getMass()));

		// vb = ub + norm*(jb/mb)
		p2.setVel(p2.getVel().addScaled(norm, jb/p2.getMass()));

		// va = ua + norm * (-jb/ma)
		p1.setVel(p1.getVel().addScaled(norm, -jb/p1.getMass()));

		// NOTE TO PERSON READING THIS: USE THIS INSTEAD IF YOU WANT VERY NICE COLLISION HANDLING WITH PRECISE T AND SUCH
		//CollidaBall.implementElasticCollision(p1, p2, e);

	}
	

}
