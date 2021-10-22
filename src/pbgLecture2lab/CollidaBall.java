package pbgLecture2lab;

/**
 * An interface for balls that are collidable with each other
 * (the name's supposed to be a pun on the adjective 'collidable' and 'ball')
 * (yes, I know, it's a terrible pun)
 */
public interface CollidaBall {

    /**
     * Whether or not this collidaball is colliding with the other CollidaBall
     * @param other the other collidaball
     * @return true if they're colliding, otherwise returns false.
     */
    public boolean collidesWith(CollidaBall other);//{
        // bad implementation at a default method

        //final double t = getExactCollisionTime(this, other);
        //return (t >= 0) && (t < 1) && (getVel().normalise().scalarProduct(other.getVel().normalise()) < 0);
    //}

    /**
     * Obtains position
     * @return position of this CollidaBall
     */
    Vect2D getPos();

    /**
     * Obtains velocity
     * @return velocity of this CollidaBall
     */
    Vect2D getVel();

    /**
     * Obtains radius
     * @return the radius of this CollidaBall
     */
    double getRadius();

    /**
     * Give this CollidaBall a new velocity
     * @param newVel the new velocity to give it
     */
    void setVel(Vect2D newVel);

    /**
     * Obtains the mass of this CollidaBall
     * @return mass of the CollidaBall
     */
    double getMass();

    /**
     * Returns the next position of the CollidaBall, expressed as quarter steps.
     * [1/4 step, 2/4 step, 3/4 step, next pos]
     *
     * CollidaBall implementations are responsible for coming up with their own method of updating
     * this array.
     * @return the next 4 predicted quarter-steps for this CollidaBall
     */
    Vect2D[] getQuarterSteps();


    /**
     * Attempts to implement an elastic collision between two CollidaBalls
     * @param a the first CollidaBall
     * @param b the other CollidaBall
     * @param e the restitution coefficient we're using
     */
    static void implementElasticCollision(CollidaBall a, CollidaBall b, double e){

        final double t = getExactCollisionTime(a, b);
        // that's when the collision actually happened.

        final Vect2D aCollidePos = a.getPos().addScaled(a.getVel(),t);
        final Vect2D bCollidePos = b.getPos().addScaled(b.getVel(),t);

        final Vect2D norm = Vect2D.minus(bCollidePos, aCollidePos).normalise();

        final double jb = ((e+1) * (a.getVel().scalarProduct(norm) - b.getVel().scalarProduct(norm)))/
                ((1/a.getMass()) + (1/b.getMass()));

        b.setVel(b.getVel().addScaled(norm, jb/b.getMass()));

        a.setVel(a.getVel().addScaled(norm, jb/a.getMass()));

    }

    /**
     * Finds the timestep t where CollidaBalls a and b actually collide with each other
     * If t is greater than 1, they didn't collide in this timestep.
     * @param a the first collidaball
     * @param b the other collidaball
     * @return the exact time (relative to their current velocities) when they intersect each other
     */
    static double getExactCollisionTime(CollidaBall a, CollidaBall b){

        // A moves according to  x = xa + Va(t)
        // B moves according to  x = xb + Vb(t)
        // A->B: (xb - xa) + (Vb - Va)t
        //     : c + vt
        //         c = xb - xa
        //         v = vb - va
        // ||c + vt|| = d
        //      where d = radius a + radius b
        //      find t.

        // ||c + vt|| = d
        // (c+vt).(c+vt) = d^2
        // c.c + (2c.v)t + (v.v)t^2 = d^2
        // (v.v)t^2 + (2c.v)t + (c.c - d^2) = 0

        // find t with quadratic formula (choosing negative root), substituting in
        //  a (v.v)
        //  b (c.v)
        //  c (c.c - d^2)

        final Vect2D c = Vect2D.minus(b.getPos(), a.getPos());
        final Vect2D v = Vect2D.minus(b.getVel(), a.getVel());
        final double d = a.getRadius() + b.getRadius();

        final double vv = v.scalarProduct(v);
        final double cv = c.scalarProduct(v);
        final double ccd = c.scalarProduct(c) - Math.pow(d,2);

        return (-cv - Math.sqrt(Math.pow(cv,2) - (vv * ccd)))/vv;
    }
}
