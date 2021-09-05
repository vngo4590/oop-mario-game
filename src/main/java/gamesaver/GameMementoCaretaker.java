package gamesaver;

public interface GameMementoCaretaker {
    public GameMemento getMostRecentMemento();
        public void overwriteMemento(GameMemento gameMemento);
}
