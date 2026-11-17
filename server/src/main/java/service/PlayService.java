package service;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.NotFoundException;
import dataaccess.UnauthorizedException;
import dataaccess.sql.SQLGameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PlayService {
    private static final GameDAO GAME_DAO = new SQLGameDAO();

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
            switch (command.getCommandType()) {
                case CONNECT -> doConnect(command, ctx);
            }
        });
        ws.onClose(ctx -> System.out.println("WebSocket closed"));
    }

    private void doConnect(UserGameCommand command, WsMessageContext ctx) throws DataAccessException {
        // Group user into game room, Respond with LOAD_GAME
        GameData game = GAME_DAO.getGame(command.getGameID());
        if (game == null) {
            throw new NotFoundException("Game does not exist");
        }
        Collection<WsContext> room = ROOMS.getOrDefault(command.getGameID(), new ArrayList<>());
        room.add(ctx);
        ROOMS.put(command.getGameID(), room);
        ctx.send(new LoadGameMessage(game.game()));
    }

    private final ConcurrentHashMap<Integer, Collection<WsContext>> ROOMS = new ConcurrentHashMap<>();

    private static final Gson COMMAND_SERIALIZER = createCommandSerializer();

    private static Gson createCommandSerializer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class,
                (JsonDeserializer<UserGameCommand>) (el, _, _) -> {
                    UserGameCommand command = null;
                    if (el.isJsonObject()) {
                        JsonObject current = el.getAsJsonObject();
                        UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(current.get("commandType").getAsString());
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
}
