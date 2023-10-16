import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import DB.weatherDB;

/**
 *  Данный класс получает информацию о погоде и выводит её в удобном для восприятия формате в файл "weather_output.txt"
 */
public class Start {

    public static String getWeatherCondition(String conditionCode) {
        Map<String, String> conditionMap = new HashMap<>();
        conditionMap.put("clear", "ясно");
        conditionMap.put("partly-cloudy", "малооблачно");
        conditionMap.put("cloudy", "облачно с  прояснениями");
        conditionMap.put("overcast", "пасмурно");
        conditionMap.put("light-rain", "небольшой дождь");
        conditionMap.put("rain", "дождь");
        conditionMap.put("heavy-rain", "сильный дождь");
        conditionMap.put("showers", "ливень");
        conditionMap.put("wet-snow", "дождь со снегом");
        conditionMap.put("light-snow", "небольшой снег");
        conditionMap.put("snow", "снег");
        conditionMap.put("snow-showers", "снегопад");
        conditionMap.put("hail", "град");
        conditionMap.put("thunderstorm", "гроза");
        conditionMap.put("thunderstorm-with-rain", "дождь с грозой");
        conditionMap.put("thunderstorm-with-hail", "гроза с градом");

        return conditionMap.getOrDefault(conditionCode, "Неизвестно");
    }

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
            String city = weatherData.getAsJsonObject("info")
                    .getAsJsonObject("tzinfo")
                    .get("name")
                    .getAsString();

            double currentTemp = weatherData.get("fact").getAsJsonObject().get("temp").getAsDouble();
            double currentFeelsLike = weatherData.get("fact").getAsJsonObject().get("feels_like").getAsDouble();
            String GetCurrentCondition = weatherData.get("fact").getAsJsonObject().get("condition").getAsString();

            String currentCondition = getWeatherCondition(GetCurrentCondition);

            System.out.println("Город: " + city );
            System.out.println("Текущая погода: " + currentCondition );
            System.out.println("Температура: " + currentTemp + " °C");
            System.out.println("Ощущается как: " + currentFeelsLike + " °C" );


            // Сохранение данных в базе данных
           weatherDB.insertWeatherData(city, currentCondition, currentTemp, currentFeelsLike);

        } catch (Exception e) {
            e.printStackTrace();
        }




        // Запрос для погоды на каждый час
            try {
                JsonObject hourlyWeatherData = hourlyWeatherService.getWeatherForecast();

                // Извлечение информации о погоде на каждый час
                JsonArray forecasts = hourlyWeatherData.getAsJsonArray("forecasts");
                if (forecasts != null && forecasts.size() > 0) {
                    JsonObject firstForecast = forecasts.get(0).getAsJsonObject();
                    JsonArray hours = firstForecast.getAsJsonArray("hours");

                    if (hours != null) {
                        System.out.println("\nПогода на каждый час:");
                        System.out.println("-----------------------------------------------------------------------------------------");
                        System.out.printf("%-12s %-15s %-18s %-25s %-22s%n", "Час", "Температура,°C", "Ощущается как,°C", "Погодные условия", "Кол-во осадков, мм");
                        System.out.println("-----------------------------------------------------------------------------------------");

                        for (int i = 0; i < hours.size(); i++) {
                            JsonObject hourData = hours.get(i).getAsJsonObject();
                            int hour = hourData.get("hour").getAsInt();
                            String formattedHour = String.format("%d:00", hour);
                            double temp = hourData.get("temp").getAsDouble();
                            double feelsLike = hourData.get("feels_like").getAsDouble();
                            String GetCondition = hourData.get("condition").getAsString();
                            double prec_mm = hourData.get("prec_mm").getAsDouble();
                            String condition = getWeatherCondition(GetCondition);

                            // Приведение prec_mm к формату с одной десятичной знака
                            String formattedPrecipitation = String.format("%.1f", prec_mm);

                            System.out.printf("%-12s %-15.1f %-18.1f %-25s %-22s%n", formattedHour, temp, feelsLike, condition, formattedPrecipitation);
                        }

                        System.out.println("------------------------------------------------------------------------------------------");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        // Запрос для прогноза на три дня вперед
            try {
                JsonArray threeDayForecast = WeatherService.getThreeDayForecast();

                System.out.println("\nПрогноз на три дня вперед:\n");
                for (int i = 0; i < threeDayForecast.size(); i++) {
                    JsonObject forecast = threeDayForecast.get(i).getAsJsonObject();
                    String date = forecast.get("date").getAsString();
                    double dayTemp = forecast.get("parts").getAsJsonObject().get("day_short").getAsJsonObject().get("temp").getAsDouble();
                    double nightTemp = forecast.get("parts").getAsJsonObject().get("night_short").getAsJsonObject().get("temp").getAsDouble();
                    String TempCondition = forecast.get("parts").getAsJsonObject().get("day_short").getAsJsonObject().get("condition").getAsString();
                    double prec_mm = forecast.get("parts").getAsJsonObject().get("day_short").getAsJsonObject().get("prec_mm").getAsDouble();
                    String condition = getWeatherCondition(TempCondition);

                    // Форматирование и вывод
                    System.out.println("Дата: " + date);
                    System.out.println("Температура днем: " + dayTemp + " °C");
                    System.out.println("Температура ночью: " + nightTemp + " °C");
                    System.out.println("Погодные условия: " + condition);
                    System.out.println("Количество осадков: " + prec_mm + " мм");
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