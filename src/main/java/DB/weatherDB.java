package DB;

import DB.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Данный класс предоставляет метод insertWeatherData для вставки данных о погоде в базу данных.
 */
public class weatherDB {
    // SQL-запрос для вставки данных в базу
    private static final String INSERT_WEATHER_QUERY = "INSERT INTO weather_data (city, current_condition, current_temp, feels_like) VALUES (?, ?, ?, ?)";

    /**
     * Метод `insertWeatherData` выполняет вставку данных о погоде в базу данных.
     * @param city Название города, для которого сохраняются данные о погоде.
     * @param currentCondition Текущие погодные условия (например, "ясно").
     * @param currentTemp Текущая температура в градусах Цельсия.
     * @param feelsLike Ощущаемая температура в градусах Цельсия.
     */
    public static void insertWeatherData(String city, String currentCondition, double currentTemp, double feelsLike) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_WEATHER_QUERY)) {

            preparedStatement.setString(1, city);
            preparedStatement.setString(2, currentCondition);
            preparedStatement.setDouble(3, currentTemp);
            preparedStatement.setDouble(4, feelsLike);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}