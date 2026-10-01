package model;

public record SingleGameResponse(Integer gameID,
                                 String whiteUsername,
                                 String blackUsername,
                                 String gameName) {
    public SingleGameResponse(GameData other) {
        this(other.gameID(), other.whiteUsername(), other.blackUsername(), other.gameName());
    }
}
