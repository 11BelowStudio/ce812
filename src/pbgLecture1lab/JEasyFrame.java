package pbgLecture1lab;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sholtz on 14/01/2016.
 */
public class JEasyFrame  extends JFrame {
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
