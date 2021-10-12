package pbgLecture1lab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class ThreadedGuiForPhysicsEngine {


	public ThreadedGuiForPhysicsEngine() {
	}

	private static JButton jButton_go;
	private static Thread theThread;


	public static void main(String[] args) throws Exception {
		BasicPhysicsEngine game = new BasicPhysicsEngine ();
		final BasicView view = new BasicView(game);
		JComponent mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(view, BorderLayout.CENTER);
		JPanel sidePanel=new JPanel();
		sidePanel.setLayout(new FlowLayout());
		jButton_go=new JButton("Go");
		sidePanel.add(jButton_go);
		mainPanel.add(sidePanel, BorderLayout.WEST);
		// add any new buttons or textfields to side panel here...
		
		JComponent topPanel=new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("JLabel"));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		new JEasyFrame(mainPanel, "Basic Physics Engine");
		
		ActionListener listener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==jButton_go) {
					try {
						// recreate all particles in their original positions:
						final BasicPhysicsEngine game2 = new BasicPhysicsEngine ();
						// Tell the view object to start displaying this new Physics engine instead:
						view.updateGame(game2);
						// start a new thread for the new game object:
						startThread(game2, view);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		jButton_go.addActionListener(listener);
	}
	private static void startThread(final BasicPhysicsEngine game, final BasicView view) throws InterruptedException {
	    Runnable r = new Runnable() {
	         public void run() {
	        	// this while loop will exit any time this method is called for a second time, because 
	    		while (theThread==Thread.currentThread()) {
	    			for (int i=0;i<BasicPhysicsEngine.NUM_EULER_UPDATES_PER_SCREEN_REFRESH;i++) {
	    				game.update();
	    			}
    				view.repaint();
    				Toolkit.getDefaultToolkit().sync();
	    			try {
						Thread.sleep(BasicPhysicsEngine.DELAY);
					} catch (InterruptedException e) {
					}
	    		}
	         }
	     };

	     theThread=new Thread(r);// this will cause any old threads running to self-terminate
	     theThread.start();
	}
	


	
	

}


