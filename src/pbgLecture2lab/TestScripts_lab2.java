package pbgLecture2lab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.awt.Color;

import org.junit.Test;

public class TestScripts_lab2 {

	// A test class to help you check if you've programmed some basic steps correctly
	// DO NOT alter any code in this file.  If you feel the need to alter it then you are doing something wrong elsewhere
	//
	// If this class is causing compile problems then make sure the JUnit4 jars are in your classpath 
	// (i.e. including junit.jar and org.hamcrest.core_1.3.0.v201303031735.jar) 
	// You can add JUnit quickly to your class path in Eclipse by right-clicking on line "18 @Test" and choose "Quick Fix: Add JUnit to Build Path"
	
	@Test
	public void test1_basicVect2DOperations() {
		assertEquals("scalar product check", 11, new Vect2D(1,2).scalarProduct(new Vect2D(3,4)),1e-6);
		assertEquals("magnitude check", 5, new Vect2D(-3,4).mag(),1e-6);
		assertEquals("rotate90degreesAnticlockwise check", 13, new Vect2D(4,5).rotate90degreesAnticlockwise().scalarProduct(new Vect2D(3,7)),1e-6);
	}

	@Test
	public void test2_bounceOffCurvedBarrier() {
		final int SCREEN_HEIGHT = 680;
		final int SCREEN_WIDTH = 640;
		final double WORLD_WIDTH=10;//metres
		final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);
		AnchoredBarrier_Curve barrier_inwardNormal = new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 180.0,true, Color.WHITE);
		final Vect2D vel = new Vect2D(-6.0,15.6);

		Vect2D returnVel = barrier_inwardNormal.calculateVelocityAfterACollision(new Vect2D(4.2,10.2),vel, 1.0);
		assertEquals("basic collision off curved barrier check",-69.99244342711442, returnVel.scalarProduct(new Vect2D(9,4)),1e-6);
	}

	@Test
	public void test3_bounceOffCurvedBarrier_doubleReflectionBugCheck() {
		final int SCREEN_HEIGHT = 680;
		final int SCREEN_WIDTH = 640;
		final double WORLD_WIDTH=10;//metres
		final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);
		final Vect2D vel = new Vect2D(-6.0,15.6);
		AnchoredBarrier_Curve barrier_inwardNormal = new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 180.0,true, Color.WHITE);

		AnchoredBarrier_Curve barrier_outwardNormal = new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 180.0,false, Color.WHITE);

		Vect2D returnVel3 = barrier_outwardNormal.calculateVelocityAfterACollision(new Vect2D(4.2,10.2),vel.mult(-1), 1.0);
		assertEquals("normal pointing outwards collision check",69.99244342711442, returnVel3.scalarProduct(new Vect2D(9,4)),1e-6);

		Vect2D returnVel2 = barrier_inwardNormal.calculateVelocityAfterACollision(new Vect2D(4.2,10.2),vel.mult(-1), 1.0).addScaled(vel, 1);
		assertEquals("double reflection bug check (inward normal)",0, returnVel2.mag(),1e-6);
		Vect2D returnVel4 = barrier_outwardNormal.calculateVelocityAfterACollision(new Vect2D(4.2,10.2),vel.mult(1), 1.0).addScaled(vel, -1);
		assertEquals("double reflection bug check (outward normal)",0, returnVel4.mag(),1e-6);
	}

	@Test
	public void test4_coefficientOfRestitutionCheck() {
		final int SCREEN_HEIGHT = 680;
		final int SCREEN_WIDTH = 640;
		final double WORLD_WIDTH=10;//metres
		final double WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);
		AnchoredBarrier_Curve barrier_inwardNormal = new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0, 180.0,true, Color.WHITE);
		final Vect2D vel = new Vect2D(-6.0,15.6);

		Vect2D returnVel = barrier_inwardNormal.calculateVelocityAfterACollision(new Vect2D(4.2,10.2),vel, 0.6);
		assertEquals("check of coefficient of restitution (e).  Note that you have to use the argument e in the function calculateVelocityAfterACollision for this check to work.",-54.31395474169153 , returnVel.scalarProduct(new Vect2D(9,4)),1e-6);
	}

	@Test
	public void test5_checkCollisionWithBarrierWorksProperly() {
		AnchoredBarrier_StraightLine barrier1 = new AnchoredBarrier_StraightLine(10, 10.6, 0, 10.6, Color.WHITE);
		assertTrue("Basic straight-line barrier check",barrier1.isCircleCollidingBarrier(new Vect2D(4.2,10.3), 0.4));
		AnchoredBarrier_StraightLine barrier2 = new AnchoredBarrier_StraightLine(5, 4.8, 0, 6.6, Color.WHITE);
		assertFalse("Barrier length check not implemented correctly",barrier2.isCircleCollidingBarrier(new Vect2D(6.4,3.9), 0.4));
	}
	
}

