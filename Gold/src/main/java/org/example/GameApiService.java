package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.Const.*;

public class GameApiService {
    public final String apiKey;

    public GameApiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void sendShipCommands(ShipCommand[] commands) throws IOException, InterruptedException {
        String requestBody = createRequestBodyForShipCommands(commands);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "shipCommand")).header("X-API-Key", apiKey).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Send Ship Commands: " + response.body());
    }

    public void scanRemotePoint(int x, int y) throws IOException, InterruptedException {
        String json = String.format("{\"x\": %d, \"y\": %d}", x, y);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "longScan")).header("X-API-Key", apiKey).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

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
    }
}
