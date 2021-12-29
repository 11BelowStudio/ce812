package crappy;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.collisions.A_CrappyShape;
import crappy.collisions.Crappy_AABB;
import crappy.utils.bitmasks.IHaveBitmask;

public interface I_View_CrappyBody extends I_Transform, IHaveBitmask {

    I_Vect2D getTempPos();

    I_Rot2D getTempRot();

    I_Vect2D getTempVel();

    double getTempAngVel();

    double getMass();

    Crappy_AABB getAABB();

    A_CrappyShape getShape();

    double getMomentOfInertia();

    CrappyBody.CRAPPY_BODY_TYPE getBodyType();

    double getRestitution();

    Object getUserData();


    class I_CrappyBody_Temp_Transform implements I_Transform{

        private final I_View_CrappyBody cb;

        private Vect2D tempPos;

        private Vect2D tempVel;

        private Rot2D tempRot;

        private double tempAngVel;

        I_CrappyBody_Temp_Transform(final I_View_CrappyBody cb){
            this.cb = cb;
        }

        void update(){
            tempPos = cb.getTempPos().toVect2D();
            tempVel = cb.getTempVel().toVect2D();
            tempRot = cb.getTempRot().toRot2D();
            tempAngVel = cb.getTempAngVel();
        }

        @Override
        public Vect2D getPos() {
            return tempPos;
        }

        @Override
        public Rot2D getRot() {
            return tempRot;
        }

        @Override
        public Vect2D getVel() {
            return tempVel;
        }

        @Override
        public double getAngVel() {
            return tempAngVel;
        }

        /**
         * Returns the 'root' transform of the crappybody this is a temp transform of
         * @return the 'root' transform of the attached crappybody.
         */
        @Override
        public I_Transform getRootTransform(){
            return cb;
        }


    }
}
