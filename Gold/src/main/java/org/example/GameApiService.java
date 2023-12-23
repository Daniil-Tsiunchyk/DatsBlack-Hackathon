package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Класс для работы с API
public class GameApiService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey; //API ключ

    public GameApiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public ScanResult scan() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://example.com/api/scan"))
                .header("X-API-Key", apiKey)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parseScanResult(response.body());
    }

    private ScanResult parseScanResult(String json) {
        return null;
    }
}
