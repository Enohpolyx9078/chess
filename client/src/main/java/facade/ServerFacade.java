package facade;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final String URL;

    public ServerFacade(String url) {
        this.URL = url;
    }

    public String register(String username, String password, String email) throws NetworkException {
        try {
            var body = Map.of("username", username, "password", password, "email", email);
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    URL + "/user", NetworkSender.Methods.POST, null, new Gson().toJson(body));
            var respBody = new Gson().fromJson(resp.body(), Map.class);
            if (isError(resp)) {
                throw new NetworkException(resp.statusCode() + " " + respBody.get("message"));
            }
            return respBody.get("authToken").toString();
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    public String login(String username, String password) throws NetworkException {
        try {
            var body = Map.of("username", username, "password", password);
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    URL + "/session", NetworkSender.Methods.POST, null, new Gson().toJson(body));
            var respBody = new Gson().fromJson(resp.body(), Map.class);
            if (isError(resp)) {
                throw new NetworkException(resp.statusCode() + " " + respBody.get("message"));
            }
            return respBody.get("authToken").toString();
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    public void create(String authToken, String gameName) throws NetworkException {
        //TODO throw on fail (network drop, error, or no gameID in resp)
        //TODO post to game
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

    private static boolean isError(HttpResponse<String> resp) {
        return (resp.statusCode() < 200 || resp.statusCode() >= 300);
    }
}
