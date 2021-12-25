package crappy.math;

import crappy.internals.CrappyWarning;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * And here's a mutable version, for purposes of intermediate calculations.
 */
public final class M_Rot2D implements Serializable, I_Rot2D{

    double sin;

    double cos;

    @SuppressWarnings("StaticCollection")
    private static final Queue<M_Rot2D> POOL = new ConcurrentLinkedQueue<>();

    static {
        for (int i = 0; i < 5; i++) {
            POOL.add(new M_Rot2D());
        }
    }

    public static M_Rot2D _GET_RAW(){
        M_Rot2D candidate = POOL.poll();
        if (candidate != null){
            return candidate;
        }
        return new M_Rot2D();
    }

    /**
     * Creates a mutable M_Rot2D, not using the pool.
     * PLEASE ONLY USE THIS IF YOU'RE ABSOLUTELY SURE THAT YOU DON'T WANT TO GET ONE FROM THE POOL!
     * @return a new M_Rot2D.
     * @deprecated intentionally misusing deprecated tag here, in an attempt to get your IDE to complain if you use this.
     */
    @CrappyWarning("PLEASE USE THE OTHER GET METHODS OF M_ROT2D INSTEAD!")
    @Deprecated
    public static M_Rot2D __GET_NONPOOLED(){
        return new M_Rot2D();
    }

    public static M_Rot2D GET(){
        return _GET_RAW().setIdentity();
    }

    public static M_Rot2D GET(final double angle){
        return _GET_RAW().set(angle);
    }

    public static M_Rot2D GET(final I_Rot2D rot){
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

    public M_Rot2D set(final I_Rot2D r){
        this.sin = r.get_sin();
        this.cos = r.get_cos();
        return this;
    }

    public M_Rot2D set(final double angle){
        this.sin = Math.sin(angle);
        this.cos = Math.cos(angle);
        return this;
    }

    public void dispose(){
        POOL.add(this);
    }

    public Rot2D finished(){
        final Rot2D out = new Rot2D(this);
        POOL.add(this);
        return out;
    }

    public M_Rot2D setIdentity(){
        sin = 0;
        cos = 1;
        return this;
    }

    public M_Rot2D mul(final I_Rot2D other){
        final double new_cos = cos * other.get_cos() - sin * other.get_sin();
        sin = sin * other.get_cos() + cos * other.get_sin();
        cos = new_cos;
        return this;
    }

    public M_Rot2D mulTrans(final I_Rot2D other){
        final double new_cos = cos * other.get_cos() + sin * other.get_sin();
        sin = cos * other.get_sin() - sin * other.get_cos();
        cos = new_cos;
        return this;
    }

    public M_Rot2D rotateBy(final double angle){
        return this.set(this.angle() + angle);
    }

    public double angle(){ return Math.atan2(sin, cos); }


}
