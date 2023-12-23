package org.example.scripts;

import com.google.gson.Gson;
import org.example.models.BattleMap.BattleMap;
import org.example.models.BattleMap.Island;
import org.example.models.ScanResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.example.Const.*;

public class ScriptMap {
    private static JFrame frame;
    private static MapDrawer mapDrawer;

    public static void main(String[] args) {
        frame = new JFrame("Battle Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        updateMap();
        Timer timer = new Timer(5000, e -> updateMap());
        timer.start();
    }

    private static void updateMap() {
        try {
            BattleMap battleMap = fetchBattleMap();
            ScanResult scanResult = fetchScanResult();
            ScanResult.Ship[] myShips = scanResult.getScan().getMyShips();
            ScanResult.Ship[] enemyShips = scanResult.getScan().getEnemyShips();

            mapDrawer = new MapDrawer(battleMap, myShips, enemyShips);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(mapDrawer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            mapDrawer.repaint();
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

    private static ScanResult fetchScanResult() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "scan"))
                .header("X-API-Key", apiKey)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), ScanResult.class);
    }

    private static void createAndShowGui(BattleMap battleMap, ScanResult.Ship[] myShips, ScanResult.Ship[] enemyShips) {
        JFrame frame = new JFrame("Battle Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MapDrawer(battleMap, myShips, enemyShips));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static class MapDrawer extends JPanel {
        private final BattleMap battleMap;
        private final ScanResult.Ship[] myShips;
        private final ScanResult.Ship[] enemyShips;
        private final int gridSize = 25;

        public MapDrawer(BattleMap battleMap, ScanResult.Ship[] myShips, ScanResult.Ship[] enemyShips) {
            this.battleMap = battleMap;
            this.myShips = myShips;
            this.enemyShips = enemyShips;
            setPreferredSize(new Dimension(1000, 1000));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);
            if (battleMap != null) {
                drawShips(g, myShips, Color.BLUE);
                drawShips(g, enemyShips, Color.RED);
                for (Island island : battleMap.getIslands()) {
                    drawIsland(g, island);
                }
            }
        }


        private void drawShips(Graphics g, ScanResult.Ship[] ships, Color color) {
            g.setColor(color);
            for (ScanResult.Ship ship : ships) {
                int x = ship.getX() / 2;
                int y = ship.getY() / 2;
                g.fillOval(x, y, 10, 10);
            }
        }

        private void drawIsland(Graphics g, Island island) {
            int[][] map = island.getMap();
            List<Integer> start = island.getStart();
            int startX = start.get(0) / 2;
            int startY = start.get(1) / 2;

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 1) {
                        g.fillRect(startX + j / 2, startY + i / 2, 1, 1);
                    }
                }
            }
        }

        private void drawGrid(Graphics g) {
            g.setColor(Color.GRAY);
            for (int i = 0; i <= getWidth(); i += gridSize) {
                g.drawLine(i, 0, i, getHeight());
            }
            for (int i = 0; i <= getHeight(); i += gridSize) {
                g.drawLine(0, i, getWidth(), i);
            }
        }
    }
}
