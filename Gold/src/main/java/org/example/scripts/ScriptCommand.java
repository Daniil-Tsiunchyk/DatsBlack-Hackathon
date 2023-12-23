package org.example.scripts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.ScanResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.example.Const.*;
import static org.example.scripts.ScriptMap.fetchScanResult;

public class ScriptCommand {
    public static final Integer CHANGE_SPEED = -5; // Изменяемая скорость
    public static final Integer ROTATE_ANGLE = null; // Угол поворота

    public static void main(String[] args) {
        try {
            System.out.println("Запуск команды контроля кораблей");
            ScanResult scanResult = fetchScanResult();
            ScanResult.Ship[] myShips = scanResult.getScan().getMyShips();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gson.toJson(scanResult);

            System.out.println("Обработанный результат сканирования: \n" + prettyJson);

            sendShipCommands(myShips);
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    static class ShipsWrapper {
        private List<ShipCommand> ships;
    }

    public static void sendShipCommands(ScanResult.Ship[] myShips) throws IOException, InterruptedException {
        List<ShipCommand> commands = new ArrayList<>();
        for (ScanResult.Ship ship : myShips) {
            ShipCommand command = new ShipCommand(ship.getId(), CHANGE_SPEED, ROTATE_ANGLE);
            commands.add(command);
        }

        ShipsWrapper shipsWrapper = new ShipsWrapper(commands);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(shipsWrapper);
        System.out.println("Отправляемые команды: " + requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "shipCommand"))
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Результат: " + parseResponse(response.body()));
    }

    private static String parseResponse(String responseBody) {
        ScriptRegister.Response response = gson.fromJson(responseBody, ScriptRegister.Response.class);
        return response.toString();
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ShipCommand {
        private int id;
        private Integer changeSpeed;
        private Integer rotate;
    }
}
