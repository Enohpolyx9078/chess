package service;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.NotFoundException;
import model.GameRequest;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTests {
    ClearService clearService = new ClearService();
    GameService gameService = new GameService();
    UserService userService = new UserService();
    GameDAO gameDAO = new MemoryGameDAO();

    @Test
    public void normalJoinTest() throws AlreadyTakenException, NotFoundException {
        clearService.clearApplication();
        UserData user = new UserData("username", "password", "email");
        String authOne = userService.register(user).authToken();
        Integer gameId = gameService.createGame(
                new GameRequest("First")
        ).gameID();

        assertNull(gameDAO.getGame(gameId).whiteUsername());

        JoinGameRequest reqOne = new JoinGameRequest(gameId, ChessGame.TeamColor.WHITE);
        gameService.joinGame(reqOne, authOne);

        assertEquals(user.username(), gameDAO.getGame(gameId).whiteUsername());
    }

    @Test
    public void gameDoesNotExistTest() {
        clearService.clearApplication();
        assertThrows(NotFoundException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(0, ChessGame.TeamColor.WHITE), "ABC123"));
    }

    @Test
    public void colorIsTakenTest() throws AlreadyTakenException, NotFoundException {
        clearService.clearApplication();
        String authOne = userService.register(
                new UserData("username", "password", "email")
        ).authToken();
        String authTwo = userService.register(
                new UserData("second", "password", "email")
        ).authToken();

        Integer gameId = gameService.createGame(
                new GameRequest("First")
        ).gameID();

        JoinGameRequest reqOne = new JoinGameRequest(gameId, ChessGame.TeamColor.WHITE);
        JoinGameRequest reqTwo = new JoinGameRequest(gameId, ChessGame.TeamColor.WHITE);

        gameService.joinGame(reqOne, authOne);
        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(reqTwo, authTwo));
    }
}
