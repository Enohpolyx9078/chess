package facade;

import client.Observer;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsConnector extends Endpoint {
    private final Session session;

    public WsConnector(String url, Observer observer) throws DeploymentException, IOException, URISyntaxException {
        URI uri = new URI(url);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler((MessageHandler.Whole<String>) observer::showMessage);
    }

    public void sendCommand(UserGameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //TODO
    }
}
