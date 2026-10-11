package dataaccess;

import chess.ChessGame;
import dataaccess.sql.SQLGameDAO;
import model.GameData;
import model.GameRequest;
import model.GameResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static private final GameDAO gameDAO = new SQLGameDAO();

    @Test
    public void clearAuthTest() {
        assertDoesNotThrow(gameDAO::clear);
        assertEquals(0, gameDAO.getSize());
    }

    @Test
    public void getSizeTest() throws DataAccessException {
        gameDAO.clear();
        assertEquals(0, gameDAO.getSize());
        gameDAO.createGame(new GameRequest("game"));
        gameDAO.createGame(new GameRequest("game1"));
        assertEquals(2, gameDAO.getSize());
    }

    @Test
    public void createAndGetTest() throws DataAccessException {
        gameDAO.clear();
        GameRequest req = new GameRequest("Name");
        GameResult res = gameDAO.createGame(req);
        GameData expected = new GameData(
                res.gameID(),
                null,
                null,
                req.gameName(),
                new ChessGame());
        assertEquals(expected, gameDAO.getGame(res.gameID()));
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        gameDAO.clear();
        gameDAO.createGame(new GameRequest("Name"));
        gameDAO.createGame(new GameRequest("Name1"));
        gameDAO.createGame(new GameRequest("Name2"));
        assertEquals(3, gameDAO.listGames().size());
    }

    @Test
    public void addPlayerTest() throws DataAccessException {
        gameDAO.clear();
        String white = "awesome474";
        String black = "waxymarlin";
        GameRequest req = new GameRequest("Name");
        GameResult res = gameDAO.createGame(req);
        gameDAO.addPlayer(res.gameID(), white, ChessGame.TeamColor.WHITE);
        gameDAO.addPlayer(res.gameID(), black, ChessGame.TeamColor.BLACK);
        GameData expected = new GameData(
                res.gameID(),
                white,
                black,
                req.gameName(),
                new ChessGame());
        assertEquals(expected, gameDAO.getGame(res.gameID()));
    }
}
