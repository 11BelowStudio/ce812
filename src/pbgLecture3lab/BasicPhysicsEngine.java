package pbgLecture3lab;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class BasicPhysicsEngine {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 * 		convertScreenXToWorldX and Y functions implemented
	 * 		Now responsible for drawing itself (draw function)
	 */
	
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final double WORLD_WIDTH=10;//metres
	public static final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static final double DEFAULT_GRAVITY = 9.8;

	private final double actual_gravity;

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
	public static double convertScreenXtoWorldX(int screenX) {

		return ((double)screenX)/SCREEN_WIDTH * WORLD_WIDTH;
		// to get this to work you need to program the inverse function to convertWorldXtoScreenX
		// this means rearranging the equation z=(worldX/WORLD_WIDTH*SCREEN_WIDTH) to make worldX the subject, 
		// and then returning worldX
	}
	public static double convertScreenYtoWorldY(int screenY) {

		return (((double)SCREEN_HEIGHT - screenY)/(double)SCREEN_HEIGHT) * WORLD_HEIGHT;
		// to get this to work you need to program the inverse function to convertWorldYtoScreenY
		// this means rearranging the equation z= (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT)) to make 
		// worldY the subject, and then returning worldY
	}
	
	
	
	public List<BasicParticle> particles;
	public List<AnchoredBarrier> barriers;

	private BasicCueBall theCueBall;

	private final Controller theController;

	private final LayoutMode layout = LayoutMode.SNOOKER_TABLE;

	public static enum LayoutMode {CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE};


	public BasicPhysicsEngine(Controller control) {

		theController = control;

		barriers = new ArrayList<>();
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<>();


		double r=.2;

		
		particles.add(new BasicParticle(r+WORLD_WIDTH/2-1,WORLD_HEIGHT/2,3.5,5.2, r,true, Color.RED, 2));
		particles.add(new BasicParticle(r+WORLD_WIDTH/2-2,WORLD_HEIGHT/2,-3.5,5.2, r*2,true, Color.PINK, 4));		
		particles.add(new BasicParticle(r+WORLD_WIDTH/2,WORLD_HEIGHT/2,3.5,-5.2, r*3,true, Color.BLUE, 10));
		
		
		if (layout == LayoutMode.SNOOKER_TABLE){
			actual_gravity = 0;
		} else{
			actual_gravity = 9.8;
		}
		
		
		

		
		barriers = new ArrayList<AnchoredBarrier>();
		
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


				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT/2));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, (WORLD_HEIGHT/2)-width));
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
				break;
			}
			case SNOOKER_TABLE: {


				particles.clear();


				double snookerTableHeight=WORLD_HEIGHT;
				double pocketSize=0.6;
				double cushionDepth=0.45;
				double cushionLength = snookerTableHeight/2-pocketSize-cushionDepth;
				double snookerTableWidth=cushionLength+cushionDepth*2+pocketSize*2;

				theCueBall = new BasicCueBall(
						new Vect2D(snookerTableWidth/2, snookerTableHeight/2), 0.2, true, 1
				);

				particles.add(theCueBall);

				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.25,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.75,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, snookerTableHeight-cushionDepth/2, Math.PI/2, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.25,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.75,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, cushionDepth/2, Math.PI*3/2, cushionLength, cushionDepth); 
				
				
				break;
			}
		}
			
			
	}
	private void createCushion(List<AnchoredBarrier> barriers, double centrex, double centrey, double orientation, double cushionLength, double cushionDepth) {
		// on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
		Color col=Color.WHITE;
		Vect2D p1=new Vect2D(cushionDepth/2, -cushionLength/2-cushionDepth/2);
		Vect2D p2=new Vect2D(-cushionDepth/2, -cushionLength/2);
		Vect2D p3=new Vect2D(-cushionDepth/2, +cushionLength/2);
		Vect2D p4=new Vect2D(cushionDepth/2, cushionLength/2+cushionDepth/2);
		p1=p1.rotate(orientation);
		p2=p2.rotate(orientation);
		p3=p3.rotate(orientation);
		p4=p4.rotate(orientation);
		// we are being careful here to list edges in an anticlockwise manner, so that normals point inwards!
		barriers.add(new AnchoredBarrier_StraightLine(centrex+p1.x, centrey+p1.y, centrex+p2.x, centrey+p2.y, col));
		barriers.add(new AnchoredBarrier_StraightLine(centrex+p2.x, centrey+p2.y, centrex+p3.x, centrey+p3.y, col));
		barriers.add(new AnchoredBarrier_StraightLine(centrex+p3.x, centrey+p3.y, centrex+p4.x, centrey+p4.y, col));

		barriers.add(new AnchoredBarrier_Point(centrex+p1.x, centrey+p1.y));
		barriers.add(new AnchoredBarrier_Point(centrex+p2.x, centrey+p2.y));
		barriers.add(new AnchoredBarrier_Point(centrex+p3.x, centrey+p3.y));
		barriers.add(new AnchoredBarrier_Point(centrex+p4.x, centrey+p4.y));
		// oops this will have concave corners so will need to fix that some time!

	}
	
	public static void main(String[] args) throws Exception {
		final JFrame theFrame = new JFrame("screw JEasyFrame, all my homies hate JEasyFrame");

		// setting up any of the controllers that I prefer using
		//final Controller control = new Controller();
		//theFrame.addKeyListener(control);
		final Controller control = new Controller();
		//theFrame.addMouseListener(control);
		//theFrame.addMouseMotionListener(control);


		theFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		final BasicPhysicsEngine game = new BasicPhysicsEngine(control);
		final BasicView view = new BasicView(game);
		view.addMouseListener(control);
		view.addMouseMotionListener(control);
		//view.addMouseMotionListener(new BasicMouseListener());
		theFrame.getContentPane().add(BorderLayout.CENTER, view);
		theFrame.pack();
		theFrame.setVisible(true);
		theFrame.repaint();

		JOptionPane.showMessageDialog(theFrame,
				"press ok to start",
				"insert funny message here",
				JOptionPane.INFORMATION_MESSAGE
		);
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

		if (layout == LayoutMode.SNOOKER_TABLE){
			theCueBall.updateFromAction(theController.getCurrentAction());
		}


		for (BasicParticle p : particles) {
			p.update(actual_gravity, DELTA_T); // tell each particle to move
		}
		for (BasicParticle particle : particles) {
			for (AnchoredBarrier b : barriers) {
				if (b.isCircleCollidingBarrier(particle.getPos(), particle.getRadius())) {
					Vect2D bouncedVel=b.calculateVelocityAfterACollision(particle.getPos(), particle.getVel(),1.0);
					particle.setVel(bouncedVel);
				}
			}
		}
		double e=0.9; // coefficient of restitution for all particle pairs
		for (int n=0;n<particles.size();n++) {
			BasicParticle p1 = particles.get(n);

			for (int m=0;m<n;m++) {// avoids double check by requiring m<n
				BasicParticle p2 = particles.get(m);
				if (p1.collidesWith(p2)) {
					BasicParticle.implementElasticCollision(p1, p2, e);
				}
			}
		}
	}


	public void draw(Graphics2D g){
		for (BasicParticle p : particles){
			p.draw(g);
		}
		for (AnchoredBarrier b : barriers) {
			b.draw(g);
		}
	}

	
	

}


