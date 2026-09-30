package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameRequest;
import model.GameResult;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private static final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public GameResult createGame(GameRequest request) {
        int gameId = games.size() + 1; //TODO make this a unique ID later
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
