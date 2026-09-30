package server;

import com.google.gson.JsonSyntaxException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerHandler)
                .exception(Exception.class, this::exceptionHandler)
                .error(404, this::notFound);



    }

    private void registerHandler(@NotNull Context context) {
        //TODO register a new user
        // success 200 -> username, authToken
        // bad request 400 -> message "Error: bad request"
        // already taken 403 -> message "Error: already taken"
        try {
            UserData registerRequest = getBodyObject(context, UserData.class);
        } catch (JsonSyntaxException e) {
            context.status(400);
            context.json("{\"message\":\"Error: bad request\"}");
        }
    }

    private void notFound(@NotNull Context context) {
        //TODO
    }

    private void exceptionHandler(Exception e, @NotNull Context context) {
        //TODO internal error 500 -> message "Error: $error"
    }

    // method from the MasteryLS docs
    private static <T> T getBodyObject(Context context, Class<T> clazz) {
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
