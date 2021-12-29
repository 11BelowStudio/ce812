package crappy;

import crappy.collisions.AABBQuadTreeTools;
import crappy.collisions.Crappy_AABB;
import crappy.math.Vect2D;
import crappy.utils.LazyFinal;

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

    public static final double DELTA_T = DELAY / 1000.0 / (double) EULER_UPDATES_PER_RENDER_ATTEMPT / EULER_SUBSTEPS;

    public static final Vect2D GRAVITY = new Vect2D(0, -10.15625);


    final Set<CrappyBody> dynamicBodies = new LinkedHashSet<>();

    final Set<CrappyBody> kinematicBodies = new LinkedHashSet<>();

    final LazyFinal<AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode> staticGeometry = new LazyFinal<>();

    final Set<CrappyConnector> connectors = new LinkedHashSet<>();

    private final Object SYNC_OBJECT = new Object();

    public CrappyWorld(){

    }

    // TODO: synchronized sets of bodies/connectors that are 'safe' for the programmer using CRAPPY
    //  to use to get a view of the physics world


    void update(final double delta, final Vect2D grav){

        for (int i = 0; i < EULER_UPDATES_PER_RENDER_ATTEMPT; i++) {

            connectors.forEach(CrappyConnector::applyForcesToBodies);

            dynamicBodies.forEach(c -> c.first_euler_sub_update(delta, grav));
            kinematicBodies.forEach(c->c.first_euler_sub_update(delta,grav));



            for (int steps = 1; i < EULER_SUBSTEPS; i++) {
                connectors.forEach(CrappyConnector::applyForcesToBodies);
                dynamicBodies.forEach(crappyBody -> crappyBody.euler_substep(delta));
                kinematicBodies.forEach(crappyBody -> crappyBody.euler_substep(delta));
            }

            final Crappy_AABB combinedAABB = new Crappy_AABB();
            dynamicBodies.forEach(c -> {
                c.applyAllTempChanges();
                combinedAABB.add_aabb(c.getAABB());
            });
            kinematicBodies.forEach(c -> {
                c.applyAllTempChanges();
                combinedAABB.add_aabb(c.getAABB());
            });



            final AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode qTree =
                    AABBQuadTreeTools.DYN_KIN_AABB_FACTORY(
                    combinedAABB.getMin(), combinedAABB.getMidpoint(), 4, kinematicBodies
            );

            // TODO:
            //   * Put the dynamic bodies into qTree, obtain bounding box intersects per dynamic body
            //      * also check dynamics against static geometry
            //   * Narrow-phase collision detection for all of these intersects
            //   * Also collision handling (applying forces, callbacks, etc)
            //  and remove any bodies marked for removal as a result of collision handling

        }

        // TODO:
        //   update the synchronized collections representing the physics world


    }



    /**
     * Set the world's static geometry.
     * @param geom the Quadtree describing the world's static geometry.
     * @throws IllegalStateException if static geometry has already been set
     */
    public void setStaticGeometry(AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode geom) throws IllegalStateException{
        staticGeometry.set(geom);
    }

    public void addBody(final CrappyBody b){
        switch (b.bodyType){
            case STATIC:
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
