package client;

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
}
