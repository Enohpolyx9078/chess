package server;

import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

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
        // internal error 500 -> message "Error: $error"
    }

    private void notFound(@NotNull Context context) {
        //TODO
    }

    private void exceptionHandler(Exception e, @NotNull Context context) {
        //TODO
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
