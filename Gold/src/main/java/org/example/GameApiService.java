package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Класс для работы с API
public class GameApiService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey;

    public GameApiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public ScanResult scan() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://datsteam.dev/datsblack/api/scan")).header("X-API-Key", apiKey).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parseScanResult(response.body());
    }

    public void registerForDeathMatch() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://datsteam.dev/datsblack/api/deathMatch/registration")).header("X-API-Key", apiKey).POST(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Register for DeathMatch: " + response.body());
    }

    public void exitDeathMatch() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://datsteam.dev/datsblack/api/deathMatch/exitBattle")).header("X-API-Key", apiKey).POST(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Exit DeathMatch: " + response.body());
    }

    public void registerForRoyalBattle() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://datsteam.dev/datsblack/api/royalBattle/registration")).header("X-API-Key", apiKey).POST(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Register for Royal Battle: " + response.body());
    }

    public void sendShipCommands(ShipCommand[] commands) throws IOException, InterruptedException {
        String requestBody = createRequestBodyForShipCommands(commands);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://datsteam.dev/datsblack/api/shipCommand")).header("X-API-Key", apiKey).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Send Ship Commands: " + response.body());
    }

    public void scanRemotePoint(int x, int y) throws IOException, InterruptedException {
        String json = String.format("{\"x\": %d, \"y\": %d}", x, y);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://datsteam.dev/datsblack/api/longScan")).header("X-API-Key", apiKey).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Scan Remote Point: " + response.body());
    }

    private String createRequestBodyForShipCommands(ShipCommand[] commands) {
        Gson gson = new Gson();
        String json = gson.toJson(new ShipCommandsWrapper(commands));
        return json;
    }

    static class ShipCommandsWrapper {
        private ShipCommand[] ships;

        public ShipCommandsWrapper(ShipCommand[] ships) {
            this.ships = ships;
        }

        private ScanResult parseScanResult(String json) {
            return null;
        }
    }

    private ScanResult parseScanResult(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ScanResult.class);
    }
}
