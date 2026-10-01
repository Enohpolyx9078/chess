package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameRequest;
import model.GameResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private static final Map<Integer, GameData> GAMES = new HashMap<>();

    private int getNextId() {
        int id = 1;
        while (GAMES.containsKey(id)) {
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
        GAMES.put(gameId, data);
        return new GameResult(gameId);
    }

    @Override
    public List<GameData> listGames() {
        //TODO
        return List.of();
    }

    @Override
    public void clear() {
        GAMES.clear();
    }

    @Override
    public int getSize() {
        return GAMES.size();
    }
}
