package pbgLecture2lab;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JFrame;

public class JEasyFrame extends JFrame {
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
}
