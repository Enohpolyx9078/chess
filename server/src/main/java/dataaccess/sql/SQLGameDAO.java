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
    public List<SingleGameResponse> listGames() {
        return List.of();
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
                            new Gson().fromJson(
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
    public void addPlayer(Integer gameID, String username, ChessGame.TeamColor playerColor) {

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
