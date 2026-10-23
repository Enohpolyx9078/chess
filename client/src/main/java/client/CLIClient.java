package client;

import chess.ChessGame;
import facade.NetworkException;
import facade.ServerFacade;
import model.GameData;
import model.SingleGameResponse;
import ui.BoardPrinter;
import ui.EscapeSequences;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CLIClient {
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

    private static final ServerFacade FACADE = new ServerFacade(SERVER_URL);

    private String authToken;
    private Status state = Status.SIGNED_OUT;
    private static Scanner scanner;

    public void run() {
        try {
            scanner = new Scanner(System.in);
            System.out.println(
                    EscapeSequences.beautify(
                            SYSTEM_STYLE,
                            "♕ 240 Chess Client ♕"));
            System.out.println(EscapeSequences.beautify(
                    EscapeSequences.SET_TEXT_FAINT,
                    "Type Help to get started."));
            while (!state.equals(Status.QUIT)) {
                System.out.printf(
                        EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD,
                                "[%s] >>> "),
                        state.name());
                String input = scanner.nextLine();
                if (state == Status.SIGNED_OUT) {
                    handlePreLogin(input);
                } else {
                    handlePostLogin(input);
                }
            }
            System.out.println(EscapeSequences.beautify(
                    SYSTEM_STYLE,
                    "Thanks for playing!"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scanner.close();
        }
    }

    private void showNetworkError(NetworkException e) {
        System.out.println(EscapeSequences.beautify(
                ERROR_STYLE,
                e.getMessage()));
    }

    private void doUnknown(String input) {
        System.out.printf(EscapeSequences.beautify(
                ERROR_STYLE,
                "'%s' is not a recognized command.\n"), input);
        doHelp();
    }

    private void doHelp() {
        System.out.println((state == Status.SIGNED_OUT) ?
                SIGNED_OUT_HELP :
                SIGNED_IN_HELP);
    }

    private void doQuit() {
        state = Status.QUIT;
    }

    private void doLogin() {
        // show a login prompt
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // interact with the Facade to login
        try {
            authToken = FACADE.login(username, password);
            state = Status.SIGNED_IN;
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doRegister() {
        // show a register prompt
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        // interact with Facade to register
        try {
            authToken = FACADE.register(username, password, email);
            state = Status.SIGNED_IN;
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doCreate() {
        // show a creation prompt
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine();

        // interact with the Facade to create a game
        try {
            FACADE.create(authToken, gameName);
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void printOneGame(SingleGameResponse game) {
        System.out.println(EscapeSequences.beautify(
                EscapeSequences.SET_TEXT_BOLD,
                game.gameName()) +
                "\n    White: " +
                EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_COLOR_BLUE,
                        game.whiteUsername()) +
                "\n    Black: " +
                EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_COLOR_RED,
                        game.blackUsername())
        );
    }

    private void doList() {
        try {
            Collection<SingleGameResponse> currentGames = FACADE.listGames(authToken);
            if (currentGames.isEmpty()) {
                System.out.printf("%s Start a new one with %s\n",
                        EscapeSequences.beautify(
                                EscapeSequences.SET_TEXT_BOLD,
                                "No games yet."
                        ),
                        EscapeSequences.beautify(
                                COMMAND_STYLE,
                                "create"));
                return;
            }
            int i = 1;
            for (SingleGameResponse game : currentGames) {
                System.out.print(EscapeSequences.beautify(
                        EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_GREEN,
                        i + " - "));
                printOneGame(game);
            }
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doPlay() {
        String color = "none";
        try {
            // show a select and join game prompt
            System.out.print("Game ID: ");
            int gameId = scanner.nextInt();
            scanner.nextLine(); // flush the \n character
            if (gameId <= 0) {
                System.out.println(EscapeSequences.beautify(
                        ERROR_STYLE,
                        "Game ID must be greater than 0."));
                return;
            }

            System.out.print("Color: ");
            color = scanner.nextLine();
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(color.toUpperCase());

            // interact with the Facade to join the game
            FACADE.play(authToken, gameId, teamColor);

            // just print a throwaway board for now
            ChessGame game = new ChessGame();
            if (teamColor == ChessGame.TeamColor.WHITE) {
                BoardPrinter.printBoardWhite(game.getBoard());
            } else {
                BoardPrinter.printBoardBlack(game.getBoard());
            }

        } catch (InputMismatchException _) {
            System.out.printf(EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid ID: '%s'\n"), scanner.nextLine());
            System.out.println("Game ID must be a number.");
        } catch (IllegalArgumentException _) {
            System.out.printf(EscapeSequences.beautify(
                    ERROR_STYLE,
                    "Invalid color: '%s'\n"), color);
            System.out.println("Color must be either " +
                    EscapeSequences.beautify(EscapeSequences.SET_TEXT_BOLD,"'BLACK'") + " or " +
                    EscapeSequences.beautify(EscapeSequences.SET_TEXT_BOLD,"'WHITE'"));
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doObserve() {
        try {
            // show a select game prompt
            System.out.print("Game ID: ");
            int gameId = scanner.nextInt();
            scanner.nextLine(); // flush the \n character
            System.out.print("Color: ");

            // interact with the Facade to watch the game
            FACADE.observe(authToken, gameId);
        } catch (InputMismatchException _) {
            System.out.printf("Invalid ID: '%s'\nGame ID must be a number.\n", scanner.nextLine());
        } catch (NetworkException e) {
            showNetworkError(e);
        }
    }

    private void doLogout() {
        // interact with the Facade to sign out
        try {
            FACADE.logout(authToken);
        } catch (NetworkException e) {
            // display as a warning
            System.out.print("Warning: ");
            showNetworkError(e);
        } finally {
            // logout on the client regardless of network errors
            authToken = null;
            state = Status.SIGNED_OUT;
        }
    }

    private void handlePreLogin(String input) {
        switch (input.toLowerCase().trim()) {
            case "help":
                doHelp();
                break;
            case "quit":
                doQuit();
                break;
            case "login":
                doLogin();
                break;
            case "register":
                doRegister();
                break;
            default:
                doUnknown(input);
        }
    }

    private void handlePostLogin(String input) {
        switch (input.toLowerCase().trim()) {
            case "help":
                doHelp();
                break;
            case "create":
                doCreate();
                break;
            case "list":
                doList();
                break;
            case "play":
                doPlay();
                break;
            case "observe":
                doObserve();
                break;
            case "logout":
                doLogout();
                break;
            default:
                doUnknown(input);
        }
    }

    private enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        QUIT
    }
}
