package pbgLecture3lab.stuffThatsBeenPutToOneSide;

import pbgLecture3lab.CollidaBall;
import pbgLecture3lab.Vect2D;

import java.awt.*;

public class SnookerBall implements CollidaBall {


    private Vect2D pos;
    private Vect2D vel;

    private final Vect2D initialPosition;

    private final double radius;
    private final int SCREEN_RADIUS;
    private final double mass;
    private Color theColour;

    private static final double DRAG = 0.9;

    private boolean potted = false;

    private final double value;


    public SnookerBall(Vect2D pos, Vect2D vel, double radius, Color col, double mass, double value){
        setPos(pos);
        setVel(vel);
        initialPosition = pos;
        this.radius = radius;
        this.SCREEN_RADIUS=Math.max(SnookerEngine.convertWorldLengthToScreenLength(radius),1);
        this.theColour=col;
        potted = false;
        this.value = value;
        this.mass = mass;
    }

    public SnookerBall(double sx, double sy, double vx, double vy, double radius, Color col, double mass, double value) {
        this(new Vect2D(sx,sy),new Vect2D(vx,vy), radius, col, mass, value);
    }

    public void reset(){
        potted = false;
        this.pos = initialPosition;
    }

    /**
     * Whether or not this SnookerBall is colliding with the other SnookerBall
     * @param other the other SnookerBall
     * @return true if they're colliding, otherwise returns false.
     */
    public boolean collidesWith(SnookerBall other){
        return (!potted) && (!other.isPotted()) && (CollidaBall.collidesWith(this, other));
    }

    /**
     * Whether or not this snookerBall is colliding with the other snookerBall, using some fancy maths and such
     * @param other the other snookerBall
     * @param delta the length of the timestep
     * @return true if they're colliding, otherwise returns false.
     */
    public boolean collidesWith(SnookerBall other, double delta){//};//{

        if (isPotted() || other.isPotted()){
            return false;
        } else {
            return CollidaBall.collidesWith(this, other, delta);
        }
    }

    public void update(double deltaT) {
        if (potted){
            return;
        }
        Vect2D acc= new Vect2D();

        // improved euler
        //Vect2D pos2=getPos().addScaled(getVel(), deltaT);// in theory this could be used,e.g. if acc2 depends on pos - but in this constant gravity field it will not be relevant
        Vect2D vel2=getVel().addScaled(acc, deltaT);
        Vect2D velAv=vel2.add(getVel()).mult(0.5);
        Vect2D acc2= acc;//same as acc in this simple example of constant acceleration, but that won't generally be true
        Vect2D accAv=acc2.add(acc).mult(0.5);
        setPos(getPos().addScaled(velAv, deltaT));
        setVel(getVel().addScaled(accAv, deltaT));

        setVel(getVel().mult(DRAG));
    }

    public boolean isPotted(){
        return potted;
    }

    public double getValue(){
        return value;
    }

    public void setPotted(final boolean newPot){
        potted = newPot;
    }


    public void draw(Graphics2D g) {
        if (potted){
            return;
        }
        int x = SnookerEngine.convertWorldXtoScreenX(getPos().x);
        int y = SnookerEngine.convertWorldYtoScreenY(getPos().y);
        g.setColor(theColour);
        g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
    }

    @Override
    public Vect2D getPos() {
        return pos;
    }

    @Override
    public Vect2D getVel() {
        return vel;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public void setVel(Vect2D newVel) {
        vel = newVel;
    }

    public void setPos(Vect2D pos) {
        this.pos = pos;
    }

    @Override
    public double getMass() {
        return mass;
    }
}
