package stickman.level;

import gamesaver.Prototype;
import observer.Observer;
import stickman.entity.Entity;
import stickman.entity.moving.player.Controllable;

import java.util.List;

/**
 * The interface describing the behaviours of a Level.
 * Since the level has the entities, it can be defined as an observer of entities
 * Since there are entities that are Point Giver, its holder must also be
 *      responsible to recieve the points whenever necessary
 */
public interface Level extends Observer, PointReceiver, Prototype {

    /**
     * Gets all the entities within the Level.
     * @return All the entities within the level
     */
    List<Entity> getEntities();

    /**
     * Gets the height of the level.
     * @return The height of the level
     */
    double getHeight();

    /**
     * Gets the width of the level.
     * @return The width of the level
     */
    double getWidth();

    /**
     * Updates the level every frame.
     */
    void tick();

    /**
     * Gets the height of the floor.
     * @return The height of the floor
     */
    double getFloorHeight();

    /**
     * Gets the x-coordinate of the player character.
     * @return The x-coordinate of the player
     */
    double getHeroX();

    /**
     * Gets the y-coordinate of the player character.
     * @return The y-coordinate of the player
     */
    double getHeroY();

    /**
     * Makes the player jump.
     * @return Whether the player jumped
     */
    boolean jump();

    /**
     * Makes the player move left.
     * @return Whether the player moved left
     */
    boolean moveLeft();

    /**
     * Makes the player move right.
     * @return Whether the player moved right
     */
    boolean moveRight();

    /**
     * Stops all horizontal movement of the player.
     * @return Whether the player stopped moving
     */
    boolean stopMoving();

    /**
     * Resets the level.
     */
    void reset();

    /**
     * Makes the player shoot.
     */
    void shoot();

    /**
     * Returns the source file of the level.
     * @return The file the level is based off of
     */
    String getSource();

    /**
     * Stops level and shows victory message.
     */
    void win();

    /**
    * Stops the level and show losing messgae
    * */
    void lose();

    /**
     * This method returns a boolean value to let the GameManager to know whether the level is still running
     * @return status that determines whether the level is running
     */
    boolean getLevelStatus();

    /**
     * This method returns a number of seconds that the game can be played without penalty. For every 1
     * second below this time,
     * there is 1 point added into the score;
     * whereas for every 1 second over this time,
     * there is 1 point deducted from the score.
     * @return Second Limit
     */
    int getTargetPoint();

    /**
    * This method returns the hero
     * @return reference to the stickman
    * */
    Controllable getStickman();

    /**
    * Overwrite the copy method so it returns the level object instead
    * */
    Level copy();

}
