package websocket.messages;

import chess.ChessGame;
import com.google.gson.*;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public static Gson createMessageSerializer() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class,
                (JsonDeserializer<ServerMessage>) (el, _, _) -> {
                    ServerMessage message = null;
                    if (el.isJsonObject()) {
                        JsonObject current = el.getAsJsonObject();
                        ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(
                                current.get("serverMessageType").getAsString());
                        message = switch (type) {
                            case LOAD_GAME -> {
                                new ChessGame();
                                yield ChessGame.createSerializer().fromJson(el, LoadGameMessage.class);
                            }
                            case ERROR -> new Gson().fromJson(el, ErrorMessage.class);
                            case NOTIFICATION -> new Gson().fromJson(el, NotificationMessage.class);
                        };
                    }
                    return message;
                });
        return builder.create();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
