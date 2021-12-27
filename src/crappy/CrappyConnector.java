package crappy;

import crappy.math.Vect2D;
import crappy.math.Vect2DMath;
import crappy.utils.containers.IPair;


import java.util.function.DoubleUnaryOperator;

@SuppressWarnings("BooleanParameter")
public class CrappyConnector implements IPair<Vect2D, Vect2D> {


    private static final TruncationRule _NO_TRUNCATION = new NoTruncation();

    private final CrappyBody_Connector_Interface bodyA;
    private final Vect2D bodyALocalPos;

    private final CrappyBody_Connector_Interface bodyB;
    private final Vect2D bodyBLocalPos;

    private final double naturalLength;
    private final double springConstant;
    private final double motionDampingConstant;
    private final boolean canGoSlack;

    private final DoubleUnaryOperator truncationRule;


    public CrappyConnector(
            final CrappyBody_Connector_Interface bodyA,
            final Vect2D bodyALocalPos,
            final CrappyBody_Connector_Interface bodyB,
            final Vect2D bodyBLocalPos,
            final double naturalLength,
            final double constant,
            final double damping,
            final boolean slack,
            final DoubleUnaryOperator trunc
    ){
        this.bodyA = bodyA;
        this.bodyALocalPos = bodyALocalPos;
        this.bodyB = bodyB;
        this.bodyBLocalPos = bodyBLocalPos;
        this.naturalLength = naturalLength;
        this.springConstant = constant;
        this.motionDampingConstant = damping;
        this.canGoSlack = slack;
        this.truncationRule = trunc;
    }

    public CrappyConnector(
            final CrappyBody_Connector_Interface bodyA,
            final Vect2D bodyALocalPos,
            final CrappyBody_Connector_Interface bodyB,
            final Vect2D bodyBLocalPos,
            final double constant,
            final double damping,
            final boolean slack,
            final DoubleUnaryOperator trunc
    ){
        this(
                bodyA, bodyALocalPos, bodyB, bodyBLocalPos,
                Vect2DMath.DIST(
                        bodyALocalPos.localToWorldCoordinates(bodyA),
                        bodyBLocalPos.localToWorldCoordinates(bodyB)
                ),
                constant, damping, slack, trunc
        );
    }

    /**
     * This method actually applies the forces to the bodies which this connector connects
     */
    public void applyForcesToBodies(){
        final double tension = calculateTension();
        final Vect2D normDist = normalizedVectorFromAToB();

        bodyA.applyMidTimestepForce(normDist.mult(tension), bodyALocalPos, CrappyBody.FORCE_SOURCE.ENGINE);
        bodyB.applyMidTimestepForce(normDist.mult(-tension), bodyBLocalPos, CrappyBody.FORCE_SOURCE.ENGINE);

    }

    double calculateTension(){

        final double dist = Vect2DMath.DIST(
                bodyAWorldPos(),
                bodyBWorldPos()
        );
        if (canGoSlack && dist < naturalLength){
            return 0;
        }

        final double extensionRatio = (dist-naturalLength)/naturalLength;

        final double hookeTension = truncationRule.applyAsDouble(extensionRatio) * springConstant;
        final double dampingTension = motionDampingConstant * rateOfChangeOfExtension();

        return hookeTension + dampingTension;
    }

    double rateOfChangeOfExtension(){

        return Vect2DMath.MINUS(
                bodyALocalPos.getWorldVelocityOfLocalCoordinate(bodyA.getTempRot(), bodyA.getTempPos(), bodyA.getTempAngVel(), bodyA.getTempVel()),
                bodyBLocalPos.getWorldVelocityOfLocalCoordinate(bodyB.getTempRot(), bodyB.getTempPos(), bodyB.getTempAngVel(), bodyB.getTempVel())
        ).dot(
                normalizedVectorFromAToB()
        );

    }

    private Vect2D bodyAWorldPos(){
        return bodyALocalPos.localToWorldCoordinates(bodyA.getTempPos(), bodyA.getTempRot());
    }

    private Vect2D bodyBWorldPos(){
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


    public static enum TruncationEnum{
        NO_TRUNCATION,
        STANDARD_TRUNCATION,
        COSINE_TRUNCATION;
    }

    public static TruncationRule TRUNCATION_RULE_FACTORY(final TruncationEnum rule, final double trunc){
        switch (rule){
            case STANDARD_TRUNCATION:
                return new StandardTruncation(trunc);
            case COSINE_TRUNCATION:
                return new SineTruncation(trunc);
            case NO_TRUNCATION:
            default:
                return _NO_TRUNCATION;
        }
    }


    @FunctionalInterface
    public static interface TruncationRule extends DoubleUnaryOperator {
        double applyAsDouble(final double rawTension);
    }

    /**
     * Truncation rule which we apply when we aren't bothering to truncate the spring stuff
     */
    static final class NoTruncation implements TruncationRule{

        NoTruncation(){}

        @Override
        public double applyAsDouble(final double rawTension) {
            return rawTension;
        }
    }

    /**
     * Truncation rule which just limits extension ratios to be in range(-limit, limit)
     */
    public static class StandardTruncation implements TruncationRule{

        /**
         * upper/lower bound for the extension ratio
         */
        final double limit;

        /**
         * Initialize a StandardTruncation rule with given limit
         * @param d defines lower/upper bound for extension ratio (forced to be positive)
         */
        public StandardTruncation(final double d){
            limit = Math.abs(d);
        }

        /**
         * Truncates rawTension to ensure it is in range (-limit <= rawTension <= limit)
         * @param rawTension the double to truncate
         * @return rawTension, capped to be in range (-limit <= rawTension <= limit)
         */
        @Override
        public double applyAsDouble(final double rawTension){
            if (rawTension > limit){
                return limit;
            } else if (rawTension < -limit){
                return -limit;
            }
            return rawTension;
        }

    }

    /**
     * Truncation rule which just limits values to be in range (-limit, limit) but uses the cosine rule to
     * smoothen the truncation a bit
     */
    public static class SineTruncation extends StandardTruncation{


        public SineTruncation(final double d){
            super(d);
        }

        @Override
        public double applyAsDouble(final double rawTension){
            if (rawTension >= limit){
                return limit;
            } else if (rawTension <= -limit){
                return -limit;
            }
            return Math.cos(
                    ((rawTension-limit)/(limit*2)) // -1 if aDouble is at lower bound, 0 if aDouble is at upper bound
                            * Math.PI //-PI if at lower bound, 0 if at upper bound
            ) * limit * 2;
        }
    }

}


