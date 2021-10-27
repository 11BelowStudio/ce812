package pbgLecture3lab;

public interface CollidingParticle extends CollidaBall{

    /**
     * Updates the particle with given gravity and deltaT
     * @param gravity
     * @param deltaT
     */
    void update(double gravity, double deltaT);
}
