package crappy;

import crappy.utils.IPair;

import java.io.Serializable;
import java.lang.Math;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    M_Vect2D getXAxis(final M_Vect2D xAxis){
        return xAxis.set(cos, sin);
    }

    M_Vect2D getYAxis(final M_Vect2D yAxis){
        return yAxis.set(-sin, cos);
    }

    public Vect2D getXAxis(){
        return new Vect2D(cos, sin);
    }

    public Vect2D getYAxis(){
        return new Vect2D(-sin, cos);
    }

    public Rot2D mul(final I_Rot2D other){
        return M_Rot2D._GET_RAW().mul(this, other).finished();
    }

    public Rot2D mulTrans(final I_Rot2D other){
        return M_Rot2D._GET_RAW().mulTrans(this, other).finished();
    }

    @Override
    public double get_sin(){
        return sin;
    }

    @Override
    public double get_cos() {
        return cos;
    }
}

/**
 * An interface for the mutable/immutable Rot2D objects.
 */
interface I_Rot2D extends IPair<Double, Double> {

    double get_sin();

    double get_cos();

    static double GET_ANGLE(final I_Rot2D rot){
        return Math.atan2(rot.get_sin(), rot.get_cos());
    }

    @Override
    default Double getFirst() {
        return get_sin();
    }

    @Override
    default Double getSecond(){
        return get_cos();
    }
}

/**
 * And here's a mutable version, for purposes of intermediate calculations.
 */
final class M_Rot2D implements Serializable, I_Rot2D{

    double sin;

    double cos;

    private static final Queue<M_Rot2D> POOL = new ConcurrentLinkedQueue<>();


    static {
        for (int i = 0; i < 5; i++) {
            POOL.add(new M_Rot2D());
        }
    }

    static M_Rot2D _GET_RAW(){
        M_Rot2D candidate = POOL.poll();
        if (candidate != null){
            return candidate;
        }
        return new M_Rot2D();
    }

    static M_Rot2D GET(){
        return _GET_RAW().setIdentity();
    }

    static M_Rot2D GET(final double angle){
        return _GET_RAW().set(angle);
    }

    static M_Rot2D GET(final I_Rot2D rot){
        return _GET_RAW().set(rot);
    }


    @Override
    public double get_cos() {
        return cos;
    }

    @Override
    public double get_sin() {
        return sin;
    }

    private M_Rot2D(){
        this.setIdentity();
    }

    M_Rot2D set(final I_Rot2D r){
        this.sin = r.get_sin();
        this.cos = r.get_cos();
        return this;
    }

    M_Rot2D set(double angle){
        this.sin = Math.sin(angle);
        this.cos = Math.cos(angle);
        return this;
    }

    void dispose(){
        POOL.add(this);
    }

    Rot2D finished(){
        final Rot2D out = new Rot2D(this);
        POOL.add(this);
        return out;
    }

    M_Rot2D setIdentity(){
        sin = 0;
        cos = 1;
        return this;
    }

    M_Rot2D mul(final I_Rot2D a, final I_Rot2D b){
        sin = a.get_sin() * b.get_cos() + a.get_cos() * b.get_sin();
        cos = a.get_cos() * b.get_cos() - a.get_sin() * b.get_sin();
        return this;
    }

    M_Rot2D mulTrans(final I_Rot2D a, final I_Rot2D b){
        sin = a.get_cos() * b.get_sin() - a.get_sin() * b.get_cos();
        cos = a.get_cos() * b.get_cos() + a.get_sin() * b.get_sin();
        return this;
    }


}


