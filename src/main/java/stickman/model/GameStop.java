package stickman.model;

import gamesaver.Prototype;

public class GameStop implements GameState{

    @Override
    public GameState run() {
        System.out.println("Game is running now...");
        return new GameRun();
    }

    @Override
    public GameState stop() {
        System.out.println("Game has already stopped");
        return this;
    }

    @Override
    public GameStop copy() {
        return new GameStop();
    }
}
