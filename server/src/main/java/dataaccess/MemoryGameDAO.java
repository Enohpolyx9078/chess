package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameRequest;
import model.GameResult;
import model.SingleGameResponse;

import java.util.ArrayList;
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
    public List<SingleGameResponse> listGames() {
        List<SingleGameResponse> gamesList = new ArrayList<>();
        GAMES.forEach((Integer _, GameData game) -> gamesList.add(new SingleGameResponse(game)));
        return gamesList;
    }

    @Override
    public GameData getGame(Integer key) {
        //TODO get the came provided by key
        return null;
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
