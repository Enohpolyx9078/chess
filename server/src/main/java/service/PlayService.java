package service;

import com.google.gson.Gson;
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
            // check the authToken
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            if (!AuthUtility.isAuthorized(command.getAuthToken())) {
                throw new UnauthorizedException("Missing or invalid token");
            }
            //TODO handle events:
            // CONNECT -> Group user into game room, Respond with LOAD_GAME
            // MAKE_MOVE -> Validate, respond with LOAD_GAME, and check game state
            // LEAVE
            // RESIGN
            // send NOTIFICATION to relevant parties afterward all commands

            ctx.send("WebSocket response: " + ctx.message());
        });
        ws.onClose(ctx -> System.out.println("WebSocket closed"));
    }
}
