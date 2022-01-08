package crappyGame;

import javax.swing.*;

public interface IGameRunner extends IPause, IQuit, IChangeScenes{

    JComponent getViewComponent();

}
