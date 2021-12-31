package crappy;

import crappy.collisions.AABBQuadTreeTools;
import crappy.collisions.CrappyCollisionHandler;
import crappy.collisions.CrappyShape_QuadTree_Interface;
import crappy.collisions.Crappy_AABB;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.utils.lazyFinal.I_LazyFinal;
import crappy.utils.lazyFinal.LazyFinal;
import crappy.utils.lazyFinal.LazyFinalDefault;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The physics world within CRAPPY
 */
public class CrappyWorld {

    /**
     * How many stages are there in each euler update?
     */
    public static final int EULER_SUBSTEPS = 2;

    public static final int EULER_UPDATES_PER_RENDER_ATTEMPT = 100;

    public static final int DELAY = 20;

    public static final double DELTA_T = DELAY / 1000.0 / (double) EULER_UPDATES_PER_RENDER_ATTEMPT;

    public static final Vect2D GRAVITY = new Vect2D(0, -0.95);

    public final Vect2D grav;


    final Set<CrappyBody> dynamicBodies = new LinkedHashSet<>();

    final Set<CrappyBody> kinematicBodies = new LinkedHashSet<>();

    final I_LazyFinal<AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode> staticGeometry =
            new LazyFinalDefault<>(AABBQuadTreeTools.DEFAULT_STATIC_GEOMETRY_TREE());

    final Set<CrappyConnector> connectors = new LinkedHashSet<>();

    private final Object SYNC_OBJECT = new Object();

    public CrappyWorld(final Vect2D gravity){
        this.grav = gravity;
    }

    // TODO: synchronized sets of bodies/connectors that are 'safe' for the programmer using CRAPPY
    //  to use to get a view of the physics world


    public void update(){
        update(DELTA_T, grav, EULER_SUBSTEPS);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void update(final double delta, final I_Vect2D grav, final int eulerSubsteps){

        if (eulerSubsteps <= 0){
            throw new IllegalArgumentException("Cannot have 0 or fewer euler substeps!");
        }

        Iterator<? extends I_CrappyBody_CrappyWorld_Interface> iter;
        Iterator<CrappyConnector> conIter;

        final double subDelta = delta/(double) eulerSubsteps;


        for (int i = 0; i < EULER_UPDATES_PER_RENDER_ATTEMPT; i++) {

            //connectors.forEach(CrappyConnector::applyForcesToBodies);


            //dynamicBodies.forEach(c -> c.first_euler_sub_update(delta, grav));
            //kinematicBodies.forEach(c->c.first_euler_sub_update(delta,grav));

            // time for some for abuse.
            for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies());
            for (iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().first_euler_sub_update(delta, grav, subDelta));
            for (iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().first_euler_sub_update(delta, grav, subDelta));


            for (int steps = 1; steps < EULER_SUBSTEPS; steps++) {
                for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies());
                for (iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta));
                for (iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta));
            }

            final Crappy_AABB combinedAABB = new Crappy_AABB();



            for (iter = dynamicBodies.iterator(); iter.hasNext(); ){
                I_CrappyBody_CrappyWorld_Interface b = iter.next();
                b.applyAllTempChanges();
                combinedAABB.add_aabb(b.getAABB());
            }
            for (iter = kinematicBodies.iterator(); iter.hasNext(); ){
                I_CrappyBody_CrappyWorld_Interface b = iter.next();
                b.applyAllTempChanges();
                combinedAABB.add_aabb(b.getAABB());
            }




            final AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode qTree =
                    AABBQuadTreeTools.DYN_KIN_AABB_FACTORY(
                    combinedAABB.getMin(), combinedAABB.getMidpoint(), 4, kinematicBodies
            );

            for (iter = dynamicBodies.iterator(); iter.hasNext(); ) {
                I_CrappyBody_CrappyWorld_Interface b = iter.next();
                if (b.isActive()){
                    CrappyCollisionHandler.HANDLE_COLLISIONS(
                            b.getShape(),
                            staticGeometry.get().getShapesThatProbablyCollideWithToOut(
                                    b.getShape().getBoundingBox(),
                                    qTree.checkDynamicBodyAABB(b.getShape())
                            ),
                            delta
                    );
                }
            }

            for(iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().performPostCollisionBitmaskCallback());
            for(iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().performPostCollisionBitmaskCallback());
            for(iter = staticGeometry.get().iterator(); iter.hasNext(); iter.next().performPostCollisionBitmaskCallback());


            resolveChangesToBodies(dynamicBodies);
            resolveChangesToBodies(kinematicBodies);

            // remove any connectors that need to be removed..
            for(conIter = connectors.iterator(); conIter.hasNext();){
                if (!conIter.next().shouldIStillExist()){
                    conIter.remove();
                }
            }

            // TODO:
            //   * Put the dynamic bodies into qTree, obtain bounding box intersects per dynamic body
            //      * also check dynamics against static geometry
            //   * Narrow-phase collision detection for all of these intersects
            //   * Also collision handling (applying forces, callbacks, etc)
            //  and remove any bodies marked for removal as a result of collision handling

        }




    }

    private void resolveChangesToBodies(final Iterable<CrappyBody> bodies) {
        for (Iterator<? extends I_CrappyBody_CrappyWorld_Interface> iter = bodies.iterator(); iter.hasNext();) {
            I_CrappyBody_CrappyWorld_Interface b = iter.next();

            if (b.resolveRemovalChange()){
                iter.remove();
            } else {
                b.resolveActiveChange();
                b.resolvePositionLockChange();
                b.resolveTangibilityChange();
                b.resolveRotationLockChange();
            }
        }
    }


    /**
     * Set the world's static geometry.
     * @param geom the Quadtree describing the world's static geometry.
     * @throws IllegalStateException if static geometry has already been set
     */
    public void setStaticGeometry(AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode geom) throws IllegalStateException{
        staticGeometry.set(geom);
    }

    public void addBody(final CrappyBody b) throws IllegalArgumentException{
        if (b.getShape() == null){
            throw new IllegalArgumentException("Please initialize a shape for this body first!");
        }
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
