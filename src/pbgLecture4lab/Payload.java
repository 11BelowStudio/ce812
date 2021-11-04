package pbgLecture4lab;

import java.awt.*;

public class Payload extends BasicParticle implements Towable, CanBeBannedFromRespawning {


    boolean towed;

    boolean bannedFromRespawning;

    public Payload(Vect2D pos, double radius, boolean improvedEuler, Color col, double mass, double dragForce) {
        super(pos, new Vect2D(), radius, improvedEuler, col, mass, dragForce);
        towed = false;
        bannedFromRespawning = false;
    }

    public void respawn(){
        if (bannedFromRespawning){
            return;
        }
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
        if (towed) {
            super.update(gravity, deltaT);
        }
    }

    @Override
    public void setBannedFromRespawning(boolean ban) {
        bannedFromRespawning = ban;
    }
}
