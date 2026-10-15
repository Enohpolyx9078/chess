package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import model.GameRequest;
import model.GameResult;
import model.SingleGameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private static final Logger log = LoggerFactory.getLogger(SQLGameDAO.class);

    private static String getStringFromBlob(Blob blob) throws SQLException {
        if (blob == null) return "";
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public GameResult createGame(GameRequest request) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String insertGame = "INSERT INTO game (gameName, chessGame) VALUES(?, ?)";
            try (var insertGameStatement = conn.prepareStatement(insertGame, Statement.RETURN_GENERATED_KEYS)) {
                insertGameStatement.setString(1, request.gameName());
                insertGameStatement.setString(2, new Gson().toJson(new ChessGame()));
                insertGameStatement.executeUpdate();
                ResultSet rs = insertGameStatement.getGeneratedKeys();
                if (rs.next()) {
                    return new GameResult(rs.getInt(1));
                }
                return null;
            }
        } catch (SQLException e) {
            log.error(e.toString());
            throw new DataAccessException("unable to create game");
        }
    }

    @Override
    public List<SingleGameResponse> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            List<SingleGameResponse> gamesList = new ArrayList<>();
            String getGames = "SELECT * FROM game";
            try (var getGamesStatement = conn.prepareStatement(getGames)) {
                ResultSet rs = getGamesStatement.executeQuery();
                while (rs.next()) {
                    gamesList.add(new SingleGameResponse(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName")
                    ));
                }
                return gamesList;
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to list games");
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String findGame = "SELECT * FROM game WHERE gameID=?";
            try (var findGameStatement = conn.prepareStatement(findGame)) {
                findGameStatement.setInt(1, gameID);
                ResultSet rs = findGameStatement.executeQuery();
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            ChessGame.createSerializer().fromJson(
                                    getStringFromBlob(rs.getBlob("chessGame")),
                                    ChessGame.class));
                }
                return null;
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to get game");
        }
    }

    @Override
    public void addPlayer(Integer gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String color = playerColor.name().toLowerCase();
            String updateGame = "UPDATE game SET " + color + "Username=? WHERE gameID=?";
            try (var updateGameStatement = conn.prepareStatement(updateGame)) {
                updateGameStatement.setString(1, username);
                updateGameStatement.setInt(2, gameID);
                updateGameStatement.executeUpdate();
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to add player");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        SQLUtility.clearTable("game");
    }

    @Override
    public int getSize() throws DataAccessException {
         return SQLUtility.getTableSize("game");
    }
}
