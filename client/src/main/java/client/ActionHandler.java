package client;

import chess.ChessBoard;
import chess.ChessGame;
import facade.NetworkException;
import facade.ServerFacade;
import jakarta.websocket.DeploymentException;
import model.SingleGameResponse;
import ui.BoardPrinter;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ActionHandler {
    private static final String COMMAND_STYLE = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE;
    private static final String ERROR_STYLE = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;
    private static final Scanner SCANNER = new Scanner(System.in);

    private final GameManager manager = new GameManager();

    private final ServerFacade facade;
    private String authToken;


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

    public ActionHandler(String url, Observer observer) throws DeploymentException, IOException, URISyntaxException {
        this.facade = new ServerFacade(url, observer);
    }

    public CLIClient.Status handlePreLogin(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(CLIClient.Status.SIGNED_OUT);
            case "quit" -> doQuit();
            case "login" -> doLogin();
            case "register" -> doRegister();
            default -> doUnknown(input);
        };
    }

    public CLIClient.Status handlePostLogin(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(CLIClient.Status.SIGNED_IN);
            case "create" -> doCreate();
            case "list" -> doList();
            case "play" -> doPlay();
            case "observe" -> doObserve();
            case "logout" -> doLogout();
            default -> doUnknown(input);
        };
    }

    public CLIClient.Status handlePlaying(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(CLIClient.Status.PLAYING);
            case "redraw" -> drawScreen();
            case "leave" -> doLeave();
            default -> doUnknown(input);
        };
    }

    public CLIClient.Status handleObserving(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(CLIClient.Status.OBSERVING);
            case "redraw" -> drawScreen();
            case "leave" -> doLeave();
            default -> doUnknown(input);
        };
    }

    public void loadError(ErrorMessage message) {
        manager.setLatestMessage(EscapeSequences.beautify(ERROR_STYLE, message.getMsg()));
    }

    public void loadNotification(NotificationMessage message) {
        manager.setLatestMessage(message.getMsg());
    }

    public void loadGame(LoadGameMessage message) {
        manager.setGame(message.getGame());
    }

    private CLIClient.Status doUnknown(String input) {
        showMessage((EscapeSequences.beautify(
                ERROR_STYLE,
                "'%s' is not a recognized command. Type %s for known commands."))
                .formatted(
                        input,
                        EscapeSequences.beautify(COMMAND_STYLE, "help")));
        return null;
    }

    private CLIClient.Status doHelp(CLIClient.Status state) {
        showMessage(switch (state) {
            case SIGNED_IN -> SIGNED_IN_HELP;
            case PLAYING -> PLAYING_HELP;
            case OBSERVING -> OBSERVING_HELP;
            default -> SIGNED_OUT_HELP;
        });
        return null;
    }

    private CLIClient.Status doQuit() {
        return CLIClient.Status.QUIT;
    }

    private CLIClient.Status doLogin() {
        // show a login prompt
        showPrompt("Username: ");
        String username = SCANNER.nextLine();
        showPrompt("Password: ");
        String password = SCANNER.nextLine();

        // interact with the Facade to login
        try {
            authToken = facade.login(username, password);
            return CLIClient.Status.SIGNED_IN;
        } catch (NetworkException e) {
            showNetworkError(e);
        }
        return null;
    }

    private CLIClient.Status doRegister() {
        // show a register prompt
        showPrompt("Username: ");
        String username = SCANNER.nextLine();
        showPrompt("Password: ");
        String password = SCANNER.nextLine();
        showPrompt("Email: ");
        String email = SCANNER.nextLine();

        // interact with Facade to register
        try {
            authToken = facade.register(username, password, email);
            return CLIClient.Status.SIGNED_IN;
        } catch (NetworkException e) {
            showNetworkError(e);
        }
        return null;
    }

    private CLIClient.Status doCreate() {
        // show a creation prompt
        showPrompt("Game Name: ");
        String gameName = SCANNER.nextLine();

        // interact with the Facade to create a game
        try {
            facade.create(authToken, gameName);
        } catch (NetworkException e) {
            showNetworkError(e);
        }
        return null;
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

    private CLIClient.Status doList() {
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
                return null;
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
        return null;
    }

    private CLIClient.Status doPlay() {
        String color = "none";
        try {
            // show a select and join game prompt
            showPrompt("Game ID: ");
            int gameId = SCANNER.nextInt();
            SCANNER.nextLine(); // flush the \n character
            if (gameId <= 0) {
                showMessage(EscapeSequences.beautify(
                        ERROR_STYLE,
                        "Game ID must be greater than 0."));
                return null;
            }

            showPrompt("Color: ");
            color = SCANNER.nextLine();
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color.toUpperCase());

            // interact with the Facade to join the game
            facade.play(authToken, gameId, teamColor);
            manager.setTeamColor(teamColor);
            manager.setGameCode(gameId);
            return CLIClient.Status.PLAYING;
        } catch (InputMismatchException _) {
            showMessage((EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid ID: '%s'")).formatted(SCANNER.nextLine()));
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
        return null;
    }

    private CLIClient.Status doObserve() {
        try {
            // show a select game prompt
            showPrompt("Game ID: ");
            int gameId = SCANNER.nextInt();
            SCANNER.nextLine(); // flush the \n character

            // interact with the Facade to watch the game
            facade.observe(authToken, gameId);
            manager.setGameCode(gameId);
            return CLIClient.Status.OBSERVING;
        } catch (InputMismatchException _) {
            showMessage((EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid ID: '%s'")).formatted(SCANNER.nextLine()));
            showMessage("Game ID must be a number.");
        } catch (NetworkException e) {
            showNetworkError(e);
        }
        return null;
    }

    private CLIClient.Status doLogout() {
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
        }
        return CLIClient.Status.SIGNED_OUT;
    }

    private CLIClient.Status drawScreen() {
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
        return null;
    }

    private CLIClient.Status doLeave() {
        facade.leave(authToken, manager.getGameCode());
        manager.reset();
        return CLIClient.Status.SIGNED_IN;
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
}
