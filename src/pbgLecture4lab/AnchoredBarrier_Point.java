package pbgLecture4lab;


import java.awt.Color;
import java.awt.Graphics2D;

public class AnchoredBarrier_Point extends AnchoredBarrier_Curve {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */

	public final Vect2D barrierPointPosition;
	private final static Color col=Color.blue;


	public AnchoredBarrier_Point(double centrex, double centrey) {
		this(centrex, centrey, null);
	}

	public AnchoredBarrier_Point(double centrex, double centrey,  Double barrierDepth) {
		super(centrex, centrey, 0, 0, 360, false, barrierDepth, col);
		this.barrierPointPosition=new Vect2D(centrex,centrey);

	}

	@Override
	public void draw(Graphics2D g) {
		int x1 = BasicPhysicsEngine.convertWorldXtoScreenX(barrierPointPosition.x);
		int y1 = BasicPhysicsEngine.convertWorldYtoScreenY(barrierPointPosition.y);
		g.setColor(col);
		g.fillOval(x1, y1, 4, 4);
	}


}
