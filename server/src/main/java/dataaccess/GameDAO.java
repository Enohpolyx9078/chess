package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.List;

public interface GameDAO {
    GameResult createGame(GameRequest request);
    List<SingleGameResponse> listGames();
    GameData getGame(Integer integer);
    void addPlayer(Integer gameID, String username, ChessGame.TeamColor playerColor);
    void clear();
    int getSize();
}
