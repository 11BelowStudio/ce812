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

	public static int convertWorldRadiusToScreenRadius(double worldRadius){
		return Math.max(BasicPhysicsEngine.convertWorldLengthToScreenLength(worldRadius),1);
	}
	
	
	public List<BasicParticle> particles;
	public List<AnchoredBarrier> barriers;

	public List<BasicSnookerHole> holes;

	private BasicCueBall theCueBall;

	private final Controller theController;

	private final LayoutMode layout = LayoutMode.SNOOKER_TABLE;

	public static enum LayoutMode {CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE};


	final double snookerTableHeight=WORLD_HEIGHT;
	final double pocketSize=0.6;
	final double cushionDepth=0.2;
	final double cushionLength = snookerTableHeight/2-pocketSize-cushionDepth;
	final double snookerTableWidth=cushionLength+cushionDepth*2+pocketSize*2;

	private List<BasicParticle> particlesThatNeedResetting;

	private boolean allowInputs;

	private int pottedSpots;

	private int pottedStripes;

	private final Vect2D[] spotDisplayPositions = new Vect2D[]{
			new Vect2D( snookerTableWidth + 1, (3*WORLD_HEIGHT/4) + 0.5),
			new Vect2D( snookerTableWidth + 1.5, (3*WORLD_HEIGHT/4) + 0.5),
			new Vect2D( snookerTableWidth + 2, (3*WORLD_HEIGHT/4) + 0.5),
			new Vect2D( snookerTableWidth + 2.5, (3*WORLD_HEIGHT/4) + 0.5),
			new Vect2D( snookerTableWidth + 1, (3*WORLD_HEIGHT/4) + 0.2),
			new Vect2D( snookerTableWidth + 1.5, (3*WORLD_HEIGHT/4) + 0.2),
			new Vect2D( snookerTableWidth + 2, (3*WORLD_HEIGHT/4) + 0.2),
			new Vect2D( snookerTableWidth + 2.5, (3*WORLD_HEIGHT/4) + 0.2)
	};

	private final Vect2D[] stripeDisplayPositions = new Vect2D[]{
			new Vect2D( snookerTableWidth + 1, (3*WORLD_HEIGHT/4) - 0.5),
			new Vect2D( snookerTableWidth + 1.5, (3*WORLD_HEIGHT/4) - 0.5),
			new Vect2D( snookerTableWidth + 2, (3*WORLD_HEIGHT/4) - 0.5),
			new Vect2D( snookerTableWidth + 2.5, (3*WORLD_HEIGHT/4) - 0.5),
			new Vect2D( snookerTableWidth + 1, (3*WORLD_HEIGHT/4) - 0.8),
			new Vect2D( snookerTableWidth + 1.5, (3*WORLD_HEIGHT/4) - 0.8),
			new Vect2D( snookerTableWidth + 2, (3*WORLD_HEIGHT/4) - 0.8),
			new Vect2D( snookerTableWidth + 2.5, (3*WORLD_HEIGHT/4) - 0.8)
	};


	public BasicPhysicsEngine(Controller control) {

		theController = control;

		barriers = new ArrayList<>();
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<>();

		particlesThatNeedResetting = new ArrayList<>();

		pottedSpots = 0;

		pottedStripes = 0;

		holes = new ArrayList<>();


		double r=.2;

		allowInputs = true;

		
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

				final double halfWidth = snookerTableWidth/2;

				final double ballRadius = 0.2;

				theCueBall = new BasicCueBall(
						new Vect2D(halfWidth, snookerTableHeight/4), ballRadius, 1
				);
				particles.add(theCueBall);

				final double threeQuartersHeight = 3 * snookerTableHeight/4;

				final Color YELLOW = new Color(255,215,0);
				final Color BLUE = new Color(0,0,255);
				final Color RED = new Color(253,0,1);
				final Color VIOLET = new Color(75,0,129);
				final Color ORANGE = new Color(253,69,1);
				final Color GREEN = new Color(43, 43, 43);
				final Color MAROON = new Color(126, 0, 1);
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth, threeQuartersHeight), ballRadius, Color.BLACK, 1, 8, false
				));

				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth - (ballRadius * 2.2), threeQuartersHeight), ballRadius, YELLOW, 1, 1, false
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth + (ballRadius * 2.2), threeQuartersHeight), ballRadius, MAROON, 1, 7, true
				));

				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth - (ballRadius * 1.1), threeQuartersHeight - (ballRadius * 1.1)), ballRadius, VIOLET, 1, 4, true
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth + (ballRadius * 1.1), threeQuartersHeight- (ballRadius * 1.1)), ballRadius, MAROON, 1, 7, false
				));

				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth, threeQuartersHeight - (ballRadius * 2.2)), ballRadius, YELLOW, 1, 1, true
				));

				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth - (ballRadius * 1.1), threeQuartersHeight + (ballRadius * 1.1)), ballRadius, RED, 1, 3, false
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth + (ballRadius * 1.1), threeQuartersHeight + (ballRadius * 1.1)), ballRadius, BLUE, 1, 2, true
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth - (ballRadius * 3.3), threeQuartersHeight + (ballRadius * 1.1)), ballRadius, GREEN, 1, 6, true
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth + (ballRadius * 3.3), threeQuartersHeight + (ballRadius * 1.1)), ballRadius, GREEN, 1, 6, false
				));


				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth - (ballRadius * 2.2), threeQuartersHeight + (ballRadius * 2.2)), ballRadius, VIOLET, 1, 4, false
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth - (ballRadius * 4.4), threeQuartersHeight + (ballRadius * 2.2)), ballRadius, ORANGE, 1, 5, false
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth, threeQuartersHeight + (ballRadius * 2.2)), ballRadius, ORANGE, 1, 5, true
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth + (ballRadius * 2.2), threeQuartersHeight + (ballRadius * 2.2)), ballRadius, BLUE, 1, 2, false
				));
				particles.add(new BasicSnookerBall(
						new Vect2D(halfWidth + (ballRadius * 4.4), threeQuartersHeight + (ballRadius * 2.2)), ballRadius, RED, 1, 3, true
				));

				// listing edges anticlockwise, so normals point inwards
				barriers.add(
						new AnchoredBarrier_StraightLine(new Vect2D(0,0), new Vect2D(snookerTableWidth,0), Color.BLACK)
				);
				barriers.add(
						new AnchoredBarrier_StraightLine(new Vect2D(snookerTableWidth,0), new Vect2D(snookerTableWidth,snookerTableHeight), Color.BLACK)
				);
				barriers.add(
						new AnchoredBarrier_StraightLine(new Vect2D(snookerTableWidth,snookerTableHeight), new Vect2D(0,snookerTableHeight), Color.BLACK)
				);
				barriers.add(
						new AnchoredBarrier_StraightLine(new Vect2D(0,snookerTableHeight), new Vect2D(0,0), Color.BLACK)
				);



				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.25,0, cushionLength, cushionDepth);
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.75,0, cushionLength, cushionDepth);
				createCushion(barriers, snookerTableWidth/2, snookerTableHeight-cushionDepth/2, Math.PI/2, cushionLength, cushionDepth);
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.25,Math.PI, cushionLength, cushionDepth);
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.75,Math.PI, cushionLength, cushionDepth);
				createCushion(barriers, snookerTableWidth/2, cushionDepth/2, Math.PI*3/2, cushionLength, cushionDepth);

				holes.add(new BasicSnookerHole(
						new Vect2D((3*cushionDepth)/2, cushionDepth/8), 4*pocketSize/6
				));
				holes.add(new BasicSnookerHole(
						new Vect2D(0, snookerTableHeight/2), pocketSize/2
				));
				holes.add(new BasicSnookerHole(
						new Vect2D((3*cushionDepth)/2, snookerTableHeight - cushionDepth/8), 4*pocketSize/6
				));
				holes.add(new BasicSnookerHole(
						new Vect2D(snookerTableWidth - (3*cushionDepth)/2, cushionDepth/8), 4*pocketSize/6
				));
				holes.add(new BasicSnookerHole(
						new Vect2D(snookerTableWidth, snookerTableHeight/2), pocketSize/2
				));
				holes.add(new BasicSnookerHole(
						new Vect2D(snookerTableWidth - (3*cushionDepth)/2, snookerTableHeight - cushionDepth/8), 4*pocketSize/6
				));
				
				break;
			}
		}
			
			
	}
	private void createCushion(
			List<AnchoredBarrier> barriers, double centrex, double centrey,
			double orientation, double cushionLength, double cushionDepth
	) {
		// on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
		Color col=Color.WHITE;

		Vect2D p1=new Vect2D(cushionDepth/2, -cushionLength/2 - cushionDepth/2);
		Vect2D p2=new Vect2D(-cushionDepth/2, -cushionLength/2);
		Vect2D p3=new Vect2D(-cushionDepth/2, +cushionLength/2);
		Vect2D p4=new Vect2D(cushionDepth/2, cushionLength/2 + cushionDepth/2);
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

	private void reset(){
		for (BasicParticle p: particles) {
			p.reset();
		}
	}

	
	public static void main(String[] args) throws Exception {
		final JFrame theFrame = new JFrame("A somewhat janky implementation of Pool");

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
		theFrame.setResizable(false);

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

			// if inputs are allowed
			if (allowInputs) {

				// let the player hit the cue ball
				theCueBall.updateFromAction(theController.getCurrentAction());

				// if cue ball is now moving, ban inputs.
				if (theCueBall.isMoving()){
					allowInputs = false;
				}
			} else {
				// if no balls are moving
				if(particles.stream().noneMatch(BasicParticle::isMoving)){
					// check if any balls need resetting
					if (particlesThatNeedResetting.isEmpty()){
						// if none need resetting, allow inputs again
						allowInputs = true;
					} else {
						// otherwise, reset the particles that need resetting, and clear that list
						for (BasicParticle bp: particlesThatNeedResetting) {
							bp.reset();
							bp.setActive(true);
						}
						particlesThatNeedResetting.clear();
					}
				}

			}
		}


		for (BasicParticle p : particles) {
			p.update(actual_gravity, DELTA_T); // tell each particle to move
		}
		for (BasicParticle particle : particles) {
			for (AnchoredBarrier b : barriers) {
				if (b.isCircleCollidingBarrier(particle.getPos(), particle.getRadius())) {
					Vect2D bouncedVel=b.calculateVelocityAfterACollision(particle.getPos(), particle.getVel(),0.9);
					particle.setVel(bouncedVel);
				}
			}

			for (BasicSnookerHole h: holes) {

				if (h.checkIfBallIsPotted(particle)){

					particle.setActive(false);

					int particleValue = particle.getValue();
					if (particleValue == -1){
						particlesThatNeedResetting.add(particle);
					} else if (particleValue == 8){
						if (pottedSpots == 7){
							particle.setActive(true);
							particle.setPos(spotDisplayPositions[7]);
							pottedSpots = 8;
						} else if (pottedStripes == 7){
							particle.setActive(true);
							particle.setPos(stripeDisplayPositions[7]);
							pottedSpots = 8;
						} else {
							particlesThatNeedResetting.add(particle);
						}
					} else {
						if (particle.isStriped()){
							if (pottedStripes == particleValue -1){
								particle.setActive(true);
								particle.setPos(stripeDisplayPositions[particleValue-1]);
								pottedStripes++;
							} else {
								particlesThatNeedResetting.add(particle);
							}
						} else if (pottedSpots == particleValue -1){
							particle.setActive(true);
							particle.setPos(spotDisplayPositions[particleValue-1]);
							pottedSpots++;
						} else {
							particlesThatNeedResetting.add(particle);
						}
					}

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

		for (BasicSnookerHole h: holes){
			h.draw(g);
		}

		g.setColor(new Color(0,32,16));
		g.fillRect(
				convertWorldXtoScreenX(snookerTableWidth),
				convertWorldYtoScreenY(snookerTableHeight),
				SCREEN_WIDTH,
				SCREEN_HEIGHT
		);

		for (BasicParticle p : particles){
			p.draw(g);
		}
		for (AnchoredBarrier b : barriers) {
			b.draw(g);
		}
	}

	
	

}


