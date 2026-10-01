package model;

public record SingleGameResponse(Integer gameID,
                                 String whiteUsername,
                                 String blackUsername,
                                 String gameName) {}
