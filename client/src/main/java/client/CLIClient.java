package client;

import java.util.Scanner;

public class CLIClient {
    private Status state = Status.SIGNED_OUT;

    public void run() {
        try (Scanner SCANNER = new Scanner(System.in)) {
            System.out.println("♕ 240 Chess Client ♕");
            System.out.println("Type Help to get started.");
            while (!state.equals(Status.QUIT)) {
                System.out.printf("[%s] >>> ", state.name());
                String input = SCANNER.nextLine();
                if (state == Status.SIGNED_OUT) {
                    handlePreLogin(input);
                } else {
                    handlePostLogin(input);
                }
            }
            System.out.println("Thanks for coming!");
        }
    }

    private void doUnknown() {
        //TODO display error message
    }

    private void doHelp() {
        //TODO display the help menu
    }

    private void doQuit() {
        state = Status.QUIT;
    }

    private void doLogin() {
        //TODO show a login prompt
        //TODO interact with the Facade to login
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
            default:
                doUnknown();
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
