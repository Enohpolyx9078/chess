package client;

import chess.ChessBoard;
import chess.ChessGame;
import facade.NetworkException;
import model.SingleGameResponse;
import ui.BoardPrinter;
import ui.EscapeSequences;

import java.util.Collection;
import java.util.InputMismatchException;

public class ActionHandler {


    public void handlePreLogin(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "quit" -> doQuit();
            case "login" -> doLogin();
            case "register" -> doRegister();
            default -> doUnknown(input);
        }
    }

    public void handlePostLogin(String input) {
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

    public void handlePlaying(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "redraw" -> drawScreen();
            case "leave" -> doLeave();
            default -> doUnknown(input);
        }
    }

    public void handleObserving(String input) {
        switch (input.toLowerCase().trim()) {
            case "help" -> doHelp();
            case "redraw" -> drawScreen();
            case "leave" -> doLeave();
            default -> doUnknown(input);
        }
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
        state = CLIClient.Status.QUIT;
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
            state = CLIClient.Status.SIGNED_IN;
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
            state = CLIClient.Status.SIGNED_IN;
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
            state = CLIClient.Status.PLAYING;
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
            state = CLIClient.Status.OBSERVING;
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
            state = CLIClient.Status.SIGNED_OUT;
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
        state = CLIClient.Status.SIGNED_IN;
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
