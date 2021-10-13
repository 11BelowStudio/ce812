package pbgLecture1lab;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;


public class ExactRangeInfoGuiForPhysicsEngine {


	public ExactRangeInfoGuiForPhysicsEngine() {
	}

	private static JButton jButton_go;
	private static Thread theThread;

	private static JFormattedTextField speedField;

	private static JFormattedTextField angleField;

	private static JFormattedTextField gravityField;

	private static JFormattedTextField deltaField;

	private static JLabel ballToStringLabel;

	private static double maxY = 0;

	private static JLabel maxHeightLabel;

	private static double maxX = 0;

	private static JLabel maxDistLabel;

	private static double timeElapsed = 0;
	private static JLabel timeElapsedLabel;

	private static JLabel projectedDistLabel;

	private static JLabel projectedHeightLabel;
	private static JLabel projectedTimeLabel;



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
		speedField.setText("10");
		sidePanel.add(speedField);


		sidePanel.add(new JLabel(" "));
		sidePanel.add(new JLabel("ANGLE:"));
		angleField = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#0.####")));
		angleField.setText("30");
		sidePanel.add(angleField);

		sidePanel.add(new JLabel(" "));
		sidePanel.add(new JLabel("GRAVITY:"));
		gravityField = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#0.####")));
		gravityField.setText("9.8");
		sidePanel.add(gravityField);

		sidePanel.add(new JLabel(" "));
		sidePanel.add(new JLabel("DELTA:"));
		deltaField = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#0.####")));
		deltaField.setText("0.002");
		sidePanel.add(deltaField);

		sidePanel.add(new JLabel(" "));
		jButton_go=new JButton("Go!");
		sidePanel.add(jButton_go);

		for (int i = 0; i < 5; i++) {
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

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2,4));

		infoPanel.add(new JLabel("predicted: "));

		projectedDistLabel = new JLabel("distance");
		infoPanel.add(projectedDistLabel);

		projectedHeightLabel = new JLabel("height");
		infoPanel.add(projectedHeightLabel);

		projectedTimeLabel = new JLabel("time");
		infoPanel.add(projectedTimeLabel);

		infoPanel.add(new JLabel("actual: "));

		maxDistLabel = new JLabel("distance");
		infoPanel.add(maxDistLabel);

		maxHeightLabel = new JLabel("height");
		infoPanel.add(maxHeightLabel);

		timeElapsedLabel = new JLabel("time");
		infoPanel.add(timeElapsedLabel);

		topPanel.add(infoPanel);


		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		new JEasyFrame(mainPanel, "exact range GUI moment");
		
		ActionListener listener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==jButton_go) {



					try {

						double launchSpeed = Double.parseDouble(speedField.getText());

						double launchAngle = Double.parseDouble(angleField.getText());

						double gravityValue = Double.parseDouble(gravityField.getText());

						double deltaValue = Double.parseDouble(deltaField.getText());

						double[] hdt_info = CALC_MAX_HEIGHT_DIST_AND_TIME(launchAngle, launchSpeed, gravityValue);

						projectedHeightLabel.setText("y " + String.valueOf(hdt_info[0]));
						projectedDistLabel.setText("x " + String.valueOf(hdt_info[1]));
						projectedTimeLabel.setText("t " + String.valueOf(hdt_info[2]));

						maxX = 0;
						maxY = 0;
						timeElapsed = 0;

						// recreate all particles in their original positions:
						final BasicPhysicsEngine game2 = new BasicPhysicsEngine(launchSpeed, launchAngle, gravityValue, deltaValue);
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

	    			if (ballPos.x < 0 || ballPos.y <= 0){
	    				keepGoing = false;
					} else {

	    				timeElapsed += (game.getLocalDelay() / 1000.0);

	    				timeElapsedLabel.setText("t " + String.valueOf(timeElapsed));

						if (ballPos.x >= maxX){
							maxX = ballPos.x;
							maxDistLabel.setText("x " + String.valueOf(maxX));
						}
						if (ballPos.y > maxY){
							maxY = ballPos.y;
							maxHeightLabel.setText("y " + String.valueOf(maxY));
						}

					}



    				view.repaint();
    				Toolkit.getDefaultToolkit().sync();
	    			try {
						Thread.sleep(game.getLocalDelay());
					} catch (InterruptedException e) {
					}
	    		}
	         }
	     };

	     theThread=new Thread(r);// this will cause any old threads running to self-terminate
	     theThread.start();
	}


	/**
	 * Calculates the maximum height, travel distance, and range of the ball being fired
	 * @param launchAngle angle of the launch (in degrees)
	 * @param launchSpeed speed of the launch (in degrees)
	 * @param gravity the gravity being used
	 * @return a 3-index array of doubles, in the form [max height, total distance, time in air]
	 */
	public static double[] CALC_MAX_HEIGHT_DIST_AND_TIME(double launchAngle, double launchSpeed, double gravity){

		final double launchRadians = Math.toRadians(launchAngle);

		final double launchUpVel = launchSpeed * Math.sin(launchRadians);
		final double launchSideVel = launchSpeed * Math.cos(launchRadians);

		// v = u + (a*t)
		// 0 = up + (-grav * t)
		// up - (grav * t) = 0
		// up = (grav * t)
		// up/grav = t
		final double upTime = launchUpVel/gravity;

		System.out.println("up time " + String.valueOf(upTime));

		// s = ((u + v)*t)/2

		final double maxHeight = ((launchUpVel + 0) * upTime)/2;

		System.out.println("max height " + String.valueOf(maxHeight));

		// s = ut + (a/2) * (t*t)
		// maxHeight = 0 + (grav/2) * (t*t)
		// maxHeight/(grav/2) = (t*t)
		// sqrt(maxHeight/(grav/2)) = t

		final double downTime = Math.sqrt(maxHeight/(gravity/2));

		System.out.println("down time " + String.valueOf(downTime));

		final double totalTime = upTime + downTime;

		System.out.println("total time " + String.valueOf(totalTime));

		final double maxDist = launchSideVel * totalTime;

		System.out.println("x dist " + String.valueOf(maxDist));

		return new double[]{maxHeight, maxDist, totalTime};

	}


	
	

}


