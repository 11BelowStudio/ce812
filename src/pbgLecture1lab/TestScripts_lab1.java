package pbgLecture1lab;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

/**
 * A test class to help you check if you've programmed vectors and Euler and Improved Euler correctly
 * DO NOT alter any code in this file.  If you feel the need to alter it then you are doing something wrong elsewhere
 *
 * If this class is causing compile problems then make sure the JUnit4 jars are in your classpath
 * (i.e. including junit.jar and org.hamcrest.core_1.3.0.v201303031735.jar)
 *  You can add JUnit quickly to your class path in Eclipse by right-clicking on line "18 @Test" and choose "Quick Fix: Add JUnit to Build Path"
 */
public class TestScripts_lab1 {

	
	@Test
	public void test1_ConstantVelocityMotion() {
		BasicPhysicsEngine.GRAVITY=0;// hack way to switch gravity off to check motion in a straight line is working.
		assertEquals("DELTA_T is not what was expected for this test to work",0.002, BasicPhysicsEngine.DELTA_T,1e-6);
		BasicParticle p = new BasicParticle(5.0,5.0,-0.3,0.4,1, false,Color.BLACK);
		for (int i=0;i<10;i++)
			p.update();
		System.out.println("x="+p.getX()+", y="+p.getY());
		assertEquals("x_coordinate", 4.994, p.getX(),1e-6);
		assertEquals("y_coordinate", 5.008, p.getY(), 1e-6);//DO NOT CHANGE THIS LINE!
	}

	@Test
	public void test2_EulerMethodAccelerationGravity() {
		BasicPhysicsEngine.GRAVITY=9.8;
		assertEquals("DELTA_T is not what was expected for this test to work",0.002, BasicPhysicsEngine.DELTA_T,1e-6);
		final boolean improvedEuler=false;		
		BasicParticle p = new BasicParticle(5.0,5.0,-0.3,0.4,1, improvedEuler,Color.BLACK);
		for (int i=0;i<10;i++)
			p.update();
		System.out.println("x="+p.getX()+", y="+p.getY());
		assertEquals("x_coordinate", 4.994, p.getX(),1e-6);
		assertEquals("y_coordinate", 5.006236, p.getY(), 1e-6);//DO NOT CHANGE THIS LINE!
	}
	@Test
	public void test3_ImprovedEulerMethodAccelerationGravity() {
		BasicPhysicsEngine.GRAVITY=9.8;
		assertEquals("DELTA_T is not what was expected for this test to work",0.002, BasicPhysicsEngine.DELTA_T,1e-6);
		final boolean improvedEuler=true;
		BasicParticle p = new BasicParticle(5.0,5.0,-0.3,0.4,1, improvedEuler,Color.BLACK);
		for (int i=0;i<10;i++)
			p.update();
		System.out.println("x="+p.getX()+", y="+p.getY());
		assertEquals("x_coordinate", 4.994, p.getX(),1e-6);
		assertEquals("y_coordinate", 5.00604, p.getY(), 1e-6);//DO NOT CHANGE THIS LINE!
	}
}

