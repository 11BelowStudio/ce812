package pbgLecture5lab_wrapperForJBox2D;


import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class AnchoredBarrier_StraightLine extends AnchoredBarrier {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied: 
	 */

	private Vec2 startPos,endPos;
	private final Color col;
	public final Body body;


	public AnchoredBarrier_StraightLine(float startx, float starty, float endx, float endy, Color col) {
		
		
		startPos=new Vec2(startx,starty);
		endPos=new Vec2(endx,endy);

		World w=BasicPhysicsEngineUsingBox2D.world;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.position = new Vec2(startx,starty);
		Body body = w.createBody(bodyDef);
		this.body=body;
		Vec2[] vertices = new Vec2[] { new Vec2(), new Vec2(endx-startx, endy-starty) };
		ChainShape chainShape = new ChainShape();
		chainShape.createChain(vertices, vertices.length);
		body.createFixture(chainShape, 0);
		
		
		this.col=col;
	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(startPos.x);
		int y1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(startPos.y);
		int x2 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(endPos.x);
		int y2 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(endPos.y);
		g.setColor(col);
		g.drawLine(x1, y1, x2, y2);
	}


}
