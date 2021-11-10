package pbgLecture5lab_wrapperForJBox2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;


public class BasicPhysicsEngineUsingBox2D implements Drawable {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final float WORLD_WIDTH=10;//metres
	public static final float WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static final float GRAVITY=9.8f;
	public static final boolean ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN=false;// There's a load of code in basic mouse listener to process this, if you set it to true

	/**
	 * Box2D container for all bodies and barriers
	 */
	public static World world;

	// sleep time between two drawn frames in milliseconds 
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
	// estimate for time between two frames in seconds 
	public static final float DELTA_T = DELAY / 1000.0f;
	
	
	public static int convertWorldXtoScreenX(float worldX) {
		return (int) (worldX/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static int convertWorldYtoScreenY(float worldY) {
		// minus sign in here is because screen coordinates are upside down.
		return (int) (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT));
	}
	public static float convertWorldLengthToScreenLength(float worldLength) {
		return (worldLength/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static float convertScreenXtoWorldX(int screenX) {
		return screenX*WORLD_WIDTH/SCREEN_WIDTH;
	}
	public static float convertScreenYtoWorldY(int screenY) {
		return (SCREEN_HEIGHT-screenY)*WORLD_HEIGHT/SCREEN_HEIGHT;
	}

	
	
	
	public List<BasicParticle> particles;
	public List<BasicPolygon> polygons;
	public List<AnchoredBarrier> barriers;
	public List<ElasticConnector> connectors;
	public static MouseJoint mouseJointDef;

	private static final Vec2 PARTICLE_LAUNCH_LOCATION = new Vec2(WORLD_WIDTH/8, WORLD_HEIGHT/3);

	// Simple pendulum attached under mouse pointer
	private static final double rollingFriction=.75;
	private static final double springConstant=1000000, springDampingConstant=1000;
	private static final double hookesLawTruncation = 1000000000;
	private static final boolean canGoSlack=false;

	private boolean toppled_all_blocks = false;

	private static final Font victory_text_font = new Font(Font.SANS_SERIF, Font.BOLD,16);

	private static final String instructions_words = "click to shoot a ball at the things!";

	private boolean not_clicked_yet = true;

	private static final String victory_words = "congartulation, you're winner!";



	public static enum LayoutMode {
		CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE, PENDULUM_DEMO, PENDULUM_DEMO_2, PENDULUM_DEMO_3, PENDULUM_DEMO_4, SPACESHIP_DEMO, SPACESHIP_DEMO_2,

		/**
		 * The LayoutMode for the block-related game for lab 5
		 */
		BLOCK_GAME
	};

	/**
	 * Use this to select the layout mode for the current demo.
	 */
	private static final LayoutMode layout = LayoutMode.BLOCK_GAME;


	public BasicPhysicsEngineUsingBox2D() {

		world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
		world.setContinuousPhysics(true);

		particles = new ArrayList<BasicParticle>();
		polygons = new ArrayList<BasicPolygon>();
		barriers = new ArrayList<AnchoredBarrier>();
		connectors=new ArrayList<ElasticConnector>();
		// pinball:
		float linearDragForce=.02f;
		float r=.3f;
//			rectangles.add(new BasicRectangle(WORLD_WIDTH/2,WORLD_HEIGHT*3/4,  -4,3, r*4, r*8, 0, 5,  false, Color.BLUE, 1,0.5));
//			public BasicRectangle(double sx, double sy, double vx, double vy, double width, double height, double orientation, double angularVeloctiy, boolean improvedEuler, Color col, double mass) {

		float s=1.2f;

		if (layout != LayoutMode.BLOCK_GAME) {
			particles.add(new BasicParticle(WORLD_WIDTH / 2 - 2, WORLD_HEIGHT / 2 - 2.2f, 1.5f * s, 1.2f * s, r, Color.GREEN, 1, linearDragForce));
			polygons.add(new BasicPolygon(WORLD_WIDTH / 2 - 2, WORLD_HEIGHT / 2 + 1.4f, -1.5f * s, 1.2f * s, r * 2, Color.RED, 1, linearDragForce, 3));
			polygons.add(new BasicPolygon(WORLD_WIDTH / 2 - 2, WORLD_HEIGHT / 2 + 1.4f, -1.5f * s, 1.2f * s, r * 4, Color.RED, 1, linearDragForce, 3));
			polygons.add(new BasicPolygon(WORLD_WIDTH / 2 - 2, WORLD_HEIGHT / 2 + 1.3f, -1.2f * s, 1.2f * s, r * 2, Color.WHITE, 1, linearDragForce, 5));
			polygons.add(new BasicPolygon(WORLD_WIDTH / 2 - 2, WORLD_HEIGHT / 2 + 1.3f, 1.2f * s, 1.2f * s, r * 2, Color.YELLOW, 1, linearDragForce, 4));
		}
		
//		particles.add(new BasicParticle(WORLD_WIDTH/2+2,WORLD_HEIGHT/2+2f,-1.2f*s,-1.4f*s, r,Color.BLUE, 2, 0));
//		particles.add(new BasicParticle(3*r+WORLD_WIDTH/2,WORLD_HEIGHT/2,2,6.7f, r*3,Color.BLUE, 90, 0));
//		particles.add(new BasicParticle(r+WORLD_WIDTH/2,WORLD_HEIGHT/2,3.5f,5.2f, r,Color.RED, 2, 0));
		
//		// Example revolute joint creation:
//		BasicPolygon p1 = polygons.get(0);
//		BasicParticle p2 = particles.get(0);
//		RevoluteJointDef jointDef=new RevoluteJointDef();
//		jointDef.bodyA = p1.body;
//		jointDef.bodyB = p2.body;
//		jointDef.collideConnected = false;  // this means we don't want these two connected bodies to have collision detection.
//		jointDef.localAnchorA=new Vec2(0.2f,0.2f);
//		jointDef.localAnchorB=new Vec2(-0.2f,-0.2f);
//		world.createJoint(jointDef);
//		

		

		if (layout == LayoutMode.SPACESHIP_DEMO) {
			// spaceship flying under gravity
			particles.add(new ControllableSpaceShip(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-2,0f,2f, r,true, 2*4));
		} else if (layout == LayoutMode.SPACESHIP_DEMO_2) {
			// spaceship flying with dangling pendulum
			double springConstant=1000000, springDampingConstant=1000;
			double hookesLawTruncation=0.2;
			particles.add(new ControllableSpaceShip(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-2,0f,2f, r,true, 2*4));
			particles.add(new BasicParticle(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-4,-3f,9.7f, r,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*6, springConstant, springDampingConstant, false, Color.WHITE, hookesLawTruncation));
		} else if (layout == LayoutMode.PENDULUM_DEMO_2) {
			// Simple pendulum attached under mouse pointer
			linearDragForce=.5f;
			double springConstant=10000, springDampingConstant=10;
			Double hookesLawTruncation=null;
			boolean canGoSlack=false;
			particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r, 10000));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-2,0,0, r,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*10, springConstant,springDampingConstant, canGoSlack, Color.WHITE, hookesLawTruncation));
		} else if (layout == LayoutMode.PENDULUM_DEMO_3) {
			// 4 link chain
			linearDragForce=1;
			double springConstant=1000000, springDampingConstant=1000;
			Double hookesLawTruncation=null;//0.2;//null;//0.2;
			particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r, 10000));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-2,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-4,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(1), particles.get(2), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-6,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(2), particles.get(3), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-7,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(3), particles.get(4), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
		} else if (layout == LayoutMode.PENDULUM_DEMO_4) {
			// rectangle box
			linearDragForce=.1f;
			double springConstant=1000000, springDampingConstant=1000;
			double hookesLawTruncation=0.2;
//				particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r/2, true, 10000));				
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-1,24,34, r/2,Color.BLUE, 2*4, linearDragForce));
			particles.add(new BasicParticle(WORLD_WIDTH/2+0.1f,WORLD_HEIGHT/2-2,0f,0f, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2+0.1f,WORLD_HEIGHT/2-4,-14,14, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(1), particles.get(2), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-6,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(2), particles.get(3), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			connectors.add(new ElasticConnector(particles.get(3), particles.get(0), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			connectors.add(new ElasticConnector(particles.get(2), particles.get(0), r*6*Math.sqrt(6), springConstant,springDampingConstant, false, Color.GRAY, hookesLawTruncation));
			connectors.add(new ElasticConnector(particles.get(1), particles.get(3), r*6*Math.sqrt(6), springConstant,springDampingConstant, false, Color.GRAY, hookesLawTruncation));
		}
		
		
		
		if (layout == LayoutMode.CONVEX_ARENA || layout == LayoutMode.CONCAVE_ARENA || layout == LayoutMode.CONVEX_ARENA_WITH_CURVE) {
			Random x=new Random(3);
			for (int i=0;i<40;i++) {
				particles.add(new BasicParticle((0.5f+0.3f*(x.nextFloat()-.5f))*WORLD_HEIGHT,(0.5f+0.3f*(x.nextFloat()-.5f))*WORLD_WIDTH,0f,0f, r/2,new Color(x.nextFloat(), x.nextFloat(), x.nextFloat()), .2f, linearDragForce));				
			}
		}
		
		//particles.add(new BasicParticle(r,r,5,12, r,false, Color.GRAY, includeInbuiltCollisionDetection));

		
		barriers = new ArrayList<AnchoredBarrier>();
		
		switch (layout) {
			case RECTANGLE: {
				// rectangle walls:
				// anticlockwise listing
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				break;
			}
			case CONVEX_ARENA: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				break;
			}
			case CONCAVE_ARENA: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0f,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				float width=WORLD_HEIGHT/20;
				barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT*2/3, WORLD_WIDTH/2, WORLD_HEIGHT*1/2, Color.WHITE));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2, WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, Color.WHITE));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, 0, WORLD_HEIGHT*2/3-width, Color.WHITE));
				break;
			}
			case CONVEX_ARENA_WITH_CURVE: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0f, 180.0f,Color.WHITE));
				break;
			}
			case PINBALL_ARENA: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				// simple pinball board
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0f, 200.0f,Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT*3/4, WORLD_WIDTH/15, -0.0f, 360.0f,Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*1/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0f, 360.0f,Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*2/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0f, 360.0f,Color.WHITE));
				break;
			}
			case SNOOKER_TABLE: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				float snookerTableHeight=WORLD_HEIGHT;
				float pocketSize=0.4f;
				float cushionDepth=0.3f;
				float cushionLength = snookerTableHeight/2-pocketSize-cushionDepth;
				float snookerTableWidth=cushionLength+cushionDepth*2+pocketSize*2;
				
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.25f,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.75f,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, snookerTableHeight-cushionDepth/2, Math.PI/2, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.25f,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.75f,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, cushionDepth/2, Math.PI*3/2, cushionLength, cushionDepth); 
				
				
				break;
			}
			case PENDULUM_DEMO: {


				final ParticleAttachedToMousePointer mppart = new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r, 10000);
				final BasicParticle fixedPart = new BasicParticle(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0,0,r, Color.YELLOW, 10000, 1);
				final BasicParticle bp = new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-2,0,0, r, Color.BLUE, 2*4, (float) rollingFriction);
				final BasicParticle offsetBP = new BasicParticle(
						(float)(WORLD_WIDTH/2 - (2 * Math.sqrt(2))),(float)(WORLD_HEIGHT/2-2 + (2 * Math.sqrt(2))),0,0, r, Color.BLUE, 2*4, (float) rollingFriction);
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

				final BasicParticle chainPart2 = new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-4,0,0, r,Color.BLUE, 2*4, (float)rollingFriction);
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

				/*
				for (BasicParticle p : particles) {
					p.update(GRAVITY, DELTA_T); // tell each particle to move
				}

				 */

				break;
			}
			case BLOCK_GAME:{

				// floor
				barriers.add(
						new AnchoredBarrier_StraightLine(
								WORLD_WIDTH + 10, 0.1f, -10, 0.1f, Color.WHITE
						)
				);

				// outer box

				barriers.add(
						new AnchoredBarrier_StraightLine(
								-10, 0.1f, -10, WORLD_HEIGHT, Color.WHITE
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								-10, WORLD_HEIGHT, WORLD_WIDTH+10, WORLD_HEIGHT, Color.WHITE
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								WORLD_WIDTH+10, WORLD_HEIGHT, WORLD_WIDTH+10, 0.1f, Color.WHITE
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								WORLD_WIDTH, 0.1f, WORLD_WIDTH, 1.5f, Color.LIGHT_GRAY
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								WORLD_WIDTH, 1.5f, 0, 0.1f, Color.LIGHT_GRAY
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								5.5f * WORLD_WIDTH/6, 1.4f, 5.5f * WORLD_WIDTH/6, 1.5f, Color.GRAY
						)
				);



				barriers.add(
						new AnchoredBarrier_StraightLine(
								5.5f * WORLD_WIDTH/6, 1.5f, WORLD_WIDTH/3, 1.5f, Color.GRAY
						)
				);

				barriers.add(
						new AnchoredBarrier_StraightLine(
								WORLD_WIDTH/3, 1.5f, WORLD_WIDTH/3, 0.6f, Color.GRAY
						)
				);



				Path2D.Float rec3by9 = new Path2D.Float(new Rectangle2D.Float(-0.15f,-0.15f,0.3f,0.9f));

				Path2D.Float rec12by3 = new Path2D.Float(new Rectangle2D.Float(-0.6f,-0.15f,1.2f, 0.3f));

				Path2D.Float fp = new Path2D.Float(new Rectangle2D.Float(-0.15f,-0.2f,0.3f,0.4f));

				/*
				polygons.add(
						new BasicPolygon(WORLD_WIDTH/2 + 0.6f, WORLD_HEIGHT/2, 0, 0, 1, Color.RED, 1, (float) 0, rec3by9, 4)
				);

				polygons.add(
						new BasicPolygon(WORLD_WIDTH/2 - 0.6f, WORLD_HEIGHT/2, 0, 0, 1, Color.RED, 1, (float) 0, rec3by9, 4)
				);

				polygons.add(
						new BasicPolygon(WORLD_WIDTH/2, WORLD_HEIGHT/2 + 1, 0, 0, 1, Color.RED, 1, (float) 0, rec12by3, 4)
				);

				 */

				polygons.addAll(
						BasicPolygon.RECTANGLE_ARCH_FACTORY(
								(float) 5.25 * WORLD_HEIGHT/8,
								1.5f,
								Color.GRAY,
								Color.LIGHT_GRAY,
								10,
								0.1f,
								1.25f,
								1.25f,
								0.5f,
								0.5f,
								3
						)
				);

				polygons.addAll(
						BasicPolygon.RECTANGLE_ARCH_FACTORY(
								(float) 5.25 * WORLD_HEIGHT/8,
								2.75f,
								Color.GRAY,
								Color.LIGHT_GRAY,
								7.5f,
								0.1f,
								1.125f,
								1.125f,
								0.375f,
								0.375f,
								2
						)
				);

				polygons.addAll(
						BasicPolygon.RECTANGLE_ARCH_FACTORY(
								(float) 5.25 * WORLD_HEIGHT/8,
								3.875f,
								Color.GRAY,
								Color.LIGHT_GRAY,
								5,
								0.1f,
								1.0f,
								1.0f,
								0.25f,
								0.25f,
								1
						)
				);

				polygons.add(
						new BasicPolygon(
								(float) (5.25 * WORLD_HEIGHT/8)-0.5f,
								4.875f,
								0f,
								0f,
								0.25f,
								Color.YELLOW,
								2.5f,
								0f,
								5
						)
				);




				/*
				Random x=new Random(3);
				for (int i=0;i<40;i++) {
					particles.add(new BasicParticle((0.5f+0.3f*(x.nextFloat()-.5f))*WORLD_HEIGHT,(0.5f+0.3f*(x.nextFloat()-.5f))*WORLD_WIDTH,0f,0f, r/2,new Color(x.nextFloat(), x.nextFloat(), x.nextFloat()), .2f, linearDragForce));
				}
				 */

				break;
			}
		}

	}
	
	private void createCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
		// on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
		Color col=Color.WHITE;
		Vec2 p1=new Vec2(cushionDepth/2, -cushionLength/2-cushionDepth/2);
		Vec2 p2=new Vec2(-cushionDepth/2, -cushionLength/2);
		Vec2 p3=new Vec2(-cushionDepth/2, +cushionLength/2);
		Vec2 p4=new Vec2(cushionDepth/2, cushionLength/2+cushionDepth/2);
		p1=rotateVec(p1,orientation);
		p2=rotateVec(p2,orientation);
		p3=rotateVec(p3,orientation);
		p4=rotateVec(p4,orientation);
		// we are being careful here to list edges in an anticlockwise manner, so that normals point inwards!
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p1.x), (float)(centrey+p1.y), (float)(centrex+p2.x), (float)(centrey+p2.y), col));
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p2.x), (float)(centrey+p2.y), (float)(centrex+p3.x), (float)(centrey+p3.y), col));
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p3.x), (float)(centrey+p3.y), (float)(centrex+p4.x), (float)(centrey+p4.y), col));
		// oops this will have concave corners so will need to fix that some time! 
	}
	private static Vec2 rotateVec(Vec2 v, double angle) {
		// I couldn't find a rotate function in Vec2 so had to write own temporary one here, just for the sake of 
		// cushion rotation for snooker table...
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float nx = v.x * cos - v.y * sin;
		float ny = v.x * sin + v.y * cos;
		return new Vec2(nx,ny);
	}
	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngineUsingBox2D game = new BasicPhysicsEngineUsingBox2D();
		final BasicView view = new BasicView(game);
		JEasyFrame frame = new JEasyFrame(view, "It's like that one game where there's the birds that you shoot at the thing");
		frame.addKeyListener(new BasicKeyListener());
		final BasicMouseListener bml = new BasicMouseListener();
		view.addMouseMotionListener(bml);
		view.addMouseListener(bml);
		game.startThread(view);
	}
	private void startThread(final BasicView view) throws InterruptedException {
		final BasicPhysicsEngineUsingBox2D game=this;
		while (true) {
			game.update();
			view.repaint();
			Toolkit.getDefaultToolkit().sync();
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
		}
	}

	public void update() {
		int VELOCITY_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		int POSITION_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;

		if(layout == LayoutMode.BLOCK_GAME){

			if (BasicMouseListener.isMouseButtonClicked()){
				if (not_clicked_yet){
					not_clicked_yet = false;
				}
				if (!toppled_all_blocks){
					// if it's the block game, and not all blocks have been toppled left, and the mouse button was clicked
					// we fire a particle from the particle launcher
					particles.add(particle_launcher());
				}
			}
			BasicMouseListener.resetMouseClicked();
		}



		for (BasicParticle p:particles) {
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			p.notificationOfNewTimestep();
		}

		particles.removeIf(
				p -> {
					if (!p.getBody().isActive()){
						world.destroyBody(p.getBody());
						return true;
					}
					return false;
				}
		);

		for (BasicPolygon p:polygons) {
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			p.notificationOfNewTimestep();
		}

		world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

		if (layout == LayoutMode.BLOCK_GAME){ // if we're in the block game
			// and we haven't toppled all blocks yet
			if (!toppled_all_blocks){
				toppled_all_blocks = polygons.stream().allMatch(BasicPolygon::isToppled);
				// we see if there are any blocks left that need toppling.
			}
		}
	}

	private BasicParticle particle_launcher(){
		return new LaunchedParticle(
				PARTICLE_LAUNCH_LOCATION,
				BasicMouseListener.getWorldCoordinatesOfMousePointer().sub(PARTICLE_LAUNCH_LOCATION),
				0.2f,
				Color.MAGENTA,
				0.75f,
				(float) 0.05f
		);
	}

	@Override
	public void draw(Graphics2D g) {
		for (Drawable p : particles) {
			p.draw(g);
		}
		for (Drawable p : polygons) {
			p.draw(g);
		}
		for (Drawable c : connectors) {
			c.draw(g);
		}
		for (Drawable b : barriers) {
			b.draw(g);
		}





		if (layout == LayoutMode.BLOCK_GAME) {

			if (not_clicked_yet){
				final Font oldFont = g.getFont();

				g.setFont(victory_text_font);

				final FontMetrics fm = g.getFontMetrics();


				final int draw_x_pos = (SCREEN_WIDTH - fm.stringWidth(instructions_words))/2;
				final int draw_y_pos = (SCREEN_HEIGHT - fm.getHeight())/2;

				g.setColor(Color.BLACK);
				g.drawString(instructions_words, draw_x_pos - 1, draw_y_pos - 1);
				g.drawString(instructions_words, draw_x_pos - 1, draw_y_pos + 1);
				g.drawString(instructions_words, draw_x_pos + 1, draw_y_pos + 1);
				g.drawString(instructions_words, draw_x_pos + 1, draw_y_pos - 1);

				g.setColor(Color.WHITE);
				g.drawString(instructions_words, draw_x_pos, draw_y_pos);

				g.setFont(oldFont);
			}

			if (toppled_all_blocks){

				final Font oldFont = g.getFont();

				g.setFont(victory_text_font);

				final FontMetrics fm = g.getFontMetrics();


				final int draw_x_pos = (SCREEN_WIDTH - fm.stringWidth(victory_words))/2;
				final int draw_y_pos = (SCREEN_HEIGHT - fm.getHeight())/2;

				g.setColor(Color.BLACK);
				g.drawString(victory_words, draw_x_pos - 1, draw_y_pos - 1);
				g.drawString(victory_words, draw_x_pos - 1, draw_y_pos + 1);
				g.drawString(victory_words, draw_x_pos + 1, draw_y_pos + 1);
				g.drawString(victory_words, draw_x_pos + 1, draw_y_pos - 1);

				g.setColor(Color.WHITE);
				g.drawString(victory_words, draw_x_pos, draw_y_pos);

				g.setFont(oldFont);

			} else {
				if (BasicMouseListener.isMouseButtonPressed()) {
					final Vec2 mousePos = BasicMouseListener.getWorldCoordinatesOfMousePointer();
					g.setColor(Color.RED);
					g.drawLine(convertWorldXtoScreenX(PARTICLE_LAUNCH_LOCATION.x),
							convertWorldYtoScreenY(PARTICLE_LAUNCH_LOCATION.y),
							convertWorldXtoScreenX(mousePos.x),
							convertWorldYtoScreenY(mousePos.y)
					);
				}

			}
		}



	}


	//public BasicParticle particle_launcher()

}


