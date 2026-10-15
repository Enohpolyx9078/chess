package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SQLUtility {
    static int getTableSize(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String getSize = "SELECT COUNT(*) AS size FROM ?";
            try (var getSizeStatement = conn.prepareStatement(getSize)) {
                getSizeStatement.setString(1, name);
                ResultSet rs = getSizeStatement.executeQuery();
                return rs.getInt("size");
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to connect to database");
        }
    }

    static void clearTable(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String truncateTable = "TRUNCATE TABLE ?";
            try (var truncateStatement = conn.prepareStatement(truncateTable)) {
                truncateStatement.setString(1, name);
                truncateStatement.executeUpdate();
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to connect to database");
        }
    }
}
