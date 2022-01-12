/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy;

import crappy.graphics.DrawableConnector;
import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.containers.IPair;


import java.util.Objects;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;

/**
 * A connector (usable as an elastic joint) for use within Crappy/
 *
 * Somewhat based on the 'ElasticConnector' class provided by Dr. Michael Fairbank
 * as part of the the CE812 Physics Based Games module at the University of Essex.
 *
 * @author Rachel Lowe
 */
@SuppressWarnings("BooleanParameter")
public class CrappyConnector implements IPair<Vect2D, Vect2D>, DrawableConnector, CrappyConnectorBodyInterface, IHaveIdentifier {
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

    public final UUID id = UUID.randomUUID();


    /**
     * Constructor
     * @param bodyA first body
     * @param bodyALocalPos local pos of connector in first body
     * @param bodyB second body
     * @param bodyBLocalPos local pos of connector in second body
     * @param naturalLength natural length between connection points. Absolute value will be used.
     *                      If not finite, automatically overwritten with current dist between the points.
     * @param constant spring constant
     * @param damping damping to apply
     * @param slack can the spring go slack?
     * @param trunc truncation rule we're using,
     *              see {@link #TRUNCATION_RULE_FACTORY(TruncationEnum, double...)} for further assistance.
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
        if (Double.isFinite(naturalLength)) {
            this.naturalLength = Math.abs(naturalLength);
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
        bodyB.__addConnector_internalPlsDontUseManually(this);
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
     * @param trunc truncation rule we're using,
     *              see {@link #TRUNCATION_RULE_FACTORY(TruncationEnum, double...)} for further assistance.
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

        //System.out.println("b vel = " + bodyBLocalPos.getWorldVelocityOfLocalCoordinate(bodyB.getTempTransform()));
        //System.out.println("a vel = " + bodyALocalPos.getWorldVelocityOfLocalCoordinate(bodyA.getTempTransform()));
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




    public static CrappyConnectorMaker GET_CREATOR(){ return new CrappyConnectorMaker(); }

    /**
     * Obtains unique identifier for object
     *
     * @return unique identifier.
     */
    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrappyConnector that = (CrappyConnector) o;
        return Double.compare(that.getNaturalLength(), getNaturalLength()) == 0 && Double.compare(that.springConstant, springConstant) == 0 && Double.compare(that.motionDampingConstant, motionDampingConstant) == 0 && canGoSlack == that.canGoSlack && bodiesCanCollide == that.bodiesCanCollide && bodyA.equals(that.bodyA) && bodyALocalPos.equals(that.bodyALocalPos) && bodyB.equals(that.bodyB) && bodyBLocalPos.equals(that.bodyBLocalPos) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bodyA, bodyALocalPos, bodyB, bodyBLocalPos, getNaturalLength(), springConstant, motionDampingConstant, canGoSlack, bodiesCanCollide, id);
    }

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

        /**
         * Truncation rule being used.
         * @see #TRUNCATION_RULE_FACTORY(TruncationEnum, double...) for further help. 
         */
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

    /**
     * Used in {@link #TRUNCATION_RULE_FACTORY(TruncationEnum, double...)}
     * @see #TRUNCATION_RULE_FACTORY(TruncationEnum, double...)
     */
    public static enum TruncationEnum{
        /**
         * @see TRUNCATION_RULES#_NO_TRUNCATION
         */
        NO_TRUNCATION,
        /**
         * @see TRUNCATION_RULES.StandardTruncation
         */
        STANDARD_TRUNCATION,
        /**
         * @see TRUNCATION_RULES.TanhTruncation
         */
        TANH_TRUNCATION,
        /**
         * @see TRUNCATION_RULES.PartialTanhTruncation
         */
        PARTIAL_TANH;
    }


    /**
     * Attempts to create an appropriate truncation function based on what the programmer asks for
     * @param rule the rule to use
     * @param truncValuesEtc First index is the truncation amount to use.
     *                       Further indices are for extra variables for the specified rules.
     * @return a DoubleUnaryOperator to truncate the tension according to the desired rule.
     * @throws IllegalArgumentException if there's an incorrect value given.
     * @apiNote NaN (or zero) for truncation amount are treated as 'return zero',
     * any infinity for truncation amount is treated as 'return as-is',
     * @see TruncationEnum
     * @see TRUNCATION_RULES
     */
    public static DoubleUnaryOperator TRUNCATION_RULE_FACTORY(
            final TruncationEnum rule, final double... truncValuesEtc
    ) throws IllegalArgumentException {
        final double trunc = truncValuesEtc[0];
        if (Double.isFinite(trunc)) {
            switch (rule) {
                case PARTIAL_TANH:
                    if (Double.compare(trunc, 0) == 0){
                        // partial tanh truncation involves division by trunc,
                        // so, if trunc is zero, we shouldn't use this.
                        // If there's a second variable given, we'll
                        // see if it's possible to perform a standard truncation on that.
                        // Otherwise, we'll just return zero.
                        if (truncValuesEtc.length > 1){
                            return TRUNCATION_RULE_FACTORY(TruncationEnum.STANDARD_TRUNCATION, truncValuesEtc[1]);
                        }
                        return TRUNCATION_RULES._RETURN_ZERO;
                    }
                    if (truncValuesEtc.length > 1){
                        final double proportion = Math.abs(truncValuesEtc[1]);
                        if (proportion > 1){
                            throw new IllegalArgumentException(
                                    "Index 1 of truncValuesEtc for PARTIAL TANH needs to have absolute value between 0 and 1," +
                                    "recieved absolute value of " + proportion + "!!!"
                            );
                        }
                        return new TRUNCATION_RULES.PartialTanhTruncation(trunc, proportion);
                    }
                    return new TRUNCATION_RULES.PartialTanhTruncation(trunc);
                case TANH_TRUNCATION:
                    if (Double.compare(trunc, 0) == 0){
                        // tanh truncation involves division by trunc,
                        // so, if trunc is zero, we shouldn't use TanhTruncation,
                        // and we'll just use 'RETURN ZERO' instead
                        return TRUNCATION_RULES._RETURN_ZERO;
                    }
                    return new TRUNCATION_RULES.TanhTruncation(trunc);
                case STANDARD_TRUNCATION:
                    return new TRUNCATION_RULES.StandardTruncation(trunc);
                case NO_TRUNCATION:
                default:
                    return TRUNCATION_RULES._NO_TRUNCATION;
            }
        }
        if (Double.isNaN(trunc)){
            return TRUNCATION_RULES._RETURN_ZERO;
        }
        return TRUNCATION_RULES._NO_TRUNCATION;
    }

    /**
     * Inner class with the usable truncation rules
     */
    public static final class TRUNCATION_RULES {

        private TRUNCATION_RULES(){}


        @FunctionalInterface
        public static interface TruncationRule extends DoubleUnaryOperator {
            double applyAsDouble(final double rawTension);
        }

        /**
         * Truncation rule which we apply when we aren't bothering to truncate the spring stuff
         */
        public static final TruncationRule _NO_TRUNCATION = rawTension -> rawTension;

        /**
         * A truncation 'rule' that just returns zero. Not sure why you would want to use this,
         * but, if you really want to use it, you can.
         */
        public static final TruncationRule _RETURN_ZERO = x -> 0;


        /**
         * Truncation rule which just limits extension ratios to be in range(-limit, limit)
         */
        public static class StandardTruncation implements TruncationRule {

            /**
             * upper/lower bound for the extension ratio
             */
            protected final double limit;

            /**
             * Initialize a StandardTruncation rule with given limit
             *
             * @param l defines lower/upper bound for extension ratio (forced to be positive)
             * @throws IllegalArgumentException if l is not finite.
             */
            public StandardTruncation(final double l) throws IllegalArgumentException {
                if (Double.isFinite(l)) {
                    limit = Math.abs(l);
                } else {
                    throw new IllegalArgumentException("Cannot use a non-finite value for limit! Received " + l);
                }
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
         * Truncation rule which just limits values to be in range (-limit, limit) via the hyperbolic tangent function
         */
        public static class TanhTruncation extends StandardTruncation {


            /**
             * TanhTruncation constructor
             * @param limit limit to use
             * @throws IllegalArgumentException if limit is 0.
             */
            public TanhTruncation(final double limit) throws IllegalArgumentException {
                super(limit);
                if (Math.abs(limit) == 0){
                    throw new IllegalArgumentException("TanhTruncation cannot use a limit of zero!");
                }
            }

            /**
             *
             * @param rawTension the double to truncate
             *
             * @return tanh(raw/limit) * limit
             */
            @Override
            public double applyAsDouble(final double rawTension) {
                return Math.tanh(rawTension/limit) * limit;
            }
        }

        /**
         * Like tanhtruncation, but you can define a certain proportion of the raw tension to be 'safe'
         * from truncation.
         *
         * So, if you had a limit of lim, and defined a safeProportion of safe,
         * giving a rawtension of raw:
         * <html><pre>
         *      let S = (lim * safe)
         *      let L = lim - S
         *      let R = abs(raw)
         *
         *      if R <= S: -> raw
         *      else:
         *          S + (tanh(R-S/L) * L) * sign(raw)</pre></html>
         *
         * so basically everything within safeProportion of limit goes untruncated,
         * but everything above that safeProportion is given tanhTruncation.
         *
         */
        public static class PartialTanhTruncation implements TruncationRule{

            /**
             * 'safe' part of the limit, untruncated
             */
            private final double untruncatedLimit;

            /**
             * 'unsafe' part of the limit, given tanh truncation
             */
            private final double truncRemainder;

            /**
             * True if truncRemainder is 0, so we can avoid dividing by 0.
             */
            private final boolean noRemainder;

            /**
             * Partial truncation with proportion 0.5
             * @param limit the total amount to truncate
             */
            public PartialTanhTruncation(final double limit){
                this(limit, 0.5);
            }

            /**
             * Partial truncation with specified limit
             * @param limit total limit
             * @param safeProportion proportion of total limit that isn't truncated.
             */
            public PartialTanhTruncation(final double limit, final double safeProportion){
                final double lim = Math.abs(limit);
                if (lim == 0){
                    throw new IllegalArgumentException("Cannot use a limit of 0!");
                }
                final double safe = Math.min(Math.abs(safeProportion), 1);

                untruncatedLimit = lim * safe;
                truncRemainder = lim - untruncatedLimit;
                noRemainder = (truncRemainder >= 0);
            }


            @Override
            public double applyAsDouble(final double rawTension) {
                final double absRaw = Math.abs(rawTension); // find absolute tension
                if (absRaw <= untruncatedLimit){
                    return rawTension; // if it's within the safe limit, return as-is
                } else if (noRemainder){
                    // if there's no remainder, perform standard truncation
                    return untruncatedLimit * Math.signum(rawTension);
                }
                // if it's not in the safe limit

                // we first get the untruncated amount
                return untruncatedLimit + (
                        // then we apply tanh truncation to the remainder of the absolute raw value
                        Math.tanh((absRaw - untruncatedLimit)/truncRemainder) * truncRemainder
                ) * Math.signum(rawTension); // and then factor in the sign of the non-absolute raw value
            }
        }


    }

}


