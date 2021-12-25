package crappy;

import crappy.math.I_Rot2D;
import crappy.math.I_Vect2D;
import crappy.math.Rot2D;
import crappy.math.Vect2D;
import crappy.shapes.A_CrappyShape;
import crappy.shapes.Crappy_AABB;

public interface I_View_CrappyBody extends I_Transform {

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


    class I_CrappyBody_Temp_Transform implements I_Transform{

        private final I_View_CrappyBody cb;

        I_CrappyBody_Temp_Transform(final I_View_CrappyBody cb){
            this.cb = cb;
        }

        @Override
        public Vect2D getPos() {
            return cb.getTempPos().toVect2D();
        }

        @Override
        public Rot2D getRot() {
            return cb.getTempRot().toRot2D();
        }

        @Override
        public Vect2D getVel() {
            return cb.getTempVel().toVect2D();
        }

        @Override
        public double getAngVel() {
            return cb.getTempAngVel();
        }

    }
}
