package service;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.sql.SQLGameDAO;
import io.javalin.websocket.WsContext;
import model.GameData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.*;
import passoff.websocket.*;
import server.Server;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/*
* This class extends passoff.server.WebSocketTests.java
* with some modifications to test data persisting and pooling logic
* */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayTests extends WebSocketTests{
    private static final GameDAO GAME_DAO = new SQLGameDAO();

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() throws URISyntaxException {
        server = new Server();
        var port = Integer.toString(server.run(0));
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", port);
        serverFacade.clear();
        environment = new WebsocketTestingEnvironment("localhost", port, "/ws", passoff.server.TestFactory.getGsonBuilder());
        waitTime = passoff.server.TestFactory.getMessageTime();
    }

    @BeforeEach
    public void setup() {
        //populate database with HTTP calls
        serverFacade.clear();
        white = registerUser("white", "WHITE", "white@chess.com");
        black = registerUser("black", "BLACK", "black@chess.com");
        observer = registerUser("observer", "OBSERVER", "observer@chess.com");
        gameID = createGame(white, "testGame");
        joinGame(gameID, white, ChessGame.TeamColor.WHITE);
        joinGame(gameID, black, ChessGame.TeamColor.BLACK);
    }

    @AfterEach
    public void tearDown() {
        environment.disconnectAll();
    }

    @Test
    @Order(1)
    @DisplayName("Connect 1 User")
    public void connectSingleUser() {
        connectToGame(white, gameID, true, Set.of(), Set.of(), "white player connect");
        ConcurrentHashMap<Integer, Collection<WsContext>> rooms = server.getPlayService().getRooms();
        assertEquals(1, rooms.get(gameID).size());
    }

    @Test
    @Order(2)
    @DisplayName("Normal Connect")
    public void normalConnect() {
        connectGood();
        ConcurrentHashMap<Integer, Collection<WsContext>> rooms = server.getPlayService().getRooms();
        assertEquals(3, rooms.get(gameID).size());
    }

    @Test
    @Order(3)
    @DisplayName("Connect Bad GameID")
    public void connectBad() {
        connectBadGameID();
        ConcurrentHashMap<Integer, Collection<WsContext>> rooms = server.getPlayService().getRooms();
        assertTrue(rooms.get(gameID) == null || rooms.get(gameID).isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("Connect Bad AuthToken")
    public void connectUnauthorized() {
        connectBadAuthToken();
        ConcurrentHashMap<Integer, Collection<WsContext>> rooms = server.getPlayService().getRooms();
        assertTrue(rooms.get(gameID) == null || rooms.get(gameID).isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Normal Make Move")
    public void normalMove() throws InvalidMoveException, DataAccessException {
        validMove();
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(3, 5), null);
        ChessGame expected = new ChessGame();
        expected.makeMove(move);
        assertEquals(expected, GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(5)
    @DisplayName("Make Move Bad Authtoken")
    public void makeMoveUnauthorized() throws DataAccessException {
        makeMoveBadAuthtoken();
        assertEquals(new ChessGame(), GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(5)
    @DisplayName("Make Invalid Move")
    public void makeMoveIllegal() throws DataAccessException {
        invalidMoveBadMove();
        assertEquals(new ChessGame(), GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(5)
    @DisplayName("Make Move Wrong Turn")
    public void makeMoveOutOfTurn() throws DataAccessException {
        invalidMoveWrongTurn();
        assertEquals(new ChessGame(), GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(5)
    @DisplayName("Make Move for Opponent")
    public void makeMoveForOpponent() throws DataAccessException {
        invalidMoveOpponent();
        assertEquals(new ChessGame(), GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(5)
    @DisplayName("Make Move Observer")
    public void makeMoveObserver() throws DataAccessException {
        invalidMoveObserver();
        assertEquals(new ChessGame(), GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(5)
    @DisplayName("Make Move Game Over")
    public void makeMoveGameOver() throws InvalidMoveException, DataAccessException {
        invalidMoveGameOver();

        ChessGame expected = new ChessGame();
        ChessMove move = new ChessMove(new ChessPosition(2, 7), new ChessPosition(4, 7), null);
        expected.makeMove(move);
        move = new ChessMove(new ChessPosition(7, 5), new ChessPosition(6, 5), null);
        expected.makeMove(move);
        move = new ChessMove(new ChessPosition(2, 6), new ChessPosition(3, 6), null);
        expected.makeMove(move);
        move = new ChessMove(new ChessPosition(8, 4), new ChessPosition(4, 8), null);
        expected.makeMove(move);
        assertEquals(expected, GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(6)
    @DisplayName("Normal Resign")
    public void testResign() throws DataAccessException {
        validResign();
        ChessGame game = GAME_DAO.getGame(gameID).game();
        assertTrue(game.isGameOver());
    }

    @Test
    @Order(7)
    @DisplayName("Cannot Move After Resign")
    public void testMoveAfterResign() throws DataAccessException {
        moveAfterResign();
        assertEquals(new ChessGame(), GAME_DAO.getGame(gameID).game());
    }

    @Test
    @Order(7)
    @DisplayName("Observer Resign")
    public void testResignObserver() throws DataAccessException {
        invalidResignObserver();
        ChessGame game = GAME_DAO.getGame(gameID).game();
        assertFalse(game.isGameOver());
    }

    @Test
    @Order(8)
    @DisplayName("Leave Game")
    public void testLeave() throws DataAccessException {
        leaveGame();
        GameData gameData = GAME_DAO.getGame(gameID);
        assertNull(gameData.whiteUsername());
        assertNotNull(gameData.blackUsername());
    }

    @Test
    @Order(9)
    @DisplayName("Join After Leave Game")
    public void testJoinAfterLeave() throws DataAccessException {
        joinAfterLeaveGame();
        GameData gameData = GAME_DAO.getGame(gameID);
        assertEquals("white2", gameData.whiteUsername());
    }
}
