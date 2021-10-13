package pbgLecture1lab;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;


public class ThreadedGuiForPhysicsEngine {


	public ThreadedGuiForPhysicsEngine() {
	}

	private static JButton jButton_go;
	private static Thread theThread;

	private static JFormattedTextField speedField;

	private static JFormattedTextField angleField;

	private static JLabel ballToStringLabel;

	private static double maxY = 0;

	private static JLabel maxHeightLabel;

	private static double maxX = 0;

	private static JLabel maxDistLabel;


	public static void main(String[] args) throws Exception {
		BasicPhysicsEngine game = new BasicPhysicsEngine ();
		final BasicView view = new BasicView(game);
		JComponent mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(view, BorderLayout.CENTER);
		final JPanel sidePanel=new JPanel();

		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		//sidePanel.setLayout(new GridLayout(3,1));


		sidePanel.add(new JLabel("SPEED:"));
		speedField = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#####0.####")));
		sidePanel.add(speedField);


		sidePanel.add(new JLabel(" "));
		sidePanel.add(new JLabel("ANGLE:"));
		angleField = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#0.####")));
		sidePanel.add(angleField);

		sidePanel.add(new JLabel(" "));
		jButton_go=new JButton("Go!");
		sidePanel.add(jButton_go);

		for (int i = 0; i < 10; i++) {
			sidePanel.add(new JLabel(" "));
		}

		mainPanel.add(sidePanel, BorderLayout.WEST);
		// add any new buttons or textfields to side panel here...

		// TODO: launch speed + launch angle input fields

		// TODO: use inputs from those input fields to launch the ball
		
		JComponent topPanel=new JPanel();
		topPanel.setLayout(new FlowLayout());
		//topPanel.add(new JLabel("some fancy GUI I guess"));
		ballToStringLabel = new JLabel("Ball position");
		topPanel.add(ballToStringLabel);

		maxDistLabel = new JLabel("Furthest distance");
		topPanel.add(maxDistLabel);

		maxHeightLabel = new JLabel("Highest height");
		topPanel.add(maxHeightLabel);


		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		new JEasyFrame(mainPanel, "Basic Physics Engine");
		
		ActionListener listener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==jButton_go) {



					try {

						double launchSpeed = Double.parseDouble(speedField.getText());

						double launchAngle = Double.parseDouble(angleField.getText());

						// recreate all particles in their original positions:
						final BasicPhysicsEngine game2 = new BasicPhysicsEngine(launchSpeed, launchAngle);
						// Tell the view object to start displaying this new Physics engine instead:
						view.updateGame(game2);
						// start a new thread for the new game object:
						startThread(game2, view);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} catch (NumberFormatException e2){
						JOptionPane.showMessageDialog(
								mainPanel.getRootPane(),
								"oh no you didn't enter some numbers correctly",
								"can't launch that >:(",
								JOptionPane.WARNING_MESSAGE
						);
					}
				}
			}
		};
		jButton_go.addActionListener(listener);
	}
	private static void startThread(final BasicPhysicsEngine game, final BasicView view) throws InterruptedException {
	    Runnable r = new Runnable() {
	         public void run() {

	         	boolean keepGoing = true;

	        	// this while loop will exit any time this method is called for a second time, because 
	    		while (theThread==Thread.currentThread() && keepGoing) {
	    			for (int i=0;i<BasicPhysicsEngine.NUM_EULER_UPDATES_PER_SCREEN_REFRESH;i++) {
	    				game.update();
	    			}

	    			Vect2D ballPos = game.getPosition();

	    			ballToStringLabel.setText(ballPos.toString());

	    			if (ballPos.x < 0){
	    				keepGoing = false;
					} else if (ballPos.x > maxX){
	    				maxX = ballPos.x;
	    				maxDistLabel.setText(String.valueOf(maxX));
					}

					if (ballPos.y < 0){
						keepGoing = false;
					} else if (ballPos.y > maxY){
						maxY = ballPos.y;
						maxHeightLabel.setText(String.valueOf(maxY));
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


