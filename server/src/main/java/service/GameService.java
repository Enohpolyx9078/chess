package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.*;

public class GameService {
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public GameService() {}

    public ListGamesResponse listGames() {
        return new ListGamesResponse(GAME_DAO.listGames());
    }

    public GameResult createGame(GameRequest request) {
        return GAME_DAO.createGame(request);
    }
}
