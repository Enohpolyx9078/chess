package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SQLUtility {
    static int getTableSize(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String getSize = "SELECT COUNT(*) AS size FROM " + name;
            try (var getSizeStatement = conn.prepareStatement(getSize)) {
                ResultSet rs = getSizeStatement.executeQuery();
                rs.next();
                return rs.getInt("size");
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to count " + name);
        }
    }

    static void clearTable(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String truncateTable = "TRUNCATE TABLE " + name;
            try (var truncateStatement = conn.prepareStatement(truncateTable)) {
                truncateStatement.executeUpdate();
            }
        } catch (SQLException _) {
            throw new DataAccessException("unable to clear " + name);
        }
    }
}
