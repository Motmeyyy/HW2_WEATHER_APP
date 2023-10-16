package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс `DatabaseConnection` предоставляет метод для установления соединения с базой данных PostgreSQL.
 */
public class DatabaseConnection {
    /**
     * Метод `getConnection` устанавливает соединение с базой данных PostgreSQL.
     * @return Объект connection, представляющий установленное соединение.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            String url = "jdbc:postgresql://localhost:5432/weatherDB";
            String user = "postgres";
            String password = "********";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}