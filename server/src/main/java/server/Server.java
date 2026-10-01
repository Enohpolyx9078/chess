package server;

import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import service.*;

public class Server {
    private final Javalin javalin;

    private static final UserService userService = new UserService();
    private static final ClearService clearService = new ClearService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerHandler)
                .post("/session", this::loginHandler)
                .delete("/db", this::clearHandler)
                .exception(Exception.class, this::exceptionHandler)
                .error(404, this::notFound);
    }

    /**
     * Logs in the user and responds with an auth token
     * <ul>
     *     <li>[200] <code>{"username":,"authToken":}</code></li>
     *     <li>[400] <code>{"message": "Error: bad request"}</code></li>
     *     <li>[401] <code>{"message": "Error: unauthorized"}</code></li>
     * </ul>
     *
     */
    private void loginHandler(@NotNull Context context) {
        try {
            LoginRequest loginRequest = getBodyObject(context, LoginRequest.class);
            LoginResult result = userService.login(loginRequest);
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
        clearService.clearApplication();
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
            LoginResult result = userService.register(registerRequest);
            setResponse(context, 200, new Gson().toJson(result));
        } catch (JsonSyntaxException _) {
            setResponse(context, 400, "{\"message\":\"Error: bad request\"}");
        } catch (AlreadyTakenException _) {
            setResponse(context, 403, "{\"message\":\"Error: already taken\"}");
        }
    }

    private void notFound(@NotNull Context context) {
        //TODO
    }

    private void exceptionHandler(Exception e, @NotNull Context context) {
        //TODO internal error 500 -> message "Error: $error"
    }

    private static void setResponse(Context context, Integer code, String json) {
        context.status(code);
        context.json(json);
    }

    // method from the MasteryLS docs
    private static <T> T getBodyObject(Context context, Class<T> clazz) throws JsonSyntaxException {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new JsonSyntaxException("missing required body");
        }

        return bodyObject;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
