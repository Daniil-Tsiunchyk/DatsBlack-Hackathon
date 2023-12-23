package org.example.scripts;

import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.example.Const.*;

public class ScriptMap {
    public static void main(String[] args) {
        try {
            BattleMap battleMap = fetchBattleMap();
            System.out.println(battleMap);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static BattleMap fetchBattleMap() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mapUrl))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), BattleMap.class);
    }

    @Data
    static class BattleMap {
        private int width;
        private int height;
        private List<Island> islands;

        @Override
        public String toString() {
            return "BattleMap{" +
                    "width=" + width +
                    ", height=" + height +
                    ", islands=" + islands +
                    '}';
        }
    }

    @Data
    public class Island {
        private int[][] map;
        private List<Integer> start;

        @Override
        public String toString() {
            return "Island{" +
                    "map=" + arrayToString(map) +
                    ", start=" + start +
                    '}';
        }

        private String arrayToString(int[][] array) {
            StringBuilder sb = new StringBuilder();
            for (int[] row : array) {
                sb.append("[");
                for (int cell : row) {
                    sb.append(cell).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("], ");
            }
            return sb.toString();
        }

    }

    @Data
    static class Coordinate {
        private int x;
        private int y;

        @Override
        public String toString() {
            return "Coordinate{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
