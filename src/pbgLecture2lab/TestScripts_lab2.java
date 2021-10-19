package pbgLecture2lab;

import static org.junit.Assert.assertEquals;

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
	
}

