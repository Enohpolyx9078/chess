package client;

import facade.NetworkException;
import facade.ServerFacade;

import java.util.Scanner;

public class CLIClient {
    private static final String SIGNED_OUT_HELP = """
            register - create a new account to play chess
            login - sign in to chess with an existing account
            quit - close this application
            help - displays this menu""";
    private static final String SIGNED_IN_HELP = """
            create - creates a new game
            list - lists all games
            play - join a game as a player
            observe - join a game as an observer
            logout - sign out of your account
            help - displays this menu""";
    private static final ServerFacade FACADE = new ServerFacade();
    private static Scanner scanner;

    private String authToken;
    private Status state = Status.SIGNED_OUT;

    public void run() {
        try {
            scanner = new Scanner(System.in);
            System.out.println("♕ 240 Chess Client ♕");
            System.out.println("Type Help to get started.");
            while (!state.equals(Status.QUIT)) {
                System.out.printf("[%s] >>> ", state.name());
                String input = scanner.nextLine();
                if (state == Status.SIGNED_OUT) {
                    handlePreLogin(input);
                } else {
                    handlePostLogin(input);
                }
            }
            System.out.println("Thanks for playing!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scanner.close();
        }
    }

    private void doUnknown(String input) {
        System.out.printf("'%s' is not a recognized command.\n", input);
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
            //TODO show error on fail
        }
    }

    private void doRegister() {
        //TODO show a register prompt
        //TODO interact with Facade to register
        //TODO set state to SIGNED_IN on success
        //TODO show error on fail
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

    }

    private enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        QUIT
    }
}
