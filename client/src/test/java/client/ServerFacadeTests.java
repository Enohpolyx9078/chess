package client;

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
import org.junit.jupiter.api.*;
import server.Server;
import service.ClearService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static final ClearService CLEAR_SERVICE = new ClearService();
    private static final UserDAO USER_DAO = new SQLUserDAO();
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();
    private static final GameDAO GAME_DAO = new SQLGameDAO();
    private static final ServerFacade FACADE = new ServerFacade("http://localhost:8080");
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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void normalRegisterTest() throws DataAccessException {
        String token = FACADE.register("user", "pass", "email");
        assertNotNull(token);
        assertEquals(1, USER_DAO.getSize());
        assertNotNull(AUTH_DAO.getAuth(token));
    }

    @Test
    public void duplicateUserNameTest() {
        FACADE.register("user", "pass", "email");
        assertThrows(NetworkException.class,
                () -> FACADE.register("user", "pass", "email"));
    }

    @Test
    public void normalLoginTest() throws DataAccessException {
        FACADE.register("user", "pass", "email");
        String token = FACADE.login("user", "pass");
        assertNotNull(token);
        assertNotNull(AUTH_DAO.getAuth(token));
    }

    @Test
    public void badLoginTest() {
        FACADE.register("user", "pass", "email");
        assertThrows(NetworkException.class,
                () -> FACADE.login("user", "wrong"));
    }

    @Test
    public void normalCreateTest() throws DataAccessException {
        String authToken = FACADE.register("user", "pass", "email");
        FACADE.create(authToken, "Super Cool Name");
        assertEquals(1, GAME_DAO.getSize());
    }

    @Test
    public void listGamesTest() {
        String authToken = FACADE.register("user", "pass", "email");
        for (var i = 0; i < 3; ++i) {
            FACADE.create(authToken, "Super Cool Name");
        }
        Collection<GameData> list = FACADE.listGames(authToken);
        assertEquals(3, list.size());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void offlineTest() {
        server.stop();
        assertThrows(NetworkException.class,
                () -> FACADE.login("user", "wrong"));
        assertThrows(NetworkException.class,
                () -> FACADE.register("user", "pass", "email"));
        assertThrows(NetworkException.class,
                () -> FACADE.create("token", "name"));
        assertThrows(NetworkException.class,
                () -> FACADE.listGames("token"));
        //TODO assert play throws
        //TODO assert logout throws
    }
}
