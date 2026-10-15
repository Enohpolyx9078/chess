package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SQLUtility {
    private static final Logger log = LoggerFactory.getLogger(SQLUtility.class);

    static int getTableSize(String name) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String getSize = "SELECT COUNT(*) AS size FROM " + name;
            try (var getSizeStatement = conn.prepareStatement(getSize)) {
                ResultSet rs = getSizeStatement.executeQuery();
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
        } catch (SQLException e) {
            log.error("e: ", e);
            throw new DataAccessException("unable to clear " + name);
        }
    }
}
