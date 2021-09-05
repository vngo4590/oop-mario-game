package stickman.entity.still;

import stickman.entity.GameObject;
/**
 * The losing message displayed after collecting the flag.
 */
public class Lose extends GameObject {
    /**
     * Constructs a new lose object.
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public Lose(double x, double y) {
        // Show the vistory image once done
        super("lose.png", x, y, 400, 400, Layer.EFFECT);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public Lose copy() {
        Lose lose = new Lose(xPos, yPos);
        lose.active = this.active;
        this.getObservers().forEach((lose::attach));
        return lose;
    }
}
