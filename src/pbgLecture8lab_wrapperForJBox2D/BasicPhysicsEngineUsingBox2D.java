package pbgLecture8lab_wrapperForJBox2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.*;


public class BasicPhysicsEngineUsingBox2D implements Drawable {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 680;
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
	private List<BasicParticle> wheels;
	public static MouseJoint mouseJointDef;

	public List<RevoluteJoint> wheelRevoluteJoints;

	final RevoluteJoint leftRevWheel;

	final RevoluteJoint rightRevWheel;

	private static final Vec2 PARTICLE_LAUNCH_LOCATION = new Vec2(WORLD_WIDTH/8, WORLD_HEIGHT/3);

	// Simple pendulum attached under mouse pointer
	private static final float rollingFriction= .75F;
	private static final double springConstant=1000000, springDampingConstant=1000;
	private static final double hookesLawTruncation = 1000000000;
	private static final boolean canGoSlack=false;

	private boolean toppled_all_blocks = false;

	private static final Font victory_text_font = new Font(Font.SANS_SERIF, Font.BOLD,16);

	private static final String instructions_words = "press left and right to balance the BraveryStick!";

	private boolean not_clicked_yet = true;

	private static final String victory_words = "oh no, you failed after surviving for ";

	private String results_words = "";

	final BasicPolygon the_really_big_stick;

	int survival_time = 0;



	public static enum LayoutMode {
		CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE, PENDULUM_DEMO, PENDULUM_DEMO_2, PENDULUM_DEMO_3, PENDULUM_DEMO_4, SPACESHIP_DEMO, SPACESHIP_DEMO_2,

		/**
		 * The LayoutMode for the block-related game for lab 5
		 */
		BLOCK_GAME,
		BALANCE_CART
	};

	/**
	 * Use this to select the layout mode for the current demo.
	 */
	private static final LayoutMode layout = LayoutMode.BALANCE_CART;


	public BasicPhysicsEngineUsingBox2D() {

		world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
		world.setContinuousPhysics(true);

		particles = new ArrayList<BasicParticle>();
		polygons = new ArrayList<BasicPolygon>();
		barriers = new ArrayList<AnchoredBarrier>();
		connectors=new ArrayList<ElasticConnector>();
		wheels = new ArrayList<>();

		wheelRevoluteJoints = new ArrayList<>();
		// pinball:
		float linearDragForce=.02f;
		float r=.3f;
//			rectangles.add(new BasicRectangle(WORLD_WIDTH/2,WORLD_HEIGHT*3/4,  -4,3, r*4, r*8, 0, 5,  false, Color.BLUE, 1,0.5));
//			public BasicRectangle(double sx, double sy, double vx, double vy, double width, double height, double orientation, double angularVeloctiy, boolean improvedEuler, Color col, double mass) {

		float s=1.2f;


		
		barriers = new ArrayList<AnchoredBarrier>();

		float floor_height = 0.1f;

		// floor
		barriers.add(
				new AnchoredBarrier_StraightLine(
						WORLD_WIDTH + 10, floor_height, -10, floor_height, Color.WHITE
				)
		);

		// outer box

		barriers.add(
				new AnchoredBarrier_StraightLine(
						-10, floor_height, -10, WORLD_HEIGHT, Color.WHITE
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						0, floor_height, 0, 1.5f, Color.WHITE
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						-10, WORLD_HEIGHT, WORLD_WIDTH+10, WORLD_HEIGHT, Color.WHITE
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						WORLD_WIDTH+10, WORLD_HEIGHT, WORLD_WIDTH + 10, floor_height, Color.WHITE
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						WORLD_WIDTH, floor_height, WORLD_WIDTH, 1.5f, Color.WHITE
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						WORLD_WIDTH/2, floor_height*2, WORLD_WIDTH, floor_height * 4, Color.LIGHT_GRAY
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						WORLD_WIDTH/2, floor_height*2, 0, floor_height * 4, Color.LIGHT_GRAY
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						2* WORLD_WIDTH/3, floor_height * 2.5f, WORLD_WIDTH, floor_height * 6, Color.GRAY
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						 WORLD_WIDTH/3, floor_height * 2.5f, 0, floor_height * 6, Color.GRAY
				)
		);

		survival_time = 0;

		float rect_x = WORLD_WIDTH/2;
		float rect_y = 0.75f + floor_height;
		float rect_width = 3;
		float rect_height = 0.75f;
		float wheel_y = rect_y - (rect_height/4);

		BasicPolygon cart_body = BasicPolygon.RECTANGLE_FACTORY(
				rect_x, rect_y, 0, 0, 1, new Color(74,65,42), 10f, rollingFriction,
				rect_width, rect_height, BodyType.DYNAMIC);

		BasicParticle wheel1 = new BasicParticle(rect_x - rect_width/2, wheel_y, 0f, 0f, wheel_y - floor_height,  Color.GRAY, 5f, rollingFriction, 10f);

		BasicParticle wheel2 = new BasicParticle(rect_x + rect_width/2, wheel_y, 0f, 0f, wheel_y - floor_height,  Color.GRAY, 5f, rollingFriction, 10f);

		RevoluteJointDef wheelie = new RevoluteJointDef();

		// WheelJointDef realie = new WheelJointDef();
		// realie.enableMotor = true;

		wheelie.bodyA = cart_body.getBody();
		wheelie.bodyB = wheel1.getBody();
		wheelie.collideConnected = false;
		wheelie.localAnchorA = new Vec2(-rect_width/2, -rect_height/4);
		wheelie.localAnchorB = new Vec2(0,0);
		wheelie.enableMotor = true;
		wheelie.maxMotorTorque = 125f;

		leftRevWheel = (RevoluteJoint) world.createJoint(wheelie);

		wheelRevoluteJoints.add(leftRevWheel);

		wheelie.bodyB = wheel2.getBody();
		wheelie.localAnchorA = new Vec2(rect_width/2, -rect_height/4);

		rightRevWheel = (RevoluteJoint) world.createJoint(wheelie);
		wheelRevoluteJoints.add(rightRevWheel);




		float stick_height = WORLD_HEIGHT/2.0f;
		float stick_width = WORLD_WIDTH/10.0f;

		// it's brown AND sticky!
		the_really_big_stick = BasicPolygon.RECTANGLE_FACTORY(
				rect_x, (rect_y - rect_height/3) + stick_height/2, 0, 0, 1, new Color(78, 52, 36),
				5f, rollingFriction, stick_width, stick_height, BodyType.DYNAMIC
		);

		//the_really_big_stick.soBrave(-0.25, -(stick_height - rect_height/6));
		the_really_big_stick.soBrave(
				new Vec2(0f, -(stick_height - rect_height/6)),
				BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(stick_width),
				BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(stick_height)
		);

		RevoluteJointDef the_sticky_bit_of_the_stick = new RevoluteJointDef();

		the_sticky_bit_of_the_stick.bodyA = cart_body.getBody();
		the_sticky_bit_of_the_stick.localAnchorA = new Vec2(0, 0);
		the_sticky_bit_of_the_stick.bodyB = the_really_big_stick.getBody();
		the_sticky_bit_of_the_stick.localAnchorB = new Vec2(0, -(stick_height/2) + rect_height/6);
		the_sticky_bit_of_the_stick.collideConnected = false;

		world.createJoint(the_sticky_bit_of_the_stick);

		polygons.add(the_really_big_stick);
		polygons.add(cart_body);
		particles.add(wheel1);
		particles.add(wheel2);
		wheels.add(wheel1);
		wheels.add(wheel2);

	}
	

	public static Vec2 rotateVec(Vec2 v, double angle) {
		// I couldn't find a rotate function in Vec2 so had to write own temporary one here, just for the sake of 
		// cushion rotation for snooker table...
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float nx = v.x * cos - v.y * sin;
		float ny = v.x * sin + v.y * cos;
		return new Vec2(nx,ny);
	}

	public static double Vec2Angle(Vec2 a, Vec2 b){
		return Math.atan2(b.y - a.y, b.x - a.x);
	}

	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngineUsingBox2D game = new BasicPhysicsEngineUsingBox2D();
		final BasicView view = new BasicView(game);
		JEasyFrame frame = new JEasyFrame(view, "DAE brown and sticky??????????");
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

		if (layout == LayoutMode.BALANCE_CART){
			String placeholder = "placeholder";
		} else if(layout == LayoutMode.BLOCK_GAME){

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

		the_really_big_stick.notificationOfNewTimestep();

		if (BasicKeyListener.isRotateLeftKeyPressed()){
			if (rightRevWheel.isMotorEnabled()){
				rightRevWheel.enableMotor(false);
			}
			if (!leftRevWheel.isMotorEnabled()){
				leftRevWheel.enableMotor(true);
				leftRevWheel.setMotorSpeed(-50);
			}
			if (not_clicked_yet){
				not_clicked_yet = false;
			}
		} else if (BasicKeyListener.isRotateRightKeyPressed()){
			if (leftRevWheel.isMotorEnabled()){
				leftRevWheel.enableMotor(false);
			}
			if (!rightRevWheel.isMotorEnabled()){
				rightRevWheel.enableMotor(true);
				rightRevWheel.setMotorSpeed(50);
			}
			if (not_clicked_yet){
				not_clicked_yet = false;
			}
		} else{
			if(leftRevWheel.isMotorEnabled()){
				leftRevWheel.enableMotor(false);
			}
			if (rightRevWheel.isMotorEnabled()){
				rightRevWheel.enableMotor(false);
			}
		}

		/*
		if (BasicKeyListener.isRotateLeftKeyPressed()){
			for (RevoluteJoint r: wheelRevoluteJoints){
				if (r.isMotorEnabled()){
					r.setMotorSpeed(-25);
				}
			}
		} else if (BasicKeyListener.isRotateRightKeyPressed()){
			for (RevoluteJoint r: wheelRevoluteJoints){
				if (r.isMotorEnabled()){
					r.setMotorSpeed(25);
				}
			}
		} else{
			for (RevoluteJoint r: wheelRevoluteJoints){
				if (r.isMotorEnabled()){
					r.setMotorSpeed(0);
				}
			}
		}

		 */

		world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

		if (layout == LayoutMode.BALANCE_CART){
			if (!toppled_all_blocks){
				toppled_all_blocks = the_really_big_stick.isToppled();
				if (!toppled_all_blocks){
					toppled_all_blocks = wheels.stream().anyMatch(
							bp -> {
								final float bpposx = bp.getBody().getPosition().x;
								return (bpposx < 0 || bpposx > BasicPhysicsEngineUsingBox2D.WORLD_WIDTH);
							}
					);
				}
				if (toppled_all_blocks){
					results_words = "oh no, you failed after surviving for " + survival_time + " ticks!";
				} else {
					survival_time++;
				}
			}
		}

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
		for (Drawable p : polygons) {
			p.draw(g);
		}
		for (Drawable p : particles) {
			p.draw(g);
		}
		for (Drawable c : connectors) {
			c.draw(g);
		}
		for (Drawable b : barriers) {
			b.draw(g);
		}

		the_really_big_stick.draw(g);


		if (layout == LayoutMode.BALANCE_CART){

			final Font oldFont = g.getFont();

			g.setFont(victory_text_font);

			final FontMetrics fm = g.getFontMetrics();

			final String score_words = String.valueOf(survival_time);

			final int draw_x_pos = (SCREEN_WIDTH - fm.stringWidth(score_words));
			final int draw_y_pos = (SCREEN_HEIGHT - fm.getHeight());

			g.setColor(Color.BLACK);
			g.drawString(score_words, draw_x_pos - 1, draw_y_pos - 1);
			g.drawString(score_words, draw_x_pos - 1, draw_y_pos + 1);
			g.drawString(score_words, draw_x_pos + 1, draw_y_pos + 1);
			g.drawString(score_words, draw_x_pos + 1, draw_y_pos - 1);

			g.setColor(Color.WHITE);
			g.drawString(score_words, draw_x_pos, draw_y_pos);


			if (toppled_all_blocks){

				final FontMetrics fm2 = g.getFontMetrics();

				final int draw_x_pos2 = (SCREEN_WIDTH - fm2.stringWidth(results_words))/2;
				final int draw_y_pos2 = (SCREEN_HEIGHT - fm2.getHeight())/2;

				g.setColor(Color.BLACK);
				g.drawString(results_words, draw_x_pos2 - 1, draw_y_pos2 - 1);
				g.drawString(results_words, draw_x_pos2 - 1, draw_y_pos2 + 1);
				g.drawString(results_words, draw_x_pos2 + 1, draw_y_pos2 + 1);
				g.drawString(results_words, draw_x_pos2 + 1, draw_y_pos2 - 1);

				g.setColor(Color.WHITE);
				g.drawString(results_words, draw_x_pos2, draw_y_pos2);


			} else if (not_clicked_yet){
				g.setFont(victory_text_font);

				final FontMetrics fm2 = g.getFontMetrics();

				final int draw_x_pos2 = (SCREEN_WIDTH - fm2.stringWidth(instructions_words))/2;
				final int draw_y_pos2 = (SCREEN_HEIGHT - fm2.getHeight())/2;

				g.setColor(Color.BLACK);
				g.drawString(instructions_words, draw_x_pos2 - 1, draw_y_pos2 - 1);
				g.drawString(instructions_words, draw_x_pos2 - 1, draw_y_pos2 + 1);
				g.drawString(instructions_words, draw_x_pos2 + 1, draw_y_pos2 + 1);
				g.drawString(instructions_words, draw_x_pos2 + 1, draw_y_pos2 - 1);

				g.setColor(Color.WHITE);
				g.drawString(instructions_words, draw_x_pos2, draw_y_pos2);

				g.setFont(victory_text_font);
			}
			g.setFont(oldFont);
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


