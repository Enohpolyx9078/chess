package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.*;

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
        return GAMES.get(key);
    }

    @Override
    public void addPlayer(Integer gameID, String username, ChessGame.TeamColor playerColor) {
        GameData game = getGame(gameID);
        if (game != null) {
            GameData joined = (Objects.equals(playerColor, ChessGame.TeamColor.WHITE)) ?
                    new GameData(game, username, game.blackUsername()) :
                    new GameData(game, game.whiteUsername(), username);
            GAMES.put(gameID, joined);
        }
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
