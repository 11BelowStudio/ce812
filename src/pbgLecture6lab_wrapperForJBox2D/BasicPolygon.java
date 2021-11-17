package pbgLecture6lab_wrapperForJBox2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import pbgLecture6lab_wrapperForJBox2D.images.RatherBadImageLoader;


public class BasicPolygon implements Drawable, IHaveABody, Toppleable {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	public final float ratioOfScreenScaleToWorldScale;

	private final float rollingFriction,mass;
	public final Color col;
	protected final Body body;
	private final Path2D.Float polygonPath;

	private boolean wasToppled; // whether this polygon has been 'toppled' yet

	private final float startX, startY;

	private final float startAngle;

	private static final float topple_dist = 0.45f; // moved 0.45 world units from start = toppled.

	private static final float topple_angle = (float) Math.toRadians(60f); // rotated at least 90deg from start = toppled

	private boolean brave_if_true;

	private int braverx, bravery = 0;

	private Vec2 image_pivot = new Vec2(0,0);

	private static final Map<String, Image> images = RatherBadImageLoader.get_images();

	private static final Optional<Image> BRAVERYSTICK = images.entrySet().stream().filter(
			stringImageEntry -> stringImageEntry.getKey().equals("BraveryStick")
	).map(Map.Entry::getValue).findFirst();

	private static final Optional<Image> BRAVERYSTICK_FLIPPED = images.entrySet().stream().filter(
			stringImageEntry -> stringImageEntry.getKey().equals("BraveryStickFlipped")
	).map(Map.Entry::getValue).findFirst();

	private Optional<Rectangle2D> theBraveLittleRectangle = Optional.empty();

	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float rollingFriction, int numSides) {
		this(sx, sy, vx, vy, radius, col, mass, rollingFriction,mkRegularPolygon(numSides, radius),numSides);
	}
	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float rollingFriction, Path2D.Float polygonPath, int numSides) {
		this(sx, sy, vx, vy, radius, col, mass, rollingFriction, polygonPath, numSides, BodyType.DYNAMIC);
	}




	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float rollingFriction, Path2D.Float polygonPath, int numSides, BodyType bt){
		World w= BasicPhysicsEngineUsingBox2D.world; // a Box2D object
		BodyDef bodyDef = new BodyDef();  // a Box2D object
		bodyDef.type = bt; // this says the physics engine is to move it automatically
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		bodyDef.angularDamping = 0.1f;
		this.body = w.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		Vec2[] vertices = verticesOfPath2D(polygonPath, numSides);
		shape.set(vertices, numSides);
		FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
		fixtureDef.shape = shape;
		fixtureDef.density = (float) (mass/((float) numSides)/2f*(radius*radius)*Math.sin(2*Math.PI/numSides));
		fixtureDef.friction = 0.1f;// this is surface friction;
		fixtureDef.restitution = 0.5f;
		body.createFixture(fixtureDef);

		wasToppled = false;

		brave_if_true = false;

		startX = sx;
		startY = sy - 0.01f;
		startAngle = body.getAngle();

//		// code to test adding a second fixture:
//		PolygonShape shape2 = new PolygonShape();
//		Vec2[] vertices2 = verticesOfPath2D(polygonPath, numSides);
//		for (int i=0;i<vertices2.length;i++) {
//			vertices2[i]=new Vec2(vertices2[i].x+0.7f,vertices2[i].y+0.7f);
//		}
//		shape2.set(vertices2, numSides);
//		FixtureDef fixtureDef2 = new FixtureDef();// This class is from Box2D
//		fixtureDef2.shape = shape2;
//		fixtureDef2.density = 1;//(float) (mass/(Math.PI*radius*radius));
//		fixtureDef2.friction = 0.1f;
//		fixtureDef2.restitution = 0.5f;
//		body.createFixture(fixtureDef2);
		this.rollingFriction=rollingFriction;
		this.mass=mass;
		this.ratioOfScreenScaleToWorldScale=BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(1);
		//System.out.println("Screenradius="+ratioOfScreenScaleToWorldScale);
		this.col=col;
		this.polygonPath=polygonPath;
	}



	public void soBrave(Vec2 image_pivot, int rect_w, int rect_h){
		brave_if_true = true;
		this.image_pivot = new Vec2(image_pivot);
		braverx = rect_w;
		bravery = rect_h;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(col);
		Vec2 position = body.getPosition();
		float angle = body.getAngle();
		AffineTransform af = new AffineTransform();
		af.translate(BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(position.x), BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(position.y));
		af.scale(ratioOfScreenScaleToWorldScale, -ratioOfScreenScaleToWorldScale);// there is a minus in here because screenworld is flipped upsidedown compared to physics world
		af.rotate(angle);
		Path2D.Float p = new Path2D.Float (polygonPath,af);
		g.fill(p);
		if (brave_if_true){

			final Vec2 world_pivot_anchor = body.getWorldPoint(this.image_pivot);
			final Vec2 w_piv_to_mid = body.getPosition().sub(world_pivot_anchor);
			//double angle_diff = Math.atan2(w_piv_to_mid.x, w_piv_to_mid.y);
			if (Math.atan2(w_piv_to_mid.x, w_piv_to_mid.y) > 0){
				BRAVERYSTICK.ifPresent(i -> braverystick_drawer(i,g,world_pivot_anchor,angle));
			} else {
				BRAVERYSTICK_FLIPPED.ifPresent(i-> braverystick_drawer(i,g,world_pivot_anchor,angle));
			}
		}
		if (wasToppled){
			g.setColor(Color.RED);
			g.draw(p);
		}
	}

	/**
	 * Draws the BraveryStick (very brave)
	 * @param i the image
	 * @param g graphics context
	 * @param v the location of the pivot but in world coordinates instead
	 * @param a angle of the body we're drawing the BraveryStick over
	 */
	private void braverystick_drawer(Image i, Graphics2D g, Vec2 v, float a){
		final AffineTransform old_at = g.getTransform();
		g.translate(BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(v.x), BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(v.y));
		g.rotate(-a);
		g.drawImage(i, -braverx/2, (int)(-1.48 * bravery), braverx, bravery, null);
		g.setTransform(old_at);
	}


	public void notificationOfNewTimestep() {
		if (rollingFriction>0) {
			Vec2 rollingFrictionForce=new Vec2(body.getLinearVelocity());
			rollingFrictionForce=rollingFrictionForce.mul(-rollingFriction*mass);
			body.applyForceToCenter(rollingFrictionForce);
		}
		if (!wasToppled){
			//System.out.println(startAngle + ", " + body.getAngle() + ", " +  topple_angle + ", " + Math.abs(startAngle - body.getAngle()));
			if (Math.abs(startAngle - body.getAngle()) > topple_angle){
				wasToppled = true;
			}
		}
	}

	public boolean isToppled(){
		return wasToppled;
	}
	
	// Vec2 vertices of Path2D
	public static Vec2[] verticesOfPath2D(Path2D.Float p, int n) {
		Vec2[] result = new Vec2[n];
		float[] values = new float[6];
		PathIterator pi = p.getPathIterator(null);
		int i = 0;
		while (!pi.isDone() && i < n) {
			int type = pi.currentSegment(values);
			if (type == PathIterator.SEG_LINETO) {
				result[i++] = new Vec2(values[0], values[1]);
			}
			pi.next();
		}
		return result;
	}
	public static Path2D.Float mkRegularPolygon(int n, float radius) {
		Path2D.Float p = new Path2D.Float();
		p.moveTo(radius, 0);
		for (int i = 0; i < n; i++) {
			float x = (float) (Math.cos((Math.PI * 2 * i) / n) * radius);
			float y = (float) (Math.sin((Math.PI * 2 * i) / n) * radius);
			p.lineTo(x, y);
		}
		p.closePath();
		return p;
	}

	@Override
	public Body getBody() {
		return body;
	}

	/**
	 * A factory method which can be used to construct a rectangle with given location info and given width/height
	 * @param sx x pos
	 * @param sy y pos
	 * @param vx x vel
	 * @param vy y vel
	 * @param radius radius
	 * @param col colour
	 * @param mass mass
	 * @param rollingFriction friction
	 * @param width width of rectangle
	 * @param height height of rectangle
	 * @param bodyType body type for that rectangle
	 * @return a rectangle object with that info
	 */
	public static BasicPolygon RECTANGLE_FACTORY(float sx, float sy, float vx, float vy, float radius, Color col,
												 float mass, float rollingFriction, float width, float height,
												 BodyType bodyType){
		return new BasicPolygon(sx, sy, vx, vy, radius, col, mass, rollingFriction,
				new Path2D.Float(new Rectangle2D.Float(
				-width/2, -height/2, width, height)
				), 4, bodyType);
	}


	public static List<BasicPolygon> RECTANGLE_ARCH_FACTORY(float xMid, float yOrigin, Color pillarCol, Color barCol,
															float mass, float rollingFriction, float archHeight,
															float archWidth, float pillarWidth, float barHeight,
															int arch_count, BodyType bt){
		if(arch_count <= 0){
			return new ArrayList<>(); // not going to bother making 0 or fewer arches.
		} else if (pillarWidth <= 0f){
			throw new IllegalArgumentException("pillarWidth must be greater than 0!");
		} else if (archWidth <= 0f){
			throw new IllegalArgumentException("archWidth must be greater than 0!");
		} else if (archHeight <= 0f){
			throw new IllegalArgumentException("archHeight must be greater than 0!");
		} else if (barHeight <= 0f){
			throw new IllegalArgumentException("barHeight must be greater than 0!");
		} else if (barHeight >= archHeight){
			throw new IllegalArgumentException("barHeight must be smaller than archHeight!");
		}

		final float totalWidth = archWidth * arch_count;

		float next_arch_x_midpoint = xMid - (totalWidth/2f);

		final float pillarHeight = archHeight - barHeight;

		//final float pillarXOffset = -(pillarWidth/2f);
		//final float pillarYOffset = -(pillarHeight/2f);

		final float pillarYOrigin = yOrigin + (pillarHeight/2f);

		final float barXOffset = -(archWidth/2f);
		final float barYOffset = -(barHeight/2f);

		final float barYOrigin = yOrigin + archHeight + barYOffset;

		final List<BasicPolygon> arches = new ArrayList<>();

		for (int i = 0; i < arch_count; i++){

			arches.add(
					BasicPolygon.RECTANGLE_FACTORY(
							next_arch_x_midpoint + barXOffset,
							pillarYOrigin,
							0,
							0,
							1,
							pillarCol,
							mass,
							rollingFriction,
							pillarWidth,
							pillarHeight,
							bt
					)
			);
			arches.add(
					BasicPolygon.RECTANGLE_FACTORY(
							next_arch_x_midpoint,
							barYOrigin,
							0,
							0,
							1,
							barCol,
							mass,
							rollingFriction,
							archWidth,
							barHeight,
							bt
					)
			);
			next_arch_x_midpoint += archWidth;
		}

		arches.add(
				BasicPolygon.RECTANGLE_FACTORY(
						next_arch_x_midpoint + barXOffset,
						pillarYOrigin,
						0,
						0,
						1,
						pillarCol,
						mass,
						rollingFriction,
						pillarWidth,
						pillarHeight,
						bt
				)
		);

		return arches;


	}

}
