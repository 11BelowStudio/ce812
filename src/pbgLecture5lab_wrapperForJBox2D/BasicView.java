package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class BasicView extends JComponent {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	// background colour
	public static final Color BG_COLOR = new Color(40, 43, 47);

	private BasicPhysicsEngineUsingBox2D game;

	public BasicView(BasicPhysicsEngineUsingBox2D game) {
		this.game = game;
	}
	
	@Override
	public void paintComponent(Graphics g0) {
		BasicPhysicsEngineUsingBox2D game;
		synchronized(this) {
			game=this.game;
		}
		Graphics2D g = (Graphics2D) g0;
		// paint the background
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		game.draw(g);
	}

	@Override
	public Dimension getPreferredSize() {
		return BasicPhysicsEngineUsingBox2D.FRAME_SIZE;
	}
	
	public synchronized void updateGame(BasicPhysicsEngineUsingBox2D game) {
		this.game=game;
	}
}