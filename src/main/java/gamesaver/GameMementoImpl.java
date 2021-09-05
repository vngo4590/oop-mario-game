package gamesaver;

import stickman.level.Level;
import stickman.model.GameState;

public class GameMementoImpl implements GameMemento{
    private long seconds;
    private int lives;
    private Level level;
    private int currentPoint;
    private int totalPoint;
    private GameState gameState;
    private int levelIndex;

    public GameMementoImpl(Level level, GameState gameState, long seconds, int lives, int currentPoint, int totalPoint,
                           int levelIndex) {
        this.currentPoint = currentPoint;
        this.lives = lives;
        this.seconds = seconds;
        this.totalPoint = totalPoint;
        this.gameState = gameState.copy();
        this.level= level.copy();
        this.levelIndex = levelIndex;

    }

    @Override
    public void setTimerSeconds(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public long getTimerSeconds() {
        return this.seconds;
    }

    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }

    @Override
    public int getLives() {
        return this.lives;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level.copy();
    }

    @Override
    public Level getLevel() {
        return this.level.copy();
    }

    @Override
    public void setCurrentPoint(int currentPoint) {
        this.currentPoint = currentPoint;
    }

    @Override
    public int getCurrentPoint() {
        return this.currentPoint;
    }

    @Override
    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    @Override
    public int getTotalPoint() {
        return this.totalPoint;
    }

    @Override
    public void setGameState(GameState gameState) {
        this.gameState = gameState.copy();
    }

    @Override
    public GameState getGameState() {
        return this.gameState.copy();
    }

    @Override
    public GameMementoImpl copy() {
        return new GameMementoImpl(level.copy(), gameState.copy(), seconds, lives, currentPoint, totalPoint, levelIndex);
    }

    @Override
    public int getLevelIndex() {
        return this.levelIndex;
    }
}
