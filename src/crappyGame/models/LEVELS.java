package crappyGame.models;

import crappyGame.Controller.IController;
import crappyGame.IGameRunner;

public enum LEVELS {

    __END_OF_GAME(),

    LEVEL_4(__END_OF_GAME),
    LEVEL_3(LEVEL_4),
    LEVEL_2(LEVEL_3),
    LEVEL_1(LEVEL_2);

    public final LEVELS nextLevel;


    private LEVELS(){
        nextLevel = this;
    }

    private LEVELS(final LEVELS next){
        nextLevel = next;
    }


    public LEVELS getNextLevel() throws EndOfGameException{
        if (this == __END_OF_GAME || nextLevel == this){
            throw new EndOfGameException(this + " has no next level!");
        } else {
            return nextLevel;
        }
    }

    /**
     * Attempts to generate the current level.
     * @param ctrl controller for the game
     * @param lives how many lives are remaining?
     * @param fuelUsed how much fuel has the player used in total?
     * @return the newly-generated level
     * @throws EndOfGameException if we are at the end of the game, and no level can be generated.
     */
    public GameLevel generateThisLevel(final IController ctrl, final int lives, final double fuelUsed, final IGameRunner runner) throws EndOfGameException{

        switch (this){
            case LEVEL_4:
                return new Level4(ctrl, lives, fuelUsed, runner);
            case LEVEL_3:
                return new Level3(ctrl, lives, fuelUsed, runner);
            default:
                return new GameLevel(ctrl, getLevelGeometryMaker(), lives,fuelUsed, runner);
        }
        //return new GameLevel(ctrl, getLevelGeometryMaker(), lives,fuelUsed, runner);
    }


    /**
     * Attempts to return the bifunction which can be used to create the geometry for the next level.
     * @return the bifunction that the next level uses
     * @throws EndOfGameException if there is no level geometry for this level, as it's the end of the game.
     */
    public LevelGeometry.IGeomMaker getLevelGeometryMaker() throws EndOfGameException{

        switch (this){
            case LEVEL_1:
                return LevelGeometry::makeLevel1;
            case LEVEL_2:
                return LevelGeometry::makeLevel2;
            case LEVEL_3:
                return LevelGeometry::makeLevel3;
            case LEVEL_4:
                return LevelGeometry::makeLevel4;
            default:
                throw new EndOfGameException(this + " HAS NO LEVEL ASSOCIATED WITH IT!");
        }


    }

    public static class EndOfGameException extends RuntimeException{

        public EndOfGameException(String s){
            super(s);
        }

    }




}
