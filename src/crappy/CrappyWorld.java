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
    public static final int DEFAULT_EULER_SUBSTEPS = 2;

    public static final int DEFAULT_EULER_UPDATES_PER_RENDER_ATTEMPT = 100;

    public static final int DEFAULT_DELAY = 20;

    public static final double DEFAULT_DELTA_T = DEFAULT_DELAY / 1000.0 / (double) DEFAULT_EULER_UPDATES_PER_RENDER_ATTEMPT;

    public static final Vect2D DEFAULT_GRAVITY = new Vect2D(0, -9.81);

    public final Vect2D grav;

    public final int eulerSubsteps;

    public final int eulerUpdatesPerUpdate;

    public final int delay;

    public final double totalDelta;

    public final double deltaT;


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

    public CrappyWorld(final Vect2D gravity, final int eulerSubsteps, final int eulerUpdatesPerUpdate, final int delay){
        this.eulerSubsteps = eulerSubsteps;
        this.eulerUpdatesPerUpdate = eulerUpdatesPerUpdate;
        this.delay = delay;
        this.totalDelta = delay / 1000.0;
        this.deltaT = totalDelta / (double) eulerUpdatesPerUpdate;

        this.grav = gravity;
    }

    public CrappyWorld(final Vect2D gravity){
        this(gravity, DEFAULT_EULER_SUBSTEPS, DEFAULT_EULER_UPDATES_PER_RENDER_ATTEMPT, DEFAULT_DELAY);
    }

    public CrappyWorld(){
        this (DEFAULT_GRAVITY, DEFAULT_EULER_SUBSTEPS, DEFAULT_EULER_UPDATES_PER_RENDER_ATTEMPT, DEFAULT_DELAY);
    }

    // TODO: synchronized sets of bodies/connectors that are 'safe' for the programmer using CRAPPY
    //  to use to get a view of the physics world


    public void update(){
        update(deltaT, grav, eulerUpdatesPerUpdate, eulerSubsteps);
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

            // we make sure that any changes to the states of the bodies since the last update call have been resolved.
            resolveChangesToBodies(dynamicBodies);
            resolveChangesToBodies(kinematicBodies);

            // and doing any other prep stuff we need to resolve before the first euler update
            for(iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().handleStuffBeforeFirstEulerUpdate());
            for(iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().handleStuffBeforeFirstEulerUpdate());


            // same for the connectors.
            for (conIter = connectors.iterator(); conIter.hasNext(); ) {
                if (conIter.next().startingDisposal()) {
                    conIter.remove();
                }
            }

            final AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode newObjectTree =
                    AABBQuadTreeTools.DYN_KYN_AABB_FACTORY_BOUNDS_FINDER(dynamicBodies, kinematicBodies);

            for (int i = 0; i < eulerIterations; i++) {

                //connectors.forEach(CrappyConnector::applyForcesToBodies);


                //dynamicBodies.forEach(c -> c.first_euler_sub_update(delta, grav));
                //kinematicBodies.forEach(c->c.first_euler_sub_update(delta,grav));

                newObjectTree.clearAll(); // wipe it clean so we can do this update.

                // time for some for abuse.
                for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies()) ;
                for (iter = dynamicBodies.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta))
                    ;
                for (iter = kinematicBodies.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta))
                    ;


                for (int steps = 1; steps < eulerSubsteps; steps++) {
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
                                delta// * 1000
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
                                delta// * 1000
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
                    if (conIter.next().startingDisposal()) {
                        conIter.remove();
                    }
                }
            }

            // last-minute cleanup (removing any external forces applied last timestep that aren't needed any more etc)
            for(iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().resolveStuffAfterLastEulerUpdate());
            for(iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().resolveStuffAfterLastEulerUpdate());

            // TODO:
            //   * Put the dynamic bodies into qTree, obtain bounding box intersects per dynamic body
            //      * also check dynamics against static geometry
            //   * Narrow-phase collision detection for all of these intersects
            //   * Also collision handling (applying forces, callbacks, etc)
            //  and remove any bodies marked for removal as a result of collision handling


            // now we update the drawable versions of these bodies.

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

            /*
            synchronized (drawableStatics){
                drawableStatics.forEach(DrawableBody::updateDrawables);
            }

             */


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
    public void setStaticGeometry(final AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode geom) throws IllegalStateException{
        synchronized (UPDATE_SYNC_OBJECT) {
            staticGeometry.set(geom);
            synchronized (drawableStatics){
                drawableStatics.clear();
                for (DrawableBody s: geom) {
                    s.updateDrawables();
                    drawableStatics.add(s);
                }
            }
            System.out.println(staticGeometry.getAssert().toString());
        }
    }

    public void addBody(final CrappyBody b) throws IllegalArgumentException, AssertionError{
        synchronized (UPDATE_SYNC_OBJECT) {
            if (b.getShape() == null) {
                throw new IllegalArgumentException("Please initialize a shape for this body first!");
            }
            switch (b.getBodyType()) {
                case STATIC:
                    break;
                case KINEMATIC:
                    kinematicBodies.add(b);
                    break;
                case DYNAMIC:
                    dynamicBodies.add(b);
                    break;
                default:
                    throw new AssertionError("Did not expect a body of type " + b.getBodyType() + "!");
            }
        }
    }

    public void addConnector(final CrappyConnector c){
        synchronized (UPDATE_SYNC_OBJECT) {
            connectors.add(c);
        }
    }

    /**
     * Marks the given body B for prompt removal.
     * @param b the body that needs to be removed.
     */
    public void removeBody(final CrappyBody b){
        b.setMarkForRemoval(true);
    }

    /**
     * Marks the given connector C for prompt removal
     * @param c connector that needs to be removed.
     */
    public void removeConnector(final CrappyConnector c){
        c.setAllowedToExist(false);
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
