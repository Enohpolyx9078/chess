package dataaccess;

import model.GameRequest;
import model.GameResult;

public interface GameDAO {
    GameResult createGame(GameRequest request);
    void clear();
    int getSize();
}
