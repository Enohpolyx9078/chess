package service;

import model.GameRequest;
import model.ListGamesResponse;
import model.SingleGameResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesTests {
    ClearService clearService = new ClearService();
    GameService service = new GameService();

    @Test
    public void listTwoGamesTest() {
        clearService.clearApplication();
        GameRequest first = new GameRequest("First");
        GameRequest second = new GameRequest("Second");
        service.createGame(first);
        service.createGame(second);
        List<SingleGameResponse> expGames = new ArrayList<>();
        expGames.add(new SingleGameResponse(1, null, null, "First"));
        expGames.add(new SingleGameResponse(2, null, null, "Second"));
        ListGamesResponse result = new ListGamesResponse(expGames);
        assertEquals(result, service.listGames());
    }
}
