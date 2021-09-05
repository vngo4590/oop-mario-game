package stickman.model;

import gamesaver.MementoOriginator;
import stickman.level.Level;

/**
 * Interface for the GameEngine. Describes the necessary behaviour
 * for running the game.
 */
public interface GameEngine extends MementoOriginator {

    /**
     * Gets the current running level.
     * @return The current level
     */
    Level getCurrentLevel();

    /**
     * Makes the player jump.
     * @return Whether the input had an effect
     */
    boolean jump();

    /**
     * Moves the player left.
     * @return Whether the input had an effect
     */
    boolean moveLeft();

    /**
     * Moves the player right.
     * @return Whether the input had an effect
     */
    boolean moveRight();

    /**
     * Stops player movement.
     * @return Whether the input had an effect
     */
    boolean stopMoving();

    /**
     * Updates the scene every frame.
     */
    void tick();

    /**
     * Makes the player shoot.
     */
    void shoot();

    /**
     * Restarts the level.
     */
    void reset();

    /**
     * Gets the Timer of the game.
     * @return timer of the game
     */
    Timer getTimer ();

    /**
     * Method that updates the points according to the level's updates
     */
    void updatePoint();
    /**
     * Method that returns the current point of the game
     */
    int getCurrentLevelPoint();
    /**
    * Reset current point of the level and then add the current point to the total of points
    * */
    void resetCurrentPoint();
    /**
     * Method that updates the points according to the level's updates
     */
    int getCurrentTotalPoint();
    /**
    * Method that returns the status of the game
     * @return the state of the game
    * */
    GameState getGameState();

    /**
    * This function returns the lives of the stickman
    * */
    int getStickmanLives();

}
