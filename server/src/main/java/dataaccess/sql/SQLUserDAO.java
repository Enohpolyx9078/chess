package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    private static final Logger log = LoggerFactory.getLogger(SQLUserDAO.class);

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            String insertUser = "INSERT INTO user (username, email, password) VALUES(?, ?, ?)";
            try (var insertUserStatement = conn.prepareStatement(insertUser)) {
                insertUserStatement.setString(1, user.username());
                insertUserStatement.setString(2, user.email().toLowerCase());
                //TODO hash the password
                insertUserStatement.setString(3, user.password());
                insertUserStatement.executeUpdate();
            }
        } catch (SQLException _) {
            throw new DataAccessException("error creating user");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        SQLUtility.clearTable("user");
    }

    @Override
    public int getSize() throws DataAccessException {
        return SQLUtility.getTableSize("user");
    }
}
