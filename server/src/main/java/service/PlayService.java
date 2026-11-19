package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
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
            try {
                switch (command.getCommandType()) {
                    case CONNECT -> doConnect(command, ctx);
                    case LEAVE -> doLeave(command, ctx);
                    case MAKE_MOVE -> doMakeMove(command, ctx);
                    case RESIGN -> doResign(command, ctx);
                    default -> ctx.send(new Gson().toJson(new ErrorMessage("Unknown command")));
                }
            } catch (DataAccessException e) {
                ctx.send(new Gson().toJson(new ErrorMessage("Failed: " + e.getMessage())));
            }
        });
        ws.onClose(this::removeFromRoom);
    }

    private void doResign(UserGameCommand command, WsMessageContext ctx) {
        //TODO handle resigning
    }

    private void doMakeMove(UserGameCommand command, WsMessageContext ctx) throws DataAccessException {
        // Validate the move
        Integer roomID = ctx.attribute("gameID");
        if (roomID == null) {
            ctx.send(new Gson().toJson(new ErrorMessage("Game does not exist")));
            return;
        }
        Collection<WsContext> room = ROOMS.get(roomID);
        if (room == null) {
            ctx.send(new Gson().toJson(new ErrorMessage("Game does not exist")));
            return;
        }
        GameData game = GAME_DAO.getGame(command.getGameID());
        ChessGame chessGame = game.game();
        if (chessGame.isGameOver()) {
            ctx.send(new Gson().toJson(new ErrorMessage("Game has ended")));
            return;
        }
        //TODO error when game has been resigned

        String username = AUTH_DAO.getAuth(command.getAuthToken()).username();
        ChessGame updated = attemptMove((MakeMoveCommand) command, game, username);
        if (updated == null) {
            ctx.send(new Gson().toJson(new ErrorMessage("Invalid move")));
            return;
        }

        GAME_DAO.updateGame(command.getGameID(), updated);

        notifyAll(new LoadGameMessage(updated), room);
        notifyOthers(
                ctx,
                new NotificationMessage(parseChessMove(((MakeMoveCommand) command).getMove(), username)),
                room);
        if (updated.hasLost(ChessGame.TeamColor.WHITE)) {
            notifyAll(new NotificationMessage(game.blackUsername() + " wins!"), room);
        } else if (updated.hasLost(ChessGame.TeamColor.BLACK)) {
            notifyAll(new NotificationMessage(game.whiteUsername() + " wins!"), room);
        }
    }

    private String parseChessMove(ChessMove move, String username) {
        char[] coords = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        return "%s moved %c%d to %c%d".formatted(
                username,
                coords[move.getStartPosition().getColumn() - 1],
                move.getStartPosition().getRow(),
                coords[move.getEndPosition().getColumn() - 1],
                move.getEndPosition().getRow());
    }

    private ChessGame attemptMove(MakeMoveCommand command, GameData gameData, String username) {
        String playerRole = getPlayerRole(gameData, username);
        if (Objects.equals(playerRole, "observer")) {
            return null;
        }
        ChessGame game = gameData.game();
        ChessMove move = command.getMove();
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        if (piece == null) {
            return null;
        }
        ChessGame.TeamColor playerTeam = ChessGame.TeamColor.valueOf(playerRole.toUpperCase());
        if (piece.getTeamColor() != playerTeam) {
            return null;
        }
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            return null;
        }
        return game;
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
        AuthData auth = AUTH_DAO.getAuth(command.getAuthToken());
        String playerRole = getPlayerRole(game, auth.username());
        notifyOthers(
                ctx,
                new NotificationMessage(auth.username() + " joined as " + playerRole),
                room);
    }

    private String getPlayerRole(GameData game, String username) {
        String playerRole = "observer";
        if (Objects.equals(username, game.whiteUsername())) {
            playerRole = "white";
        } else if (Objects.equals(username, game.blackUsername())) {
            playerRole = "black";
        }
        return playerRole;
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
