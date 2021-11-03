package pbgLecture4lab;

public interface Updatable {

    public void update(double gravity, double deltaT);

    default void resetTotalForce(){};
}
