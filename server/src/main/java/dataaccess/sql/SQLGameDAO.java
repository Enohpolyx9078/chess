package dataaccess.sql;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import model.GameRequest;
import model.GameResult;
import model.SingleGameResponse;

import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public GameResult createGame(GameRequest request) {
        return null;
    }

    @Override
    public List<SingleGameResponse> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(Integer integer) {
        return null;
    }

    @Override
    public void addPlayer(Integer gameID, String username, ChessGame.TeamColor playerColor) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }
}
