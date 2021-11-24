/*
 /* Original code taken JBullet Demos, by Martin Dvorak <jezek2@advel.cz>
 * Modified by Mike Fairbank 2016-02-12

 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package pbgLecture7lab_Jbullet;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.lwjgl.input.Keyboard;

import com.bulletphysics.BulletStats;
import com.bulletphysics.demos.applet.SoftwareGL;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;

/**
 *
 * @author jezek2
 */
public class MainPanel3d extends JPanel {

	private JBulletGame3d game;
	private boolean inited = false;
	private BufferedImage img;
	private SoftwareGL sgl;

	public MainPanel3d() {
		sgl = new SoftwareGL();
		
		img = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
		sgl.init(img);
		
		setFocusable(true);
		requestFocusInWindow();
		
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
					if (game != null) game.keyboardCallback(e.getKeyChar(), 0, 0, e.getModifiersEx());
				}
				
				repaint();
			}

			public void keyPressed(KeyEvent e) {
				if (game != null) game.specialKeyboard(convertKey(e.getKeyCode()), 0, 0, e.getModifiersEx());
				repaint();
			}

			public void keyReleased(KeyEvent e) {
				if (game != null) game.specialKeyboardUp(convertKey(e.getKeyCode()), 0, 0, e.getModifiersEx());
				repaint();
			}
			
			private int convertKey(int code) {
				int key = 0;
				switch (code) {
					case KeyEvent.VK_LEFT: key = Keyboard.KEY_LEFT; break;
					case KeyEvent.VK_RIGHT: key = Keyboard.KEY_RIGHT; break;
					case KeyEvent.VK_UP: key = Keyboard.KEY_UP; break;
					case KeyEvent.VK_DOWN: key = Keyboard.KEY_DOWN; break;
					case KeyEvent.VK_F5: key = Keyboard.KEY_F5; break;
				}
				return key;
			}
		});
		
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				if (img != null) {
					img.flush();
				}
				
				img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				sgl.init(img);
				if (game != null) game.reshape(getWidth(), getHeight());
				repaint();
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
			}
		});
		
	}

	public IGL getGL() {
		return sgl;
	}
	
	public void initialisePanel(JBulletGame3d app) {
		if (game != null) {
			game.destroy();
		}
		
		game = app;
		game.getDynamicsWorld().setDebugDrawer(new GLDebugDrawer(sgl));
		inited = false;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		if (game != null) {
			if (!inited) {
				game.myinit();
				game.reshape(img.getWidth(), img.getHeight());
			}
			inited = true;
			
			BulletStats.updateTime = 0;
			game.renderme();
		}
		
		g.drawImage(img, 0, 0, null);
	
	}
	

}
