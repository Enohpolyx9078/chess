package client;

import java.util.Scanner;

public class CLIClient {
    private Status state = Status.SIGNED_OUT;

    public void run() {
        try (Scanner SCANNER = new Scanner(System.in)) {
            System.out.println("♕ 240 Chess Client ♕");
            System.out.println("Type Help to get started.");
            while (!state.equals(Status.QUIT)) {
                String input = SCANNER.nextLine();
                if (state == Status.SIGNED_OUT) {
                    handlePreLogin(input);
                } else {
                    handlePostLogin(input);
                }
            }
        }
    }

    private static void handlePreLogin(String input) {

    }

    private static void handlePostLogin(String input) {

    }

    private enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        QUIT
    }
}
