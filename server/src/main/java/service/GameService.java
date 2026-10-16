package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import model.*;

import java.util.Objects;

public class GameService {
    private static final GameDAO GAME_DAO = new SQLGameDAO();
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();

    public GameService() {}

    public ListGamesResponse listGames() throws DataAccessException {
        return new ListGamesResponse(GAME_DAO.listGames());
    }

    public GameResult createGame(GameRequest request) throws DataAccessException {
        return GAME_DAO.createGame(request);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        GameData game = GAME_DAO.getGame(request.gameID());
        if (game == null) {
            throw new NotFoundException("Game not found");
        }
        if (((Objects.equals(request.playerColor(), ChessGame.TeamColor.WHITE)) ?
                game.whiteUsername() :
                game.blackUsername()) != null) {
            throw new AlreadyTakenException("Requested color is already taken");
        }
        String username = AUTH_DAO.getAuth(authToken).username();
        GAME_DAO.addPlayer(game.gameID(), username, request.playerColor());
    }
}
