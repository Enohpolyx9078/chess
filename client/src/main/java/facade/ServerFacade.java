package facade;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public class ServerFacade {
    private String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public String register(String username, String password, String email) throws NetworkException {
        //TODO throw on fail
        //TODO return the auth token
        return "";
    }

    public String login(String username, String password) throws NetworkException {
        //TODO throw on fail
        //TODO return the auth token
        return "";
    }

    public void create(String authToken, String gameName) throws NetworkException {
        //TODO throw on fail
    }

    public Collection<GameData> listGames(String authToken) throws NetworkException {
        //TODO throw on fail
        return null;
    }

    public GameData play(String authToken, int gameID, ChessGame.TeamColor color) throws NetworkException {
        //TODO throw on fail
        return new GameData(-1, null, null, "TODO", new ChessGame());
    }

    public void observe(String authToken, int gameID) throws NetworkException {
        //TODO throw on fail
    }

    public void logout(String authToken) throws NetworkException {
        //TODO throw on fail
    }
}
