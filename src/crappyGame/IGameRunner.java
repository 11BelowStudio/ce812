package crappyGame;

import crappyGame.misc.HighScoreHandler;

import java.awt.Component;

public interface IGameRunner extends IPause, IQuit, IChangeScenes{

    Component getViewComponent();

    HighScoreHandler.ScoreRecord getHighScore();

}
