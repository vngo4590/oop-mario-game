package stickman.level;

/**
* This interface acts as a point storage that obsorbs the points
* */
public interface PointReceiver {
    /**
    * This function returns the current point.
     * @return the current point
    * */
    int getCurrentPoint();
    /**
     * This function adds the point of an object to
     * @return the current point
     * */
    void absorbPoint(int point);
}
