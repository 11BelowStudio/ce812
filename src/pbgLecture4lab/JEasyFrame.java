package pbgLecture4lab;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

public class JEasyFrame extends JFrame {
	/* Author: Norbert Voelker
	 */
	public Component comp;

	public JEasyFrame(Component comp, String title) {
		super(title);
		this.comp = comp;
		getContentPane().add(BorderLayout.CENTER, comp);
		pack();
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		repaint();
	}

	public static JFrame JFrameMaker(Component comp, String title){
		final JFrame theFrame = new JFrame(title);
		theFrame.getContentPane().add(BorderLayout.CENTER, comp);
		theFrame.pack();
		theFrame.setVisible(true);
		theFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		theFrame.repaint();
		return theFrame;
	}
}
