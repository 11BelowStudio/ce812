package crappy.graphics;

import crappy.CrappyBody;

public interface DrawableBody {

    CrappyBody.CRAPPY_BODY_TYPE getBodyType();

    void drawCrappily(I_CrappilyDrawStuff renderer);

    void updateDrawables();

    CrappyBody getBody();

    DrawableCrappyShape getShape();
}
