package org.example.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
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


public class ScriptRegularScanAndBattle {

    public static void main(String[] args) {
        startRegularScans();
    }

    public static void shoootingAPI(ResultShootJsonShips resultShootJsonShips) throws IOException, InterruptedException {
        // Преобразование объекта в JSON-строку
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(resultShootJsonShips);
//        System.out.println(requestBody);
        // Создание POST-запроса с передачей тела запроса
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "shipCommand"))
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json") // Указываем тип контента как JSON
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Результат: " + parseResponse(response.body()));
    }

    private static String parseResponse(String responseBody) {
        ScriptRegister.Response response = gson.fromJson(responseBody, ScriptRegister.Response.class);
        return response.toString();
    }

    public static void startRegularScans() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(ScriptRegularScanAndBattle::scanAndPrint, 0, 5, TimeUnit.SECONDS);
    }

    private static void scanAndPrint() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "scan"))
                    .header("X-API-Key", apiKey)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Ответ сервера: " + response.body());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ScanResult scanResult = gson.fromJson(response.body(), ScanResult.class);
            String prettyJson = gson.toJson(scanResult);

//            System.out.println("Обработанный результат сканирования: \n" + prettyJson);
            System.out.println(getAverageShipSpeed(scanResult.getScan().getMyShips()));


            if (scanResult.getScan().getEnemyShips().length != 0) {
                System.out.println("Рядом есть вражеские игроки: " + Arrays.toString(scanResult.getScan().getEnemyShips()));

                ResultShootJsonShips ships = battle2(scanResult.getScan().getMyShips(), scanResult.getScan().getEnemyShips());
                if (!ships.getShips().isEmpty()) {
                    shoootingAPI(ships);

                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка при выполнении сканирования: " + e.getMessage());
        }
    }

    private static String getAverageShipSpeed(ScanResult.Ship[] ships) {
        if (ships.length == 0) {
            return "Средняя скорость: нет кораблей";
        }

        int totalSpeed = 0;
        for (ScanResult.Ship ship : ships) {
            totalSpeed += ship.getSpeed();
        }

        double averageSpeed = (double) totalSpeed / ships.length;
        return "Средняя скорость кораблей: " + averageSpeed;
    }


    private static ResultShootJsonShips battle(ScanResult.Ship[] myShips, ScanResult.Ship[] enemyShips) {
        System.out.println("Орудия готовы!");
        for (ScanResult.Ship enemyShip : enemyShips) {
            enemyShip.move();
        }

        ResultShootJsonShips resultShootJsonShips = new ResultShootJsonShips();

        // Стреляем по первому вражескому кораблю в радиусе
        for (ScanResult.Ship myShip : myShips) {
            if (myShip.getCannonCooldownLeft() == 0) {


                Optional<ShootClass> closestEnemy = Arrays.stream(enemyShips)
                        .filter(enemyShip -> calculateDistance(myShip.getX(), myShip.getY(), enemyShip.getX(), enemyShip.getY()) <= DISTANCE_SCAN)
                        .min(Comparator.comparingInt(ScanResult.Ship::getHp))
                        .map(enemyShip -> new ShootClass(enemyShip.getX(), enemyShip.getY(), enemyShip.getHp()));

                if (closestEnemy.isPresent()) {

                    System.out.println("Бабах! Корабль " + myShip.getId() + " стреляет по " + closestEnemy);
                    ShootJson shootJson = new ShootJson();
                    shootJson.setId(myShip.getId());
                    shootJson.setCannonShoot(closestEnemy.get());
                    resultShootJsonShips.getShips().add(shootJson);
                }
            }
        }

        System.out.println("\n");
        return resultShootJsonShips;
    }
    private static ResultShootJsonShips battle2(ScanResult.Ship[] myShips, ScanResult.Ship[] enemyShips) {
        System.out.println("Орудия готовы!");
        Arrays.stream(enemyShips).forEach(ScanResult.Ship::move);
        for (ScanResult.Ship enemyShip : enemyShips) {
            System.out.println("Стапые координты: (x,у) и скорость,направление"+enemyShip.getX()+","+enemyShip.getY()+","+enemyShip.getSpeed()+","+enemyShip.getDirection());
            enemyShip.move();
            System.out.println("Новые координты: (x,у) "+enemyShip.getX()+","+enemyShip.getY());
        }
        ResultShootJsonShips resultShootJsonShips = new ResultShootJsonShips();

        for (ScanResult.Ship myShip : myShips) {
            if(myShip.getCannonCooldownLeft()==0){
                List<ScanResult.Ship> enemyShipsList = getNewCoordinatesInEnemyArray(myShip,enemyShips);
                List<ScanResult.Ship> shootClassList = enemyShipsList.stream()
                        .filter(enemyShip -> calculateDistance(myShip.getX(), myShip.getY(), enemyShip.getX(), enemyShip.getY()) <= DISTANCE_SCAN)
                        .sorted(Comparator.comparingInt(ScanResult.Ship::getHp))
                        .toList();
                System.out.println("--------Корабль " + myShip.getId() + " может стрелять по " + shootClassList+"------------");

                if(!shootClassList.isEmpty()){
                    Optional < ScanResult.Ship> enemyShipRadius= shootClassList.stream()
                    .filter(enemyShip -> enemyShip.getHp() > enemyShip.getNumberTarget())
                            .findFirst();
                    if(enemyShipRadius.isEmpty()){
                        enemyShipRadius = shootClassList.stream()
                                .findFirst();
                    }
                    if (enemyShipRadius.isPresent()) {
                        Optional<ShootClass> closestEnemy=
                        enemyShipRadius.map(enemyShip -> {
                            enemyShip.setNumberTarget(enemyShip.getNumberTarget() + 1);
                            return new ShootClass(enemyShip.getX(),enemyShip.getY(), enemyShip.getHp());
                        });
                        System.out.println("Бабах! Корабль " + myShip.getId() + " стреляет по " + closestEnemy);
                        ShootJson shootJson = new ShootJson();
                        shootJson.setId(myShip.getId());
                        shootJson.setCannonShoot(closestEnemy.get());
                        resultShootJsonShips.getShips().add(shootJson);
                    }
                }
            }
        }
        System.out.println("\n");
        return resultShootJsonShips;
    }
    private static  List<ScanResult.Ship> getNewCoordinatesInEnemyArray(ScanResult.Ship myShip, ScanResult.Ship[] enemyShips){
        List<ScanResult.Ship> enemyShipsList = new ArrayList<>(List.of(enemyShips));
        enemyShipsList.stream()
                .forEach(enemy->{
                   /* switch (enemy.getDirection().toLowerCase()) {
                        case NORTH:
                            if(myShip.getY()>enemy.getY()){
                                enemy.setY(enemy.getY()+enemy.getSize());
                            }
                            break;
                        case SOUTH:
                            if(myShip.getY()<enemy.getY()){
                                enemy.setY(enemy.getY()-enemy.getSize());
                            }
                            break;
                        case EAST:
                            if(myShip.getX()>enemy.getX()){
                                enemy.setX(enemy.getX()+enemy.getSize());
                            }
                            break;
                        case WEST:
                            if(myShip.getX()<enemy.getX()){
                                enemy.setX(enemy.getX()-enemy.getSize());
                            }
                            break;
                        default:
                            System.out.println("Неправильное направление: " + enemy.getDirection());
                            break;
                    }*/
                });
        return enemyShipsList;
    }

    private static int calculateDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    @Data
    public static class ResultShootJsonShips {
        List<ShootJson> ships = new ArrayList<>();
    }

    @Data
    public static class ShootJson {
        private int id;
        private ShootClass cannonShoot;

    }


    @Data
    @AllArgsConstructor
    public static class ShootClass {

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