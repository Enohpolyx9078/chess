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
        handlePlaying("redraw");
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

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    private void showPrompt(String msg) {
        System.out.print(msg);
    }

    private void showNetworkError(NetworkException e) {
        showMessage(EscapeSequences.beautify(
                ERROR_STYLE,
                e.getMessage()));
    }

    private void doUnknown(String input) {
        showMessage((EscapeSequences.beautify(
                ERROR_STYLE,
                "'%s' is not a recognized command.")).formatted(input));
        doHelp();
    }

    private void doHelp() {
        showMessage(switch (state) {
            case SIGNED_IN -> SIGNED_IN_HELP;
            case PLAYING -> PLAYING_HELP;
            case OBSERVING -> OBSERVING_HELP;
            default -> SIGNED_OUT_HELP;
        });
    }

    private void doQuit() {
        state = Status.QUIT;
    }

    private void doLogin() {
        // show a login prompt
        showPrompt("Username: ");
        String username = scanner.nextLine();
        showPrompt("Password: ");
        String password = scanner.nextLine();

        // interact with the Facade to login
        try {
            authToken = facade.login(username, password);
            state = Status.SIGNED_IN;
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doRegister() {
        // show a register prompt
        showPrompt("Username: ");
        String username = scanner.nextLine();
        showPrompt("Password: ");
        String password = scanner.nextLine();
        showPrompt("Email: ");
        String email = scanner.nextLine();

        // interact with Facade to register
        try {
            authToken = facade.register(username, password, email);
            state = Status.SIGNED_IN;
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doCreate() {
        // show a creation prompt
        showPrompt("Game Name: ");
        String gameName = scanner.nextLine();

        // interact with the Facade to create a game
        try {
            facade.create(authToken, gameName);
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void printOneGame(SingleGameResponse game) {
        String whiteName = game.whiteUsername();
        String blackName = game.blackUsername();
        showMessage(EscapeSequences.beautify(
                EscapeSequences.SET_TEXT_BOLD,
                game.gameName()) +
                "\n    White: " +
                EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                        (whiteName != null) ? whiteName : "-") +
                "\n    Black: " +
                EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_COLOR_RED,
                        (blackName != null) ? blackName : "-"));
    }

    private void doList() {
        try {
            Collection<SingleGameResponse> currentGames = facade.listGames(authToken);
            if (currentGames.isEmpty()) {
                showMessage(("%s Start a new one with %s").formatted(
                        EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD,
                                "No games yet."
                        ),
                        EscapeSequences.beautify(
                                COMMAND_STYLE,
                                "create")));
                return;
            }
            int i = 1;
            for (SingleGameResponse game : currentGames) {
                showPrompt(EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_GREEN,
                        i + " - "));
                printOneGame(game);
                i++;
            }
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doPlay() {
        String color = "none";
        try {
            // show a select and join game prompt
            showPrompt("Game ID: ");
            int gameId = scanner.nextInt();
            scanner.nextLine(); // flush the \n character
            if (gameId <= 0) {
                showMessage(EscapeSequences.beautify(
                        ERROR_STYLE,
                        "Game ID must be greater than 0."));
                return;
            }

            showPrompt("Color: ");
            color = scanner.nextLine();
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color.toUpperCase());

            // interact with the Facade to join the game
            facade.play(authToken, gameId, teamColor);
            state = Status.PLAYING;
            manager.setTeamColor(teamColor);
            manager.setGameCode(gameId);
            handlePlaying("redraw");
        } catch (InputMismatchException _) {
            showMessage((EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid ID: '%s'")).formatted(scanner.nextLine()));
            showMessage("Game ID must be a number.");
        } catch (IllegalArgumentException _) {
            showMessage((EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid color: '%s'")).formatted(color));
            showMessage("Color must be either " +
                    EscapeSequences.beautify(EscapeSequences.SET_TEXT_BOLD,"'BLACK'") + " or " +
                    EscapeSequences.beautify(EscapeSequences.SET_TEXT_BOLD,"'WHITE'"));
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doObserve() {
        try {
            // show a select game prompt
            showPrompt("Game ID: ");
            int gameId = scanner.nextInt();
            scanner.nextLine(); // flush the \n character

            // interact with the Facade to watch the game
            facade.observe(authToken, gameId);
            manager.setGameCode(gameId);
            state = Status.OBSERVING;
        } catch (InputMismatchException _) {
            showMessage((EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid ID: '%s'")).formatted(scanner.nextLine()));
            showMessage("Game ID must be a number.");
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doLogout() {
        // interact with the Facade to sign out
        try {
            facade.logout(authToken);
        } catch (NetworkException e) {
            // display as a warning
            showPrompt("Warning: ");
            showNetworkError(e);
        } finally {
            // logout on the client regardless of network errors
            authToken = null;
            state = Status.SIGNED_OUT;
        }
    }

    private void handlePreLogin(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "quit" -> doQuit();
            case "login" -> doLogin();
            case "register" -> doRegister();
            default -> doUnknown(input);
        }
    }

    private void handlePostLogin(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "create" -> doCreate();
            case "list" -> doList();
            case "play" -> doPlay();
            case "observe" -> doObserve();
            case "logout" -> doLogout();
            default -> doUnknown(input);
        }
    }

    private void drawScreen() {
        showMessage(EscapeSequences.CLEAR_SCREEN);
        String message = manager.getLatestMessage();
        ChessGame game = manager.getGame();
        if (message != null) {
            showMessage(message);
        }
        if (game != null) {
            ChessBoard board = game.getBoard();
            if (manager.getTeamColor() == ChessGame.TeamColor.WHITE) {
                BoardPrinter.printBoardWhite(board);
            } else {
                BoardPrinter.printBoardBlack(board);
            }
        }
    }

    private void doLeave() {
        facade.leave(authToken, manager.getGameCode());
        manager.reset();
        state = Status.SIGNED_IN;
    }

    private void handlePlaying(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "redraw" -> drawScreen();
            case "leave" -> doLeave();
            default -> doUnknown(input);
        }
    }

    private void handleObserving(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "redraw" -> drawScreen();
            case "leave" -> doLeave();
            default -> doUnknown(input);
        }
    }

    private enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        PLAYING,
        OBSERVING,
        QUIT
    }
}
