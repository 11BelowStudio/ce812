package pbgLecture8lab_wrapperForJBox2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.*;

import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


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

	public static final float VICTORY_DISTANCE = 50f;
	public static final float FULL_WIDTH = WORLD_WIDTH + VICTORY_DISTANCE;


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
	public static float convertWorldHeightToScreenHeight(float worldHeight){
		return (worldHeight/WORLD_HEIGHT*SCREEN_HEIGHT);
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

	public static final int VICTORY_DISTANCE_INT = convertWorldXtoScreenX(VICTORY_DISTANCE);




	public List<BasicParticle> particles;
	public List<BasicPolygon> polygons;
	public List<AnchoredBarrier> barriers;
	public List<ElasticConnector> connectors;
	private List<BasicParticle> wheels;
	public static MouseJoint mouseJointDef;

	private static final Vec2 PARTICLE_LAUNCH_LOCATION = new Vec2(WORLD_WIDTH/8, WORLD_HEIGHT/3);

	// Simple pendulum attached under mouse pointer
	private static final float rollingFriction= .75F;
	private static final double springConstant=1000000, springDampingConstant=1000;
	private static final double hookesLawTruncation = 1000000000;
	private static final boolean canGoSlack=false;

	private boolean toppled_all_blocks = false;

	private static final Font victory_text_font = new Font(Font.SANS_SERIF, Font.BOLD,16);

	private static final String instructions_words = "press the right key to move!";

	private boolean not_clicked_yet = true;

	private static final String victory_words = "Congartulation, you made it!";

	private final String results_placeholder = "Bike dist: %.2f";


	private float bike_dist = 0;
	private boolean won = false;

	private final Bike theBike;

	private static final float camera_offset = -convertScreenXtoWorldX(SCREEN_WIDTH/4);
	private static final float camera_speed = 0.1f;
	private static float camera_position = 0f;

	public static float get_camera_position(){
		return camera_position;
	}





	public static enum LayoutMode {
		CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, SNOOKER_TABLE, PENDULUM_DEMO, PENDULUM_DEMO_2, PENDULUM_DEMO_3, PENDULUM_DEMO_4, SPACESHIP_DEMO, SPACESHIP_DEMO_2,

		/**
		 * The LayoutMode for the block-related game for lab 5
		 */
		BLOCK_GAME,
		BALANCE_CART,
		BIKE_GAME
	};

	/**
	 * Use this to select the layout mode for the current demo.
	 */
	private static final LayoutMode layout = LayoutMode.BIKE_GAME;


	public BasicPhysicsEngineUsingBox2D(BasicKeyListener bkl) {

		world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
		world.setContinuousPhysics(true);



		particles = new ArrayList<>();
		polygons = new ArrayList<>();
		barriers = new ArrayList<>();
		connectors=new ArrayList<>();
		wheels = new ArrayList<>();

		// pinball:
		float linearDragForce=.02f;
		float r=.3f;
//			rectangles.add(new BasicRectangle(WORLD_WIDTH/2,WORLD_HEIGHT*3/4,  -4,3, r*4, r*8, 0, 5,  false, Color.BLUE, 1,0.5));
//			public BasicRectangle(double sx, double sy, double vx, double vy, double width, double height, double orientation, double angularVeloctiy, boolean improvedEuler, Color col, double mass) {

		float s=1.2f;

		float floor_height = 0.1f;

		// floor
		barriers.add(
				new AnchoredBarrier_StraightLine(
						FULL_WIDTH, floor_height, -5f, floor_height, Color.WHITE
				)
		);

		// outer box

		barriers.add(
				new AnchoredBarrier_StraightLine(
						-5f, floor_height, -5f, WORLD_HEIGHT, Color.WHITE
				)
		);


		barriers.add(
				new AnchoredBarrier_StraightLine(
						-5f, WORLD_HEIGHT, FULL_WIDTH, WORLD_HEIGHT, Color.WHITE
				)
		);

		barriers.add(
				new AnchoredBarrier_StraightLine(
						FULL_WIDTH, WORLD_HEIGHT, FULL_WIDTH, floor_height, Color.WHITE
				)
		);


		/*
		barriers.add(
				new AnchoredBarrier_StraightLine(
						WORLD_WIDTH, 0, WORLD_WIDTH +3f, floor_height + 1.0f, Color.CYAN
				)
		);
		 */

		barriers.addAll(Arrays.asList(RAMP_FACTORY(WORLD_WIDTH, WORLD_WIDTH+4f, floor_height + 1f)));


		barriers.addAll(Arrays.asList(RAMP_FACTORY(WORLD_WIDTH + 20, WORLD_WIDTH + 28f, floor_height + 2f)));


		/*
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

		 */



		bike_dist = 0;

		float rect_x = WORLD_WIDTH/2;
		float rect_y = 0.75f + floor_height;
		float rect_width = 3;
		float rect_height = 0.75f;
		float wheel_y = rect_y - (rect_height/4);



		theBike = Bike.DEFAULT_BIKE_FACTORY(bkl);





	}



	public static AnchoredBarrier[] RAMP_FACTORY(final float start_x, final float end_x, final float height){
		final float x_length = end_x - start_x;
		AnchoredBarrier[] ramp_barriers = new AnchoredBarrier[3];

		ramp_barriers[0] = new AnchoredBarrier_StraightLine(
				start_x, 0, end_x, 2*height/3, Color.CYAN
		);

		ramp_barriers[1] = new AnchoredBarrier_StraightLine(
				start_x + (x_length/3), 0, end_x, height, Color.CYAN
		);

		ramp_barriers[2] = new AnchoredBarrier_StraightLine(
				end_x, 0, end_x, height, Color.WHITE
		);
		return ramp_barriers;


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

		final JFrame theFrame = new JFrame("A very brave bike eecks dee");
		final BasicKeyListener bkl = new BasicKeyListener();
		theFrame.addKeyListener(bkl);
		theFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		final BasicPhysicsEngineUsingBox2D game = new BasicPhysicsEngineUsingBox2D(bkl);
		final BasicView view = new BasicView(game);

		theFrame.getContentPane().add(BorderLayout.CENTER, view);
		theFrame.pack();
		theFrame.setVisible(true);
		theFrame.repaint();
		theFrame.setResizable(false);

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

		theBike.update();

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

		bike_dist = theBike.get_x();
		if (!won){
			won = bike_dist >= VICTORY_DISTANCE;
		}

		final float final_position = bike_dist + camera_offset;
		camera_position = (1-camera_speed) * camera_position + (camera_speed * final_position);


		if (layout == LayoutMode.BIKE_GAME){
			if (not_clicked_yet){
				not_clicked_yet = !BasicKeyListener.isRotateRightKeyPressed();
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

		final AffineTransform at = g.getTransform();

		g.translate(-convertWorldXtoScreenX(camera_position), 0);


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

		g.setColor(Color.YELLOW);
		g.drawRect(VICTORY_DISTANCE_INT, 0, 5, SCREEN_HEIGHT);

		theBike.draw(g);

		g.setTransform(at);

		if (layout == LayoutMode.BIKE_GAME) {

			final Font oldFont = g.getFont();

			g.setFont(victory_text_font);

			final FontMetrics fm = g.getFontMetrics();

			final String score_words = String.format(results_placeholder, bike_dist);
					//"Bike position: " + String.valueOf(bike_dist);

			TEXT_DRAWER(
					g,
					(SCREEN_WIDTH - fm.stringWidth(score_words)),
					(SCREEN_HEIGHT - fm.getHeight()),
					score_words
			);

			if (not_clicked_yet){

				TEXT_DRAWER(
						g,
						(SCREEN_WIDTH - fm.stringWidth(instructions_words))/2,
						(SCREEN_HEIGHT - fm.getHeight())/2,
						instructions_words
				);
			}
			if (won){
				TEXT_DRAWER(
						g,
						(SCREEN_WIDTH - fm.stringWidth(victory_words))/2,
						(SCREEN_HEIGHT - fm.getHeight())/2,
						victory_words
				);
			}
			g.setFont(oldFont);
		}



	}


	public static void TEXT_DRAWER(Graphics2D g, final int x, final int y, final String t){
		g.setColor(Color.BLACK);
		g.drawString(t, x-1, y-1);
		g.drawString(t, x-1, y+1);
		g.drawString(t, x+1, y-1);
		g.drawString(t, x+1, y+1);
		g.setColor(Color.WHITE);
		g.drawString(t,x,y);
	}


	//public BasicParticle particle_launcher()

}


