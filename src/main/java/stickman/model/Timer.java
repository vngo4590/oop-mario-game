package stickman.model;

import java.time.Duration;
import java.time.Instant;

public class Timer {
    /**
     * When the game starts, we record the start time so that we will be able to have a counter of time
     * For more info about this, please visit : http://tutorials.jenkov.com/java-date-time/instant.html
     * */
    private static Instant startingTime;
    public Timer () {
        startingTime = Instant.now();
    }
    public Timer(Instant startingTime) {
        // Preset the time first in case if the inserted time is not working
        this();
        /*
         * We can only go backwards
         * */
        if (startingTime != null && Instant.now().compareTo(startingTime) >= 0) {
            Timer.startingTime = startingTime;
        }
    }

    public static long convertTimeCountToSeconds() {
        // Get the current running time and then compare to the starting time
        Instant currentTime = Instant.now();

        return Duration.between(startingTime, currentTime).toSeconds();
    }


    public static Duration convertTimeCountToDuration() {
        // Get the current running time and then compare to the starting time
        Instant currentTime = Instant.now();
        return Duration.between(startingTime, currentTime);
    }


    public static Instant getCurrentTime() {
        return Instant.now();
    }



    public static void setStartingTimeToNow() {
        startingTime = Instant.now();
    }


    public static void setStartingTime(Instant startingTime) {
        /*
         * We can only go backwards
         * */
        if (startingTime != null && Timer.startingTime.compareTo(startingTime) >= 0) {
            Timer.startingTime = startingTime;
        }
    }


    public static void rollTimeToSeconds(long seconds) {
        setStartingTimeToNow();
        // rolls rolls back the time
        startingTime = startingTime.minusSeconds(seconds);
    }

}
