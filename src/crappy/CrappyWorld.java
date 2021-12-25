package crappy;

import crappy.math.Vect2D;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The physics world within CRAPPY
 */
public class CrappyWorld {

    /**
     * How many stages are there in each euler update?
     */
    public static final int EULER_SUBSTEPS = 2;

    public static final int EULER_UPDATES_PER_RENDER_ATTEMPT = 50;

    public static final int DELAY = 20;

    public static final double DELTA_T = DELAY / 1000.0 / (double) EULER_UPDATES_PER_RENDER_ATTEMPT;

    public static final Vect2D GRAVITY = new Vect2D(0, -10.15625);


    final Set<CrappyBody> dynamicBodies = new LinkedHashSet<>();

    final Set<CrappyBody> kinematicBodies = new LinkedHashSet<>();

    final Set<CrappyBody> staticBodies = new LinkedHashSet<>();

    final Set<CrappyConnector> connectors = new LinkedHashSet<>();

    public CrappyWorld(){

    }


    public void addBody(final CrappyBody b){
        switch (b.bodyType){
            case STATIC:
                staticBodies.add(b);
                break;
            case KINEMATIC:
                kinematicBodies.add(b);
                break;
            case DYNAMIC:
            default:
                dynamicBodies.add(b);
        }
    }

    public void addConnector(final CrappyConnector c){
        connectors.add(c);
    }

    public void removeBody(final CrappyBody b){
        switch (b.bodyType){
            case STATIC:
                staticBodies.remove(b);
                break;
            case KINEMATIC:
                kinematicBodies.remove(b);
                break;
            case DYNAMIC:
            default:
                dynamicBodies.remove(b);
                break;
        }
    }

    public void removeConnector(final CrappyConnector c){
        connectors.remove(c);
    }






}
