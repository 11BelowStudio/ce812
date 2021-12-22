package crappy.math;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * And here's a mutable version, for purposes of intermediate calculations.
 */
public final class M_Rot2D implements Serializable, I_Rot2D{

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
