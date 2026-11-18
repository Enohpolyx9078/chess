package service;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import dataaccess.*;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PlayService {
    private static final GameDAO GAME_DAO = new SQLGameDAO();
    private static final AuthDAO AUTH_DAO = new SQLAuthDAO();

    public void register(WsConfig ws) {
        ws.onConnect(WsContext::enableAutomaticPings);
        ws.onMessage(ctx -> {
            UserGameCommand command = COMMAND_SERIALIZER.fromJson(ctx.message(), UserGameCommand.class);

            // check the command
            if (!AuthUtility.isAuthorized(command.getAuthToken())) {
                ctx.send(new Gson().toJson(new ErrorMessage("Missing or invalid auth token")));
                return;
            }
            if (GAME_DAO.getGame(command.getGameID()) == null) {
                ctx.send(new Gson().toJson(new ErrorMessage("Game does not exist")));
                return;
            }
            //TODO handle events:
            // MAKE_MOVE -> Validate, respond with LOAD_GAME, and check game state
            // RESIGN
            try {
                switch (command.getCommandType()) {
                    case CONNECT -> doConnect(command, ctx);
                    case LEAVE -> doLeave(command, ctx);
                    default -> ctx.send(new Gson().toJson(new ErrorMessage("Unknown command")));
                }
            } catch (DataAccessException e) {
                ctx.send(new Gson().toJson(new ErrorMessage("Failed: " + e.getMessage())));
            }
        });
        ws.onClose(this::removeFromRoom);
    }

    private void doLeave(UserGameCommand command, WsMessageContext ctx) throws DataAccessException {
        ctx.closeSession();
        if (command.getGameID() != ctx.attribute("gameID")) {
            return;
        }
        String username = AUTH_DAO.getAuth(command.getAuthToken()).username();
        GameData game = GAME_DAO.getGame(command.getGameID());
        boolean isObserver =
                !Objects.equals(game.whiteUsername(), username) &&
                        !Objects.equals(game.blackUsername(), username);
        if (!isObserver) {
            GAME_DAO.removePlayer(command.getGameID(),
                    Objects.equals(username, game.whiteUsername()) ?
                            ChessGame.TeamColor.WHITE :
                            ChessGame.TeamColor.BLACK);
        }
        Collection<WsContext> room = removeFromRoom(ctx);
        if (room == null) {
            return;
        }
        notifyOthers(
                ctx,
                new NotificationMessage(username + " has left the game"),
                room);
    }

    private void doConnect(UserGameCommand command, WsMessageContext ctx) throws DataAccessException {
        GameData game = GAME_DAO.getGame(command.getGameID());
        Collection<WsContext> room = addToRoom(command.getGameID(), ctx);
        ctx.send(new Gson().toJson(new LoadGameMessage(game.game())));
        String playerRole = "observer";
        AuthData auth = AUTH_DAO.getAuth(command.getAuthToken());
        if (Objects.equals(auth.username(), game.whiteUsername())) {
            playerRole = "white";
        } else if (Objects.equals(auth.username(), game.blackUsername())) {
            playerRole = "black";
        }
        notifyOthers(
                ctx,
                new NotificationMessage(auth.username() + " joined as " + playerRole),
                room);
    }

    private Collection<WsContext> addToRoom(Integer gameID, WsMessageContext ctx) {
        ctx.attribute("gameID", gameID);
        Collection<WsContext> room = ROOMS.getOrDefault(gameID, new ArrayList<>());
        room.add(ctx);
        ROOMS.put(gameID, room);
        return room;
    }

    private Collection<WsContext> removeFromRoom(WsContext ctx) {
        Integer gameID = ctx.attribute("gameID");
        if (gameID == null) {
            return null;
        }
        Collection<WsContext> room = ROOMS.get(gameID);
        if (room == null) {
            return null;
        }
        room.remove(ctx);
        ROOMS.put(gameID, room);
        return room;
    }

    private void checkAndSend(WsContext player, ServerMessage msg) {
        if (player.session.isOpen()) {
            player.send(new Gson().toJson(msg));
        } else {
            removeFromRoom(player);
        }
    }

    private void notifyOthers(WsMessageContext ctx,ServerMessage msg, Collection<WsContext> room) {
        room.forEach((player) -> {
            if (player != ctx) {
                checkAndSend(player, msg);
            }
        });
    }

    private void notifyAll(ServerMessage msg, Collection<WsContext> room) {
        room.forEach((player) -> checkAndSend(player, msg));
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
                        command = (type == UserGameCommand.CommandType.MAKE_MOVE) ?
                                new Gson().fromJson(el, MakeMoveCommand.class) :
                                new Gson().fromJson(el, UserGameCommand.class);
                    }
                    return command;
                });
        return builder.create();
    }
}
