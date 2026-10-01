package dataaccess;

import model.GameData;
import model.GameRequest;
import model.GameResult;
import model.SingleGameResponse;

import java.util.List;

public interface GameDAO {
    GameResult createGame(GameRequest request);
    List<SingleGameResponse> listGames();
    GameData getGame(Integer integer);
    void clear();
    int getSize();
}
