package server;

import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import model.*;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import service.*;

import java.util.Map;

public class Server {
    private final Javalin javalin;

    private static final UserService USER_SERVICE = new UserService();
    private static final ClearService CLEAR_SERVICE = new ClearService();
    private static final GameService GAME_SERVICE = new GameService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerHandler)
                .post("/session", this::loginHandler)
                .delete("/session", this::logoutHandler)
                .post("/game", this::createGameHandler)
                .delete("/db", this::clearHandler)
                .exception(Exception.class, this::exceptionHandler)
                .error(404, this::notFound);
        javalin.before("/game", context -> {
            if (!AuthUtility.isAuthorized(context.header("authorization"))) {
                throw new UnauthorizedException("Missing or invalid token");
            }
        });
        javalin.before("/session", context -> {
            if (context.method() == HandlerType.DELETE &&
                    !AuthUtility.isAuthorized(context.header("authorization"))) {
                throw new UnauthorizedException("Missing or invalid token");
            }
        });
    }

    /**
     * Creates a new game with the given name
     * <ul>
     *     <li>[200] <code>{"gameID":,}</code></li>
     *     <li>[400] <code>{"message": "Error: bad request"}</code></li>
     *     <li>[401] <code>{"message": "Error: unauthorized"}</code></li>
     * </ul>
     */
    private void createGameHandler(@NotNull Context context) {
        try {
            GameRequest request = getBodyObject(context, GameRequest.class);
            GameResult result = GAME_SERVICE.createGame(request);
            setResponse(context, 200, new Gson().toJson(result));
        } catch (JsonSyntaxException _) {
            setResponse(context, 400, "{\"message\":\"Error: bad request\"}");
        }
    }

    /**
     * Attempts to log out the user with the given auth token
     * <ul>
     *     <li>[200] <code>{}</code></li>
     *     <li>[401] <code>{"message": "Error: unauthorized"}</code></li>
     * </ul>
     */
    private void logoutHandler(@NotNull Context context) {
            USER_SERVICE.logout(context.header("authorization"));
            setResponse(context, 200, "{}");
    }

    /**
     * Logs in the user and responds with an auth token
     * <ul>
     *     <li>[200] <code>{"username":,"authToken":}</code></li>
     *     <li>[400] <code>{"message": "Error: bad request"}</code></li>
     *     <li>[401] <code>{"message": "Error: unauthorized"}</code></li>
     * </ul>
     */
    private void loginHandler(@NotNull Context context) {
        try {
            LoginRequest loginRequest = getBodyObject(context, LoginRequest.class);
            LoginResult result = USER_SERVICE.login(loginRequest);
            setResponse(context, 200, new Gson().toJson(result));
        } catch (JsonSyntaxException _) {
            setResponse(context, 400, "{\"message\":\"Error: bad request\"}");
        } catch (UnauthorizedException _) {
            setResponse(context, 401, "{\"message\":\"Error: unauthorized\"}");
        }
    }

    /**
     * Clears all data from the application
     * <p>
     *     <b>For testing only</b>
     * </p>
     * <ul>
     *     <li>[200] <code>{}</code></li>
     * </ul>
     */
    private void clearHandler(@NotNull Context context) {
        CLEAR_SERVICE.clearApplication();
        setResponse(context, 200, "{}");
    }

    /**
     * Registers a new user
     * <ul>
     *     <li>[200] <code>{"username":,"authToken":}</code></li>
     *     <li>[400] <code>{"message": "Error: bad request"}</code></li>
     *     <li>[403] <code>{"message": "Error: already taken"}</code></li>
     * </ul>
     */
    private void registerHandler(@NotNull Context context) {
        try {
            UserData registerRequest = getBodyObject(context, UserData.class);
            LoginResult result = USER_SERVICE.register(registerRequest);
            setResponse(context, 200, new Gson().toJson(result));
        } catch (JsonSyntaxException _) {
            setResponse(context, 400, "{\"message\":\"Error: bad request\"}");
        } catch (AlreadyTakenException _) {
            setResponse(context, 403, "{\"message\":\"Error: already taken\"}");
        }
    }

    private void notFound(@NotNull Context context) {
        setResponse(context, 404, String.format("{\"message\":\"Error: %s not found\"}", context.path()));
    }

    private void exceptionHandler(Exception e, @NotNull Context context) {
        if (e instanceof UnauthorizedException) {
            setResponse(context, 401, "{\"message\":\"Error: unauthorized\"}");
        } else {
        setResponse(context,
                500,
                new Gson().toJson(
                        Map.of("message", String.format("Error: %s", e.getMessage()))
                ));
        }
    }

    private static void setResponse(Context context, Integer code, String json) {
        context.status(code);
        context.json(json);
    }

    // method from the MasteryLS docs
    private static <T> T getBodyObject(Context context, Class<T> clazz) throws JsonSyntaxException {
        try {
            var bodyObject = new Gson().fromJson(context.body(), clazz);

            if (bodyObject == null) {
                throw new JsonSyntaxException("missing required body");
            }

            return bodyObject;
        } catch (RuntimeException _) {
            throw new JsonSyntaxException("missing required body");
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
