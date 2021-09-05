package stickman.model;

import java.time.Duration;
import java.time.Instant;

public interface TimeElapse {
    static Instant startingTime = Instant.now();
    /**
     * This method converts the running time into seconds so that we can make a record
     * @return seconds limited to 60 seconds
     * */
    public long convertTimeCountToSeconds();
    /**
     * This method converts the running time into seconds so that we can make a record
     * @return seconds limited to 60 seconds
     * */
    public Duration convertTimeCountToDuration();
    /**
     * This method an instant of the current time
     * @return current time
     * */
    public Instant getCurrentTime();

    /**
     * This method an instant of the starting time
     *
     * @return starting time
     */
    public Instant getStartingTime();

    /**
     * This method resets the timer to 0 by offsetting the starting time to the current time
     * */
    public void setStartingTimeToNow();
    /**
     * This method resets the timer with a new instant
     * */
    public void setStartingTime(Instant startingTime);
    /**
     * This method rolls the timer to the required seconds
     * */
    public void rollTimeToSeconds(long seconds);

}
