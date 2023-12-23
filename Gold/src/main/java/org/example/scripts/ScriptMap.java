package org.example.scripts;

import org.example.models.BattleMap.BattleMap;
import org.example.models.BattleMap.Island;

import javax.swing.*;
import java.awt.*;
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
            SwingUtilities.invokeLater(() -> createAndShowGui(battleMap));
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

    private static void createAndShowGui(BattleMap battleMap) {
        JFrame frame = new JFrame("Battle Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MapDrawer(battleMap));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static class MapDrawer extends JPanel {
        private final BattleMap battleMap;

        public MapDrawer(BattleMap battleMap) {
            this.battleMap = battleMap;
            setPreferredSize(new Dimension(2000, 2000));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (battleMap != null) {
                for (Island island : battleMap.getIslands()) {
                    drawIsland(g, island);
                }
            }
        }

        private void drawIsland(Graphics g, Island island) {
            int[][] map = island.getMap();
            List<Integer> start = island.getStart();
            int startX = start.get(0);
            int startY = start.get(1);

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 1) {
                        g.fillRect(startX + j * 1, startY + i * 1, 1, 1);
                    }
                }
            }
        }
    }
}
