package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public String createAuth(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String token = UUID.randomUUID().toString();
            String insertAuth = "INSERT INTO auth (username, token) VALUES(?, ?)";
            try (var insertAuthStatement = conn.prepareStatement(insertAuth)) {
                insertAuthStatement.setString(1, user.username());
                insertAuthStatement.setString(2, token);
                insertAuthStatement.executeUpdate();
                return token;
            }
        } catch (SQLException _) {
            throw new DataAccessException("Unable to create authentication");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String findAuth = "SELECT * FROM auth WHERE token=?";
            try (var findAuthStatement = conn.prepareStatement(findAuth)) {
                findAuthStatement.setString(1, authToken);
                ResultSet rs = findAuthStatement.executeQuery();
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("token"),
                            rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException _) {
            throw new DataAccessException("Unable to create authentication");
        }
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() throws DataAccessException {
        SQLUtility.clearTable("auth");
    }

    @Override
    public int getSize() throws DataAccessException {
        return SQLUtility.getTableSize("auth");
    }
}
