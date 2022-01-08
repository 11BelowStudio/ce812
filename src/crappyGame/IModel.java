package crappyGame;

import crappy.CrappyBody;
import crappyGame.UI.Drawable;
import crappyGame.UI.Viewable;

public interface IModel extends Drawable, Viewable {


    void update();

    void reset();

    boolean isFinished();




}