package stickman.model;

import gamesaver.Prototype;

public class GameRun implements GameState {
    @Override
    public GameState run() {
        System.out.println("Game is already running!");
        return this;
    }

    @Override
    public GameState stop() {
        System.out.println("Game is stopping");
        return new GameStop();
    }

    @Override
    public GameRun copy() {
        return new GameRun();
    }
}
