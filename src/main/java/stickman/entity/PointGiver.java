package stickman.entity;

import stickman.level.PointReceiver;
/**
* This interface acts as a donator to PointReceiver
* */
public interface PointGiver {
    /**
    * This function gives the points to the point receiver
     * @param receiver : The object that absorbs the points
    * */
    default void givePoint(PointReceiver receiver) {
        receiver.absorbPoint(getPointValue());
    }
    /**
    * This function returns the points of this object
    * */
    int getPointValue();
}
