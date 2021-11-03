package pbgLecture4lab;

import java.awt.*;

public class Payload extends BasicParticle implements Towable {


    boolean towed;

    public Payload(Vect2D pos, double radius, boolean improvedEuler, Color col, double mass, double dragForce) {
        super(pos, new Vect2D(), radius, improvedEuler, col, mass, dragForce);
        towed = false;
    }

    public void respawn(){
        setPos(startPos);
        setVel(startVel);
        towed = false;
        inactive = false;
    }

    public boolean isTowed(){
        return towed;
    }

    public void setTowed(boolean t){
        towed = t;
    }

    public void deactivate(){
        inactive = true;
    }

    @Override
    public void update(double gravity, double deltaT) {
        if (!towed){
            return;
        }
        super.update(gravity, deltaT);
    }
}
