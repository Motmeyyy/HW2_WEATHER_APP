import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *  Данный класс получает информацию о погоде и выводит её в удобном для восприятия формате в файл "weather_output.txt"
 */
public class Start {
    public static void main(String[] args) {
        try {
            // Создаем файл для записи вывода
            FileOutputStream fileOutputStream = new FileOutputStream("weather_output.txt");
            PrintStream printStream = new PrintStream(fileOutputStream);

            // Перенаправляем стандартный вывод в файл
            System.setOut(printStream);

            // Создаем экземпляры класса CheckWeather для выполнения запросов к погодному сервису
            CheckWeather WeatherService = new CheckWeather();
            CheckWeather hourlyWeatherService = new CheckWeather();

        // Запрос для текущей погоды
        try {
            JsonObject weatherData = WeatherService.getWeatherForecast();
            double currentTemp = weatherData.get("fact").getAsJsonObject().get("temp").getAsDouble();
            double currentFeelsLike = weatherData.get("fact").getAsJsonObject().get("feels_like").getAsDouble();
            double currentWindSpeed = weatherData.get("fact").getAsJsonObject().get("wind_speed").getAsDouble();

            System.out.println("Текущая погода:");
            System.out.println("Температура: " + currentTemp + " °C");
            System.out.println("Ощущается как: " + currentFeelsLike + " °C" );
            System.out.println("Скорость ветра: " + currentWindSpeed + " м/с");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Запрос для погоды на каждый час
        try {
            JsonObject hourlyWeatherData = hourlyWeatherService.getWeatherForecast();

            // Извлечение информации о погоде на каждый час
            JsonArray hoursArray = hourlyWeatherData.getAsJsonArray("forecasts");
            if (hoursArray != null && hoursArray.size() > 0) {
                JsonObject firstForecast = hoursArray.get(0).getAsJsonObject();
                JsonArray hours = firstForecast.getAsJsonArray("hours");

                if (hours != null) {

                    System.out.println("\nПогода на каждый час:");
                    System.out.println("------------------------------------------------------------");
                    System.out.printf("%-12s %-12s %-18s %-16s%n", "Час", "Температура", "Ощущается как", "Скорость ветра");
                    System.out.println("------------------------------------------------------------");

                    for (int i = 0; i < hours.size(); i++) {
                        JsonObject hourData = hours.get(i).getAsJsonObject();
                        int hour = hourData.get("hour").getAsInt();
                        String formattedHour = String.format("%d:00", hour);
                        double temp = hourData.get("temp").getAsDouble();
                        double feelsLike = hourData.get("feels_like").getAsDouble();
                        double windSpeed = hourData.get("wind_speed").getAsDouble();

                        System.out.printf("%-12s %-12.1f %-18.1f %-16.1f%n", formattedHour, temp, feelsLike, windSpeed);
                    }

                    System.out.println("------------------------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Запрос для прогноза на три дня вперед
        try {
            JsonArray threeDayForecast = WeatherService.getThreeDayForecast();

            System.out.println("\nПрогноз на три дня вперед:");
            for (int i = 0; i < threeDayForecast.size(); i++) {
                JsonObject forecast = threeDayForecast.get(i).getAsJsonObject();
                String date = forecast.get("date").getAsString();
                double dayTemp = forecast.get("parts").getAsJsonObject().get("day_short").getAsJsonObject().get("temp").getAsDouble();
                double nightTemp = forecast.get("parts").getAsJsonObject().get("night_short").getAsJsonObject().get("temp").getAsDouble();
                double windSpeed = forecast.get("parts").getAsJsonObject().get("day_short").getAsJsonObject().get("wind_speed").getAsDouble();

                // Форматирование и вывод
                System.out.println("Дата: " + date);
                System.out.println("Температура днем: " + dayTemp + " °C" );
                System.out.println("Температура ночью: " + nightTemp + " °C" );
                System.out.println("Скорость ветра: " + windSpeed + " м/с");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            printStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}