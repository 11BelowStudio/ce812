package crappy.math;

import crappy.math.M_Vect2D;
import crappy.math.Vect2D;

import java.io.Serializable;
import java.lang.Math;

/**
 * Yes, this class was heavily inspired by Rot.java in JBox2D (and the rot struct in Box2D)
 *
 * Credit to Erin Catto for the original Rot implementation,
 * and to Daniel Murphy for the original Java implementation of Rot.
 *
 *
 */
public final class Rot2D implements Serializable, I_Rot2D {

    public final double sin;

    public final double cos;

    public static final Rot2D IDENTITY = new Rot2D();

    public static final Rot2D R90 = new Rot2D(Math.toRadians(90));

    public static final Rot2D R180 = new Rot2D(Math.toRadians(180));

    public static final Rot2D R270 = new Rot2D(Math.toRadians(270));

    /**
     * Please use Rot2D.IDENTITY instead, so there's a bit less mess for the garbage collector to clean up.
     */
    private Rot2D(){
        sin = 0;
        cos = 1;
    }

    public Rot2D(double angle){
        sin = Math.sin(angle);
        cos = Math.cos(angle);
    }

    public Rot2D(final I_Rot2D rot){
        sin = rot.get_sin();
        cos = rot.get_cos();
    }

    public static Rot2D RANDOM_ROTATION(){
        return new Rot2D(I_Rot2D.RANDOM_RADIANS_ANGLE());
    }


    public Rot2D rotateBy(final double rotateByThisAngle){
        return new Rot2D(this.angle() + rotateByThisAngle);
    }

    public M_Vect2D getXAxis(final M_Vect2D xAxis){
        return xAxis.set(cos, sin);
    }

    public M_Vect2D getYAxis(final M_Vect2D yAxis){
        return yAxis.set(-sin, cos);
    }

    public Vect2D getXAxis(){
        return new Vect2D(cos, sin);
    }

    public Vect2D getYAxis(){
        return new Vect2D(-sin, cos);
    }

    public Rot2D mul(final I_Rot2D other){
        return M_Rot2D._GET_RAW().mul(other).finished();
    }

    public Rot2D mulTrans(final I_Rot2D other){
        return M_Rot2D._GET_RAW().mulTrans(other).finished();
    }

    @Override
    public double get_sin(){
        return sin;
    }

    @Override
    public double get_cos() {
        return cos;
    }

    public static Rot2D FROM_DEGREES(final double degrees){
        return new Rot2D(Math.toRadians(degrees));
    }

    public double angle(){ return Math.atan2(sin, cos); }

    @Override
    public Rot2D toRot2D() {
        return this;
    }

    @Override
    public String toString() {
        return "Rot2D{" +
                "sin=" + sin +
                ", cos=" + cos +
                '}';
    }
}
