package dataaccess;

import chess.ChessGame;
import dataaccess.sql.SQLGameDAO;
import model.GameData;
import model.GameRequest;
import model.GameResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static private final GameDAO GAME_DAO = new SQLGameDAO();

    @Test
    public void clearGamesTest() throws DataAccessException {
        assertDoesNotThrow(GAME_DAO::clear);
        assertEquals(0, GAME_DAO.getSize());
    }

    @Test
    public void getSizeTest() throws DataAccessException {
        GAME_DAO.clear();
        assertEquals(0, GAME_DAO.getSize());
        GAME_DAO.createGame(new GameRequest("game"));
        GAME_DAO.createGame(new GameRequest("game1"));
        assertEquals(2, GAME_DAO.getSize());
    }

    @Test
    public void createAndGetTest() throws DataAccessException {
        GAME_DAO.clear();
        GameRequest req = new GameRequest("Name");
        GameResult res = GAME_DAO.createGame(req);
        GameData expected = new GameData(
                res.gameID(),
                null,
                null,
                req.gameName(),
                new ChessGame());
        assertEquals(expected, GAME_DAO.getGame(res.gameID()));
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        GAME_DAO.clear();
        GAME_DAO.createGame(new GameRequest("Name"));
        GAME_DAO.createGame(new GameRequest("Name1"));
        GAME_DAO.createGame(new GameRequest("Name2"));
        assertEquals(3, GAME_DAO.listGames().size());
    }

    @Test
    public void addPlayerTest() throws DataAccessException {
        GAME_DAO.clear();
        String white = "awesome474";
        String black = "waxymarlin";
        GameRequest req = new GameRequest("Name");
        GameResult res = GAME_DAO.createGame(req);
        GAME_DAO.addPlayer(res.gameID(), white, ChessGame.TeamColor.WHITE);
        GAME_DAO.addPlayer(res.gameID(), black, ChessGame.TeamColor.BLACK);
        GameData expected = new GameData(
                res.gameID(),
                white,
                black,
                req.gameName(),
                new ChessGame());
        assertEquals(expected, GAME_DAO.getGame(res.gameID()));
    }
}
