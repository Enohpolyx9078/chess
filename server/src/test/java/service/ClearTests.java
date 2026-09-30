package service;

import dataaccess.*;
import model.GameRequest;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTests {
    @Test
    public void clearAuthTest() {
        AuthDAO authAccess = new MemoryAuthDAO();
        authAccess.clear();
        UserData register = new UserData("enohpolyx", "123ABC", "a@example.com");
        authAccess.createAuth(register);
        assertEquals(1, authAccess.getSize());
        authAccess.clear();
        assertEquals(0, authAccess.getSize());
    }

    @Test
    public void clearUsersTest() throws AlreadyTakenException {
        UserData register = new UserData("MandyCandy", "123ABC", "A@example.com");
        UserDAO userAccess = new MemoryUserDAO();
        UserService service = new UserService();
        service.register(register);
        assertEquals(1, userAccess.getSize());
        userAccess.clear();
        assertEquals(0, userAccess.getSize());
    }

    @Test
    public void clearGameTest() {
        GameRequest game = new GameRequest("First Game");
        GameDAO gameAccess = new MemoryGameDAO();
        GameService service = new GameService();
        service.createGame(game);
        assertEquals(1, gameAccess.getSize());
        gameAccess.clear();
        assertEquals(0, gameAccess.getSize());
    }

    @Test
    public void clearAllTest() throws AlreadyTakenException {
        ClearService clearService = new ClearService();
        UserService userService = new UserService();
        GameService gameService = new GameService();
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        UserData register = new UserData("MandyCandy", "123ABC", "A@example.com");
        userService.register(register);
        gameService.createGame(new GameRequest("New Game"));
        assertEquals(1, userDAO.getSize());
        assertEquals(1, authDAO.getSize());
        assertEquals(1, gameDAO.getSize());
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        assertEquals(0, userDAO.getSize());
        assertEquals(0, authDAO.getSize());
        assertEquals(0, gameDAO.getSize());
    }
}
