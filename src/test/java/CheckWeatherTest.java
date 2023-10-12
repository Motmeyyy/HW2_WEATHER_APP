import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;

import org.junit.Test;

import org.junit.Assert;

import java.io.IOException;

public class CheckWeatherTest {



// тест отправляет HTTP-запрос к сервису и проверяет ответ
    @Test
    public void testGetWeatherForecast() throws Exception {
        String apiKey = "a055ede3-ddd9-43c1-b9d3-08bf71504984";
        String apiUrl = "https://api.weather.yandex.ru/v2/forecast?lat=55.75396&lon=37.620393&hours=true&limit=1&extra=false";

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(apiUrl);
        request.addHeader("X-Yandex-API-Key", apiKey);

        String response = EntityUtils.toString(httpClient.execute(request).getEntity());

        // Парсим JSON-строку
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        // Проверяем, что ответ содержит необходимые данные
        Assert.assertTrue(json.has("fact"));
        Assert.assertTrue(json.getAsJsonObject("fact").has("temp"));
        Assert.assertTrue(json.getAsJsonObject("fact").has("feels_like"));
        Assert.assertTrue(json.getAsJsonObject("fact").has("wind_speed"));
    }

    @Test
    public void testServerUnavailable() throws Exception {
        String apiUrl = "https://api.weather.yandex.ru/v2/forecast?lat=55.75396&lon=37.620393&hours=true&limit=1&extra=false";

        // Настраиваем так, чтобы сервер не работал
        HttpClient httpClient = HttpClients.custom()
                .setProxy(new HttpHost("localhost", 8080))
                .build();

        HttpGet request = new HttpGet(apiUrl);
        request.addHeader("X-Yandex-API-Key", "a055ede3-ddd9-43c1-b9d3-08bf71504984");

        // Попытка подключения к серверу, которая должна завершиться неудачей
        try {
            String response = EntityUtils.toString(httpClient.execute(request).getEntity());
        } catch (IOException e) {
            // Проверяем, что запрос завершился ошибкой
            Assert.assertTrue(e.getMessage().contains("Connection refused"));
        }
    }

}
