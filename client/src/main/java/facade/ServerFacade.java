package facade;

import chess.ChessGame;
import com.google.gson.Gson;
import model.ListGamesResponse;
import model.SingleGameResponse;
import ui.EscapeSequences;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String url;
    private List<SingleGameResponse> currentGames;

    public ServerFacade(String url) {
        this.url = url;
    }

    public String register(String username, String password, String email) throws NetworkException {
        try {
            var body = Map.of("username", username, "password", password, "email", email);
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url + "/user",
                    NetworkSender.Methods.POST,
                    null,
                    new Gson().toJson(body));
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
                    url + "/session",
                    NetworkSender.Methods.POST,
                    null,
                    new Gson().toJson(body));
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
        try {
            var body = Map.of("gameName", gameName);
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url + "/game",
                    NetworkSender.Methods.POST,
                    new String[]{"authorization", authToken},
                    new Gson().toJson(body));
            var respBody = new Gson().fromJson(resp.body(), Map.class);
            if (isError(resp)) {
                throw new NetworkException(resp.statusCode() + " " + respBody.get("message"));
            }
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    public Collection<SingleGameResponse> listGames(String authToken) throws NetworkException {
        try {
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url + "/game",
                    NetworkSender.Methods.GET,
                    new String[]{"authorization", authToken},
                    null);
            if (isError(resp)) {
                var respMap = new Gson().fromJson(resp.body(), Map.class);
                throw new NetworkException(resp.statusCode() + " " + respMap.get("message"));
            }
            ListGamesResponse list = new Gson().fromJson(resp.body(), ListGamesResponse.class);
            currentGames = list.games();
            return list.games();
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    public void play(String authToken, int gameCode, ChessGame.TeamColor color) throws NetworkException {
        try {
            if (currentGames == null || currentGames.isEmpty()) {
                throw new NetworkException("Games have not been loaded. Load all games with %s".formatted(
                        EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE,
                                "list")));
            }
            var body = Map.of("playerColor", color.name(), "gameID", getGameId(gameCode));
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url + "/game",
                    NetworkSender.Methods.PUT,
                    new String[]{"authorization", authToken},
                    new Gson().toJson(body));
            var respBody = new Gson().fromJson(resp.body(), Map.class);
            if (isError(resp)) {
                throw new NetworkException(resp.statusCode() + " " + respBody.get("message"));
            }
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    public void observe(String authToken, int gameCode) throws NetworkException {
        try {
            if (currentGames == null || currentGames.isEmpty()) {
                throw new NetworkException("Games have not been loaded. Load all games with %s".formatted(
                        EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE,
                                "list")));
            }
            var body = Map.of("gameID", getGameId(gameCode), "playerColor", "OBSERVE");
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url + "/game",
                    NetworkSender.Methods.PUT,
                    new String[]{"authorization", authToken},
                    new Gson().toJson(body));
            var respBody = new Gson().fromJson(resp.body(), Map.class);
            if (isError(resp)) {
                throw new NetworkException(resp.statusCode() + " " + respBody.get("message"));
            }
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    public void logout(String authToken) throws NetworkException {
        try {
            HttpResponse<String> resp = NetworkSender.sendRequest(
                    url + "/session",
                    NetworkSender.Methods.DELETE,
                    new String[]{"authorization", authToken},
                    null);
            if (isError(resp)) {
                var respMap = new Gson().fromJson(resp.body(), Map.class);
                throw new NetworkException(resp.statusCode() + " " + respMap.get("message"));
            }
        } catch (IOException | InterruptedException _) {
            throw new NetworkException("Request incomplete. Please check your internet connection.");
        }
    }

    private int getGameId(int gameCode) {
        if (gameCode > currentGames.size() || gameCode <= 0) {
            return -1;
        }
        return currentGames.get(gameCode - 1).gameID();
    }

    private static boolean isError(HttpResponse<String> resp) {
        return (resp.statusCode() < 200 || resp.statusCode() >= 300);
    }
}
