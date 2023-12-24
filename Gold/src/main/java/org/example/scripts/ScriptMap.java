package org.example.scripts;

import com.google.gson.Gson;
import org.example.models.BattleMap;
import org.example.models.ScanResult;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.example.Const.*;

public class ScriptMap {
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Battle Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 1000));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(1000, e -> updateMap());
        timer.start();
        updateMap();
    }

    private static void updateMap() {
        try {
            BattleMap battleMap = fetchBattleMap();
            ScanResult scanResult = fetchScanResult();
            ScanResult.Zone zone = scanResult.getScan().getZone();
            MapDrawer mapDrawer = new MapDrawer(battleMap, scanResult.getScan().getMyShips(),
                    scanResult.getScan().getEnemyShips(), zone);
            mapDrawer = new MapDrawer(battleMap, scanResult.getScan().getMyShips(),
                    scanResult.getScan().getEnemyShips(), zone);

            frame.getContentPane().removeAll();
            frame.getContentPane().add(mapDrawer);
            frame.revalidate();
            frame.repaint();
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

    static ScanResult fetchScanResult() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "scan"))
                .header("X-API-Key", apiKey)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), ScanResult.class);
    }

    private static class MapDrawer extends JPanel {
        private final BattleMap battleMap;
        private final ScanResult.Ship[] myShips;
        private final ScanResult.Ship[] enemyShips;
        private final ScanResult.Zone zone;

        public MapDrawer(BattleMap battleMap, ScanResult.Ship[] myShips,
                         ScanResult.Ship[] enemyShips, ScanResult.Zone zone) {
            this.battleMap = battleMap;
            this.myShips = myShips;
            this.enemyShips = enemyShips;
            this.zone = zone;
            setPreferredSize(new Dimension(800, 800));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);
            if (battleMap != null) {
                drawShips(g, myShips, Color.BLUE);
                drawShips(g, enemyShips, Color.RED);
                for (BattleMap.Island island : battleMap.getIslands()) {
                    drawIsland(g, island);
                }
            }
            if (zone != null) {
                drawZone(g, zone);
            }
            drawBorder(g);
        }

        private void drawGrid(Graphics g) {
            g.setColor(Color.GRAY);
            int gridSize = 25;
            for (int i = 0; i <= getWidth(); i += gridSize) {
                g.drawLine(i, 0, i, getHeight());
            }
            for (int i = 0; i <= getHeight(); i += gridSize) {
                g.drawLine(0, i, getWidth(), i);
            }
        }

        private void drawBorder(Graphics g) {
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(3));
            g.drawRect(0, 0, 1000, 1000);
        }

        private void drawShips(Graphics g, ScanResult.Ship[] ships, Color color) {
            g.setColor(color);
            for (ScanResult.Ship ship : ships) {
                int x = ship.getX() / 2;
                int y = ship.getY() / 2;
                g.fillOval(x - 5, y - 5, 10, 10);
                g.drawOval(x - 15, y - 15, 30, 30);
            }
        }

        private void drawIsland(Graphics g, BattleMap.Island island) {
            g.setColor(Color.BLACK);
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

        private void drawZone(Graphics g, ScanResult.Zone zone) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(3));

            int centerSize = 6;
            int centerX = zone.getX() / 2;
            int centerY = zone.getY() / 2;
            g2d.fillOval(centerX - centerSize / 2, centerY - centerSize / 2, centerSize, centerSize);
            int radius = zone.getRadius() / 2;
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            g2d.setColor(Color.ORANGE);
            radius = radius - THIS_TICK / 2;
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
    }
}
