package bg.sofia.uni.fmi.mjt.project.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/";

    public Connection connectToDB(String dbName, String username, String password) {
        Connection conn = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(DATABASE_URL + dbName, username, password);

            if (conn != null) {
                System.out.println("Connection Established");
            } else {
                System.out.println("Connection Failed");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return conn;
    }
}
