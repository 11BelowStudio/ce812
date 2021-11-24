
package pbgLecture7lab_Jbullet;

import javax.swing.JFrame;

import com.bulletphysics.demos.applet.DemoPanel;
import com.bulletphysics.demos.basic.BasicDemo;
import com.bulletphysics.demos.bsp.BspDemo;
import com.bulletphysics.demos.character.CharacterDemo;
import com.bulletphysics.demos.concave.ConcaveDemo;
import com.bulletphysics.demos.dynamiccontrol.DynamicControlDemo;
import com.bulletphysics.demos.forklift.ForkLiftDemo;
import com.bulletphysics.demos.genericjoint.GenericJointDemo;
import com.bulletphysics.demos.movingconcave.MovingConcaveDemo;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.vehicle.VehicleDemo;


public class JBulletDemoAppStarterForCE812  {
	public static void main(String[] args) {
		DemoPanel demoPanel=new DemoPanel();
		
		DemoApplication demoApp;
		switch(0) {
			case 0:demoApp= new BasicDemo(demoPanel.getGL());break;
			case 1:demoApp= new GenericJointDemo(demoPanel.getGL());break;
			case 2:demoApp= new BspDemo(demoPanel.getGL());break;
			case 3:demoApp= new ConcaveDemo(demoPanel.getGL());break;
			case 4:demoApp= new VehicleDemo(demoPanel.getGL());break;
			case 5:demoApp= new DynamicControlDemo(demoPanel.getGL());break;
			case 6:demoApp= new MovingConcaveDemo(demoPanel.getGL());break;
			case 7:demoApp= new ForkLiftDemo(demoPanel.getGL());break;
			case 8:demoApp= new CharacterDemo(demoPanel.getGL());break;
			default: throw new IllegalArgumentException();
		}
		
		
		try {
			demoApp.initPhysics();
		} catch (Exception e) {
			e.printStackTrace();
		}
		demoPanel.runDemo(demoApp);

		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.add(demoPanel);
		frm.setSize(600, 450+50);
		frm.setVisible(true);

		demoPanel.requestFocusInWindow();

	}


}
