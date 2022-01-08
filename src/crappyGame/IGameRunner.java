package crappyGame;

import java.awt.Component;

public interface IGameRunner extends IPause, IQuit, IChangeScenes{

    Component getViewComponent();

}
