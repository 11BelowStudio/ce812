package crappy;

import crappy.graphics.DrawableConnector;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.containers.IPair;


import java.util.function.DoubleUnaryOperator;

/**
 * A connector (usable as an elastic joint) for use within Crappy
 *
 * @author Rachel Lowe
 */
@SuppressWarnings("BooleanParameter")
public class CrappyConnector implements IPair<Vect2D, Vect2D>, DrawableConnector {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    private final CrappyBody_Connector_Interface bodyA;
    private final Vect2D bodyALocalPos;

    private final CrappyBody_Connector_Interface bodyB;
    private final Vect2D bodyBLocalPos;

    private final double naturalLength;
    private final double springConstant;
    private final double motionDampingConstant;
    private final boolean canGoSlack;

    private final DoubleUnaryOperator truncationRule;

    private boolean allowedToExist = true;

    private final Object drawSyncer = new Object();

    private Vect2D drawablePosA;

    private Vect2D drawablePosB;

    private final boolean bodiesCanCollide;

    private boolean pendingRemoval = false;


    /**
     * Constructor
     * @param bodyA first body
     * @param bodyALocalPos local pos of connector in first body
     * @param bodyB second body
     * @param bodyBLocalPos local pos of connector in second body
     * @param naturalLength natural length between connection points. if not finite or below 0, automatically overwritten with current dist between the points.
     * @param constant spring constant
     * @param damping damping to apply
     * @param slack can the spring go slack?
     * @param trunc truncation rule we're using
     * @param bodiesCanCollide are these bodies allowed to collide with each other?
     */
    public CrappyConnector(
            final CrappyBody_Connector_Interface bodyA,
            final Vect2D bodyALocalPos,
            final CrappyBody_Connector_Interface bodyB,
            final Vect2D bodyBLocalPos,
            final double naturalLength,
            final double constant,
            final double damping,
            final boolean slack,
            final DoubleUnaryOperator trunc,
            final boolean bodiesCanCollide
    ){
        this.bodyA = bodyA;
        this.bodyALocalPos = bodyALocalPos;
        this.bodyB = bodyB;
        this.bodyBLocalPos = bodyBLocalPos;
        if (Double.isFinite(naturalLength) && naturalLength >= 0) {
            this.naturalLength = naturalLength;
        } else {
            this.naturalLength = Vect2DMath.DIST(
                    bodyALocalPos.localToWorldCoordinates(bodyA),
                    bodyBLocalPos.localToWorldCoordinates(bodyB)
            );
        }
        this.springConstant = constant;
        this.motionDampingConstant = damping;
        this.canGoSlack = slack;
        this.truncationRule = trunc;
        this.bodiesCanCollide = bodiesCanCollide;
        bodyA.__addConnector_internalPlsDontUseManually(this);
        bodyB.__removeConnector_internalPlsDontUseManually(this);
    }


    /**
     * Constructor that finds natural length automatically.
     * @param bodyA first body
     * @param bodyALocalPos local pos of connector in first body
     * @param bodyB second body
     * @param bodyBLocalPos local pos of connector in second body
     * @param constant spring constant
     * @param damping damping to apply
     * @param slack can the spring go slack?
     * @param trunc truncation rule we're using
     * @param bodiesCanCollide are these bodies allowed to collide with each other?
     */
    public CrappyConnector(
            final CrappyBody_Connector_Interface bodyA,
            final Vect2D bodyALocalPos,
            final CrappyBody_Connector_Interface bodyB,
            final Vect2D bodyBLocalPos,
            final double constant,
            final double damping,
            final boolean slack,
            final DoubleUnaryOperator trunc,
            final boolean bodiesCanCollide
    ){
        this(
                bodyA, bodyALocalPos, bodyB, bodyBLocalPos,
                Vect2DMath.DIST(
                        bodyALocalPos.localToWorldCoordinates(bodyA),
                        bodyBLocalPos.localToWorldCoordinates(bodyB)
                ),
                constant, damping, slack, trunc, bodiesCanCollide
        );
    }

    /**
     * Are both of the children which this joint connects still active, and is this joint still allowed to exist?
     * @return true if this joint is allowed to exist and both children are active.
     */
    public boolean shouldIStillExist(){

        return allowedToExist && bodyA.isActive() && bodyB.isActive();
    }

    /**
     * Wrapper for inverted {@link #shouldIStillExist()}, but also handles removing self from the bodies this connects
     * if this shouldn't still exist.
     * @return false if this should still exist, otherwise true if this should NOT exist (and needs disposal).
     */
    public boolean startingDisposal(){
        if (shouldIStillExist()){
            return false;
        }
        allowedToExist = false;
        bodyA.__removeConnector_internalPlsDontUseManually(this);
        bodyB.__removeConnector_internalPlsDontUseManually(this);
        return true;
    }

    /**
     * Updates whether or not this joint should be allowed to exist.
     * If set to false, this joint will be removed at the end of this timestep.
     * @param allowedToExist true if this joint should be allowed to exist.
     */
    public void setAllowedToExist(final boolean allowedToExist){
        this.allowedToExist = allowedToExist;
    }

    /**
     * This method actually applies the forces to the bodies which this connector connects
     */
    public void applyForcesToBodies(){
        final double tension = calculateTension();
        //System.out.println("tension = " + tension);
        final Vect2D normDist = normalizedVectorFromAToB();
        //System.out.println("normDist = " + normDist);

        //System.out.println("normDist.mult(-tension) = " + normDist.mult(-tension));
        //System.out.println("normDist.mult(tension) = " + normDist.mult(tension));

        //System.out.println("tension = " + tension);
        //System.out.println("normDist = " + normDist);

        bodyA.applyMidTimestepForce(normDist.mult(tension), bodyALocalPos, CrappyBody.FORCE_SOURCE.ENGINE);
        bodyB.applyMidTimestepForce(normDist.mult(-tension),  bodyBLocalPos, CrappyBody.FORCE_SOURCE.ENGINE);

    }

    double calculateTension(){

        final double dist = Vect2DMath.DIST(
                bodyAWorldPos(),
                bodyBWorldPos()
        );
        //System.out.println("dist = " + dist);
        if (canGoSlack && dist <= naturalLength){
            return 0;
        }

        
        final double extensionRatio = (dist-naturalLength)/naturalLength;
        //System.out.println("extensionRatio = " + extensionRatio);

        final double hookeTension = truncationRule.applyAsDouble(extensionRatio) * springConstant;

        //System.out.println("truncationRule.applyAsDouble(extensionRatio) = " + truncationRule.applyAsDouble(extensionRatio));
        //System.out.println("hookeTension = " + hookeTension);
        
        final double dampingTension = motionDampingConstant * rateOfChangeOfExtension();
        //System.out.println("rateOfChangeOfExtension() = " + rateOfChangeOfExtension());
        //System.out.println("dampingTension = " + dampingTension);

        return hookeTension + dampingTension;
    }

    double rateOfChangeOfExtension(){

        return Vect2DMath.MINUS(
                bodyBLocalPos.getWorldVelocityOfLocalCoordinate(bodyB.getTempTransform()),
                bodyALocalPos.getWorldVelocityOfLocalCoordinate(bodyA.getTempTransform())
        ).dot(
                normalizedVectorFromAToB()
        );

    }

    public Vect2D bodyAWorldPos(){
        return bodyALocalPos.localToWorldCoordinates(bodyA.getTempPos(), bodyA.getTempRot());
    }

    public Vect2D bodyBWorldPos(){
        return bodyBLocalPos.localToWorldCoordinates(bodyB.getTempPos(), bodyB.getTempRot());
    }


    private Vect2D normalizedVectorFromAToB(){
        return Vect2DMath.MINUS_M(
                        bodyBWorldPos(),
                        bodyAWorldPos()
                ).norm()
                .finished();
    }

    @Override
    public Vect2D getFirst() {
        return bodyAWorldPos();
    }

    @Override
    public Vect2D getSecond() {
        return bodyBWorldPos();
    }

    /**
     * Whether or not the attached bodies can collide
     * @return true if the attached bodies can collide with each other, otherwise false.
     */
    public boolean canBodiesCollide(){
        return bodiesCanCollide;
    }

    /**
     * Returns the other body this connector connects
     * @param bod the body we want the other one to
     * @return bodyB if bod is bodyA, else return bodyA.
     */
    public CrappyBody_Connector_Interface getOtherBody(final IHaveIdentifier bod){
        if (bod.equalsID(bodyA)){
            return bodyB;
        } else {
            return bodyA;
        }
    }


    public void updateDrawables(){
        synchronized (drawSyncer){
            drawablePosA = bodyAWorldPos();
            drawablePosB = bodyBWorldPos();
        }
    }


    @Override
    public Vect2D getDrawableAPos() {
        synchronized (drawSyncer){
            return drawablePosA;
        }
    }

    @Override
    public Vect2D getDrawableBPos() {
        synchronized (drawSyncer){
            return drawablePosB;
        }
    }

    @Override
    public double getNaturalLength() {
        return naturalLength;
    }


    public static enum TruncationEnum{
        NO_TRUNCATION,
        STANDARD_TRUNCATION,
        COSINE_TRUNCATION;
    }

    public static CrappyConnectorMaker GET_CREATOR(){ return new CrappyConnectorMaker(); }

    /**
     * Here to make it a bit easier to make a CrappyConnector, inspired by JBox2D JointDef stuff.
     */
    public static class CrappyConnectorMaker{

        public CrappyBody bodyA;

        public CrappyBody bodyB;

        public Vect2D bodyAPos = Vect2D.ZERO;

        public Vect2D bodyBPos = Vect2D.ZERO;

        public double naturalLength = Double.NaN;

        public double springConstant;

        public double motionDampingConstant;

        public boolean canGoSlack = false;

        public DoubleUnaryOperator truncationRule = TRUNCATION_RULES._NO_TRUNCATION;

        public boolean bodiesCanCollide = false;

        public CrappyConnectorMaker(){}

        /**
         * Turns this into a CrappyConnector, and adds it to the specified CrappyWorld
         * @param w the CrappyWorld to attach this CrappyConnector to
         * @return the newly made CrappyConnector.
         */
        public CrappyConnector MAKE_CONNECTOR(final CrappyWorld w){
            final CrappyConnector c = MAKE_CONNECTOR();
            w.addConnector(c);
            return c;
        }

        /**
         * Turns this into a CrappyConnector
         * @return the newly-generated CrappyConnector
         */
        public CrappyConnector MAKE_CONNECTOR(){

            return new CrappyConnector(
                    bodyA,
                    bodyAPos,
                    bodyB,
                    bodyBPos,
                    naturalLength,
                    springConstant,
                    motionDampingConstant,
                    canGoSlack,
                    truncationRule,
                    bodiesCanCollide
            );
        }

    }

    public static DoubleUnaryOperator TRUNCATION_RULE_FACTORY(final TruncationEnum rule, final double trunc){
        switch (rule){
            case STANDARD_TRUNCATION:
                return new TRUNCATION_RULES.StandardTruncation(trunc);
            case COSINE_TRUNCATION:
                return new TRUNCATION_RULES.SineTruncation(trunc);
            case NO_TRUNCATION:
            default:
                return TRUNCATION_RULES._NO_TRUNCATION;
        }
    }

    /**
     * Inner class with the usable truncation rules
     */
    public static final class TRUNCATION_RULES {


        @FunctionalInterface
        public static interface TruncationRule extends DoubleUnaryOperator {
            double applyAsDouble(final double rawTension);
        }

        /**
         * Truncation rule which we apply when we aren't bothering to truncate the spring stuff
         */
        public static final TruncationRule _NO_TRUNCATION = rawTension -> rawTension;


        /**
         * Truncation rule which just limits extension ratios to be in range(-limit, limit)
         */
        public static class StandardTruncation implements TruncationRule {

            /**
             * upper/lower bound for the extension ratio
             */
            final double limit;

            /**
             * Initialize a StandardTruncation rule with given limit
             *
             * @param d defines lower/upper bound for extension ratio (forced to be positive)
             */
            public StandardTruncation(final double d) {
                limit = Math.abs(d);
            }

            /**
             * Truncates rawTension to ensure it is in range (-limit <= rawTension <= limit)
             *
             * @param rawTension the double to truncate
             *
             * @return rawTension, capped to be in range (-limit <= rawTension <= limit)
             */
            @Override
            public double applyAsDouble(final double rawTension) {
                if (rawTension > limit) {
                    return limit;
                } else if (rawTension < -limit) {
                    return -limit;
                }
                return rawTension;
            }

        }

        /**
         * Truncation rule which just limits values to be in range (-limit, limit) but uses the cosine rule to smoothen
         * the truncation a bit
         */
        public static class SineTruncation extends StandardTruncation {


            public SineTruncation(final double d) {
                super(d);
            }

            @Override
            public double applyAsDouble(final double rawTension) {
                if (rawTension >= limit) {
                    return limit;
                } else if (rawTension <= -limit) {
                    return -limit;
                }
                return Math.cos(
                        ((rawTension - limit) / (limit * 2)) // -1 if aDouble is at lower bound, 0 if aDouble is at upper bound
                                * Math.PI //-PI if at lower bound, 0 if at upper bound
                ) * limit * 2;
            }
        }

    }

}


