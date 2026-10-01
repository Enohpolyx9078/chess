package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameRequest;
import model.GameResult;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private static final Map<Integer, GameData> games = new HashMap<>();

    private int getNextId() {
        int id = 1;
        while (games.containsKey(id)) {
            id++;
        }
        return id;
    }

    @Override
    public GameResult createGame(GameRequest request) {
        int gameId = getNextId();
        GameData data = new GameData(
                gameId,
                null,
                null,
                request.gameName(),
                new ChessGame());
        games.put(gameId, data);
        return new GameResult(gameId);
    }

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int getSize() {
        return games.size();
    }
}
