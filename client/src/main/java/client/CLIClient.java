package client;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import facade.NetworkException;
import facade.ServerFacade;
import jakarta.websocket.DeploymentException;
import model.SingleGameResponse;
import ui.BoardPrinter;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CLIClient implements Observer {
    private static final String SYSTEM_STYLE = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String SERVER_URL = "http://localhost:8080";

    private final Gson serializer = ServerMessage.createMessageSerializer();
    private final ActionHandler actionHandler = new ActionHandler(SERVER_URL, this);

    private Status state = Status.SIGNED_OUT;
    private static Scanner scanner;

    public CLIClient() throws DeploymentException, IOException, URISyntaxException {
    }

    public void run() {
        try {
            scanner = new Scanner(System.in);
            showMessage(
                    EscapeSequences.beautify(
                            SYSTEM_STYLE,
                            "♕ 240 Chess Client ♕"));
            showMessage(EscapeSequences.beautify(
                    EscapeSequences.SET_TEXT_FAINT,
                    "Type Help to get started."));
            while (!state.equals(Status.QUIT)) {
                showPrompt((EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD,
                                "[%s] >>> "))
                        .formatted(state.name()));
                String input = scanner.nextLine();
                if (state == Status.SIGNED_OUT) {
                    actionHandler.handlePreLogin(input);
                } else if (state == Status.PLAYING) {
                    actionHandler.handlePlaying(input);
                } else if (state == Status.OBSERVING) {
                    actionHandler.handleObserving(input);
                } else {
                    actionHandler.handlePostLogin(input);
                }
            }
            showMessage(EscapeSequences.beautify(
                    SYSTEM_STYLE,
                    "Thanks for playing!"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scanner.close();
        }
    }

    public void notify(String msg) {
        System.out.print("[NOTIFICATION] >>> " + msg);
        ServerMessage message = serializer.fromJson(msg, ServerMessage.class);
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> actionHandler.loadGame((LoadGameMessage) message);
            case NOTIFICATION -> actionHandler.loadNotification((NotificationMessage) message);
            case ERROR -> actionHandler.loadError((ErrorMessage) message);
        }
        if (state == CLIClient.Status.PLAYING) {
            handlePlaying("redraw");
        }
        showPrompt((EscapeSequences.beautify(
                EscapeSequences.SET_TEXT_BOLD,
                "[%s] >>> "))
                .formatted(state.name()));
    }

    public enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        PLAYING,
        OBSERVING,
        QUIT
    }
}
