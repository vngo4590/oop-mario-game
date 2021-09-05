package stickman.entity.still;

import stickman.entity.GameObject;
import stickman.entity.PointGiver;
import stickman.entity.moving.player.Controllable;
import stickman.entity.Interactable;
import stickman.level.PointReceiver;

/**
 * Mushroom object that the player can pick up to get the ability to shoot.
 */
public class Mushroom extends GameObject implements Interactable, PointGiver {
    /**
    * The value of the Mushroom
    * */
    private final static int POINTS = 50;
    /**
     * Creates a new Mushroom object.
     * @param xPos The x-coordinate
     * @param yPos The y-coordinate
     */
    public Mushroom(double xPos, double yPos) {
        super("mushroom_mario.png", xPos, yPos, 20, 20, Layer.FOREGROUND);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void interact(Controllable hero) {

        if (this.active) {
            System.out.println("Mushroom DIED");
            this.active = false;
            // Notify observers when it dies
            notifyObservers();
            hero.upgrade();
        }
    }

    @Override
    public int getPointValue() {
        return POINTS;
    }

    @Override
    public Mushroom copy() {
        Mushroom mushroom = new Mushroom(xPos, yPos);
        mushroom.active = this.active;
        this.getObservers().forEach((mushroom::attach));
        return mushroom;
    }
}
