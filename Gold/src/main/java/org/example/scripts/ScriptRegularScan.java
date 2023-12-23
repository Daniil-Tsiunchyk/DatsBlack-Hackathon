package org.example.scripts;

import org.example.models.ScanResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.example.Const.*;

public class ScriptRegularScan {

    public static void main(String[] args) {
        startRegularScans();
    }

    public static void startRegularScans() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(ScriptRegularScan::scanAndPrint, 0, 5, TimeUnit.SECONDS);
    }

    private static void scanAndPrint() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "scan"))
                    .header("X-API-Key", apiKey)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Ответ сервера: " + response.body());
            ScanResult scanResult = gson.fromJson(response.body(), ScanResult.class);
            System.out.println("Привет");
            System.out.println(scanResult);
            System.out.println("Привет");
            System.out.println(scanResult.getScan().getEnemyShips().length);
            if(scanResult.getScan().getEnemyShips().length!=0){
                System.out.println("Рядом есть вражеские игроки: "+scanResult.getScan().getEnemyShips());
                battle(scanResult.getScan().getMyShips(), scanResult.getScan().getEnemyShips());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка при выполнении сканирования: " + e.getMessage());
        }
    }
    private static void battle( ScanResult.Ship[] myShips,ScanResult.Ship[] enemyShips){

    }
}