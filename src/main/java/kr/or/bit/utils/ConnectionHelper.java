package kr.or.bit.utils;

import kr.or.bit.config.DatabaseProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionHelper {
    public static Connection getConnection(DBType dbType) {
        Connection conn = null;

        try {
            String driver = DatabaseProperties.getRequired(dbType, "driver");
            String url = DatabaseProperties.getRequired(dbType, "url");
            String username = DatabaseProperties.getRequired(dbType, "username");
            String password = DatabaseProperties.getRequired(dbType, "password");

            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }

        return conn;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }

    public static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try { pstmt.close(); } catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }
}
