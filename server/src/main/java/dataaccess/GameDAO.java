package dataaccess;

import model.GameRequest;
import model.GameResult;
import model.SingleGameResponse;

import java.util.List;

public interface GameDAO {
    GameResult createGame(GameRequest request);
    List<SingleGameResponse> listGames();
    void clear();
    int getSize();
}
