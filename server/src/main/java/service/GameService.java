package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.GameRequest;
import model.GameResult;

public class GameService {
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public GameService() {}

    public GameResult createGame(GameRequest request) {
        return GAME_DAO.createGame(request);
    }
}
