package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import model.GameRequest;
import model.GameResult;
import model.ListGamesResponse;

import java.util.List;

public class GameService {
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public GameService() {}

    public ListGamesResponse listGames() {
        List<GameData> games = GAME_DAO.listGames();
        //TODO return a ListGamesResponse
        return null;
    }

    public GameResult createGame(GameRequest request) {
        return GAME_DAO.createGame(request);
    }
}
