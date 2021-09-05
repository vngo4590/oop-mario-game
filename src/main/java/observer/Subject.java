package observer;


import java.util.List;

public interface Subject {
    /**
    * This method adds or sets the observer into the observer items
    * */
    void attach(Observer observer);

    /**
     * This method removes the observer from the object
     * */
    void detach(Observer observer);

    /**
     * This method alerts the observer any update made during this stage
     * */
    void notifyObservers();

    /**
     * This method returns the list of the observer the subject is current having so that when the update can be lossless
     * @return List of observers
     * */
    List<Observer> getObservers();
}
