package crappyGame;

import crappyGame.UI.Viewable;

public interface IModel extends Drawable, Viewable {

    void update();

    boolean isFinished();




}
