package pbgLecture4lab;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BasicPhysicsEngine implements Drawable {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	
	// frame dimensions
	public static final int SCREEN_HEIGHT = 650;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final double WORLD_WIDTH=10;//metres
	public static final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static final double GRAVITY= (60.8 /(double)SCREEN_HEIGHT) * WORLD_HEIGHT;

	/*
	falls ~190px from standstill in ~2.5 seconds
	s = (u + v/2)*t
	190 = (0 + v/2)*2.5
	190 = (v/2)*2.5
	190/2.5 = 76 = v/2
	v = 76 * 2 = 152
	152 = 0 + a*2.5
	152/2.5 = 60.8 gravity (screen coords)
	screen roughly 650px high, so we shrink the screen size of this appropriately
	*/



	// sleep time between two drawn frames in milliseconds 
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=100;
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
	public List<DecorativeParticle> decorativeParticles;
	public List<AnchoredBarrier> barriers;
	public List<ElasticConnector> connectors;


	public static enum LayoutMode {
		CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE,
		/**
		 * Set LAYOUT_TO_USE to PENDULUM_DEMO to get the pendulum stuff working
		 */
		PENDULUM_DEMO,
		/**
		 * Set LAYOUT_TO_USE to THRUST_LAB to get this stuff working
		 */
		THRUST_SHIP_DEMO,
		/**
		 * Set LAYOUT_TO_USE to THRUST_GAME to get the thrust game working.
		 */
		THRUST_GAME
	};

	public static final LayoutMode LAYOUT_TO_USE = LayoutMode.THRUST_GAME;

	private final Optional<ControllableSpaceShip> mayOrMayNotHoldTheShip;

	private final List<StringObject> hudStuff;

	private final Optional<AttributeStringObject<Integer>> mayOrMayNotBeTheLifeCounter;

	private final Optional<Payload> mayOrMayNotBeThePayload;

	private Optional<ElasticConnector> mayOrMayNotBeThePayloadHolder;

	private static final int DEFAULT_LIVES = 3;

	private int lives = 3;

	// Simple pendulum attached under mouse pointer
	private static final double rollingFriction=.75;
	private static final double springConstant=1000000, springDampingConstant=1000;
	private static final double hookesLawTruncation = 1000000000;
	private static final boolean canGoSlack=false;

	private static enum GAME_STATE{
		ALIVE,
		DEAD,
		GAME_OVER,
		WON
	};

	private GAME_STATE gameState = GAME_STATE.ALIVE;


	public BasicPhysicsEngine() {
		barriers = new ArrayList<AnchoredBarrier>();
		// empty particles array, so that when a new thread starts it clears current particle state:
		particles = new ArrayList<BasicParticle>();
		connectors=new ArrayList<ElasticConnector>();

		hudStuff = new ArrayList<StringObject>();

		decorativeParticles = new ArrayList<DecorativeParticle>();

		// pinball:
		double r=.2;
		

		


		
		barriers = new ArrayList<AnchoredBarrier>();
		
		switch (LAYOUT_TO_USE) {
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
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2, WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, Color.WHITE,width/10));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width));
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
				break;
			}
			case SNOOKER_TABLE: {
				double snookerTableHeight=WORLD_HEIGHT;
				double pocketSize=0.4;
				double cushionDepth=0.3;
				double cushionLength = snookerTableHeight/2-pocketSize-cushionDepth;
				double snookerTableWidth=cushionLength+cushionDepth*2+pocketSize*2;
				
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.25,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.75,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, snookerTableHeight-cushionDepth/2, Math.PI/2, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.25,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.75,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, cushionDepth/2, Math.PI*3/2, cushionLength, cushionDepth); 
				
				
				break;
			}
			case PENDULUM_DEMO: {


				final ParticleAttachedToMousePointer mppart = new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r, true, 10000);
				final BasicParticle fixedPart = new BasicParticle(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0,0,r, true, Color.YELLOW, 10000, 1, true);
				final BasicParticle bp = new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-2,0,0, r,true, Color.BLUE, 2*4, rollingFriction, true);
				final BasicParticle offsetBP = new BasicParticle(WORLD_WIDTH/2 - (2 * Math.sqrt(2)),WORLD_HEIGHT/2-2 + (2 * Math.sqrt(2)),0,0, r,true, Color.BLUE, 2*4, rollingFriction, true);
				particles.add(mppart);
				particles.add(offsetBP);
				particles.add(bp);

				final ElasticConnector ec = new ElasticConnector(
						mppart,
						//fixedPart,
						//new BasicParticle(WORLD_WIDTH/2, WORLD_HEIGHT/2,0,0,r, true, Color.YELLOW, 10000, 1),
						//offsetBP,
						bp,
						2,
						springConstant,
						springDampingConstant,
						canGoSlack,
						Color.RED,
						hookesLawTruncation
				);
				connectors.add(ec);

				final BasicParticle chainPart2 = new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-4,0,0, r,true, Color.BLUE, 2*4, rollingFriction);
				final ElasticConnector ec2 = new ElasticConnector(
						bp,
						chainPart2,
						2,
						springConstant,
						springDampingConstant,
						canGoSlack,
						Color.RED,
						hookesLawTruncation
				);
				connectors.add(ec2);
				particles.add(chainPart2);
				//connectors.add(ec);

				for (BasicParticle p : particles) {
					p.update(GRAVITY, DELTA_T); // tell each particle to move
				}

				break;
			}
			case THRUST_SHIP_DEMO:{


				// rectangle walls:
				// anticlockwise listing
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));

				particles.add(new ControllableSpaceShip(
						new Vect2D(WORLD_WIDTH/2, WORLD_HEIGHT/2),
						new Vect2D(),
						0.1,
						true,
						1
						)
				);

				break;
			}
			case THRUST_GAME: {

				// ship and HUD elements are added later on because they're inside optionals (to allow the pendulum demo to 'work' albeit with the different gravity)

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(0, WORLD_HEIGHT/4 * 3),
								new Vect2D(WORLD_WIDTH/3, WORLD_HEIGHT/4 * 3),
								Color.WHITE,
								0.1
						)
				);
				barriers.add(
						new AnchoredBarrier_Point(
								new Vect2D(WORLD_WIDTH/3, WORLD_HEIGHT/4 * 3)
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/3, WORLD_HEIGHT/4 * 3),
								new Vect2D(WORLD_WIDTH/2, WORLD_HEIGHT/2),
								Color.LIGHT_GRAY,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/2, WORLD_HEIGHT/2))
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/2, WORLD_HEIGHT/2),
								new Vect2D(WORLD_WIDTH/16 * 3, WORLD_HEIGHT/16 * 7),
								Color.GRAY,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/16 * 3, WORLD_HEIGHT/16 * 7))
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/16 * 3, WORLD_HEIGHT/16 * 7),
								new Vect2D(WORLD_WIDTH/8, WORLD_HEIGHT/16),
								Color.GRAY,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/8, WORLD_HEIGHT/16))
				);


				// this one is the cavern floor.
				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/8, WORLD_HEIGHT/16),
								new Vect2D(WORLD_WIDTH/16*13, WORLD_HEIGHT/8),
								Color.DARK_GRAY,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/16*13, WORLD_HEIGHT/8))
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/16*13, WORLD_HEIGHT/8),
								new Vect2D(WORLD_WIDTH/12*10, WORLD_HEIGHT/2),
								Color.GRAY,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/12*10, WORLD_HEIGHT/2))
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/12*10, WORLD_HEIGHT/2),
								new Vect2D(WORLD_WIDTH/4*3, WORLD_HEIGHT/4*3),
								Color.LIGHT_GRAY,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/4*3, WORLD_HEIGHT/4*3))
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								new Vect2D(WORLD_WIDTH/4*3, WORLD_HEIGHT/4*3),
								new Vect2D(WORLD_WIDTH/2, WORLD_HEIGHT),
								Color.WHITE,
								0.1
						)
				);

				barriers.add(
						new AnchoredBarrier_Point(new Vect2D(WORLD_WIDTH/2, WORLD_HEIGHT))
				);


				// floor
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));

				// TODO: add the thing that needs to be picked up by the ship

				break;
			}
		}

		if (LAYOUT_TO_USE == LayoutMode.THRUST_GAME){
			mayOrMayNotHoldTheShip = Optional.of(
					new ControllableSpaceShip(
							new Vect2D(WORLD_WIDTH/6, WORLD_HEIGHT/8 * 7),
							new Vect2D(),
							0.1,
							true,
							1
					)
			);
			mayOrMayNotBeTheLifeCounter = Optional.of(
					new AttributeStringObject<Integer>(
							new Vect2D(WORLD_WIDTH/16 * 15, WORLD_HEIGHT/16 * 15),
							new AttributeString<Integer>("Lives: ",lives),
							StringObject.ALIGNMENT_ENUM.RIGHT_ALIGN
					)
			);

			mayOrMayNotBeThePayload = Optional.of(
					new Payload(
							new Vect2D(WORLD_WIDTH/4, WORLD_HEIGHT/16 * 3),
							0.2,
							true,
							Color.YELLOW,
							0.3,
							0.75
					)
			);
		} else {
			mayOrMayNotHoldTheShip = Optional.empty();
			mayOrMayNotBeTheLifeCounter = Optional.empty();
			mayOrMayNotBeThePayload = Optional.empty();
		}

		mayOrMayNotBeThePayloadHolder = Optional.empty();

		mayOrMayNotBeTheLifeCounter.ifPresent(hudStuff::add);
			
			
	//	
	//	particles.add(new BasicParticle(r,r,-3,12, r));
//		particles.add(new BasicParticle(0,0,4,10, r,true, Color.BLUE, includeInbuiltCollisionDetection, 2));
//		particles.add(new BasicParticle(0,0,4,10, r,false, Color.RED, includeInbuiltCollisionDetection, 2));
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
		// oops this will have concave corners so will need to fix that some time! 
	}
	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngine game = new BasicPhysicsEngine();
		final BasicView view = new BasicView(game);
		JEasyFrame frame = new JEasyFrame(view, "Basic Physics Engine");
		frame.addKeyListener(new BasicKeyListener());
		view.addMouseMotionListener(new BasicMouseListener());
		JOptionPane.showMessageDialog(frame, "press ok to start","ready up",JOptionPane.INFORMATION_MESSAGE);
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


	@Override
	public void draw(Graphics2D g) {

		for (Drawable d: decorativeParticles){
			d.draw(g);
		}

		mayOrMayNotHoldTheShip.ifPresent(
				s -> s.draw(g)
		);

		for (Drawable p : particles) {
			p.draw(g);
		}

		mayOrMayNotBeThePayload.ifPresent(
				p -> p.draw(g)
		);

		for (Drawable c : connectors) {
			c.draw(g);
		}

		mayOrMayNotBeThePayloadHolder.ifPresent(
				e -> e.draw(g)
		);

		for (Drawable b : barriers) {
			b.draw(g);
		}
		for (Drawable s: hudStuff){
			s.draw(g);
		}
	}


	public void update() {

		// TODO: win condition

		for (Updatable p : particles) {
			p.resetTotalForce();// reset to zero at start of time step, so accumulation of forces can begin.
		}

		// reset ship forces if the ship is here
		mayOrMayNotHoldTheShip.ifPresent(ControllableSpaceShip::resetTotalForce);

		mayOrMayNotBeThePayload.ifPresent(Payload::resetTotalForce);

		for (ElasticConnector ec : connectors) {
			ec.applyTensionForceToBothParticles();
		}

		// payload holder does its thing
		mayOrMayNotBeThePayloadHolder.ifPresent(ElasticConnector::applyTensionForceToBothParticles);

		for (Updatable p : particles) {
			p.update(GRAVITY, DELTA_T); // tell each particle to move
		}
		// also tell the ship to move
		//mayOrMayNotHoldTheShip.ifPresent(s1 -> s1.update(GRAVITY, DELTA_T));


		mayOrMayNotHoldTheShip.ifPresent(
				s1 -> {
					s1.update(GRAVITY, DELTA_T); // update the ship as well

					if (s1.isInactive()){
						return;
					} else if (gameState == GAME_STATE.DEAD){
						// if the gamestate was 'DEAD' but the ship is active again, go back to the 'alive' gamestate.
						gameState = GAME_STATE.ALIVE;
						// and make sure the payload has respawned.
						mayOrMayNotBeThePayload.ifPresent(Payload::respawn);
					}

					// then check the ship against the barriers
					for (AnchoredBarrier b: barriers){
						if (b.isCircleCollidingBarrier(s1.getPos(), s1.getRadius())){
							System.out.println("Oh no! The ship got hit!");
							lostALife();
							break;
						}
					}
				}
		);

		mayOrMayNotBeThePayload.ifPresent(
				payload -> {
					if (payload.isInactive()){
						return;
					}
					assert mayOrMayNotHoldTheShip.isPresent();
					final ControllableSpaceShip ship = mayOrMayNotHoldTheShip.get();
					if (ship.isInactive()){
						return;
					}

					if (payload.isTowed()) {

						payload.update(GRAVITY, DELTA_T);

						for (AnchoredBarrier b : barriers) {
							if (b.isCircleCollidingBarrier(payload.getPos(), payload.getRadius())) {
								System.out.println("Oh no, the payload got hit!");
								lostALife();
							}
						}
					} else if (BasicKeyListener.isSpacebarPressed() && ship.canThisItemBeTowed(payload)) {
						payload.setTowed(true);
						mayOrMayNotBeThePayloadHolder = Optional.of(
								new ElasticConnector(
										ship,
										payload,
										ship.getVectorTo(payload).mag(),
										springConstant,
										springDampingConstant,
										canGoSlack,
										Color.GREEN,
										hookesLawTruncation
								)
						);
					}

				}
		);

		for (CollidaBall particle : particles) {
			for (AnchoredBarrier b : barriers) {
				if (b.isCircleCollidingBarrier(particle.getPos(), particle.getRadius())) {
					Vect2D bouncedVel=b.calculateVelocityAfterACollision(particle.getPos(), particle.getVel(),1);
					particle.setVel(bouncedVel);
				}
			}
		}

		double e=0.9; // coefficient of restitution for all particle pairs
		for (int n=0;n<particles.size();n++) {
			final CollidaBall p1 = particles.get(n);

			mayOrMayNotHoldTheShip.ifPresent(
				s2 -> {
					if (p1.collidesWith(s2)){
						System.out.println("Oh no!");
						lostALife();
						// TODO probably could be a bit more elegant, but at the same time this is probably also redundant.
					}
				}
			);

			for (int m=0;m<n;m++) {// avoids double check by requiring m<n
				final CollidaBall p2 = particles.get(m);
				if (p1.collidesWith(p2)) {
					CollidaBall.implementElasticCollision(p1, p2, e);
				}
			}
		}

		for (Updatable d: decorativeParticles){
			d.resetTotalForce();
			d.update(GRAVITY, DELTA_T);
		}
		decorativeParticles.removeIf(DecorativeParticle::isInactive);
		for (int i = 0; i < decorativeParticles.size(); i++){
			final CollidaBall d1 = decorativeParticles.get(i);
			for (AnchoredBarrier b: barriers) {
				if (b.isCircleCollidingBarrier(d1.getPos(), d1.getRadius())) {
					Vect2D bouncedVel=b.calculateVelocityAfterACollision(d1.getPos(), d1.getVel(),0.5);
					d1.setVel(bouncedVel);
				}
			}
			for (int j = i + 1; j < decorativeParticles.size(); j++){
				final CollidaBall d2 = decorativeParticles.get(j);
				if (d1.collidesWith(d2)) {
					CollidaBall.implementElasticCollision(d1, d2, e);
				}
			}
		}



		for (StringObject s: hudStuff){s.update(GRAVITY, DELTA_T);}
			
	}
	
	private void lostALife(){

		if (mayOrMayNotBeThePayloadHolder.isPresent()){
			mayOrMayNotBeThePayloadHolder = Optional.empty();
		}

		assert mayOrMayNotHoldTheShip.isPresent();
		assert mayOrMayNotBeTheLifeCounter.isPresent();
		assert mayOrMayNotBeThePayload.isPresent();

		final ControllableSpaceShip theShip = mayOrMayNotHoldTheShip.get();
		final AttributeStringObject<Integer> lifeCounter = mayOrMayNotBeTheLifeCounter.get();
		final Payload thePayload = mayOrMayNotBeThePayload.get();


		theShip.gotHit();
		decorativeParticles.addAll(theShip.spawnDebris());

		if (thePayload.isTowed()){
			thePayload.deactivate();
			thePayload.setTowed(false);
			decorativeParticles.addAll(thePayload.spawnDebris());
		}

		lives -= 1;
		if (lives < 0){
			lifeCounter.getTheAttributeString().rename("CONGRATS U LOST!");
			// TODO: proper game over routine
			gameState = GAME_STATE.GAME_OVER;
		} else {
			lifeCounter.getTheAttributeString().showValue(lives);
			gameState = GAME_STATE.DEAD;
		}

	}
	

}


