package AppManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/BANKING_DB";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    protected static Connection dbConnection(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);

        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
