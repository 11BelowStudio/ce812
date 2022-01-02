package crappy;

import crappy.collisions.AABBQuadTreeTools;
import crappy.collisions.CrappyCollisionHandler;
import crappy.collisions.CrappyShape_QuadTree_Interface;
import crappy.graphics.DrawableBody;
import crappy.graphics.DrawableConnector;
import crappy.graphics.I_CrappilyDrawStuff;
import crappy.math.I_Vect2D;
import crappy.math.Vect2D;
import crappy.utils.lazyFinal.*;

import java.util.*;

import static java.util.Collections.synchronizedList;

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


    final List<CrappyBody> dynamicBodies = new ArrayList<>();

    final List<CrappyBody> kinematicBodies = new ArrayList<>();

    /**
     * AABB holding static geometry
     */
    final I_LazyFinal<AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode> staticGeometry =
            new LazyFinalDefault<>(AABBQuadTreeTools.DEFAULT_STATIC_GEOMETRY_TREE());


    /**
     * AABB for dynamic/kinematic bodies
     */
    final I_ProtectedOverwriter<AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode> dynKineGeometry =
            new ProtectedOverwrite<>(
                    AABBQuadTreeTools.DYN_KIN_AABB_FACTORY(
                            Vect2D.ZERO, Vect2D.ZERO, 1, new HashSet<>(0)
                    ),
                    I_ProtectedOverwrite.ProtectedOverwriteLockMode.PRIVATE_WRITEABLE
            );

    final Set<CrappyConnector> connectors = new LinkedHashSet<>();

    private final Object UPDATE_SYNC_OBJECT = new Object();


    public final List<DrawableBody> drawableDynamics = synchronizedList(new ArrayList<>());
    public final List<DrawableBody> drawableKinematics = synchronizedList(new ArrayList<>());
    public final List<DrawableBody> drawableStatics = synchronizedList(new ArrayList<>());
    public final List<DrawableConnector> drawableConnectors = synchronizedList(new ArrayList<>());

    public CrappyWorld(final Vect2D gravity){
        this.grav = gravity;
    }

    // TODO: synchronized sets of bodies/connectors that are 'safe' for the programmer using CRAPPY
    //  to use to get a view of the physics world


    public void update(){
        update(DELTA_T, grav, EULER_UPDATES_PER_RENDER_ATTEMPT, EULER_SUBSTEPS);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void update(final double delta, final I_Vect2D grav, final int eulerIterations, final int eulerSubsteps){

        if (eulerIterations <= 0){
            throw new IllegalArgumentException("I cannot perform 0 (or fewer) update iterations!");
        }
        if (eulerSubsteps <= 0) {
            throw new IllegalArgumentException("Cannot have 0 or fewer euler substeps!");
        }

        Iterator<? extends I_CrappyBody_CrappyWorld_Interface> iter;
        Iterator<CrappyConnector> conIter;

        final double subDelta = delta / (double) eulerSubsteps;


        synchronized (UPDATE_SYNC_OBJECT) {

            final AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode newObjectTree =
                    AABBQuadTreeTools.DYN_KYN_AABB_FACTORY_BOUNDS_FINDER(dynamicBodies, kinematicBodies);

            for (int i = 0; i < EULER_UPDATES_PER_RENDER_ATTEMPT; i++) {

                //connectors.forEach(CrappyConnector::applyForcesToBodies);


                //dynamicBodies.forEach(c -> c.first_euler_sub_update(delta, grav));
                //kinematicBodies.forEach(c->c.first_euler_sub_update(delta,grav));

                newObjectTree.clearAll(); // wipe it clean so we can do this update.

                // time for some for abuse.
                for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies());
                for (iter = dynamicBodies.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta));
                for (iter = kinematicBodies.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta));


                for (int steps = 1; steps < EULER_SUBSTEPS; steps++) {
                    for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies()) ;
                    for (iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta)) ;
                    for (iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta)) ;
                }

                for (iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().applyAllTempChanges()) ;
                for (iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().applyAllTempChanges()) ;


                // adds all the kinematic bodies to the dynamic/kinematic AABB tree
                newObjectTree.addAllKinematicBodies(kinematicBodies);

                // and then attempts to perform all of the collisions for dynamic bodies
                for (iter = dynamicBodies.iterator(); iter.hasNext(); ) {
                    I_CrappyBody_CrappyWorld_Interface b = iter.next();
                    if (b.isActive()) {
                        CrappyCollisionHandler.HANDLE_COLLISIONS(
                                b.getShape(),
                                staticGeometry.get().getShapesThatProbablyCollideWithToOut(
                                        b.getAABB(),
                                        newObjectTree.checkDynamicBodyAABB(b.getShape())
                                ),
                                delta
                        );
                    }
                }

                // and then seeing if the kinematics collided with anything
                for (iter = kinematicBodies.iterator(); iter.hasNext(); ) {
                    I_CrappyBody_CrappyWorld_Interface k = iter.next();
                    if (k.isActive()) {
                        CrappyCollisionHandler.HANDLE_COLLISIONS(
                                k.getShape(),
                                staticGeometry.get().getShapesThatProbablyCollideWith(
                                        k.getAABB()
                                ),
                                delta
                        );
                    }
                }

                for (
                        iter = dynamicBodies.iterator(); iter.hasNext();
                        iter.next().performPostCollisionBitmaskCallback()
                );
                for (
                        iter = kinematicBodies.iterator(); iter.hasNext();
                        iter.next().performPostCollisionBitmaskCallback()
                );
                for (
                        iter = staticGeometry.get().iterator(); iter.hasNext();
                        iter.next().performPostCollisionBitmaskCallback()
                );


                resolveChangesToBodies(dynamicBodies);
                resolveChangesToBodies(kinematicBodies);

                // remove any connectors that need to be removed
                for (conIter = connectors.iterator(); conIter.hasNext(); ) {
                    if (!conIter.next().shouldIStillExist()) {
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


            synchronized (drawableDynamics){
                drawableDynamics.clear();
                for (DrawableBody b: dynamicBodies) {
                    b.updateDrawables();
                    drawableDynamics.add(b);
                }
            }
            synchronized (drawableKinematics){
                drawableKinematics.clear();
                for (DrawableBody b: kinematicBodies) {
                    b.updateDrawables();
                    drawableKinematics.add(b);
                }
            }
            synchronized (drawableConnectors) {
                drawableConnectors.clear();
                for (DrawableConnector c : connectors) {
                    c.updateDrawables();
                    drawableConnectors.add(c);
                }
            }

            synchronized (drawableStatics){
                drawableStatics.forEach(DrawableBody::updateDrawables);
            }

            // TODO: overwrite main dynamic/kinematic quadtree

            // TODO: method to accept clicks

            // TODO: debug draw.
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
        synchronized (UPDATE_SYNC_OBJECT) {
            staticGeometry.set(geom);
            synchronized (drawableStatics){
                drawableStatics.clear();
                for (DrawableBody s: geom) {
                    s.updateDrawables();
                    drawableStatics.add(s);
                }
            }
        }
    }

    public void addBody(final CrappyBody b) throws IllegalArgumentException{
        synchronized (UPDATE_SYNC_OBJECT) {
            if (b.getShape() == null) {
                throw new IllegalArgumentException("Please initialize a shape for this body first!");
            }
            switch (b.bodyType) {
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
    }

    public void addConnector(final CrappyConnector c){
        synchronized (UPDATE_SYNC_OBJECT) {
            connectors.add(c);
        }
    }

    public void removeBody(final CrappyBody b){
        synchronized (UPDATE_SYNC_OBJECT) {
            switch (b.bodyType) {
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
    }

    public void removeConnector(final CrappyConnector c){
        synchronized (UPDATE_SYNC_OBJECT) {
            connectors.remove(c);
        }
    }

    /**
     * Uses the I_CrappilyDrawStuff object to crappily render the world.
     * @param crapRenderer the renderer object.
     * @apiNote very inelegant, please don't use this unless you're really desperate.
     */
    public void renderCrappily(final I_CrappilyDrawStuff crapRenderer){


        synchronized (drawableStatics){
            drawableStatics.forEach(crapRenderer::drawThisBody);
        }
        synchronized (drawableKinematics){
            drawableKinematics.forEach(crapRenderer::drawThisBody);
        }
        synchronized (drawableDynamics){
            drawableDynamics.forEach(crapRenderer::drawThisBody);
        }
        synchronized (drawableConnectors){
            drawableConnectors.forEach(crapRenderer::acceptConnector);
        }

    }

    public enum CLICK_MODE{
        CLICK_FIRST_SUCCESSFUL,
        CLICK_ALL
    }

    public enum CLICK_WHICH{
        STATIC_ONLY,
        NON_STATIC_ONLY,
        ANYTHING
    }

    /**
     * Attempts clicking
     * @param clickPos where click
     * @param mode click first successful or click everything?
     * @param which what do I click?
     */
    public void attemptClick(final I_Vect2D clickPos, final CLICK_MODE mode, final CLICK_WHICH which){

        synchronized (UPDATE_SYNC_OBJECT){

            if (which != CLICK_WHICH.NON_STATIC_ONLY) {
                for (CrappyShape_QuadTree_Interface s : staticGeometry.getAssert().getPotentialPointCollisions(clickPos)) {
                    if (s.getShape().isPointInShape(clickPos) &&
                            s.getShape().getBody().wasClicked() &&
                            mode == CLICK_MODE.CLICK_FIRST_SUCCESSFUL
                    ){
                        return;
                    }
                }
            }

            if (which != CLICK_WHICH.STATIC_ONLY){
                for (CrappyShape_QuadTree_Interface s : dynKineGeometry.get().getPointCollisions(clickPos)) {
                    if (s.getShape().isPointInShape(clickPos) &&
                            s.getShape().getBody().wasClicked() &&
                            mode == CLICK_MODE.CLICK_FIRST_SUCCESSFUL
                    ){
                        return;
                    }
                }
            }
        }

    }






}
