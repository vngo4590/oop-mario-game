package stickman.level;

import observer.Observer;
import observer.Subject;
import stickman.entity.*;
import stickman.entity.moving.MovingEntity;
import stickman.entity.moving.other.Bullet;
import stickman.entity.moving.other.Projectile;
import stickman.entity.moving.player.Controllable;
import stickman.entity.moving.player.StickMan;
import stickman.entity.still.Lose;
import stickman.entity.still.Win;
import stickman.model.GameEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * + Implementation of the Level interface. Manages the running of
 * the level and all the entities within it.
 * + Implementation of the subject interface. Attaches and updates to the GameEngine about the status of game
 */
public class LevelManager implements Level, Subject {

    /**
     * The player character.
     */
    private Controllable hero;

    /**
     * A list of all the entities in the level.
     */
    private List<Entity> entities;

    /**
     * A list of all the moving entities in the level.
     */
    private List<MovingEntity> movingEntities;

    /**
     * A list of all the entities that can interact with the player.
     */
    private List<Interactable> interactables;

    /**
     * A list of all the projectiles (bullets) in the level.
     */
    private List<Projectile> projectiles;

    /**
     * The height of the level.
     */
    private double height;

    /**
     * The width of the level.
     */
    private double width;

    /**
     * The height of the floor in the level.
     */
    private double floorHeight;

    /**
     * Whether the entities should update, or the player has reached the flag.
     */
    private boolean active;

    /**
     * The name of the file the level is from.
     */
    private String filename;

    /**
     * The GameEngine the level is running inside of.
     */
    private GameEngine model;
    /**
     * The list of the observers for the level to report to
     * Although, GameEngine is an observer, there might be a lot more observers in different types as well
     */
    private List<Observer> observers;

    /**
     * Set the level's default target point to 30
     */
    private static final int DEFAULT_LEVEL_TARGET_POINT = 30;

    /**
     * This variable defines how many points the level has
     * */
    private int points;

    /**
    * SIze of the hero in String
    * */
    private String heroSize;

    /**
     * The target point of the level.
     */
    private final int targetPoint;

    /**
     * Creates a new LevelManager object.
     * @param model The GameEngine the level is in
     * @param filename The file the level is based off of
     * @param height The height of the level
     * @param width The width of the level
     * @param floorHeight The height of the floor
     * @param heroX The starting x of the hero
     * @param heroSize The size of the hero
     * @param entities The list of entities in the level
     * @param movingEntities The list of moving entities in the level
     * @param interactables The list of entities that can interact with the hero in the level
     * @param targetPoint target point of the level
     */
    public LevelManager(GameEngine model, String filename, double height, double width,
                        double floorHeight, double heroX,
                        String heroSize,
                        List<Entity> entities,
                        List<MovingEntity> movingEntities, List<Interactable> interactables,
                        int targetPoint) {
        this.model = model;
        this.filename = filename;
        this.height = height;
        this.width = width;
        this.floorHeight = floorHeight;
        this.entities = entities;
        this.movingEntities = movingEntities;
        this.interactables = interactables;

        this.projectiles = new ArrayList<>();
        this.heroSize = heroSize;
        // Create new hero
        this.hero = new StickMan(heroX, floorHeight, heroSize, this);
        this.movingEntities.add(this.hero);

        // Ensure entities has all entities (including moving ones)
        this.entities.addAll(movingEntities);
        this.entities = new ArrayList<>(new HashSet<>(entities));

        // This provides a check whether the level is running
        this.active = true;

        // Initialise the list of levels
        this.observers = new ArrayList<>();

        // Set default target point to
        if (targetPoint >= 0) {
            this.targetPoint = targetPoint;
        } else {
            this.targetPoint = DEFAULT_LEVEL_TARGET_POINT;
        }

        // Set the point holder to 0
        this.points = 0;

        // attach the entities with this level
        attachLevelToSubjects();
    }
    /**
    * Because the level is the observer of entities, we have this function to attach this level to those entities
     * who are the subject
    * */
    private void attachLevelToSubjects() {
        for (Entity entity: this.entities) {
            if (entity != null) {
                entity.attach(this);
            }
        }
    }


    @Override
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public void tick() {

        if (!active) {
            return;
        }

        for (MovingEntity entity : this.movingEntities) {
            entity.tick(this.entities, this.hero.getXPos(), this.floorHeight);
        }

        this.manageCollisions();

        // Remove inactive entities
        this.clearOutInactive();
    }

    /**
     * Removes inactive entities from all the lists.
     */
    private void clearOutInactive() {
        this.entities.removeIf(x -> !x.isActive());
        this.movingEntities.removeIf(x -> !this.entities.contains(x));
        this.interactables.removeIf(x -> !this.entities.contains(x));
        this.projectiles.removeIf(x -> !this.entities.contains(x));
    }

    /**
     * Calls interact methods on interactables and projectiles.
     */
    private void manageCollisions() {
        if (!entities.contains(this.hero)) {
            System.out.println("Doesnt have hero");
            return;
        }
        // Collision between hero and other entity
        for (Interactable interactable : this.interactables) {
            if (interactable.checkCollide(this.hero)) {
                interactable.interact(this.hero);
            }
        }

        // Collision between bullet and moving entity (not hero)
        for (Projectile projectile : this.projectiles) {
            projectile.movingCollision(this.movingEntities.stream().filter(x -> x != hero).collect(Collectors.toList()));
        }

        // Collision between bullet and other entity
        for (Projectile projectile : this.projectiles) {
            projectile.staticCollision(this.entities.stream().filter(x -> x != hero).collect(Collectors.toList()));
        }
    }

    @Override
    public double getFloorHeight() {
        return this.floorHeight;
    }

    @Override
    public double getHeroX() {
        return this.hero.getXPos();
    }

    @Override
    public double getHeroY() {
        return this.hero.getYPos();
    }

    @Override
    public boolean jump() {
        if (!active) {
            return false;
        }
        return this.hero.jump();
    }

    @Override
    public boolean moveLeft() {
        if (!active) {
            return false;
        }
        return this.hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        if (!active) {
            return false;
        }
        return this.hero.moveRight();
    }

    @Override
    public boolean stopMoving() {
        if (!active) {
            return false;
        }
        return this.hero.stop();
    }

    @Override
    public void reset() {
        notifyObservers();
        if (this.model != null) {
            this.model.reset();
        }
    }

    @Override
    public void shoot() {
        if (!this.hero.upgraded() || !active) {
            return;
        }

        double x = this.hero.getXPos() + this.hero.getWidth();

        if (this.hero.isLeftFacing()) {
            x = this.hero.getXPos();
        }

        Projectile bullet = new Bullet(x, this.hero.getYPos() + (2 * this.hero.getWidth() / 3), this.hero.isLeftFacing());

        this.entities.add(bullet);
        this.movingEntities.add(bullet);
        this.projectiles.add(bullet);
    }

    @Override
    public String getSource() {
        return this.filename;
    }

    @Override
    public void win() {
        // Once won, we are going to shut down the game
        this.active = false;
        // Once done, the level is going to show the winning message
        this.entities.add(new Win(hero.getXPos() - 200, hero.getYPos() - 200));
        // We must now notify the observers that the game has won
        notifyObservers();
    }
    @Override
    public void lose() {
        // Once won, we are going to shut down the game
        this.active = false;
        // Once done, the level is going to show the losing message
        this.entities.add(new Lose(hero.getXPos(), hero.getYPos() - 200));
        // We must now notify the observers that the game has lost
        notifyObservers();
    }


    @Override
    public boolean getLevelStatus() {
        return this.active;
    }

    @Override
    public int getTargetPoint() {
        return this.targetPoint;
    }

    @Override
    public Controllable getStickman() {
        return this.hero;
    }

    /*
    * The idea is that once the level has updated is status,
    * we will run the update method of the other classes to make sure
    * that those classes get the status of our level
    * */
    @Override
    public void attach(Observer observer) {
        if (observer != null && !this.observers.contains(observer)) {
            this.observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        if (observer != null) {
            this.observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        // run update with the observer to let it knows about the level updates
        this.observers.forEach(Observer::update);
    }

    @Override
    public List<Observer> getObservers() {
        return this.observers;
    }

    @Override
    public void update() {
        // Update the points from the entities
        updatePointsFromDeadEntities();
    }

    /**
    * This function takes responsibility to update the points of the game. It will read thru dead entities
     * and then add points to the level
    * */
    private void updatePointsFromDeadEntities() {
        // For the level, we will have to update our points by looking into dead entities
        // Once we have looked into those entities, we shall remove them
        for (int i = this.entities.size()-1; i >= 0 ; i--) {
            // Now we are going to test whether that this entity is the point giver to absorb the points
            // And that whether this entity has been found dead
            if (this.entities.get(i) instanceof PointGiver && !this.entities.get(i).isActive()) {

                // We now absorb the points from the the entity
                ((PointGiver) this.entities.get(i)).givePoint(this);

                // We now remove it from the level since it is useless now anyway
                this.entities.remove(this.entities.get(i));
            }
        }
    }

    @Override
    public int getCurrentPoint() {
        return this.points;
    }

    @Override
    public void absorbPoint(int point) {
        if (point >=0) {
            this.points += point;
        }
    }


    @Override
    public LevelManager copy() {
        List<Entity> tempEntities = new ArrayList<>();
        List<MovingEntity> tempMovingEntities = new ArrayList<>();
        List<Interactable> tempInteractables = new ArrayList<>();
        List<Projectile> tempProjectile = new ArrayList<>();
        for (Entity entity : entities) {
            Entity sampleEntity = entity.copy();
            if (!(entity instanceof MovingEntity)) {
                tempEntities.add(sampleEntity);
            } else if (!(entity instanceof Controllable)) {
                tempMovingEntities.add((MovingEntity) sampleEntity);
            }
            if (entity instanceof Interactable) {
                tempInteractables.add((Interactable) sampleEntity);
            }
            if (entity instanceof Projectile) {
                tempProjectile.add((Projectile) sampleEntity);
            }
        }

        LevelManager level = new LevelManager (this.model, this.filename, this.height, this.width,
                this.floorHeight, this.hero.getXPos(),
                this.heroSize, tempEntities, tempMovingEntities, tempInteractables,
                this.targetPoint);
//        this.hero.setLevel(level);
        level.points = this.points;
        // Attach observers as we are copying
        this.observers.forEach((level::attach));
        level.projectiles.addAll(tempProjectile);
        level.active = this.active;
        level.entities.remove(level.hero);
        level.movingEntities.remove(level.hero);
        level.hero = (Controllable) this.hero.copy();
        level.hero.setLevel(level);
        level.hero.attach(level);
        level.movingEntities.add(level.hero);
        level.entities.add(level.hero);
        return level;
    }
}
