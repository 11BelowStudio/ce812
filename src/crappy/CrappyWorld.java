/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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

import static crappy.CrappyBody.CRAPPY_BODY_TYPE.STATIC;
import static java.util.Collections.synchronizedList;

/**
 * The physics world within CRAPPY
 * @author Rachel Lowe
 */
public class CrappyWorld {

    /**
     * We initially use a simple 'improved euler' (euler method with 2 half-steps) per update iteration
     */
    public static final int DEFAULT_EULER_SUBSTEPS_PER_UPDATE_ITERATION = 2;

    /**
     * How many physics world updates are there per update?
     * We initially have 100 euler updates per call to the update loop.
     */
    public static final int DEFAULT_UPDATE_ITERATIONS_WITHIN_UPDATE_METHOD = 100;

    /**
     * What length of time (milliseconds) should the update loop simulate?
     * By default, it simulates 20 milliseconds.
     */
    public static final int DEFAULT_UPDATE_DELAY = 20;

    /**
     * Calculator for DeltaT, given update delay (milliseconds) and update iterations
     * @param update_delay length of time (milliseconds) to simulate in the update method
     * @param update_iterations number of update iterations within the update method
     * @return an appropriate DELTA_T value for those inputs.
     */
    public static double DELTA_T_CALCULATOR(final int update_delay, final int update_iterations){
        return update_delay / 1000.0 / (double) update_iterations;
    }

    public static final double DEFAULT_DELTA_T = DELTA_T_CALCULATOR(DEFAULT_UPDATE_DELAY, DEFAULT_UPDATE_ITERATIONS_WITHIN_UPDATE_METHOD);

    /**
     * Default gravity for update loop (set to roughly equal to earth gravity)
     */
    public static final Vect2D DEFAULT_GRAVITY = new Vect2D(0, -9.81);

    /**
     * The chosen default gravity for update loop.
     */
    public final Vect2D grav;

    /**
     * How many stages are there in each euler update?
     */
    public final int eulerSubsteps;

    /**
     * How many update iterations per update method call?
     */
    public final int eulerUpdatesPerUpdate;

    /**
     * Length of time (ms) simulated by update loop?
     */
    public final int delay;

    /**
     * DeltaT for all the sub-updates combined?
     */
    public final double totalDelta;

    /**
     * Per-iteration update timestep
     */
    public final double deltaT;


    /**
     * map of (ID, body) for all dynamic bodies
     */
    private final Map<UUID, CrappyBody> dynamics = new LinkedHashMap<>();

    /**
     * map of (ID, body) for all kinematic bodies
     */
    private final Map<UUID, CrappyBody> kinematics = new LinkedHashMap<>();
    /**
     * Map of (ID, connector) for all connectors
     */
    private final Map<UUID, CrappyConnector> connects = new LinkedHashMap<>();

    /**
     * AABB holding static geometry
     */
    final ILazyFinal<AABBQuadTreeTools.I_StaticGeometryQuadTreeRootNode> staticGeometry =
            new LazyFinalDefault<>(AABBQuadTreeTools.DEFAULT_STATIC_GEOMETRY_TREE());


    /**
     * AABB for dynamic/kinematic bodies
     */
    final IProtectedOverwriter<AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode> dynKineGeometry =
            new ProtectedOverwrite<>(
                    AABBQuadTreeTools.DYN_KIN_AABB_FACTORY(
                            Vect2D.ZERO, Vect2D.ZERO, 1, new HashSet<>(0)
                    ),
                    IProtectedOverwrite.ProtectedOverwriteLockMode.PRIVATE_WRITEABLE
            );



    private final Object UPDATE_SYNC_OBJECT = new Object();


    public final List<DrawableBody> drawableDynamics = synchronizedList(new ArrayList<>());
    public final List<DrawableBody> drawableKinematics = synchronizedList(new ArrayList<>());
    public final List<DrawableBody> drawableStatics = synchronizedList(new ArrayList<>());
    public final List<DrawableConnector> drawableConnectors = synchronizedList(new ArrayList<>());

    /**
     * Creates a CrappyWorld with fully tuned parameters.
     * @param gravity default gravity
     * @param eulerSubsteps default number of euler substeps per update iteration
     * @param eulerUpdatesPerUpdate number of update iterations in update loop
     * @param delay length of time (milliseconds) that the update method should simulate (in terms of physics)
     */
    public CrappyWorld(final Vect2D gravity, final int eulerSubsteps, final int eulerUpdatesPerUpdate, final int delay){
        this.eulerSubsteps = eulerSubsteps;
        this.eulerUpdatesPerUpdate = eulerUpdatesPerUpdate;
        this.delay = delay;

        this.deltaT = DELTA_T_CALCULATOR(delay, eulerUpdatesPerUpdate);

        this.totalDelta = delay / 1000.0;

        this.grav = gravity;
    }

    /**
     * Creates a CrappyWorld using all the default parameters except for gravity
     * @param gravity default gravity to apply in the update loop
     */
    public CrappyWorld(final Vect2D gravity){
        this(gravity, DEFAULT_EULER_SUBSTEPS_PER_UPDATE_ITERATION, DEFAULT_UPDATE_ITERATIONS_WITHIN_UPDATE_METHOD, DEFAULT_UPDATE_DELAY);
    }

    /**
     * Creates a new CrappyWorld, using all of the default parameters.
     */
    public CrappyWorld(){
        this (DEFAULT_GRAVITY, DEFAULT_EULER_SUBSTEPS_PER_UPDATE_ITERATION, DEFAULT_UPDATE_ITERATIONS_WITHIN_UPDATE_METHOD, DEFAULT_UPDATE_DELAY);
    }



    /**
     * Runs the update method, using the parameters we had already specified for the CrappyWorld earlier
     */
    public void update(){
        update(deltaT, grav, eulerUpdatesPerUpdate, eulerSubsteps);
    }



    /**
     * Manually tunable update loop.
     * @param delta the timestep used for each iteration
     * @param grav constant gravity
     * @param updateIterations how many update iterations are we doing?
     * @param eulerSubsteps how many euler method substeps per iteration?
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public void update(final double delta, final I_Vect2D grav, final int updateIterations, final int eulerSubsteps){

        if (updateIterations <= 0){
            throw new IllegalArgumentException("I cannot perform 0 (or fewer) update iterations!");
        }
        if (eulerSubsteps <= 0) {
            throw new IllegalArgumentException("Cannot have 0 or fewer euler substeps!");
        }

        Iterator<? extends I_CrappyBody_CrappyWorld_Interface> iter;
        Iterator<CrappyConnector> conIter;

        final double subDelta = delta / (double) eulerSubsteps;

        final Collection<CrappyBody> dyns = dynamics.values();
        final Collection<CrappyBody> kins = kinematics.values();
        final Collection<CrappyConnector> cons = connects.values();


        synchronized (UPDATE_SYNC_OBJECT) {

            // we make sure that any changes to the states of the bodies since the last update call have been resolved.
            //resolveChangesToBodies(dynamicBodies);
            //resolveChangesToBodies(kinematicBodies);

            resolveChangesToBodies(dyns);
            resolveChangesToBodies(kins);

            // same for the connectors.
            //connectors.removeIf(CrappyConnector::startingDisposal);
            cons.removeIf(CrappyConnector::startingDisposal);

            // and doing any other prep stuff we need to resolve before the first euler update
            //for(iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().handleStuffBeforeFirstEulerUpdate());
            //for(iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().handleStuffBeforeFirstEulerUpdate());

            for(iter = dyns.iterator(); iter.hasNext(); iter.next().handleStuffBeforeFirstEulerUpdate());
            for(iter = kins.iterator(); iter.hasNext(); iter.next().handleStuffBeforeFirstEulerUpdate());


            final AABBQuadTreeTools.I_DynamicKinematicAABBQuadTreeRootNode newObjectTree =
                    //AABBQuadTreeTools.DYN_KYN_AABB_FACTORY_BOUNDS_FINDER(dynamicBodies, kinematicBodies);
                    AABBQuadTreeTools.DYN_KYN_AABB_FACTORY_BOUNDS_FINDER(dyns, kins);



            for (int i = 0; i < updateIterations; i++) {

                //connectors.forEach(CrappyConnector::applyForcesToBodies);


                //dynamicBodies.forEach(c -> c.first_euler_sub_update(delta, grav));
                //kinematicBodies.forEach(c->c.first_euler_sub_update(delta,grav));

                newObjectTree.clearAll(); // wipe it clean so we can do this update.

                // time for some for abuse.
                /*
                for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies()) ;
                for (iter = dynamicBodies.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta))
                    ;
                for (iter = kinematicBodies.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta))
                    ;

                 */
                for (conIter = cons.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies()) ;
                for (iter = dyns.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta))
                    ;
                for (iter = kins.iterator(); iter.hasNext();
                     iter.next().first_euler_sub_update(delta, grav, subDelta))
                    ;

                for (int steps = 1; steps < eulerSubsteps; steps++) {
                    for (conIter = cons.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies()) ;
                    for (iter = dyns.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta)) ;
                    for (iter = kins.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta)) ;
                    /*
                    for (conIter = connectors.iterator(); conIter.hasNext(); conIter.next().applyForcesToBodies()) ;
                    for (iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta)) ;
                    for (iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().euler_substep(subDelta)) ;

                     */
                }

                /*
                for (iter = dynamicBodies.iterator(); iter.hasNext(); iter.next().applyAllTempChanges()) ;
                for (iter = kinematicBodies.iterator(); iter.hasNext(); iter.next().applyAllTempChanges()) ;

                 */
                for (iter = dyns.iterator(); iter.hasNext(); iter.next().applyAllTempChanges()) ;
                for (iter = kins.iterator(); iter.hasNext(); iter.next().applyAllTempChanges()) ;

                // adds all the kinematic bodies to the dynamic/kinematic AABB tree
                //newObjectTree.addAllKinematicBodies(kinematicBodies);
                //newObjectTree.addAllKinematicBodies(kins);

                //
                //   * Put the dynamic bodies into qTree, obtain bounding box intersects per dynamic body
                //      * also check dynamics against static geometry
                //   * Narrow-phase collision detection for all of these intersects
                //   * Also collision handling (applying forces, callbacks, etc)
                //  and remove any bodies marked for removal as a result of collision handling


                // attempts to check the kinematic bodies against the static objects and each other
                for (iter = kins.iterator(); iter.hasNext(); ) {
                    I_CrappyBody_CrappyWorld_Interface b = iter.next();
                    if (b.isActive()) {
                        CrappyCollisionHandler.HANDLE_COLLISIONS(
                                b.getShape(),
                                staticGeometry.get().getShapesThatProbablyCollideWithToOut(
                                        b.getAABB(),
                                        newObjectTree.checkAndAddBodyAABB(b.getShape())
                                ),
                                delta// * 1000
                        );
                    }
                }

                // and then seeing if the dynamics collided with anything
                for (iter = dyns.iterator(); iter.hasNext(); ) {
                    I_CrappyBody_CrappyWorld_Interface d = iter.next();
                    if (d.isActive()) {
                        CrappyCollisionHandler.HANDLE_COLLISIONS(
                                d.getShape(),
                                staticGeometry.get().getShapesThatProbablyCollideWithToOut(
                                        d.getAABB(),
                                        newObjectTree.checkAndAddBodyAABB(d.getShape())
                                ),
                                delta// * 1000
                        );
                    }
                }

                for (
                        iter = dyns.iterator(); iter.hasNext();
                        iter.next().performPostCollisionBitmaskCallback()
                );
                for (
                        iter = kins.iterator(); iter.hasNext();
                        iter.next().performPostCollisionBitmaskCallback()
                );
                for (
                        iter = staticGeometry.get().iterator(); iter.hasNext();
                        iter.next().performPostCollisionBitmaskCallback()
                );


                resolveChangesToBodies(dyns);
                resolveChangesToBodies(kins);

                // remove any connectors that need to be removed
                cons.removeIf(CrappyConnector::startingDisposal);
            }

            // last-minute cleanup (removing any external forces applied last timestep that aren't needed any more etc)
            for(iter = dyns.iterator(); iter.hasNext(); iter.next().resolveStuffAfterLastEulerUpdate());
            for(iter = kins.iterator(); iter.hasNext(); iter.next().resolveStuffAfterLastEulerUpdate());

            dynKineGeometry.setOverride(newObjectTree);

            // now we update the drawable versions of these bodies.

            synchronized (drawableDynamics){
                drawableDynamics.clear();
                for (DrawableBody b: dyns) {
                    b.updateDrawables();
                    drawableDynamics.add(b);
                }
            }
            synchronized (drawableKinematics){
                drawableKinematics.clear();
                for (DrawableBody b: kins) {
                    b.updateDrawables();
                    drawableKinematics.add(b);
                }
            }
            synchronized (drawableConnectors) {
                drawableConnectors.clear();
                for (DrawableConnector c : cons) {
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

    /**
     * Attempts to handle state changes within bodies (such as removals, position locks/unlocks, being active or not, etc).
     * ONLY USED FOR DYNAMIC AND KINEMATIC BODIES!
     * Called before and after each update loop.
     * @param bodies the iterable of all the bodies that are being dealt with.
     */
    private void resolveChangesToBodies(final Iterable<? extends I_CrappyBody_CrappyWorld_Interface> bodies) {
        for (final Iterator<? extends I_CrappyBody_CrappyWorld_Interface> iter = bodies.iterator(); iter.hasNext();) {
            final I_CrappyBody_CrappyWorld_Interface b = iter.next();

            if (b.resolveRemovalChange()){
                b.$_$_$__discard_INTERNAL_USE_ONLY_DO_NOT_USE_YOURSELF_EVER_SERIOUSLY_DONT_GRRR();
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
            //System.out.println(staticGeometry.getAssert().toString());
        }
    }

    /**
     * Attempts to add the given body b to the CrappyWorld. INTENDED FOR KINEMATIC AND DYNAMIC BODIES ONLY!
     * @param b the body to add.
     * @throws IllegalArgumentException if the body is static, or if the body has no shape.
     * @throws AssertionError if I have made a massive cock-up due to not expecting a currently unknown type of body.
     */
    public void addBody(final CrappyBody b) throws IllegalArgumentException, AssertionError{
        if (b.getBodyType() == STATIC){
            throw new IllegalArgumentException("Please use the 'setStaticGeometry' method to add static geometry.");
        } else if (b.getShape() == null) {
            throw new IllegalArgumentException("Please initialize a shape for this body first!");
        }
        synchronized (UPDATE_SYNC_OBJECT) {
            switch (b.getBodyType()) {
                case KINEMATIC:
                    kinematics.put(b.getID(), b);
                    break;
                case DYNAMIC:
                    dynamics.put(b.getID(), b);
                    break;
                case STATIC:
                    throw new IllegalArgumentException("Please use the 'setStaticGeometry' method to add static geometry.");
                default:
                    throw new AssertionError("Did not expect a body of type " + b.getBodyType() + "!");
            }
        }
    }

    public void addConnector(final CrappyConnector c){
        synchronized (UPDATE_SYNC_OBJECT) {
            connects.put(c.getID(), c);
        }
    }

    /**
     * Marks the given body B for prompt removal.
     * @param b the body that needs to be removed.
     * @throws IllegalArgumentException if one attempts to remove a static body.
     */
    public void removeBody(final I_ManipulateCrappyBody b) throws IllegalArgumentException{
        if (b.getBodyType() == STATIC){
            throw new IllegalArgumentException("Cannot remove a static body!");
        }
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

    public List<DrawableBody> getRenderableBodies(final CrappyBody.CRAPPY_BODY_TYPE b){

        switch (b){
            case KINEMATIC:
                synchronized (drawableKinematics){
                    return Collections.unmodifiableList(new ArrayList<>(drawableKinematics));
                }
            case STATIC:
                synchronized (drawableStatics){
                    return Collections.unmodifiableList(new ArrayList<>(drawableStatics));
                }
            case DYNAMIC:
                synchronized (drawableDynamics){
                    return Collections.unmodifiableList(new ArrayList<>(drawableDynamics));
                }
            default:
                throw new AssertionError("Was not expecting a body of type " + b + "!");
        }
    }


    public List<DrawableConnector> getRenderableConnectors() {
        synchronized (drawableConnectors){
            return Collections.unmodifiableList(new ArrayList<>(drawableConnectors));
        }
    }

    /**
     * Attempts to obtain a CrappyBody in the world from ID
     * @param id
     * @return an optional that holds that CrappyBody (if it is known)
     */
    public Optional<CrappyBody> getBodyFromID(final UUID id){
        if (dynamics.containsKey(id)){
            return Optional.of(dynamics.get(id));
        } else if (kinematics.containsKey(id)){
            return Optional.of(kinematics.get(id));
        } else {
            staticGeometry.getOptional().ifPresent(s -> s.getBody(id));
        }
        return Optional.empty();
    }

    /**
     * Attempts to obtain a CrappyConnector in the world from ID
     * @param id
     * @return an optional that holds that connector (if it is known)
     */
    public Optional<CrappyConnector> getConnectorFromID(final UUID id){
        if (connects.containsKey(id)){
            return Optional.of(connects.get(id));
        }
        return Optional.empty();
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
