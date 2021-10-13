package pbgLecture1lab;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;



public class BasicPhysicsEngine implements IHaveAPosition {
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final double WORLD_WIDTH=10;//metres
	public static final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static double GRAVITY=9.8;

	// sleep time between two drawn frames in milliseconds
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
	// estimate for time between two frames in seconds 
	public static final double DELTA_T = DELAY / 1000.0 / NUM_EULER_UPDATES_PER_SCREEN_REFRESH ;

	private double localGravity = GRAVITY;

	private double localDelta = DELTA_T;

	private int localDelay = DELAY;
	
	
	public static int convertWorldXtoScreenX(double worldX) {
		return (int) (worldX/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static int convertWorldYtoScreenY(double worldY) {
		// minus sign in here is because screen coordinates are upside down.
		return (int) (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT));
	}
	public static int convertWorldLengthToScreenLength(double worldLength) {
		return (int) (worldLength/WORLD_WIDTH*SCREEN_WIDTH);
	}
	
	public List<BasicParticle> particles;

	private BasicParticle theParticle;

	public static final boolean includeInbuiltCollisionDetection=false;

	public BasicPhysicsEngine() {
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<>();
		double r=.2;
		boolean improvedEuler = false;
		theParticle = new BasicParticle(r,r,1.5,3, r, improvedEuler, Color.GREEN);
		particles.add(theParticle);
		//particles.add(new BasicParticle(r,r,1.5,3, r, improvedEuler, Color.GREEN));
	}

	/**
	 * A constructor for the BasicPhysicsEngine that launches a ball at a given speed and angle
	 * @param launchSpeed the speed at which the ball is to be launched at
	 * @param launchAngle the angle (in degrees) at which the ball is to be launched at
	 */
	public BasicPhysicsEngine(double launchSpeed, double launchAngle){

		final Vect2D ballVect = Vect2D.POLAR_VECT(Math.toRadians(launchAngle), launchSpeed);

		System.out.println(localDelta);

		particles = new ArrayList<>();
		double r=.2;
		boolean improvedEuler = false;

		theParticle = new BasicParticle(r,r, ballVect.x, ballVect.y, r, improvedEuler, Color.GREEN);

		particles.add(theParticle);
	}

	/**
	 * A constructor for the BasicPhysicsEngine that launches a ball at a given speed and angle, with given gravity and deltaT
	 * @param launchSpeed the speed at which the ball is to be launched at
	 * @param launchAngle the angle (in degrees) at which the ball is to be launched at
	 * @param overrideGravity gravity value to use instead
	 * @param overrideDelta deltaT value to use instead
	 */
	public BasicPhysicsEngine(double launchSpeed, double launchAngle, double overrideGravity, double overrideDelta){

		this(launchSpeed, launchAngle);

		localGravity = overrideGravity;

		localDelta = overrideDelta;

		localDelay = (int)(localDelta * NUM_EULER_UPDATES_PER_SCREEN_REFRESH * 1000.00);
		System.out.println(localDelay);
	}

	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngine game = new BasicPhysicsEngine();
		final BasicView view = new BasicView(game);
		new JEasyFrame(view, "Basic Physics Engine");
		while (true) {
			for (int i=0;i<BasicPhysicsEngine.NUM_EULER_UPDATES_PER_SCREEN_REFRESH;i++) {
				game.update();
			}
			view.repaint();
			Toolkit.getDefaultToolkit().sync();
			try {
				Thread.sleep(BasicPhysicsEngine.DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
	public void update() {
		for (BasicParticle p : particles) {
			p.update(localGravity, localDelta); // tell each particle to move
		}
	}

	/**
	 * Renders the game (rectangle under the ball area, then draws the particles)
	 * @param g the graphics2D thing we're using for the drawing
	 */
	public void draw(Graphics2D g){

		g.setColor(Color.RED);
		g.fillRect(
				BasicPhysicsEngine.convertWorldXtoScreenX(0),
				BasicPhysicsEngine.convertWorldYtoScreenY(0),
				BasicPhysicsEngine.convertWorldXtoScreenX(1000),
				BasicPhysicsEngine.convertWorldYtoScreenY(-1)
		);
		for (BasicParticle p : particles) {
			p.draw(g);
		}

	}

	/**
	 * obtains the gravity value we're using
	 * @return the gravity we're using
	 */
	public double getLocalGravity(){
		return localGravity;
	}

	/**
	 * obtains the delta value we're using
	 * @return the delta we're using.
	 */
	public double getLocalDelta(){
		return localDelta;
	}

	public int getLocalDelay(){
		return localDelay;
	}


	/**
	 * Returns the position of the ball (minus its radius)
	 * @return position of ball (minus radius), or a new Vect2D if no ball exists.
	 */
	@Override
	public Vect2D getPosition() {
		if (theParticle == null){
			return new Vect2D();
		} else{
			double radius = theParticle.getRadius();
			return theParticle.getPosition().add(new Vect2D(-radius, -radius));
		}
	}
}


