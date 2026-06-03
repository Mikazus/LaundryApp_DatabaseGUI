package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 1433;
    private static final String DB_NAME = "PAIS_LAUNDRY";
    private static final String DB_USER = "paisatmin";
    private static final String DB_PASSWORD = "pais123";
    private static final String URL = "jdbc:sqlserver://" + DB_HOST + ":" + DB_PORT + ";databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Koneksi berhasil ke " + DB_NAME);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver gagal di load. coba cek lagi pom.xml", e);
            }
        }
        return connection;
    }
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Gagal menutup koneksi: " + e.getMessage());
        }
    }
    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("[DB] ✓ Koneksi BERHASIL ke " + DB_NAME);
            }
        } catch (SQLException e) {
            System.err.println("[DB] ✗ Koneksi GAGAL: " + e.getMessage());
            System.err.println("[DB] Periksa konfigurasi di DBConnection.java");
        }
    }
}