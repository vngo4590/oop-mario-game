package stickman.model;

import gamesaver.Prototype;

public interface GameState extends Prototype {
    GameState run();
    GameState stop();
    GameState copy();
}
