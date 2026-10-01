package service;

import dataaccess.AlreadyTakenException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.NotFoundException;
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

    public void joinGame(JoinGameRequest request) throws NotFoundException, AlreadyTakenException {
        //TODO verify the specified game exists
        //TODO verify the requested color is not taken
        //TODO add the user as the requested color
        GameData game = GAME_DAO.getGame(request.gameID());
    }
}
