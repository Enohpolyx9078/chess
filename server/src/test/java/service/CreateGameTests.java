package service;

import dataaccess.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import model.GameRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTests {
    ClearService clearService = new ClearService();
    GameService service = new GameService();
    GameDAO gameDAO = new MemoryGameDAO();

    @Test
    public void normalCreateTest() {
        clearService.clearApplication();
        assertEquals(0, gameDAO.getSize());
        service.createGame(new GameRequest("First Game"));
        assertEquals(1, gameDAO.getSize());
    }

    @Test
    public void noDuplicateIdsTest() {
        clearService.clearApplication();
        int first = service.createGame(new GameRequest("First Game")).gameID();
        int second = service.createGame(new GameRequest("First Game")).gameID();
        assertNotEquals(first, second);
    }
}
