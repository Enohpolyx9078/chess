package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.List;

public interface GameDAO {
    GameResult createGame(GameRequest request) throws DataAccessException;
    List<SingleGameResponse> listGames() throws DataAccessException;
    GameData getGame(Integer integer) throws DataAccessException;
    void addPlayer(Integer gameID, String username, ChessGame.TeamColor playerColor);
    void clear() throws DataAccessException;
    int getSize() throws DataAccessException;
}
