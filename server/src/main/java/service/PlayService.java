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

            ctx.send("WebSocket response: " + ctx.message());
        });
        ws.onClose(ctx -> System.out.println("WebSocket closed"));
    }
}
