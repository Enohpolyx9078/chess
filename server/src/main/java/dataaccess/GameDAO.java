package dataaccess;

import model.GameData;
import model.GameRequest;
import model.GameResult;

import java.util.List;

public interface GameDAO {
    GameResult createGame(GameRequest request);
    List<GameData> listGames();
    void clear();
    int getSize();
}
