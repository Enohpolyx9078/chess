package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

public final class SQLUtility {
    static void clearTable(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String truncateTable = "TRUNCATE TABLE " + name;
            try (var truncateStatement = conn.prepareStatement(truncateTable)) {
                truncateStatement.executeUpdate();
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to connect to database");
        }
    }
}
