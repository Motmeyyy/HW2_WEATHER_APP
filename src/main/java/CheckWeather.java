import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;

/**
 * Класс предоставляет методы для получения данных о погоде с помощью открытого API Яндекс.Погода
 * Получаем погоду на момент вызова, на каждый час даты вызова, на три дня вперед
 */
public class CheckWeather {
    // API-ключ для доступа к сервису Яндекс.Погода
    private final String apiKey = "a055ede3-ddd9-43c1-b9d3-08bf71504984";
    // URL для получения погоды с API (на каждый час)
    private final String apiUrl = "https://api.weather.yandex.ru/v2/forecast?lat=54.9848&lon=73.3674&hours=true&limit=1&extra=false";


    /**
     * Метод для получения информации по погоде на каждый час дня
     * @return объект JsonObject содержащий данные о погоде
     * @throws Exception если произошла ошибка при выполении HTTP-запроса
     */
    public JsonObject getWeatherForecast() throws Exception {

        // Создание HTTP-клиента и запроса
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(apiUrl);

        // Добавление заголовка с API-ключом
        request.addHeader("X-Yandex-API-Key", apiKey);

        // Выполнение запроса и получение ответа
        String response = EntityUtils.toString(client.execute(request).getEntity());

        // Парсинг JSON-строки с помощью библиотеки Gson
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(response).getAsJsonObject();

        return json;
    }

    /**
     * Метод для получения информации о погоде на три дня вперед
     * @return Массив JsonArray с данными о погоде на три дня вперед
     * @throws Exception если произошла ошибка при выполении HTTP-запроса
     */
    public JsonArray getThreeDayForecast() throws Exception {


        HttpClient client = HttpClients.createDefault();
        // URL для получения погоды с API (на три дня, параметр limit = 3)
        String threeDayApiUrl = "https://api.weather.yandex.ru/v2/forecast?lat=48.4647&lon=135.0715&lang=ru_RU&hours=true&limit=3&extra=false";
        HttpGet request = new HttpGet(threeDayApiUrl);
        request.addHeader("X-Yandex-API-Key", apiKey);

        String response = EntityUtils.toString(client.execute(request).getEntity());

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(response).getAsJsonObject();

        // Извлечение массива с данными о погоде на три дня вперед

        JsonArray threeDayForecasts = json.getAsJsonArray("forecasts");

        return threeDayForecasts;
    }
}

