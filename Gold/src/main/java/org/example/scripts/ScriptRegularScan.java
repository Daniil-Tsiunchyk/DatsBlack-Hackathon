package org.example.scripts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.example.models.ScanResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.example.Const.*;

public class ScriptRegularScan {
    private final static int DISTANCE_SCAN=20;

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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ScanResult scanResult = gson.fromJson(response.body(), ScanResult.class);
            String prettyJson = gson.toJson(scanResult);

            System.out.println("Обработанный результат сканирования: \n" + prettyJson);



            if(scanResult.getScan().getEnemyShips().length!=0){
                System.out.println("Рядом есть вражеские игроки: "+scanResult.getScan().getEnemyShips());

                battle(scanResult.getScan().getMyShips(), scanResult.getScan().getEnemyShips());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка при выполнении сканирования: " + e.getMessage());
        }
    }

    private static void battle(ScanResult.Ship[] myShips,ScanResult.Ship[] enemyShips) {
        System.out.println("Бой начинается!");

        // Создаем список для хранения выстрелов
        List<ShootData> shoots = new ArrayList<>();

        // Перебираем каждый из ваших кораблей
        for (ScanResult.Ship myShip : myShips) {
            ShootData shootData = new ShootData();
            shootData.setId(myShip.getId());
            // Проверяем, есть ли вражеские корабли в радиусе 20
            for (ScanResult.Ship enemyShip : enemyShips) {
                int distance = calculateDistance(myShip.getX(), myShip.getY(), enemyShip.getX(), enemyShip.getY());

                if (distance <= DISTANCE_SCAN) {
                    System.out.println("Корабль вражеский: "+enemyShip);
                    ShootClass shoot = new ShootClass();


                    shoot.setX(enemyShip.getX());
                    shoot.setY(enemyShip.getY());
                    shoot.setHp(enemyShip.getHp());
                    shootData.getShootClassList().add(shoot);
                }
            }
            shoots.add(shootData);
        }


        Set<ShootClass> uniqueCoordinatesSet = new HashSet<>();
        for (ShootData data:
                shoots) {
            uniqueCoordinatesSet.addAll(data.getShootClassList());
        }



        List<ShootClass> uniqueCoordinatesList = uniqueCoordinatesSet.stream()
                .distinct()
                .sorted(Comparator.comparingInt(ShootClass::getHp))
                .collect(Collectors.toList());



        for (ShootData data:
                shoots) {

            if (!data.getShootClassList().isEmpty()){
                data.setShootClassList( data.getShootClassList().subList(0, 1));
                System.out.println("Выстрел:");
                System.out.println(data);
            }
            else{
                shoots.remove(data);
            }

        }



        System.out.println("Бой заканчивается!");
    }

    private static int calculateDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    @Data
    private static class ShootData{
        private int id;
        private List<ShootClass> shootClassList = new ArrayList<>();
    }
    @Data
    public static class ShootClass{

        private int x;
        private int y;
        private int hp;
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ShootClass that = (ShootClass) obj;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}