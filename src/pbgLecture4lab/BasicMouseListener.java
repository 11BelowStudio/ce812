package pbgLecture4lab;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class BasicMouseListener extends MouseInputAdapter {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	private static int mouseX, mouseY;
	private static boolean mouseButtonPressed;
	public void mouseMoved(MouseEvent e) {
       mouseX=e.getX();
       mouseY=e.getY();
       mouseButtonPressed=false;
       System.out.println("Move event: "+mouseX+","+mouseY);
    }
	public void mouseDragged(MouseEvent e) {
//	   mouseX=e.getX();
//	   mouseY=e.getY();
//	   mouseButtonPressed=true;
//       System.out.println("Drag event: "+mouseX+","+mouseY);
	}
	public boolean isMouseButtonPressed() {
		return mouseButtonPressed;
	}
	public static Vect2D getWorldCoordinatesOfMousePointer() {
		return new Vect2D(BasicPhysicsEngine.convertScreenXtoWorldX(mouseX), BasicPhysicsEngine.convertScreenYtoWorldY(mouseY));
	}
}
