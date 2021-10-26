package pbgLecture2lab;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class BasicPhysicsEngine {
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final double WORLD_WIDTH=10;//metres
	public static final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static final double GRAVITY=9.8;

	// sleep time between two drawn frames in milliseconds
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
	// estimate for time between two frames in seconds 
	public static final double DELTA_T = DELAY / 1000.0 / NUM_EULER_UPDATES_PER_SCREEN_REFRESH ;
	
	
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
	public List<AnchoredBarrier> barriers;

	public List<Flipper> flippers;
	
	public static enum LayoutMode {CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE};

	/**
	 * Set this to true if you want to see lines connecting each particle.
	 * They're blue if the particles are far enough away from each other, red if they're intersecting.
	 */
	private final boolean SHOWING_LINES_CONNECTING_PARTICLES_TO_SEE_IF_THEYRE_TOO_CLOSE = true;

	private final Controller control;

	public BasicPhysicsEngine(Controller ctrl) {
		control = ctrl;

		barriers = new ArrayList<>();
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<>();

		flippers = new ArrayList<>();
		
		//LayoutMode layout=LayoutMode.CONVEX_ARENA;
		//LayoutMode layout = LayoutMode.CONCAVE_ARENA;
		//LayoutMode layout = LayoutMode.CONVEX_ARENA_WITH_CURVE;
		LayoutMode layout = LayoutMode.PINBALL_ARENA;

		if (layout==LayoutMode.PINBALL_ARENA) {
			final double pinballradius=0.2;
			particles.add(new BasicParticle(
					WORLD_WIDTH-pinballradius*1.01, WORLD_HEIGHT/2,-1,15, pinballradius,false, Color.RED, 2));
			particles.add(new BasicParticle(
					(WORLD_WIDTH/2),WORLD_HEIGHT/2,2,15, pinballradius * 2,false, Color.BLUE, 4));
			particles.add(new BasicParticle(
					(WORLD_WIDTH/2),WORLD_HEIGHT/2 - 1,2,15, pinballradius * 2,false, Color.CYAN, 4));
			particles.add(new BasicParticle(
					(WORLD_WIDTH/4),WORLD_HEIGHT/2,1,15, pinballradius * 2.5,false, Color.GREEN, 5));
		} else {
			double r=.2;
			particles.add(new BasicParticle(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-2,-3*2,9.7*2, 0.4,true, Color.BLUE, 2*4));
			
		}

		
		barriers = new ArrayList<>();
		
		switch (layout) {
			case RECTANGLE: {
				// rectangle walls:
				// anticlockwise listing
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				break;
			}
			case CONVEX_ARENA: {
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				break;
			}
			case CONCAVE_ARENA: {
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				double width=WORLD_HEIGHT/20;
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT*2/3, WORLD_WIDTH/2, WORLD_HEIGHT*1/2, Color.WHITE,width/10));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2, WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, Color.WHITE,width/10));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, 0, WORLD_HEIGHT*2/3-width, Color.WHITE,width/10));
				break;
			}
			case CONVEX_ARENA_WITH_CURVE: {
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 180.0,true, Color.WHITE));
				break;
			}
			case PINBALL_ARENA: {
				// simple pinball board
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 200.0,true, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT*3/4, WORLD_WIDTH/15, -0.0, 360.0,false, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*1/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0, 360.0,false, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*2/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0, 360.0,false, Color.WHITE));
				
				flippers.add(
						new Flipper(
								new Vect2D(WORLD_WIDTH/8, WORLD_HEIGHT/4.5),
								new Vect2D(WORLD_WIDTH/2.5, WORLD_HEIGHT/5.5),
								0.5,
								0.25,
								false
						)
				);
				flippers.add(
						new Flipper(
								new Vect2D(WORLD_WIDTH * 7/8, WORLD_HEIGHT/4.5),
								new Vect2D(WORLD_WIDTH - (WORLD_WIDTH/2.5), WORLD_HEIGHT/5.5),
								0.5,
								0.25,
								true
						)
				);
				
				break;
			}
		}
			
			
	}
	public static void main(String[] args) throws Exception {

		final JFrame theFrame = new JFrame("Barrier moment.");
		final Controller control = new Controller();
		theFrame.addKeyListener(control);
		theFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		final BasicPhysicsEngine game = new BasicPhysicsEngine(control);
		final BasicView view = new BasicView(game);
		theFrame.getContentPane().add(BorderLayout.CENTER, view);
		theFrame.pack();
		theFrame.setVisible(true);
		theFrame.repaint();

		//new JEasyFrame(view, "Basic Physics Engine");
		game.startThread(view);
	}
	private void startThread(final BasicView view) throws InterruptedException {
		final BasicPhysicsEngine game=this;
		while (true) {
			for (int i=0;i<NUM_EULER_UPDATES_PER_SCREEN_REFRESH;i++) {
				game.update();
			}
			view.repaint();
			Toolkit.getDefaultToolkit().sync();

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
	


	public void update() {
		ActionView currentAction = control.getAction();
		for (BasicParticle p : particles) {
			p.update(GRAVITY, DELTA_T); // tell each particle to move
		}
		for (Flipper f: flippers){
			f.update(DELTA_T, currentAction);
		}
		for (int i = 0; i < particles.size(); i++) {
			final BasicParticle particle = particles.get(i);
			for (AnchoredBarrier b : barriers) {
				if (b.isCircleCollidingBarrier(particle.getPos(), particle.getRadius())) {
					Vect2D bouncedVel=b.calculateVelocityAfterACollision(particle.getPos(), particle.getVel(), 0.9);// 1.0);
					particle.setVel(bouncedVel);
				}
			}
			for (Flipper f: flippers){
				if (f.isCircleCollidingBarrier(particle.getPos(), particle.getRadius())) {
					Vect2D bouncedVel=f.calculateVelocityAfterACollision(particle.getPos(), particle.getVel(), 0.9);// 1.0);
					particle.setVel(bouncedVel);
				}
			}
			for (int j = i + 1; j < particles.size(); j++){
				CollidaBall other = particles.get(j);
				if (other.collidesWith(particle, DELTA_T)){//, DELTA_T)){
					CollidaBall.implementElasticCollision(other, particle, 0.9);
				}
			}

		}
		/*
		for (int i = 0; i < particles.size(); i++) {
			final BasicParticle particle = particles.get(i);
			for (int j = i + 1; j < particles.size(); j++){
				CollidaBall other = particles.get(j);
				if (other.collidesWith(particle)){
					CollidaBall.implementElasticCollision(other, particle, 1.0);
				}
			}
		}
		 */
	}

	public void draw(Graphics2D g) {
		for (BasicParticle p : particles){
			p.draw(g);
		}
		for (AnchoredBarrier b : barriers) {
			b.draw(g);
		}
		for (Flipper f: flippers){
			f.draw(g);
		}
		if (SHOWING_LINES_CONNECTING_PARTICLES_TO_SEE_IF_THEYRE_TOO_CLOSE) {
			for (int i = 0; i < particles.size(); i++) {
				Vect2D p1Pos = particles.get(i).getPos();
				double p1rad = particles.get(i).getRadius();
				for (int j = i + 1; j < particles.size(); j++) {
					Vect2D p2Pos = particles.get(j).getPos();
					if (Vect2D.minus(p2Pos, p1Pos).mag() < p1rad + particles.get(j).getRadius()) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.CYAN);
					}
					g.drawLine(
							BasicPhysicsEngine.convertWorldXtoScreenX(p1Pos.x),
							BasicPhysicsEngine.convertWorldYtoScreenY(p1Pos.y),
							BasicPhysicsEngine.convertWorldXtoScreenX(p2Pos.x),
							BasicPhysicsEngine.convertWorldYtoScreenY(p2Pos.y)
					);
				}
			}
		}
	}
	
	
	

}


