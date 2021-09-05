package stickman.model;

import gamesaver.*;
import observer.Observer;
import observer.Subject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stickman.entity.Entity;
import stickman.entity.still.Lose;
import stickman.entity.still.Win;
import stickman.level.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of GameEngine. Manages the running of the game.
 */
public class GameManager implements GameEngine, Observer {

    /**
     * The current level
     */
    private Level level;

    /**
     * List of all level files
     */
    private List<String> levelFileNames;
    /**
     * The index of the level
     */
    private int levelIndex;

    /**
    * We have the list of subjects to receive the communication
    * */
    private List<Subject> subjects;

    /**
    * This is the timer of the game
    * */
    private Timer timer;

    /**
    * This is the total point of the game
    * */
    private int totalPoint;

    /**
    * This is the total point of the current level
    * */
    private int currentPoint;

    /**
    * This is the timer of the previous second
    * */
    private long previousSeconds;
    /**
    * This is the status of the game -> determines whether the game is running
    * */
    private GameState gameState;

    /**
     * The lives of the stickman. Which is 3 by default
     */
    private int stickmanLives = 3;

    /**
    * Care taker of the game's memory
    * */
    private GameMementoCaretaker gameMementoCaretaker;


    /**
     * Creates a GameManager object.
     * @param levels The config file containing the names of all the levels
     */
    public GameManager(String levels) {
        /*
        * This leads us to the levels.json file, where all config files are stored there
        * */
        this.levelFileNames = this.readConfigFile(levels);
        this.levelIndex = 0;
        this.level = LevelBuilderImpl.generateFromFile(levelFileNames.get(this.levelIndex), this);
        if (this.level != null) {
            this.level.getStickman().attach(this);
        }

        this.subjects = new ArrayList<>();
        // We use casting to add the level to the subject list
        this.subjects.add((Subject) this.level);
        // Now we attach this class to this new level to update
        this.subjects.get(this.levelIndex).attach(this);
        timer = new Timer();
        this.previousSeconds = timer.convertTimeCountToSeconds();
        this.gameState = new GameRun();
        this.gameMementoCaretaker = new GameMementoCaretakerImpl();
    }

    @Override
    public Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public boolean jump() {
        return this.level.jump();
    }

    @Override
    public boolean moveLeft() {
        return this.level.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return this.level.moveRight();
    }

    @Override
    public boolean stopMoving() {
        return this.level.stopMoving();
    }

    @Override
    public void tick() {

        if (this.gameState instanceof GameStop) {
            return;
        }

        // Update the points of the game
        updatePoint();

        // Update the previous seconds
        this.previousSeconds = this.timer.convertTimeCountToSeconds();

        this.level.tick();
    }

    @Override
    public void shoot() {
        this.level.shoot();
    }

    @Override
    public void reset() {
        // Avoid the level from getting reset when the game as stopped
        if (gameState instanceof GameStop) {
            return;
        }
        // Get the original observers
        List<Observer> originalObservers = new ArrayList<>();
        if (this.level instanceof Subject) {
            originalObservers = ((Subject) this.level).getObservers();
        }
        this.currentPoint = 0;
        // Reset the level as usual
        this.level = LevelBuilderImpl.generateFromFile(this.level.getSource(), this);


        // When reset the game, we also need to reattach the level to the game
        if (this.level != null) {
            if (this.level instanceof Subject) {
                Subject levelSubject = (Subject) this.level;
                for (Observer observer : originalObservers) {
                    levelSubject.attach(observer);
                }
            }
            this.level.getStickman().attach(this);
        }


    }
    /**
     * Loads the next level. We don't want the other classes to access this method because it can be
     * troublesome if the game is running and the client just wanna skip to the next level!
     * @return whether the operation is successful .
     */
    private boolean loadNextLevel() {
        resetCurrentPoint();
        // So we have into the level to make sure the level has stopped running
        boolean status = false;
        if (this.levelIndex < this.levelFileNames.size()-1) {
            // This level has been loaded and good to go
            this.levelIndex ++;
            this.level = LevelBuilderImpl.generateFromFile(this.levelFileNames.get(this.levelIndex), this);
            if (level instanceof Subject) {
                System.out.println("Subject Attached");
                Subject newLevel = (Subject) this.level;
                this.subjects.add(newLevel);
                // Now we attach this class to this new level to update
                newLevel.attach(this);
            }
            status = true;
        }

        return status;
    }

    /**
     * Retrieves the list of level filenames from a config file
     * @param config The config file
     * @return The list of level names
     */
    @SuppressWarnings("unchecked")
    private List<String> readConfigFile(String config) {

        List<String> res = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {
            /*
            * Using Json to read the config file
            * */
            Reader reader = new FileReader(config);
            /*
            * Create an object of the Json file
            * */
            JSONObject object = (JSONObject) parser.parse(reader);
            /*
            * Get all items from the levelFiles object
            * */
            JSONArray levelFiles = (JSONArray) object.get("levelFiles");

            /*
            * Loads the stickman's lives
            * */
            long sampleStickmanLives = (long) object.get("gameLives");

            /*
            * Check if the lives is valid
            * */
            if (sampleStickmanLives > 0) {
                this.stickmanLives = (int) sampleStickmanLives;
            }

            Iterator<String> iterator = (Iterator<String>) levelFiles.iterator();

            // Get level file names
            while (iterator.hasNext()) {
                String file = iterator.next();
                res.add("levels/" + file);
            }

        } catch (IOException e) {
            System.exit(10);
            return null;
        } catch (ParseException e) {
            return null;
        }

        return res;
    }

    /**
    * Once the level has been completed, it will notify this GameManager to update itself to the next level (if the level has been stopped)
    * */
    @Override
    public void update() {
        // Once the observer has been notified, this function will check whether the
        // level has been completed.
        // Now, we must check for the level updates (whether that it has stopped running)
        updateLevel();

        // Now, we update the lives of the stickman
        updateLives();
    }
    /**
    * This method aids in loading the next level when the conditions are met (eg: The level is stopped)
    * */
    private void updateLevel() {
        if (this.level != null && !this.level.getLevelStatus()) {
            /*
             * Now, because there are 2 main situations when the level stops running
             *   1. The hero wins
             *   2. The hero dies (Not implemented yet)
             * */
            // To make sure that the game has won(or lost),
            // We are going to look into the list of entities to see if we can get the
            // Win (or lose) object
            for (Entity entity : this.level.getEntities()) {
                // Check if this entity is the win object
                if (entity instanceof Win) {
                    // IF it is correct, then we break the loop and
                    if (loadNextLevel()) {
                        System.out.printf("Level has been updated : %s\n", this.levelFileNames.get(this.levelIndex));
                    } else {
                        // If we cannot load the next level
                        this.gameState = this.gameState.stop();
                    }
                    break;
                } else if (entity instanceof Lose) {
                    // If the level can not run anymore
                    this.gameState = this.gameState.stop();
                    break;
                }
            }
            // Once loaded with the next level, we shall reset the timer
            this.timer.setStartingTimeToNow();
        }
    }

    /**
    * This method gets the update of the hero and then deducts lives. if lives =0, the game will be stopped
    * */
    private void updateLives() {
        if (!this.level.getStickman().isActive()) {
            if (this.stickmanLives > 0 && this.gameState instanceof GameRun) {
                this.stickmanLives --;
                if (this.stickmanLives == 0) {
                    this.gameState = this.gameState.stop();
                    // Make the level lose
                    this.level.lose();
                }
            }
        }
    }

    @Override
    public Timer getTimer () {
        return this.timer;
    }

    @Override
    public void updatePoint() {
        // Check if a second has passed
        if (this.timer.convertTimeCountToSeconds() - this.previousSeconds >= 1 && this.gameState instanceof GameRun) {
            // first, we need to get the level limit point
            // then we will compare the current seconds with the target point
            int targetPoint = this.level.getTargetPoint();
            if (this.timer.convertTimeCountToSeconds() <= targetPoint) {
                this.currentPoint ++;
            } else if (this.currentPoint + this.level.getCurrentPoint() >= 1) {
                // Point will not set updated if currentpoint - 1 = negative value
                this.currentPoint --;
            }
        }
    }

    @Override
    public int getCurrentLevelPoint() {
        // The current point should be the combination of this object and the level, who is the PointReceiver
        return this.currentPoint + this.level.getCurrentPoint();
    }

    @Override
    public void resetCurrentPoint() {
        // Upon calling, we
        // are going to add point of the
        // current level to the total point of all levels
        this.totalPoint += this.currentPoint + this.level.getCurrentPoint();
        // Reset the current point to 0
        this.currentPoint = 0;
    }

    @Override
    public int getCurrentTotalPoint() {
        return this.totalPoint;
    }

    @Override
    public GameState getGameState() {
        return this.gameState;
    }

    @Override
    public int getStickmanLives() {
        return this.stickmanLives;
    }

    @Override
    public GameMemento saveMemento() {
        // Creates new memento
        GameMemento gameMemento = new GameMementoImpl(level, gameState, this.timer.convertTimeCountToSeconds(),
                this.stickmanLives, this.currentPoint, this.totalPoint, this.levelIndex);
        this.gameMementoCaretaker.overwriteMemento(gameMemento);
        return gameMemento;
    }

    @Override
    public void loadMemento() {
        if (this.gameMementoCaretaker.getMostRecentMemento() != null) {
//            reset();
            GameMemento gameMemento = this.gameMementoCaretaker.getMostRecentMemento();
            this.level = gameMemento.getLevel();
            this.gameState = gameMemento.getGameState();
            // Sets the timer to the second
            this.timer.rollTimeToSeconds(gameMemento.getTimerSeconds());
            this.stickmanLives = gameMemento.getLives();
            this.currentPoint = gameMemento.getCurrentPoint();
            this.totalPoint = gameMemento.getTotalPoint();
            this.levelIndex = gameMemento.getLevelIndex();
            if (this.level instanceof Subject) {
                ((Subject) this.level).notifyObservers();
            }
        } else {
            System.out.println("You need to save to Load the game");
        }
    }
}