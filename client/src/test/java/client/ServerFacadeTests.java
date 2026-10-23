package client;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import facade.NetworkException;
import facade.ServerFacade;
import model.GameData;
import model.SingleGameResponse;
import org.junit.jupiter.api.*;
import server.Server;
import service.ClearService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {
    private static final ClearService CLEAR_SERVICE = new ClearService();
    private static final UserDAO USER_DAO = new SQLUserDAO();
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();
    private static final GameDAO GAME_DAO = new SQLGameDAO();
    private static ServerFacade facade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        CLEAR_SERVICE.clearApplication();
        facade = new ServerFacade("http://localhost:8080");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void normalRegisterTest() throws DataAccessException {
        String token = facade.register("user", "pass", "email");
        assertNotNull(token);
        assertEquals(1, USER_DAO.getSize());
        assertNotNull(AUTH_DAO.getAuth(token));
    }

    @Test
    public void duplicateUserNameTest() {
        facade.register("user", "pass", "email");
        assertThrows(NetworkException.class,
                () -> facade.register("user", "pass", "email"));
    }

    @Test
    public void normalLoginTest() throws DataAccessException {
        facade.register("user", "pass", "email");
        String token = facade.login("user", "pass");
        assertNotNull(token);
        assertNotNull(AUTH_DAO.getAuth(token));
    }

    @Test
    public void badLoginTest() {
        facade.register("user", "pass", "email");
        assertThrows(NetworkException.class,
                () -> facade.login("user", "wrong"));
    }

    @Test
    public void normalCreateTest() throws DataAccessException {
        String authToken = facade.register("user", "pass", "email");
        facade.create(authToken, "Super Cool Name");
        assertEquals(1, GAME_DAO.getSize());
    }

    @Test
    public void listGamesTest() {
        String authToken = facade.register("user", "pass", "email");
        for (var i = 0; i < 3; ++i) {
            facade.create(authToken, "Super Cool Name");
        }
        Collection<SingleGameResponse> list = facade.listGames(authToken);
        assertEquals(3, list.size());
    }

    @Test
    public void joinGameTest() throws DataAccessException {
        String authToken = facade.register("user", "pass", "email");
        facade.create(authToken, "Super Cool Name");
        facade.listGames(authToken);
        facade.play(authToken, 1, ChessGame.TeamColor.WHITE);
        GameData game = GAME_DAO.getGame(1);
        assertEquals("user", game.whiteUsername());
    }

    @Test
    public void colorTakenTest() {
        String authToken = facade.register("user", "pass", "email");
        facade.create(authToken, "Super Cool Name");
        facade.listGames(authToken);
        facade.play(authToken, 1, ChessGame.TeamColor.WHITE);
        assertThrows(NetworkException.class,
                () -> facade.play(authToken, 1, ChessGame.TeamColor.WHITE));
    }

    @Test
    public void badIDTest() {
        String authToken = facade.register("user", "pass", "email");
        facade.create(authToken, "Super Cool Name");
        facade.listGames(authToken);
        assertThrows(NetworkException.class,
                () -> facade.play(authToken, 255, ChessGame.TeamColor.WHITE));
    }

    @Test
    public void observeTest() {
        String authToken = facade.register("user", "pass", "email");
        facade.create(authToken, "Super Cool Name");
        facade.listGames(authToken);
        assertDoesNotThrow(() -> facade.observe(authToken, 1));
    }

    @Test
    public void badObserveTest() {
        String authToken = facade.register("user", "pass", "email");
        facade.create(authToken, "Super Cool Name");
        facade.listGames(authToken);
        assertThrows(NetworkException.class,
                () -> facade.observe(authToken, 255));
    }

    @Test
    public void logoutTest() throws DataAccessException {
        String authToken = facade.register("user", "pass", "email");
        facade.logout(authToken);
        assertNull(AUTH_DAO.getAuth(authToken));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void offlineTest() {
        server.stop();
        assertThrows(NetworkException.class,
                () -> facade.login("user", "wrong"));
        assertThrows(NetworkException.class,
                () -> facade.register("user", "pass", "email"));
        assertThrows(NetworkException.class,
                () -> facade.create("token", "name"));
        assertThrows(NetworkException.class,
                () -> facade.listGames("token"));
        assertThrows(NetworkException.class,
                () -> facade.play("token", 1, ChessGame.TeamColor.WHITE));
        assertThrows(NetworkException.class,
                () -> facade.observe("token", 1));
        assertThrows(NetworkException.class,
                () -> facade.logout("token"));
    }
}
