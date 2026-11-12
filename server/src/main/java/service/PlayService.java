package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import dataaccess.UnauthorizedException;
import io.javalin.websocket.WsConfig;
import websocket.commands.UserGameCommand;

public class PlayService {
    public void register(WsConfig ws) {
        ws.onConnect(ctx -> {
            ctx.enableAutomaticPings();
            System.out.println("WebSocket connected");
        });
        ws.onMessage(ctx -> {
            UserGameCommand command = COMMAND_SERIALIZER.fromJson(ctx.message(), UserGameCommand.class);

            // check the authToken
            if (!AuthUtility.isAuthorized(command.getAuthToken())) {
                throw new UnauthorizedException("Missing or invalid token");
            }
            //TODO handle events:
            // CONNECT -> Group user into game room, Respond with LOAD_GAME
            // MAKE_MOVE -> Validate, respond with LOAD_GAME, and check game state
            // LEAVE
            // RESIGN
            // send NOTIFICATION to relevant parties after all commands
            //TODO handle invalid commands

            ctx.send("WebSocket response: " + ctx.message());
        });
        ws.onClose(ctx -> System.out.println("WebSocket closed"));
    }

    private static final Gson COMMAND_SERIALIZER = createCommandSerializer();

    private static Gson createCommandSerializer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class,
                (JsonDeserializer<UserGameCommand>) (el, _, _) -> {
                    UserGameCommand command = null;
                    if (el.isJsonObject()) {
                        JsonObject current = el.getAsJsonObject();
                        commandType type = commandType.valueOf(current.get("commandType").getAsString());
                        command = switch (type) {
                            case CONNECT -> new Gson().fromJson(el, UserGameCommand.class);
                            case MAKE_MOVE -> null; //TODO handle MAKE_MOVE
                            case LEAVE -> null; //TODO handle LEAVE
                            case RESIGN -> null; //TODO handle RESIGN
                        };
                    }
                    return command;
                });
        return builder.create();
    }

    private enum commandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }
}
