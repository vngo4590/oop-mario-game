package gamesaver;

public class GameMementoCaretakerImpl implements GameMementoCaretaker{
    private GameMemento gameMemento;

    public GameMementoCaretakerImpl() {}

    @Override
    public GameMemento getMostRecentMemento() {
        return gameMemento.copy();
    }

    @Override
    public void overwriteMemento(GameMemento gameMemento) {
        this.gameMemento = gameMemento.copy();
    }
}
