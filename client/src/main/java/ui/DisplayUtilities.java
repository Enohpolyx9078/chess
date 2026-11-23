package ui;

import facade.NetworkException;

public final class DisplayUtilities {
    public final String commandStyle = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE;
    public final String errorStyle = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;
    public final String systemStyle = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_GREEN;

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showPrompt(String msg) {
        System.out.print(msg);
    }

    public void showNetworkError(NetworkException e) {
        showMessage(EscapeSequences.beautify(
                errorStyle,
                e.getMessage()));
    }

    public enum Status {
        SIGNED_OUT,
        SIGNED_IN,
        PLAYING,
        OBSERVING,
        QUIT
    }
}
