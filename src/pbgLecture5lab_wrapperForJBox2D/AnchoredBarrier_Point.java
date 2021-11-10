package pbgLecture5lab_wrapperForJBox2D;


import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.common.Vec2;

public class AnchoredBarrier_Point extends AnchoredBarrier_Curve {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */

	public final Vec2 barrierPointPosition;
	private final static Color col=Color.blue;


	public AnchoredBarrier_Point(float centrex, float centrey) {
		super(centrex, centrey, 0, 0, 360, col);
		this.barrierPointPosition=new Vec2(centrex,centrey);

	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(barrierPointPosition.x);
		int y1 = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(barrierPointPosition.y);
		g.setColor(col);
		g.fillOval(x1, y1, 4, 4);
	}


}
