package gamesaver;

import stickman.entity.moving.other.Projectile;
import stickman.level.Level;
import stickman.model.GameState;

/**
* This interface is dedicated to defining the memory of the game
* */
public interface GameMemento extends Prototype {
    /**
    * Save the number of seconds in the game
     * @param seconds : The number of seconds that the game has been running
    * */
    void setTimerSeconds(long seconds);
    /**
    * Gets the number of seconds in the game
     * @return the number of seconds stored in this memento
    * */
    long getTimerSeconds();
    /**
    * Set the lives of the current level
     * @param lives : current level's lives
    * */
    void setLives(int lives);
    /**
    * Returns the number of lives of the level
     * @return the number of lives
    * */
    int getLives();
    /**
     * Set the level using prototype pattern
     * @param level : the level of the game
     * */
    void setLevel(Level level);
    /**
    * Returns the copy of the level to avoid modifying references
     * @return copy of the level.
    * */
    Level getLevel();

    void setCurrentPoint(int currentPoint);
    int getCurrentPoint();

    void setTotalPoint(int totalPoint);
    int getTotalPoint();

    void setGameState(GameState gameState);
    GameState getGameState();

    GameMemento copy();
    int getLevelIndex();


}
