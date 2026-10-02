package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Objects;

public class GameService {
    private static final GameDAO GAME_DAO = new MemoryGameDAO();
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();

    public GameService() {}

    public ListGamesResponse listGames() {
        return new ListGamesResponse(GAME_DAO.listGames());
    }

    public GameResult createGame(GameRequest request) {
        return GAME_DAO.createGame(request);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws NotFoundException, AlreadyTakenException {
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
