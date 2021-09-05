package stickman.entity;

import gamesaver.Prototype;
import observer.Observer;
import observer.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of Entity interface. Used to reduce
 * repetitive code in classes implementing Entity.
 */
public abstract class GameObject implements Entity {

    /**
     * The path to the sprite file.
     */
    protected String imagePath;

    /**
     * The x-coordinate of the entity.
     */
    protected double xPos;

    /**
     * The y-coordinate of the entity.
     */
    protected double yPos;

    /**
     * The height of the entity.
     */
    protected double height;

    /**
     * The width of the entity.
     */
    protected double width;

    /**
     * The layer on which the entity exists.
     */
    protected Entity.Layer layer;

    /**
     * Whether the entity is active. (This determines the livelihood of the object)
     */
    protected boolean active;
    /**
     * Observers of this entity. Observers will get notified if there are any changes.
     * */
    List<Observer> observers;

    /**
     * Constructs a GameObject object.
     * @param imagePath The path to the sprite.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param height The height of the entity.
     * @param width The width of the entity.
     * @param layer The layer the entity exists on.
     */
    public GameObject(String imagePath, double x, double y, double height, double width, Entity.Layer layer) {
        this.imagePath = imagePath;
        this.xPos = x;
        this.yPos = y;
        this.width = width;
        this.height = height;
        this.layer = layer;
        this.active = true;
        this.observers = new ArrayList<>();
    }

    @Override
    public String getImagePath() {
        return this.imagePath;
    }

    @Override
    public double getXPos() {
        return this.xPos;
    }

    @Override
    public double getYPos() {
        return this.yPos;
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
    public Entity.Layer getLayer() {
        return this.layer;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void attach(Observer observer) {
        if (observer != null) {
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


}
