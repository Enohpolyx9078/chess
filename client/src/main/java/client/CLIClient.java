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
    private static final String SERVER_URL = "http://localhost:8080";

    private static final String SYSTEM_STYLE = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_GREEN;
    private static final String COMMAND_STYLE = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static final String ERROR_STYLE = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;

    private static final String SIGNED_OUT_HELP = """
            %s - create a new account to play chess
            %s - sign in to chess with an existing account
            %s - close this application
            %s - displays this menu""".formatted(
                    EscapeSequences.beautify(COMMAND_STYLE,"register"),
                    EscapeSequences.beautify(COMMAND_STYLE,"login"),
                    EscapeSequences.beautify(COMMAND_STYLE,"quit"),
                    EscapeSequences.beautify(COMMAND_STYLE,"help"));

    private static final String SIGNED_IN_HELP = """
            %s - creates a new game
            %s - lists all games
            %s - join a game as a player
            %s - join a game as an observer
            %s - sign out of your account
            %s - displays this menu""".formatted(
                    EscapeSequences.beautify(COMMAND_STYLE,"create"),
                    EscapeSequences.beautify(COMMAND_STYLE,"list"),
                    EscapeSequences.beautify(COMMAND_STYLE,"play"),
                    EscapeSequences.beautify(COMMAND_STYLE,"observe"),
                    EscapeSequences.beautify(COMMAND_STYLE,"logout"),
                    EscapeSequences.beautify(COMMAND_STYLE,"help"));

    private static final String PLAYING_HELP = """
            %s - redraws the board
            %s - leave the game
            %s - make a move
            %s - give up (you lose the game)
            %s - show available moves for a given piece
            %s - displays this menu""".formatted(
                    EscapeSequences.beautify(COMMAND_STYLE, "redraw"),
                    EscapeSequences.beautify(COMMAND_STYLE, "leave"),
                    EscapeSequences.beautify(COMMAND_STYLE, "move"),
                    EscapeSequences.beautify(COMMAND_STYLE, "resign"),
                    EscapeSequences.beautify(COMMAND_STYLE, "highlight"),
                    EscapeSequences.beautify(COMMAND_STYLE, "help"));

    private static final String OBSERVING_HELP = """
            %s - redraws the board
            %s - leave the game
            %s - show available moves for a given piece
            %s - displays this menu""".formatted(
            EscapeSequences.beautify(COMMAND_STYLE, "redraw"),
            EscapeSequences.beautify(COMMAND_STYLE, "leave"),
            EscapeSequences.beautify(COMMAND_STYLE, "highlight"),
            EscapeSequences.beautify(COMMAND_STYLE, "help"));

    private final ServerFacade facade = new ServerFacade(SERVER_URL, this);
    private final Gson serializer = ServerMessage.createMessageSerializer();
    private final GameManager manager = new GameManager();

    private String authToken;
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
                    handlePreLogin(input);
                } else if (state == Status.PLAYING) {
                    handlePlaying(input);
                } else if (state == Status.OBSERVING) {
                    handleObserving(input);
                } else {
                    handlePostLogin(input);
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
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
            case NOTIFICATION -> loadNotification((NotificationMessage) message);
            case ERROR -> loadError((ErrorMessage) message);
        }
        if (state == Status.PLAYING) {
            handlePlaying("redraw");
        }
        showPrompt((EscapeSequences.beautify(
                EscapeSequences.SET_TEXT_BOLD,
                "[%s] >>> "))
                .formatted(state.name()));
    }

    private void loadError(ErrorMessage message) {
        manager.setLatestMessage(EscapeSequences.beautify(ERROR_STYLE, message.getMsg()));
    }

    private void loadNotification(NotificationMessage message) {
        manager.setLatestMessage(message.getMsg());
    }

    private void loadGame(LoadGameMessage message) {
        manager.setGame(message.getGame());
    }

    private enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        PLAYING,
        OBSERVING,
        QUIT
    }
}
