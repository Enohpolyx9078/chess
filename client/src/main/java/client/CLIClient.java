package client;

import com.google.gson.Gson;
import jakarta.websocket.DeploymentException;
import ui.DisplayUtilities;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class CLIClient implements Observer {
    private static final String SERVER_URL = "http://localhost:8080";

    private final Gson serializer = ServerMessage.createMessageSerializer();
    private final ActionHandler actionHandler = new ActionHandler(SERVER_URL, this);
    private final DisplayUtilities du = new DisplayUtilities();

    private DisplayUtilities.Status state = DisplayUtilities.Status.SIGNED_OUT;
    private static Scanner scanner;

    public CLIClient() throws DeploymentException, IOException, URISyntaxException {
    }

    public void run() {
        try {
            scanner = new Scanner(System.in);
            du.showMessage(
                    EscapeSequences.beautify(
                            du.systemStyle,
                            "♕ 240 Chess Client ♕"));
            du.showMessage(EscapeSequences.beautify(
                    EscapeSequences.SET_TEXT_FAINT,
                    "Type Help to get started."));
            while (!state.equals(DisplayUtilities.Status.QUIT)) {
                du.showPrompt((EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD,
                                "[%s] >>> "))
                        .formatted(state.name()));
                String input = scanner.nextLine();

                DisplayUtilities.Status changed;
                changed = switch(state) {
                    case SIGNED_IN -> actionHandler.handlePostLogin(input);
                    case PLAYING -> actionHandler.handlePlaying(input);
                    case OBSERVING -> actionHandler.handleObserving(input);
                    default -> actionHandler.handlePreLogin(input);
                };
                if (changed != null) {
                    state = changed;
                }
            }
            du.showMessage(EscapeSequences.beautify(
                    du.systemStyle,
                    "Thanks for playing!"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scanner.close();
        }
    }

    public void notify(String msg) {
        ServerMessage message = serializer.fromJson(msg, ServerMessage.class);
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> actionHandler.loadGame((LoadGameMessage) message);
            case NOTIFICATION -> actionHandler.loadNotification((NotificationMessage) message);
            case ERROR -> actionHandler.loadError((ErrorMessage) message);
        }
        if (state == DisplayUtilities.Status.PLAYING) {
            actionHandler.handlePlaying("redraw");
        } else if (state == DisplayUtilities.Status.OBSERVING) {
            actionHandler.handleObserving("redraw");
        }
        du.showPrompt((EscapeSequences.beautify(
                EscapeSequences.SET_TEXT_BOLD,
                "[%s] >>> "))
                .formatted(state.name()));
    }
}
