package crappy.math;

import crappy.utils.containers.ITriplet;

public final class Vect3D implements ITriplet<Double, Double, Double> {

    public final double x;

    public final double y;

    public final double z;

    public final Vect3D IDENTITY = new Vect3D(0, 0, 1);

    public Vect3D(final double x, final double y, final double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vect3D(final I_Vect2D v){
        this.x = v.getX();
        this.y = v.getY();
        this.z = 1;
    }

    public Vect3D(final I_Vect2D v, final double z){
        this.x = v.getX();
        this.y = v.getY();
        this.z = z;
    }

    public Vect3D(final double z){
        this.x = this.y = 0;
        this.z = z;
    }

    @Override
    public Double getFirst() {
        return x;
    }

    @Override
    public Double getSecond() {
        return y;
    }

    @Override
    public Double getThird() {
        return z;
    }
}
