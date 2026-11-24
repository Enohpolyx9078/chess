package client;

import chess.*;
import facade.NetworkException;
import facade.ServerFacade;
import jakarta.websocket.DeploymentException;
import model.SingleGameResponse;
import ui.BoardPrinter;
import ui.DisplayUtilities;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class ActionHandler {
    private static final Scanner SCANNER = new Scanner(System.in);

    private final GameManager manager = new GameManager();
    private final DisplayUtilities du = new DisplayUtilities();

    private final ServerFacade facade;
    private String authToken;


    private final String signedOutHelp = """
            %s - create a new account to play chess
            %s - sign in to chess with an existing account
            %s - close this application
            %s - displays this menu""".formatted(
            EscapeSequences.beautify(du.commandStyle,"register"),
            EscapeSequences.beautify(du.commandStyle,"login"),
            EscapeSequences.beautify(du.commandStyle,"quit"),
            EscapeSequences.beautify(du.commandStyle,"help"));

    private final String signedInHelp = """
            %s - creates a new game
            %s - lists all games
            %s - join a game as a player
            %s - join a game as an observer
            %s - sign out of your account
            %s - displays this menu""".formatted(
            EscapeSequences.beautify(du.commandStyle,"create"),
            EscapeSequences.beautify(du.commandStyle,"list"),
            EscapeSequences.beautify(du.commandStyle,"play"),
            EscapeSequences.beautify(du.commandStyle,"observe"),
            EscapeSequences.beautify(du.commandStyle,"logout"),
            EscapeSequences.beautify(du.commandStyle,"help"));

    private final String playingHelp = """
            %s - redraws the board
            %s - leave the game
            %s - make a move
            %s - give up (you lose the game)
            %s - show available moves for a given piece
            %s - displays this menu""".formatted(
            EscapeSequences.beautify(du.commandStyle, "redraw"),
            EscapeSequences.beautify(du.commandStyle, "leave"),
            EscapeSequences.beautify(du.commandStyle, "move"),
            EscapeSequences.beautify(du.commandStyle, "resign"),
            EscapeSequences.beautify(du.commandStyle, "highlight"),
            EscapeSequences.beautify(du.commandStyle, "help"));

    private final String observingHelp = """
            %s - redraws the board
            %s - leave the game
            %s - show available moves for a given piece
            %s - displays this menu""".formatted(
            EscapeSequences.beautify(du.commandStyle, "redraw"),
            EscapeSequences.beautify(du.commandStyle, "leave"),
            EscapeSequences.beautify(du.commandStyle, "highlight"),
            EscapeSequences.beautify(du.commandStyle, "help"));

    public ActionHandler(String url, Observer observer) throws DeploymentException, IOException, URISyntaxException {
        this.facade = new ServerFacade(url, observer);
    }

    public DisplayUtilities.Status handlePreLogin(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(DisplayUtilities.Status.SIGNED_OUT);
            case "quit" -> doQuit();
            case "login" -> doLogin();
            case "register" -> doRegister();
            default -> doUnknown(input);
        };
    }

    public DisplayUtilities.Status handlePostLogin(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(DisplayUtilities.Status.SIGNED_IN);
            case "create" -> doCreate();
            case "list" -> doList();
            case "play" -> doPlay();
            case "observe" -> doObserve();
            case "logout" -> doLogout();
            default -> doUnknown(input);
        };
    }

    public DisplayUtilities.Status handlePlaying(String input) {
        drawScreen();
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(DisplayUtilities.Status.PLAYING);
            case "leave" -> doLeave();
            case "redraw" -> null;
            case "resign" -> doResign();
            case "move" -> doMove();
            default -> doUnknown(input);
        };
    }

    public DisplayUtilities.Status handleObserving(String input) {
        return switch (input.toLowerCase().trim()) {
            case "help" -> doHelp(DisplayUtilities.Status.OBSERVING);
            case "leave" -> doLeave();
            case "redraw" -> null;
            default -> doUnknown(input);
        };
    }

    public void loadError(ErrorMessage message) {
        manager.setLatestError(message.getMsg());
    }

    public void loadNotification(NotificationMessage message) {
        manager.setLatestMessage(message.getMsg());
    }

    public void loadGame(LoadGameMessage message) {
        manager.setGame(message.getGame());
    }

    private DisplayUtilities.Status doUnknown(String input) {
        du.showMessage((EscapeSequences.beautify(
                du.errorStyle,
                "'%s' is not a recognized command.".formatted(input))));
        du.showMessage("Type %s for known commands."
                .formatted(
                        EscapeSequences.beautify(du.commandStyle, "help")));
        return null;
    }

    private DisplayUtilities.Status doHelp(DisplayUtilities.Status state) {
        du.showMessage(switch (state) {
            case SIGNED_IN -> signedInHelp;
            case PLAYING -> playingHelp;
            case OBSERVING -> observingHelp;
            default -> signedOutHelp;
        });
        return null;
    }

    private DisplayUtilities.Status doQuit() {
        return DisplayUtilities.Status.QUIT;
    }

    private DisplayUtilities.Status doLogin() {
        // show a login prompt
        du.showPrompt("Username: ");
        String username = SCANNER.nextLine();
        du.showPrompt("Password: ");
        String password = SCANNER.nextLine();

        // interact with the Facade to login
        try {
            authToken = facade.login(username, password);
            return DisplayUtilities.Status.SIGNED_IN;
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private DisplayUtilities.Status doRegister() {
        // show a register prompt
        du.showPrompt("Username: ");
        String username = SCANNER.nextLine();
        du.showPrompt("Password: ");
        String password = SCANNER.nextLine();
        du.showPrompt("Email: ");
        String email = SCANNER.nextLine();

        // interact with Facade to register
        try {
            authToken = facade.register(username, password, email);
            return DisplayUtilities.Status.SIGNED_IN;
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private DisplayUtilities.Status doCreate() {
        // show a creation prompt
        du.showPrompt("Game Name: ");
        String gameName = SCANNER.nextLine();

        // interact with the Facade to create a game
        try {
            facade.create(authToken, gameName);
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }
    private void printOneGame(SingleGameResponse game) {
        String whiteName = game.whiteUsername();
        String blackName = game.blackUsername();
        du.showMessage(EscapeSequences.beautify(
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

    private DisplayUtilities.Status doList() {
        try {
            Collection<SingleGameResponse> currentGames = facade.listGames(authToken);
            if (currentGames.isEmpty()) {
                du.showMessage(("%s Start a new one with %s").formatted(
                        EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD,
                                "No games yet."
                        ),
                        EscapeSequences.beautify(
                                du.commandStyle,
                                "create")));
                return null;
            }
            int i = 1;
            for (SingleGameResponse game : currentGames) {
                du.showPrompt(EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_GREEN,
                        i + " - "));
                printOneGame(game);
                i++;
            }
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private DisplayUtilities.Status doPlay() {
        String color = "none";
        try {
            // show a select and join game prompt
            du.showPrompt("Game ID: ");
            int gameId = SCANNER.nextInt();
            SCANNER.nextLine(); // flush the \n character
            if (gameId <= 0) {
                du.showMessage(EscapeSequences.beautify(
                        du.errorStyle,
                        "Game ID must be greater than 0."));
                return null;
            }

            du.showPrompt("Color: ");
            color = SCANNER.nextLine();
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color.toUpperCase());

            // interact with the Facade to join the game
            facade.play(authToken, gameId, teamColor);
            manager.setTeamColor(teamColor);
            manager.setGameCode(gameId);
            return DisplayUtilities.Status.PLAYING;
        } catch (InputMismatchException _) {
            du.showMessage((EscapeSequences.beautify(
                    du.errorStyle,
                    "Invalid ID: '%s'")).formatted(SCANNER.nextLine()));
            du.showMessage("Game ID must be a number.");
        } catch (IllegalArgumentException _) {
            du.showMessage((EscapeSequences.beautify(
                    du.errorStyle,
                    "Invalid color: '%s'")).formatted(color));
            du.showMessage("Color must be either " +
                    EscapeSequences.beautify(EscapeSequences.SET_TEXT_BOLD,"'BLACK'") + " or " +
                    EscapeSequences.beautify(EscapeSequences.SET_TEXT_BOLD,"'WHITE'"));
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private DisplayUtilities.Status doObserve() {
        try {
            // show a select game prompt
            du.showPrompt("Game ID: ");
            int gameId = SCANNER.nextInt();
            SCANNER.nextLine(); // flush the \n character

            // interact with the Facade to watch the game
            facade.observe(authToken, gameId);
            manager.setGameCode(gameId);
            return DisplayUtilities.Status.OBSERVING;
        } catch (InputMismatchException _) {
            du.showMessage((EscapeSequences.beautify(
                    du.errorStyle,
                    "Invalid ID: '%s'")).formatted(SCANNER.nextLine()));
            du.showMessage("Game ID must be a number.");
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private DisplayUtilities.Status doLogout() {
        // interact with the Facade to sign out
        try {
            facade.logout(authToken);
        } catch (NetworkException e) {
            // display as a warning
            du.showPrompt("Warning: ");
            du.showNetworkError(e);
        } finally {
            // logout on the client regardless of network errors
            authToken = null;
        }
        return DisplayUtilities.Status.SIGNED_OUT;
    }

    private DisplayUtilities.Status doResign() {
        du.showPrompt("Really Resign? (Y/n): ");
        if (!Objects.equals(SCANNER.nextLine(), "Y")) {
            return null;
        }
        try {
            facade.resign(authToken, manager.getGameCode());
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private DisplayUtilities.Status doLeave() {
        facade.leave(authToken, manager.getGameCode());
        manager.reset();
        du.showMessage(EscapeSequences.beautify(du.systemStyle, "You have left the game."));
        return DisplayUtilities.Status.SIGNED_IN;
    }

    private boolean isPromotion(String start, String end, ChessBoard board) {
        try {
            ChessPiece piece = board.getPiece(new ChessPosition(start));
            if (piece == null || piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                return false;
            }
            String endRow = end.split("")[1];
            return Objects.equals(endRow, "1") || Objects.equals(endRow, "" + board.getSize());
        } catch (IndexOutOfBoundsException _) {
            return false;
        }
    }

    private DisplayUtilities.Status doMove() {
        try {
            ChessGame game = manager.getGame();
            ChessBoard board = game.getBoard();
            if (manager.getTeamColor() != game.getTeamTurn()) {
                du.showMessage(EscapeSequences.beautify(
                        du.errorStyle,
                        "It is not your turn."
                ));
                return null;
            }

            du.showPrompt("Start Position: ");
            String start = SCANNER.next("[a-hA-H][1-%d]".formatted(board.getSize()));
            du.showPrompt("End Position: ");
            String end = SCANNER.next("[a-hA-H][1-%d]".formatted(board.getSize()));
            String promotion = null;
            if (isPromotion(start, end, board)) {
                Collection<String> promotions = List.of("q", "r", "n", "b");
                du.showPrompt("Promotion: ");
                promotion = SCANNER.nextLine();
                if (!promotions.contains(promotion.toLowerCase())) {
                    throw new InputMismatchException();
                }
            }

            ChessMove move = new ChessMove(start, end, promotion);
            //TODO validate the move client-side
            // if the promotion is offered, it can't be null (the MovementValidator should handle this already)

            facade.move(move, authToken, manager.getGameCode());
        } catch (InputMismatchException _) {
            du.showMessage(EscapeSequences.beautify(
                    du.errorStyle,
                    "Invalid position."));
            du.showMessage("Positions must be of the form %s".formatted(
                    EscapeSequences.beautify(du.commandStyle, "'a1'")
            ));
            du.showMessage("Promotions must be one of %s, %s, %s, %s".formatted(
                    EscapeSequences.beautify(du.commandStyle, "Q"),
                    EscapeSequences.beautify(du.commandStyle, "R"),
                    EscapeSequences.beautify(du.commandStyle, "N"),
                    EscapeSequences.beautify(du.commandStyle, "B")
            ));
        } catch (NetworkException e) {
            du.showNetworkError(e);
        }
        return null;
    }

    private void drawScreen() {
        du.showMessage(EscapeSequences.CLEAR_SCREEN);
        String message = manager.getLatestMessage();
        String error = manager.getLatestError();
        ChessGame game = manager.getGame();
        if (error != null) {
            du.showMessage(EscapeSequences.beautify(du.errorStyle, error));
        }
        if (message != null) {
            du.showMessage(EscapeSequences.beautify(du.notificationStyle, message));
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
}
