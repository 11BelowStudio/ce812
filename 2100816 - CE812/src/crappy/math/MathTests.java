package crappy.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class MathTests {

    @Test
    public void testMethodA(){
        assertEquals("scalar product check", 11, new Vect2D(1,2).dot(new Vect2D(3,4)),1e-6);
        assertEquals("magnitude check", 5, new Vect2D(-3,4).mag(),1e-6);
        assertEquals("rotate90degreesAnticlockwise check", 13, new Vect2D(4,5).rotate90degreesAnticlockwise().dot(new Vect2D(3,7)),1e-6);
    }

    @Test
    public void test3_vectorRotate() {
        Vect2D v1=new Vect2D(1,1).rotate(new Rot2D(0.2));
        assertEquals("vect2D.rotate check preserves magnitude",Math.sqrt(2),v1.mag(),1e-6);
        assertEquals("vect2D.rotate check x result",0.78139724,v1.x,1e-6);
        assertEquals("vect2D.rotate check y result",1.178735908,v1.y,1e-6);
    }
}
