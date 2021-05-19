package AppIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/BANKING_DB";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection dbconnection;

    public static Connection dbConnection(){

        try {
            if (dbconnection == null || dbconnection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                dbconnection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            }

        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return dbconnection;
    }
}
