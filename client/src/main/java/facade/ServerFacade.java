package facade;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public String register(String username, String password, String email) throws NetworkException {
        try {
            var body = Map.of("username", username, "password", password, "email", email);
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url, NetworkSender.Methods.POST, new String[]{}, new Gson().toJson(body));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                //TODO success
                System.out.println(resp.body());
                //TODO return the auth token
            } else {
                //TODO fail
                System.out.println("Error: received status code " + resp.statusCode());
            }
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
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
