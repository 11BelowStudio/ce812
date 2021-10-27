package pbgLecture3lab;

/**
 * An interface for balls that are collidable with each other
 * (the name's supposed to be a pun on the adjective 'collidable' and 'ball')
 * (yes, I know, it's a terrible pun)
 * @author Rachel Lowe
 */
public interface CollidaBall {

    /**
     * Whether or not this collidaball is colliding with the other CollidaBall
     * @param other the other collidaball
     * @return true if they're colliding, otherwise returns false.
     */
    public default boolean collidesWith(CollidaBall other){
        return (
                Vect2D.minus(other.getPos(), getPos()).mag() <= getRadius() + other.getRadius()
        ) && (
                getVel().normalise().scalarProduct(other.getVel().normalise()) <= 0
        );
    }

    /**
     * Static version of the above 'collidesWith' method
     * @param a first CollidaBall
     * @param b other CollidaBall
     * @return true if they collided, false otherwise
     */
    static boolean collidesWith(CollidaBall a, CollidaBall b){
        return (
                Vect2D.minus(b.getPos(), a.getPos()).mag() <= a.getRadius() + b.getRadius()
        ) && (
                a.getVel().normalise().scalarProduct(b.getVel().normalise()) <= 0
        );
    }

    /**
     * Whether or not this collidaball is colliding with the other CollidaBall, using some fancy maths and such
     * @param other the other collidaball
     * @param delta the length of the timestep
     * @return true if they're colliding, otherwise returns false.
     */
    public default boolean collidesWith(CollidaBall other, double delta){//};//{
        // bad implementation at a default method

        final double t = getExactCollisionTime(this, other);
        return (
                (t >= -delta) && // make sure it happened in the most recent timestep
                (t <= 0) && // and not in the future
                (Vect2D.minus(other.getVel(), getVel()).scalarProduct(Vect2D.minus(other.getPos(), getPos())) <= 0)
                // and make sure the two objects were actually moving towards each other
        );
    }

    /**
     * static version of the precise collideswith that has delta
     * @param a first collidaball
     * @param b second collidaball
     * @param delta timestep
     * @return true if they collided, otherwise false
     */
    static boolean collidesWith(CollidaBall a, CollidaBall b, double delta){//};//{
        // bad implementation at a default method

        final double t = getExactCollisionTime(a, b);
        return (
                (t >= -delta) && // make sure it happened in the most recent timestep
                        (t <= 0) && // and not in the future
                        (Vect2D.minus(b.getVel(), a.getVel()).scalarProduct(Vect2D.minus(b.getPos(), a.getPos())) <= 0)
                // and make sure the two objects were actually moving towards each other
        );
    }

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
     * Attempts to implement an elastic collision between two CollidaBalls, using the really nice time scale stuff
     * @param a the first CollidaBall
     * @param b the other CollidaBall
     * @param e the restitution coefficient we're using
     */
    static void implementElasticCollision(CollidaBall a, CollidaBall b, double e){

        final double t = getExactCollisionTime(a, b);
        // that's when the collision actually happened.

        // move a back to where it was when it collided
        final Vect2D aCollidePos = a.getPos().addScaled(a.getVel(),t);

        // move b back to where it was when it collided
        final Vect2D bCollidePos = b.getPos().addScaled(b.getVel(),t);

        // calculate the AB vector (a to b) normalize it to get collision normal
        final Vect2D norm = Vect2D.minus(bCollidePos, aCollidePos).normalise();

        // jb = (e+1) * (Ua.norm - Ub.norm) / (1/Ma + 1/Mb)

        final double jb = ((e+1) * (a.getVel().scalarProduct(norm) - b.getVel().scalarProduct(norm)))/
                ((1/a.getMass()) + (1/b.getMass()));

        // vb = ub + norm*(jb/mb)
        b.setVel(b.getVel().addScaled(norm, jb/b.getMass()));

        // va = ua + norm * (-jb/ma)
        a.setVel(a.getVel().addScaled(norm, -jb/a.getMass()));

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

        // (-b +- sqrt(b^2 - 4ac))/2a

        final Vect2D c = Vect2D.minus(b.getPos(), a.getPos());
        final Vect2D v = Vect2D.minus(b.getVel(), a.getVel());
        final double d = a.getRadius() + b.getRadius();

        final double vv = v.scalarProduct(v);
        final double cv = c.scalarProduct(v);
        final double ccd = c.scalarProduct(c) - Math.pow(d,2);

        // sqrt(b^2 - 4ac)
        final double theThingThatHasThePlusMinus = Math.sqrt(Math.pow(cv,2) - (vv * ccd));

        return Math.min(
                (-cv - theThingThatHasThePlusMinus)/vv,
                (-cv + theThingThatHasThePlusMinus)/vv
        );
    }
}
