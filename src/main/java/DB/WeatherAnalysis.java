package DB;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Данный класс производит анализ данных по погоде из базы данных и производит запись в файл
 */
public class WeatherAnalysis {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/weatherDB";
        String user = "postgres";
        String password = "********";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // SQL-запрос для поиска города с самой низкой температурой
            String sql = "SELECT city FROM weather_data WHERE current_temp = (SELECT MIN(current_temp) FROM weather_data)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String coldestCity = resultSet.getString("city");
                        // Запись результата в файл
                        writeToFile("Сейчас холоднее всего в - " + coldestCity, "анализ.txt");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод `writeToFile` записывает переданный текст в указанный файл.
     * @param content Текст для записи в файл.
     * @param fileName Имя файла, в который будет записан текст.
     */
    private static void writeToFile(String content, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}