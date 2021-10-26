package pbgLecture3lab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Test;

public class TestScripts_lab3 {

	// A test class to help you check if you've programmed some basic steps correctly
	// DO NOT alter any code in this file.  If you feel the need to alter it then you are doing something wrong elsewhere
	//
	// If this class is causing compile problems then make sure the JUnit4 jars are in your classpath 
	// (i.e. including junit.jar and org.hamcrest.core_1.3.0.v201303031735.jar) 
	// You can add JUnit quickly to your class path in Eclipse by right-clicking on line "18 @Test" and choose "Quick Fix: Add JUnit to Build Path"
	
	@Test
	public void test1_elasticCollision() {
		BasicParticle p1=new BasicParticle(2.4,4.0,-7.9,1.5,0.6,false, Color.BLACK,10);
		BasicParticle p2=new BasicParticle(1.8,4.1,0,-8,0.4,false, Color.BLACK,4);
		assertTrue("Collides with", p1.collidesWith(p2));
		BasicParticle.implementElasticCollision(p1, p2, 0.7);
		assertEquals("p1 pos unchanged",0.0,Vect2D.minus(p1.getPos(), new Vect2D(2.4,4)).mag(),1e-6);
		assertEquals("p2 pos unchanged",0.0,Vect2D.minus(p2.getPos(), new Vect2D(1.8,4.1)).mag(),1e-6);
		double c1=Vect2D.minus(p1.getVel(), new Vect2D(-3.4,0.8)).mag();
		assertEquals("p1 vel after collision",0.05039064166874663,c1,1e-6);
		double c2=Vect2D.minus(p2.getVel(), new Vect2D(-11.2,-6.1)).mag();
		assertEquals("p2 vel after collision",0.032900760037297694,c2,1e-6);
	}
	
		
}


