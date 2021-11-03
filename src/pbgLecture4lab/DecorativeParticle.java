package pbgLecture4lab;

import java.awt.*;

public class DecorativeParticle extends BasicParticle{


    static final double DEFAULT_EXISTENCE_LENGTH = 10.0;

    private double seconds_until_despawning;

    public DecorativeParticle(Vect2D pos, Vect2D vel, double radius, boolean improvedEuler, Color col, double mass) {
        this(pos, vel, radius, true, col, mass, 0.5);
    }


    public DecorativeParticle(Vect2D pos, Vect2D vel, double radius, boolean improvedEuler, Color col, double mass, double dragForce) {
        super(pos, vel, radius, true, col, mass, dragForce);
        seconds_until_despawning = DEFAULT_EXISTENCE_LENGTH;
    }

    @Override
    public void update(double gravity, double deltaT) {
        super.update(gravity, deltaT);
        seconds_until_despawning -= deltaT;
        if (seconds_until_despawning < 0){
            inactive = true;
        }
    }
}
