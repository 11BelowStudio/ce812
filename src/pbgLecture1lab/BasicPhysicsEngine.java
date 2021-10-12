package pbgLecture1lab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;



public class BasicPhysicsEngine {
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

	public static final boolean includeInbuiltCollisionDetection=false;


	public BasicPhysicsEngine() {
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<BasicParticle>();
		double r=.2;
		boolean improvedEuler = false;
		particles.add(new BasicParticle(r,r,1.5,3, r,improvedEuler, Color.GREEN));
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
			p.update(); // tell each particle to move
		}
	}
	
	
	

}


